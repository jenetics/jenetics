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

import io.jenetics.DoubleChromosome;
import io.jenetics.DoubleGene;
import io.jenetics.Genotype;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Codecs;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.IntRange;
import io.jenetics.util.LongRange;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.4
 * @since 3.3
 */
public class CodecExample {


	/*
	 * This codec creates a split range, where only values between [0, 2) and
	 * [8, 10) are valid. The valid range is marked with '-' and the invalid
	 * range with 'x'.
	 *
	 *   +--+--+--+--+--+--+--+--+--+--+
	 *   |  |  |  |  |  |  |  |  |  |  |
	 *   0  1  2  3  4  5  6  7  8  9  10
	 *   |-----|xxxxxxxxxxxxxxxxx|-----|
	 *      ^  |llllllll|rrrrrrrr|  ^
	 *      |       |        |      |
	 *      +-------+        +------+
	 *
	 * The mapping function maps the left part of the invalid range, denoted
	 * with 'l', to the lower valid range. The upper half of the invalid range,
	 * 'r', is mapped to the upper valid range. This way we have created a more
	 * complex codec by using an existing one.
	 * If you want to get the best value back from the best genotype, you have
	 * to decode it using the same codec.
	 *
	 * final Genotype<DoubleGene> gt = stream
	 *     .limit(100)
	 *     .collect(EvolutionResult.toBestGenotype());
	 *
	 * final double bestValue = CODEC.decode(gt);
	 */
	private static final Codec<Double, DoubleGene> CODEC = Codecs
		.ofScalar(DoubleRange.of(0, 10))
		.map((Double v) -> {
				if (v >= 2 && v < 8) {
					return v < 5 ? ((v - 2)/3)*2 : ((8 - v)/3)*2 + 8;
				}
				return v;
			});


	public static void main(final String[] args) {
		for (int i = 0; i < 100; ++i) {
			final Genotype<DoubleGene> gt = CODEC.encoding().newInstance();
			System.out.println(CODEC.decode(gt));
		}
	}


	private static double repair(final double v) {
		/*
		EvolutionStream<DoubleGene, Double> stream = null;

		final Genotype<DoubleGene> gt = stream
			.limit(100)
			.collect(EvolutionResult.toBestGenotype());

		final double value = CODEC.decode(gt);

		Object o = CODEC;
		if (v >= 2 && v <= 8) {
			return v < 5 ? (v/3)*2 : (v/3)*2 + 8;
		}
		return v;

		 */
		return 0;
	}

	// The domain class
	final static record Tuple(int a, long b, double c) {}

	// The fitness functions. No need to know anything about GAs. Decoupling of
	// the problem function from the GA usage.
	static double f(final Tuple param) {
		return param.a + param.b + param.c;
	}

	// The encoding/decoding of the problem domain is defined at ONE place.
	static Codec<Tuple, DoubleGene> codec(
		final IntRange v1Domain,
		final LongRange v2Domain,
		final DoubleRange v3Domain
	) {
		return Codec.of(
			Genotype.of(
				DoubleChromosome.of(DoubleRange.of(v1Domain.min(), v1Domain.max())),
				DoubleChromosome.of(DoubleRange.of(v2Domain.min(), v2Domain.max())),
				DoubleChromosome.of(v3Domain)
			),
			gt -> new Tuple(
				gt.get(0).gene().intValue(),
				gt.get(1).gene().longValue(),
				gt.get(2).gene().doubleValue()
			)
		);
	}

//	public static void main(final String[] args) {
//		// The domain of your fitness function.
//		final IntRange domain1 = IntRange.of(0, 100);
//		final LongRange domain2 = LongRange.of(0, 1_000_000_000_000L);
//		final DoubleRange domain3 = DoubleRange.of(0, 1);
//
//		// The problem domain encoder/decoder.
//		final Codec<Tuple, DoubleGene> codec = codec(domain1, domain2, domain3);
//
//		final Engine<DoubleGene, Double> engine = Engine
//			.builder(CodecExample::f, codec)
//			.build();
//
//		final Genotype<DoubleGene> gt = engine.stream()
//			.limit(100)
//			.collect(EvolutionResult.toBestGenotype());
//
//		final Tuple param = codec.decoder().apply(gt);
//		System.out.println(String.format("Result: \t%s", param));
//	}

}
