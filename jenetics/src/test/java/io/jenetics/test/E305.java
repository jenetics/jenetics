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
package io.jenetics.test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import io.jenetics.EnumGene;
import io.jenetics.Genotype;
import io.jenetics.Optimize;
import io.jenetics.PartiallyMatchedCrossover;
import io.jenetics.RouletteWheelSelector;
import io.jenetics.SwapMutator;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Codecs;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class E305 {
	static final int ROW_NUM = 20;
	static final int COL_NUM = 20;
	static final int TOTAL_NUM = ROW_NUM*COL_NUM;
	static final ISeq<Integer> A = ISeq.of(40, 40, 40, 40, 40, 40, 40, 40, 39);

	static ISeq<Integer> buildOrders() {
		List<Integer> tmp = new ArrayList<>(TOTAL_NUM);
		for (int i = 1; i <= A.size(); i++) {
			int finalI = i;
			List<Integer> a = IntStream.rangeClosed(1, A.get(i - 1))
				.boxed()
				.map(x -> finalI)
				.toList();

			tmp.addAll(a);
		}

		for (int i = 0; i < TOTAL_NUM - A.stream().mapToInt(x -> x).sum(); i++) {
			tmp.add(0);
		}
		return ISeq.of(tmp);
	}

	static Codec<ISeq<Integer>, EnumGene<Integer>> buildCodec(ISeq<Integer> orders) {
		return Codecs.ofPermutation(orders);
	}

	private static Integer fitness(ISeq<Integer> result) {
		result = result.stream().filter(x -> x != 0).collect(ISeq.toISeq());
		int count = 0;
		for (int i = 1; i < result.size(); i++) {
			if (!result.get(i).equals(result.get(i - 1))) {
				count += 1;
			}
		}
		return count;
	}

	static Engine<EnumGene<Integer>, Integer> buildEngine(Codec<ISeq<Integer>, EnumGene<Integer>> codec) {
		return Engine.builder(E305::fitness, codec)
			.offspringSelector(new RouletteWheelSelector<>())
			.alterers(
				new SwapMutator<>(),
				new PartiallyMatchedCrossover<>(0.9)
			)
			.populationSize(20)
			.optimize(Optimize.MINIMUM)
			.build();
	}


	public static void main(final String[] args) {
		final ISeq<Integer> orders = buildOrders();
		final Codec<ISeq<Integer>, EnumGene<Integer>> codec = buildCodec(orders);
		final Engine<EnumGene<Integer>, Integer> engine = buildEngine(codec);

		final Genotype<EnumGene<Integer>> collect = engine.stream()
			.limit(1000)
			.collect(EvolutionResult.toBestGenotype());

		final ISeq<Integer> best = codec.decode(collect);
		System.out.println(best);
		System.out.println(fitness(best));

	}

}
