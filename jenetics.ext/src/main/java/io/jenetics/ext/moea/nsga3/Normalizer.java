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
package io.jenetics.ext.moea.nsga3;

import static io.jenetics.ext.internal.util.Finding.argmin;

import java.util.Arrays;
import java.util.function.ToDoubleFunction;

import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;

import io.jenetics.ext.moea.Solutions;
import io.jenetics.ext.moea.Vec;

/**
 * This class implements the <em>normalization</em> step of the NSGA3 algorithm,
 * as described in (1).
 * <p>
 * <img alt="Normalization" src="doc-files/normalize.png" width="500">
 * </p>
 * <b>References:</b>
 * <ol>
 *   <li>
 *       Rajnikant H. Bhesdadiya, Indrajit N. Trivedi, Pradeep Jangir,
 *       Narottam Jangir and Arvind Kumar.<em> An NSGA-III algorithm for solving
 *       multi-objective economic/environmental dispatch problem</em>,
 *       Cogent Engineering, 3:1,
 *       <a href="http://dx.doi.org/10.1080/23311916.2016.1269383">
 *           DOI: 10.1080/23311916.2016.1269383</a>
 *   </li>
 * </ol>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Normalizer {

	private Normalizer() {
	}

	private static final double EPS = 1e-10;


	public static Solutions<double[]> normalize(final Solutions<double[]> solutions) {
		// Compute ideal points.
		final Vec<double[]> z_j_min = Z_j_min(solutions);
		assert z_j_min.length() == solutions.objectives();

		// Translate objectives.
		final Solutions<double[]> translated = translate(solutions, z_j_min);
		assert solutions.values().size() == translated.values().size();

		// Compute extreme points.
		final ISeq<Vec<double[]>> z_j_max = Z_j_max(translated);
		assert z_j_max.size() == solutions.objectives();

		// Compute intercepts.
		final double[] intercepts = intercepts(solutions, z_j_max);
		assert intercepts.length == solutions.objectives();

		// Normalize objectives.
		return normalize(translated, intercepts);
	}

	/**
	 * Calculates the intercepts between the hyperplane formed by the extreme
	 * points and each axis.  The original paper (1) is unclear how to handle
	 * degenerate cases, which occur more frequently in larger dimensions.  In
	 * this implementation, we simply use the nadir point for scaling.
	 *
	 * @return an array of the intercept points for each objective
	 */
	static double[] intercepts(final Solutions<double[]> solutions, final ISeq<Vec<double[]>> z_j_max) {
		boolean degenerate = false;
		double[] intercepts = new double[solutions.objectives()];

		try {
			final var b = new double[solutions.objectives()];
			Arrays.fill(b, 1.0);
			final var A = new double[solutions.objectives()][solutions.objectives()];

			for (int i = 0; i < solutions.objectives(); ++i) {
				//(double[])extremePoints[i].getAttribute(NORMALIZED_OBJECTIVES);
				final double[] obj = z_j_max.get(i).data();
				System.arraycopy(obj, 0, A[i], 0, solutions.objectives());
			}

			double[] result = solve(A, b);

			for (int i = 0; i < solutions.objectives(); i++) {
				intercepts[i] = 1.0 / result[i];
			}
		} catch (ArithmeticException e) {
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

	static Solutions<double[]> normalize(
		final Solutions<double[]> solutions,
		final double[] intercepts
	) {
		for (var solution : solutions) {
			final double[] objectives = solution.data();
			for (int i = 0; i < solutions.objectives(); ++i) {
				objectives[i] /= intercepts[i];
			}
		}

		return solutions;
	}

	/**
	 * Calculates the <em>ideal point</em> of the given {@code solution}.
	 *
	 * @param solutions the solution for which to calculate the ideal point
	 * @return the ideal point
	 */
	static Vec<double[]> Z_j_min(final Solutions<double[]> solutions) {
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
		final Vec<double[]> z_j_min
	) {
		final var result = solutions.stream()
			.map(solution -> {
				final var p = solution.data().clone();
				for (int i = 0; i < p.length; ++i) {
					p[i] -= z_j_min.data()[i];
				}
				return Vec.of(p);
			})
			.collect(ISeq.toISeq());

		return new Solutions<>(result);
	}

	/**
	 * Returns the <em>extreme points</em> for all objectives.
	 *
	 * @return an array of extreme points, each index corresponds to each
	 *         objective
	 */
	static ISeq<Vec<double[]>> Z_j_max(final Solutions<double[]> solutions) {
		final MSeq<Vec<double[]>> result = MSeq.ofLength(solutions.objectives());

		for (int i = 0; i < result.length(); i++) {
			result.set(i, Z_j_max(solutions, i));
		}

		return result.toISeq();
	}

	/**
	 * Returns the extreme point in the given objective.  The extreme point is
	 * the point that minimizes the achievement of scalarizing function using a
	 * reference point near the given objective.
	 *
	 * @param objective the objective index
	 * @return the extreme point in the given objective
	 */
	static Vec<double[]> Z_j_max(
		final Solutions<double[]> solutions,
		final int objective
	) {
		// Initialize the weight vector for the given objective.
		double[] weights = new double[solutions.objectives()];
		Arrays.fill(weights, Math.pow(10, -6));
		weights[objective] = 1;

		return argmin(solutions, ASF(weights));
	}

	/**
	 * The Chebyshev achievement scalarizing function (ASF).
	 *
	 * @param weights the reference point (weight vector)
	 * @return the scalarizing function for the given weights
	 */
	static ToDoubleFunction<Vec<double[]>> ASF(final double[] weights) {
		return solution -> {
			double max = Double.NEGATIVE_INFINITY;
			for (int i = 0; i < solution.length(); ++i) {
				max = Math.max(max, solution.data()[i]/weights[i]);
			}
			return max;
		};
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
	static double[] solve(final double[][] A, final double[] b) {
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
