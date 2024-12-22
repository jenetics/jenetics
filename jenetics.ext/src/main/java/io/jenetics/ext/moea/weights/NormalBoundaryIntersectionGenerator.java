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
package io.jenetics.ext.moea.weights;

import static io.jenetics.internal.math.Basics.binomial;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public record NormalBoundaryIntersectionGenerator(int objectives, Divisions divisions)
	implements Generator
{

	public record Divisions(int inner, int outer) {
		 int size(int objectives) {
			final long result =
				binomial(objectives + outer - 1, outer) +
				inner == 0 ? 0 : binomial(objectives + inner - 1, inner);

			return Math.toIntExact(result);
		}
	}

	public NormalBoundaryIntersectionGenerator {
		if (divisions.inner() > 0) {
			if (divisions.outer() >= objectives) {
				throw new IllegalArgumentException("""
					The specified number of outer divisions produces intermediate /
					reference points, recommend setting divisions.outer < objectives: /
					[%s, objectives=%s].
					""".formatted(divisions, objectives)
				);
			}
		} else {
			if (divisions.outer() < objectives) {
				throw new IllegalArgumentException("""
                    No intermediate reference points will be generated for the /
                    specified number of divisions, recommend increasing divisions: /
                    [%s, objectives=%s].
                    """.formatted(divisions, objectives)
				);
			}
		}
	}

	@Override
	public int size() {
		return divisions.size(objectives);
	}

	@Override
	public Weights next() {
		List<double[]> weights = null;

		if (divisions.inner() > 0) {
			weights = weights(divisions.outer());

			final List<double[]> inner = weights(divisions.inner());
            for (var weight : inner) {
                for (int j = 0; j < weight.length; j++) {
                    weight[j] = (1.0/objectives + weight[j])/2.0;
                }
            }

			weights.addAll(inner);
		} else {
			weights = weights(divisions.outer());
		}

		return Weights.of(weights);
	}

	/**
	 * Generates the reference points (weights) for the given number of
	 * divisions.
	 *
	 * @param divisions the number of divisions
	 * @return the list of reference points
	 */
	private List<double[]> weights(final int divisions) {
		final var result = new ArrayList<double[]>();
		final var weight = new double[objectives];

		weights(result, weight, divisions, divisions, 0);

		return result;
	}

	/**
	 * Generate reference points (weights) recursively.
	 *
	 * @param weights list storing the generated reference points
	 * @param weight the partial reference point being recursively generated
	 * @param left the number of remaining divisions
	 * @param total the total number of divisions
	 * @param index the current index being generated
	 */
	private void weights(
		final List<double[]> weights,
		final double[] weight,
		final int left,
		final int total,
		final int index
	) {
		if (index == objectives - 1) {
			weight[index] = (double)left/total;
			weights.add(weight.clone());
		} else {
			for (int i = 0; i <= left; i += 1) {
				weight[index] = (double)i/(double)total;
				weights(weights, weight, left - i, total, index + 1);
			}
		}
	}

}
