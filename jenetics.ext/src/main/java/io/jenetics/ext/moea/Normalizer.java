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

import io.jenetics.util.ISeq;

import io.jenetics.ext.moea.weights.Weights;
import io.jenetics.util.MSeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public record Normalizer(Weights weights) {

	private static final double EPS = 1e-10;

	/**
	 * Calculates the ideal point of the given {@code solution}.
	 *
	 * @param solutions the solution for which to calculate the ideal point
	 * @return the ideal point
	 */
	static Vec<double[]> ideal(final Solutions<double[]> solutions) {
		final var point = new double[solutions.objectives()];
		Arrays.fill(point, Double.POSITIVE_INFINITY);

		for (var solution : solutions) {
			for (int i = 0; i < solutions.objectives(); ++i) {
				point[i] = Math.max(point[i], solution.data()[i]);
			}
		}

		return Vec.of(point);
	}

	static Solutions<double[]> translate(
		final Solutions<double[]> solutions,
		final Vec<double[]> point
	) {
		final var result = solutions.stream()
			.map(solution -> {
				final var p = solution.data().clone();
				for (int i = 0; i < p.length; ++i) {
					p[i] -= point.data()[i];
				}
				return Vec.of(p);
			})
			.collect(ISeq.toISeq());

		return new Solutions<>(result);
	}

	/**
	 * Calculates the intercepts between the hyperplane formed by the extreme
	 * points and each axis.  The original paper (1) is unclear how to handle
	 * degenerate cases, which occur more frequently in larger dimensions.  In
	 * this implementation, we simply use the nadir point for scaling.
	 *
	 * @return an array of the intercept points for each objective
	 */
	static double[] calculateIntercepts(final Solutions<double[]> solutions) {
		ISeq<Vec<double[]>> extremePoints = extremePoints(solutions);
		boolean degenerate = false;
		double[] intercepts = new double[solutions.objectives()];

		try {
			final var b = new double[solutions.objectives()];
			Arrays.fill(b, 1.0);
			final var A = new double[solutions.objectives()][solutions.objectives()];

			for (int i = 0; i < solutions.objectives(); ++i) {
				double[] obj = null; //(double[])extremePoints[i].getAttribute(NORMALIZED_OBJECTIVES);
				System.arraycopy(obj, 0, A[i], 0, solutions.objectives());
			}

			double[] result = solve(A, b);

			for (int i = 0; i < solutions.objectives(); i++) {
				intercepts[i] = 1.0 / result[i];
			}
		} catch (RuntimeException e) {
			degenerate = true;
		}

		if (!degenerate) {
			// Avoid small or negative intercepts.
			for (int i = 0; i < solutions.objectives(); i++) {
				if (intercepts[i] < 0.001) {
					degenerate = true;
					break;
				}
			}
		}

		if (degenerate) {
			Arrays.fill(intercepts, Double.NEGATIVE_INFINITY);

			for (var solution : solutions) {
				for (int i = 0; i < solutions.objectives(); i++) {
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
	private static ISeq<Vec<double[]>> extremePoints(final Solutions<double[]> solutions) {
		final MSeq<Vec<double[]>> result = MSeq.ofLength(solutions.objectives());

		for (int i = 0; i < result.length(); i++) {
			result.set(i, extremePoint(solutions, i));
		}

		return result.toISeq();
	}

	// z_j_max
	/**
	 * Returns the extreme point in the given objective.  The extreme point is
	 * the point that minimizes the achievement of scalarizing function using a
	 * reference point near the given objective.
	 * <p>
	 * The NSGA-III paper (1) does not provide any details on the scalarizing
	 * function, but an earlier paper by the authors (2) where some precursor
	 * experiments are performed does define a possible function, replicated
	 * below.
	 *
	 * @param objective the objective index
	 * @return the extreme point in the given objective
	 */
	private static Vec<double[]> extremePoint(
		final Solutions<double[]> solutions,
		final int objective
	) {
		double[] weights = new double[solutions.objectives()];
		Arrays.fill(weights, 0.000001);
		weights[objective] = 1;

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
	private static double achievementScalarizingFunction(
		final Vec<double[]> solution,
		final double[] weights
	) {
		double max = Double.NEGATIVE_INFINITY;

		for (int i = 0; i < solution.length(); ++i) {
			max = Math.max(max, solution.data()[i]/weights[i]);
		}

		return max;
	}

	/**
	 * Solves the linear equation system with the coefficient matrix {@code A}
	 * and the result vector {@code b}, using the Gaussian elimination with
	 * partial pivoting.
	 *
	 * @param A the coefficient matrix
	 * @param b the result vector
	 * @return the solution for the given equation
	 * @throws ArithmeticException if {@code A} is (near) singular
	 */
	private static double[] solve(final double[][] A, final double[] b) {
		final int n  = b.length;

		for (int p = 0; p < n; ++p) {
			// Find pivot row and swap.
			int max = p;
			for (int i = p + 1; i < n; ++i) {
				if (Math.abs(A[i][p]) > Math.abs(A[max][p])) {
					max = i;
				}
			}
			io.jenetics.internal.util.Arrays.swap(A, p, max);
			io.jenetics.internal.util.Arrays.swap(b, p, max);

			// Singular or nearly singular.
			if (Math.abs(A[p][p]) < EPS) {
				throw new ArithmeticException("Matrix is (near) singular.");
			}

			// Pivot within A and b.
			for (int i = p + 1; i < n; ++i) {
				final double alpha = A[i][p]/A[p][p];
				//b[i] -= alpha*b[p];
				b[i] = -Math.fma(alpha, b[p], -b[i]);

				for (int j = p; j < n; j++) {
					//A[i][j] -= alpha*A[p][j];
					A[i][j] = -Math.fma(alpha, A[p][j], A[i][j]);
				}
			}
		}

		// Back substitution.
		final var x = new double[n];
		for (int i = n - 1; i >= 0; i--) {
			double sum = 0.0;

			for (int j = i + 1; j < n; j++) {
				// sum += A[i][j]*x[j];
				sum = Math.fma(A[i][j], x[j], sum);
			}
			x[i] = (b[i] - sum)/A[i][i];
		}

		return x;
	}


}
