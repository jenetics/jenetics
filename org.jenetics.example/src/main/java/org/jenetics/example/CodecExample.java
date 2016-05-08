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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.example;

import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.Genotype;
import org.jenetics.engine.Codec;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.util.DoubleRange;
import org.jenetics.util.IntRange;
import org.jenetics.util.LongRange;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.4
 * @since 3.3
 */
public class CodecExample {

	// The domain class
	final static class Tuple {
		final int _1;
		final long _2;
		final double _3;

		Tuple(final int v1, final long v2, final double v3) {
			_1 = v1;
			_2 = v2;
			_3 = v3;
		}

		@Override
		public String toString() {
			return String.format("_1: %d - _2: %d - _3: %f", _1, _2, _3);
		}
	}

	// The fitness function. No need to know anything about GAs. Decoupling of
	// the problem function from the GA usage.
	static double f(final Tuple param) {
		return param._1 + param._2 + param._3;
	}

	// The encoding/decoding of the problem domain is defined at ONE place.
	static Codec<Tuple, DoubleGene> codec(
		final IntRange v1Domain,
		final LongRange v2Domain,
		final DoubleRange v3Domain
	) {
		return Codec.of(
			Genotype.of(
				DoubleChromosome.of(DoubleRange.of(v1Domain.getMin(), v1Domain.getMax())),
				DoubleChromosome.of(DoubleRange.of(v2Domain.getMin(), v2Domain.getMax())),
				DoubleChromosome.of(v3Domain)
			),
			gt -> new Tuple(
				gt.getChromosome(0).getGene().intValue(),
				gt.getChromosome(1).getGene().longValue(),
				gt.getChromosome(2).getGene().doubleValue()
			)
		);
	}

	public static void main(final String[] args) {
		// The domain of your fitness function.
		final IntRange domain1 = IntRange.of(0, 100);
		final LongRange domain2 = LongRange.of(0, 1_000_000_000_000L);
		final DoubleRange domain3 = DoubleRange.of(0, 1);

		// The problem domain encoder/decoder.
		final Codec<Tuple, DoubleGene> codec = codec(domain1, domain2, domain3);

		final Engine<DoubleGene, Double> engine = Engine
			.builder(CodecExample::f, codec)
			.build();

		final Genotype<DoubleGene> gt = engine.stream()
			.limit(100)
			.collect(EvolutionResult.toBestGenotype());

		final Tuple param = codec.decoder().apply(gt);
		System.out.println(String.format("Result: \t%s", param));
	}

}
