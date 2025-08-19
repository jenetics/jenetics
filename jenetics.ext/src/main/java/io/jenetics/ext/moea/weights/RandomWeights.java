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

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.random.RandomGenerator;

import io.jenetics.internal.math.Basics;
import io.jenetics.internal.util.Requires;
import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;

import io.jenetics.ext.moea.Vec;

/**
 * Weight generator for creating random weights.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public record RandomWeights(int objectives, int size, RandomGenerator random)
	implements Generator
{

	public RandomWeights {
		if (objectives < 2) {
			throw new IllegalArgumentException(
				"Number of objectives must be > 1: " + objectives
			);
		}
		Requires.positive(size);
		requireNonNull(random);
	}

	@Override
	public Weights next() {
		return objectives == 2 ? next2d() : nextNd();
	}

	private Weights next2d() {
		MSeq<Vec<double[]>> weights = MSeq.ofLength(size);

		// Add boundary weights.
		weights.set(0, Vec.of(0.0, 1.0));
		weights.set(size - 1, Vec.of(1.0, 0.0));

		for (int i = 1; i < size - 1; i++) {
			double a = i/(double)(size - 1);
			weights.set(i, Vec.of(a, 1 - a));
		}

		return new Weights(weights.toISeq());
	}

	private Weights nextNd() {
		int N = 50;
		final var candidates = new ArrayList<double[]>();

		// Create normalized, random weights.
		for (int i = 0; i < size*N; ++i) {
			final double[] weight = new double[objectives];
			for (int j = 0; j < objectives; ++j) {
				weight[j] = random.nextDouble();
			}

			candidates.add(Basics.normalize(weight));
		}

		final var weights = new ArrayList<double[]>();

		// Add boundary weights (1,0,...,0), (0,1,...,0), ..., (0,...,0,1).
		for (int i = 0; i < objectives; ++i) {
			double[] weight = new double[objectives];
			weight[i] = 1.0;
			weights.add(weight);
		}

		// Fill in remaining weights with the weight vector with the largest
		// distance from the assigned weights.
		while (weights.size() < size) {
			double[] weight = null;
			double distance = Double.NEGATIVE_INFINITY;

            for (var candidate : candidates) {
                double d = Double.POSITIVE_INFINITY;

                for (var doubles : weights) {
                    d = Math.min(d, Basics.distance(candidate, doubles));
                }

                if (d > distance) {
                    weight = candidate;
                    distance = d;
                }
            }

			weights.add(weight);
			candidates.remove(weight);
		}

		return Weights.of(weights);
	}

}
