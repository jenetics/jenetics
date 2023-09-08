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

import java.util.ArrayList;
import java.util.List;

import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;
import io.jenetics.util.MSeq;

import io.jenetics.ext.internal.util.Finding;
import io.jenetics.ext.moea.Solutions;
import io.jenetics.ext.moea.Vec;
import io.jenetics.ext.moea.weights.Weights;

/**
 * This class implements the <em>associate</em> step of the NSGA3 algorithm,
 * as described in (1).
 * <p>
 * <img alt="Normalization" src="doc-files/associate.png" width="500">
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public record Associate(Weights weights) {

	/**
	 * Associates each solution to the nearest reference point, returning a
	 * list-of-lists.  The outer list maps to each reference point using their
	 * index.  The inner list is an unordered collection of the solutions
	 * associated with the reference point.
	 *
	 * @param solutions the solutions
	 * @return the association of solutions to reference points
	 */
	public ISeq<List<Vec<double[]>>> associate(final Solutions<double[]> solutions) {
		final var result = MSeq.<List<Vec<double[]>>>ofLength(weights.size())
			.fill(ArrayList::new)
			.toISeq();

		for (var solution : solutions) {
			final int minWeightIndex = Finding.<Integer>argmin(
				IntRange.of(0, weights.size()),
				i -> distance(weights.get(i).data(), solution.data())
			);

			result.get(minWeightIndex).add(solution);
		}

		return result;
	}

	/**
	 * Returns the minimum perpendicular distance between a point and a line.
	 *
	 * @param line the line originating from the origin
	 * @param point the point
	 * @return the minimum distance
	 */
	private static double distance(double[] line, double[] point) {
		return magnitude(subtract(multiply(dot(line, point)/dot(line, line), line), point));
	}

	private static double dot(double[] u, double[] v) {
		double dot = 0.0;
		for (int i = 0; i < u.length; i++) {
			//dot += u[i] * v[i];
			dot = Math.fma(u[i], v[i], dot);
		}
		return dot;
	}

	private static double[] subtract(double[] u, double[] v) {
		final var w = new double[u.length];
		for (int i = 0; i < u.length; i++) {
			w[i] = u[i] - v[i];
		}
		return w;
	}

	private static double[] multiply(double a, double[] u) {
		final var w = new double[u.length];
		for (int i = 0; i < u.length; i++) {
			w[i] = a*u[i];
		}
		return w;
	}

	private static double magnitude(double[] u) {
		return Math.sqrt(dot(u, u));
	}

}
