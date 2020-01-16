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

import io.jenetics.DoubleGene;
import io.jenetics.EnumGene;
import io.jenetics.Genotype;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Codecs;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.ProxySorter;

/**
 * Creating permutation codec with DoubleGene and combining with an other
 * DoubleGene codec.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class DoubleGenePermutationCodec {

	/*
	 * Class specific for the problem domain.
	 */
	static final class Points {
		final int[] permutation;
		final double[] values;
		Points(final int[] permutation, final double[] values) {
			this.permutation = permutation;
			this.values = values;
		}
		int length() {
			return permutation.length;
		}
		double get(final int index) {
			return values[permutation[index]];
		}
	}

	/*
	 * This is the classical way for creating a  permutation codec. It can be
	 * used for solving the TSP, or other permutation problems. One drawback
	 * with this solution is, that it fixes your gene type to an `EnumGene`.
	 * This can be overly restricting if you need a second chromosome, with a
	 * different gene type, e.g. DoubleGene. Jenetics allows only one gene
	 * type for a given genotype.
	 */
	private static final Codec<int[], EnumGene<Integer>> CODEC_1 = Codecs
		.ofPermutation(100);


	/*
	 * With a little trick, it is possible to encode the permutation (int[])
	 * using a DoubleGene/DoubleChromosome. The ProxySorter indirectly
	 * creates the permutation for you, out of a given (evenly distributed)
	 * double[] array. Instead of sorting the double[] directly, the ProxySorter
	 * returns a proxy int[] array, which lets you then access the original
	 * values in ascending order.
	 *
	 * double[] array = new Random().doubles(100).toArray();
	 * int[] proxy = ProxySorter.sort(array);
	 *
	 * // Printing the array in ascending order.
	 * for (int i = 0; i < array.length; ++i) {
	 *     System.out.println(array[proxy[i]]);
	 * }
	 *
	 * Since the returned proxy array is always a permutation of the values
	 * between [0, 100) it can also be used at one. But keep in mind: the
	 * sorting is not for free ;-)
	 */
	private static final Codec<int[], DoubleGene> CODEC_2 = Codecs
		.ofVector(DoubleRange.of(0, 1), 100)
		.map(ProxySorter::sort);


	/*
	 * Codec used for the point values.
	 */
	private static final Codec<double[], DoubleGene> CODEC_3 = Codecs
		.ofVector(DoubleRange.of(1, 10), 100);

	/*
	 * The composite codec used for the evolution.
	 */
	private static final Codec<Points, DoubleGene> CODEC_4 = Codec
		.of(CODEC_2, CODEC_3, Points::new);

	// OK, maybe a little weird fitness function ;-)
	private static double fitness(final Points points) {
		double last = points.get(0);
		double length = 0;
		for (int i = 1; i < points.length(); ++i) {
			final double value = points.get(i);
			length += Math.abs(value - last);
			last = value;
		}
		return length;
	}

	/*
	 * Now you can create an Engine with one gene type.
	 */
	private static final Engine<DoubleGene, Double> ENGINE = Engine
		.builder(DoubleGenePermutationCodec::fitness, CODEC_4)
		.build();

	public static void main(final String[] args) {
		Genotype<DoubleGene> genotype = ENGINE.stream()
			.limit(100)
			.collect(EvolutionResult.toBestGenotype());

		System.out.println(genotype);
	}

}
