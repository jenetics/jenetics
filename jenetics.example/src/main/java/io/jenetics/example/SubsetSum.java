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

import java.util.Random;
import java.util.function.Function;

import io.jenetics.EnumGene;
import io.jenetics.Mutator;
import io.jenetics.PartiallyMatchedCrossover;
import io.jenetics.Phenotype;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Codecs;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Limits;
import io.jenetics.engine.Problem;
import io.jenetics.prngine.LCG64ShiftRandom;
import io.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.4
 * @since 3.4
 */
public class SubsetSum
	implements Problem<ISeq<Integer>, EnumGene<Integer>, Integer>
{

	private final ISeq<Integer> _basicSet;
	private final int _size;

	public SubsetSum(final ISeq<Integer> basicSet, final int size) {
		_basicSet = requireNonNull(basicSet);
		_size = size;
	}

	@Override
	public Function<ISeq<Integer>, Integer> fitness() {
		return subset -> Math.abs(
			subset.stream()
				.mapToInt(Integer::intValue)
				.sum()
		);
	}

	@Override
	public Codec<ISeq<Integer>, EnumGene<Integer>> codec() {
		return Codecs.ofSubSet(_basicSet, _size);
	}

	public static SubsetSum of(final int n, final int k, final Random random) {
		return new SubsetSum(
			random.doubles()
				.limit(n)
				.mapToObj(d -> (int)((d - 0.5)*n))
				.collect(ISeq.toISeq()),
			k
		);
	}


	public static void main(final String[] args) {
		final SubsetSum problem = of(500, 15, new LCG64ShiftRandom(101010));

		final Engine<EnumGene<Integer>, Integer> engine = Engine.builder(problem)
			.minimizing()
			.maximalPhenotypeAge(5)
			.alterers(
				new PartiallyMatchedCrossover<>(0.4),
				new Mutator<>(0.3))
			.build();

		final Phenotype<EnumGene<Integer>, Integer> result = engine.stream()
			.limit(Limits.bySteadyFitness(55))
			.collect(EvolutionResult.toBestPhenotype());

		System.out.print(result);
	}

}
