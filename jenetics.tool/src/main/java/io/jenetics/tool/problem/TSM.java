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
package io.jenetics.tool.problem;

import java.awt.Point;
import java.util.stream.IntStream;

import io.jenetics.EnumGene;
import io.jenetics.Optimize;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.codecs;
import io.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.6
 * @since 3.6
 */
public class TSM {
	// The locations to visit.
	static final ISeq<Point> POINTS = ISeq.of(
		new Point(0, 0), new Point(1, 2), new Point(4, 5) // ...
	);

	// The permutation codec.
	static final Codec<ISeq<Point>, EnumGene<Point>> CODEC =
		codecs.ofPermutation(POINTS);

	// The fitness function (in the problem domain).
	static double dist(final ISeq<Point> p) {
		return IntStream.range(0, p.length())
			.mapToDouble(i -> p.get(i).distance(p.get(i + i%p.length())))
			.sum();
	}

	// The evolution engine.
	static final Engine<EnumGene<Point>, Double> ENGINE = Engine
		.builder(TSM::dist, CODEC)
		.optimize(Optimize.MINIMUM)
		.build();

	// Find the solution.
	public static void main(final String[] args) {
		final ISeq<Point> result = ENGINE.stream()
			.limit(10)
			.collect(EvolutionResult.toBestResult(CODEC));

		System.out.println(result);
	}
}
