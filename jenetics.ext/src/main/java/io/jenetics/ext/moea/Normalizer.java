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

public record Normalizer(Weights weights) {


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

		return new Solutions<>(solutions.objectives(), result);
	}

	// z_j_max
	static Solutions<double[]> findExtremePoint(int objective) {
		return null;
	}


}
