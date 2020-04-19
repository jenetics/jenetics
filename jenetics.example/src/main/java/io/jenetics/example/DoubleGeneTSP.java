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
package io.jenetics.example;

import static java.util.Objects.requireNonNull;
import static io.jenetics.jpx.Length.Unit.METER;

import java.util.function.Function;
import java.util.stream.IntStream;

import io.jenetics.jpx.WayPoint;
import io.jenetics.jpx.geom.Geoid;

import io.jenetics.DoubleChromosome;
import io.jenetics.DoubleGene;
import io.jenetics.Genotype;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Problem;
import io.jenetics.util.ISeq;
import io.jenetics.util.ProxySorter;

/**
 * Implementation of the Traveling Salesman Problem, encoded with
 * {@link io.jenetics.DoubleGene}s.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 6.0
 * @since 6.0
 */
public class DoubleGeneTSP
	implements Problem<ISeq<WayPoint>, DoubleGene, Double>
{
	private final ISeq<WayPoint> _points;

	public DoubleGeneTSP(final ISeq<WayPoint> points) {
		_points = requireNonNull(points);
	}

	@Override
	public Function<ISeq<WayPoint>, Double> fitness() {
		return route -> route.stream()
			.collect(Geoid.DEFAULT.toTourLength())
			.to(METER);
	}

	@Override
	public Codec<ISeq<WayPoint>, DoubleGene> codec() {
		return Codec.of(
			Genotype.of(
				DoubleChromosome.of(0.0, 1.0, _points.length())
			),
			gt -> {
				final int[] order = ProxySorter.sort(
					gt.chromosome()
						.as(DoubleChromosome.class)
						.toArray()
				);
				return IntStream.of(order)
					.mapToObj(_points)
					.collect(ISeq.toISeq());
			}
		);
	}
}
