/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.ext.moea;

import java.util.Arrays;

import io.jenetics.util.BaseSeq;
import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;

/**
 * Implementation of methods used for the NSGA3 algorithm.
 *
 * @see <a href="https://sci-hub.st/10.1109/TEVC.2013.2281535">
 *     IEEE TRANSACTIONS ON EVOLUTIONARY COMPUTATION, VOL. 18, NO. 4, AUGUST 2014 577
 *     An Evolutionary Many-Objective Optimization
 *     Algorithm Using Reference-Point-Based
 *     Nondominated Sorting Approach,
 *     Part I: Solving Problems With Box Constraints</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class NSGA3 {
	private NSGA3() {
	}


	/**
	 * Calculates the intercepts between the hyperplane formed by the extreme
	 * points and each axis.  The original paper (1) is unclear how to handle
	 * degenerate cases, which occur more frequently in larger dimensions.  In
	 * this implementation, we simply use the nadir point for scaling.
	 *
	 * @return an array of the intercept points for each objective
	 */
	static double[] calculateIntercepts(
		final BaseSeq<? extends Vec<double[]>> solutions,
		final int objectives
	) {
		ISeq<Vec<double[]>> extremePoints = extremePoints(solutions, objectives);
		boolean degenerate = false;
		double[] intercepts = new double[objectives];

		try {
			double[] b = new double[objectives];
			Arrays.fill(b, 1.0);
			double[][] A = new double[objectives][objectives];

			for (int i = 0; i < objectives; ++i) {
				double[] obj = null; //(double[])extremePoints[i].getAttribute(NORMALIZED_OBJECTIVES);
                System.arraycopy(obj, 0, A[i], 0, objectives);
			}

			double[] result = lsolve(A, b);

			for (int i = 0; i < objectives; i++) {
				intercepts[i] = 1.0 / result[i];
			}
		} catch (RuntimeException e) {
			degenerate = true;
		}

		if (!degenerate) {
			// Avoid small or negative intercepts.
			for (int i = 0; i < objectives; i++) {
				if (intercepts[i] < 0.001) {
					degenerate = true;
					break;
				}
			}
		}

		if (degenerate) {
			Arrays.fill(intercepts, Double.NEGATIVE_INFINITY);

			for (var solution : solutions) {
				for (int i = 0; i < objectives; i++) {
					intercepts[i] = Math.max(
						Math.max(intercepts[i], EPS),
						solution.data()[i]
					);
				}
			}
		}

		return intercepts;
	}

	/**
	 * Returns the extreme points for all objectives.
	 *
	 * @return an array of extreme points, each index corresponds to each
	 *         objective
	 */
	static ISeq<Vec<double[]>> extremePoints(
		final BaseSeq<? extends Vec<double[]>> solutions,
		final int objectives
	) {
		final MSeq<Vec<double[]>> result = MSeq.ofLength(objectives);

		for (int i = 0; i < objectives; i++) {
			result.set(i, findExtremePoint(solutions, i, objectives));
		}

		return result.toISeq();
	}

	/**
	 * Returns the extreme point in the given objective.  The extreme point is
	 * the point that minimizes the achievement of scalarizing function using a
	 * reference point near the given objective.
	 *
	 * The NSGA-III paper (1) does not provide any details on the scalarizing
	 * function, but an earlier paper by the authors (2) where some precursor
	 * experiments are performed does define a possible function, replicated
	 * below.
	 *
	 * @param objective the objective index
	 * @return the extreme point in the given objective
	 */
	static Vec<double[]> findExtremePoint(
		final BaseSeq<? extends Vec<double[]>> solutions,
		final int objective,
		final int objectives
	) {
		double eps = 0.000001;
		double[] weights = new double[objectives];

		for (int i = 0; i < objectives; ++i) {
			if (i == objective) {
				weights[i] = 1.0;
			} else {
				weights[i] = eps;
			}
		}

		Vec<double[]> result = null;
		double resultASF = Double.POSITIVE_INFINITY;

		for (var solution : solutions) {
			double solutionASF = achievementScalarizingFunction(solution, weights);

			if (solutionASF < resultASF) {
				result = solution;
				resultASF = solutionASF;
			}
		}

		return result;
	}


	/**
	 * The Chebyshev achievement scalarizing function.
	 *
	 * @param solution the normalized solution
	 * @param weights the reference point (weight vector)
	 * @return the value of the scalarizing function
	 */
	static double achievementScalarizingFunction(
		final Vec<double[]> solution,
		final double[] weights
	) {
		double max = Double.NEGATIVE_INFINITY;

		for (int i = 0; i < solution.length(); ++i) {
			max = Math.max(max, solution.data()[i]/weights[i]);
		}

		return max;
	}

	static final double EPS = 1e-10;

	// Gaussian elimination with partial pivoting
	// Copied from http://introcs.cs.princeton.edu/java/95linear/GaussianElimination.java.html
	/**
	 * Gaussian elimination with partial pivoting.
	 *
	 * @param A the matrix
	 * @param b the b vector
	 * @return the solved equation using Gaussian elimination
	 */
	private static double[] lsolve(double[][] A, double[] b) {
		int N  = b.length;

		for (int p = 0; p < N; p++) {
			// find pivot row and swap
			int max = p;

			for (int i = p + 1; i < N; i++) {
				if (Math.abs(A[i][p]) > Math.abs(A[max][p])) {
					max = i;
				}
			}

			double[] temp = A[p];
			A[p] = A[max];
			A[max] = temp;

			double t = b[p];
			b[p] = b[max];
			b[max] = t;

			// singular or nearly singular
			if (Math.abs(A[p][p]) <= EPS) {
				throw new RuntimeException("Matrix is singular or nearly singular");
			}

			// pivot within A and b
			for (int i = p + 1; i < N; i++) {
				double alpha = A[i][p] / A[p][p];
				b[i] -= alpha * b[p];

				for (int j = p; j < N; j++) {
					A[i][j] -= alpha * A[p][j];
				}
			}
		}

		// back substitution
		double[] x = new double[N];

		for (int i = N - 1; i >= 0; i--) {
			double sum = 0.0;

			for (int j = i + 1; j < N; j++) {
				sum += A[i][j] * x[j];
			}

			x[i] = (b[i] - sum) / A[i][i];
		}

		return x;
	}


}
