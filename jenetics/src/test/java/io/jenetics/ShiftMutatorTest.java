/*
 * Java Genetic Algorithm Library (@__identifier__@).
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
 */
package io.jenetics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.in;
import static io.jenetics.TestUtils.newDoubleGenePopulation;

import java.io.IOException;
import java.util.Random;
import java.util.random.RandomGenerator;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.stat.Sampler;
import io.jenetics.stat.Samplers;
import io.jenetics.testfixtures.stat.Histogram;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;
import io.jenetics.util.MSeq;

/**
 * @author <a href="mailto:feichtenschlager10@gmail.com">Paul Feichtenschlager</a>
 */
public class ShiftMutatorTest extends MutatorTester {

	@Override
	public Alterer<DoubleGene, Double> newAlterer(double p) {
		return new ShiftMutator<>(p);
	}

	@Test
	public void shift() {
		final var chromosomes = ISeq.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
		final var genes = MSeq.of(chromosomes);

		new ShiftMutator.Range(2, 4, 7).shift(genes);
		assertThat(genes)
			.isEqualTo(ISeq.of(0, 1, 4, 5, 6, 2, 3, 7, 8, 9));
	}

	@Test
	public void generator() {
		final var random = RandomGenerator.getDefault();
		for (int i = 0; i < 10_000; ++i) {
			assertThatNoException().isThrownBy(() ->
				ShiftMutator.RangeRandom.UNIFORM.newRange(random, 100)
			);
		}
	}

	@Test
	public void mutate() {
		final var mutator =  new ShiftMutator<EnumGene<Integer>, Integer>(0.1);

		final var values = IntRange.of(0, 10).stream().boxed().collect(ISeq.toISeq());
		final var genes = values.stream()
			.map(i -> EnumGene.of(i, values))
			.collect(ISeq.toISeq());
		final var chromosome = new PermutationChromosome<>(genes);
		System.out.println(chromosome);

		final var random = new Random(133);
		final var result = mutator.mutate(chromosome, 1, random);
		System.out.println(result.result());
	}

	@Override
	@Test(dataProvider = "alterCountParameters")
	public void alterCount(
		final Integer ngenes,
		final Integer nchromosomes,
		final Integer npopulation
	) {
		final ISeq<Phenotype<DoubleGene, Double>> p1 =
			newDoubleGenePopulation(ngenes, nchromosomes, npopulation);

		final MSeq<Phenotype<DoubleGene, Double>> p2 = p1.copy();
		Assert.assertEquals(p2, p1);

		final Alterer<DoubleGene, Double> mutator = newAlterer(0.01);

		final int alterations = mutator.alter(p2, 1).alterations();
		//final int diff = diff(p1, p2);

		if (ngenes == 1) {
			Assert.assertEquals(alterations, 0);
		} else {
			//Assert.assertTrue(alterations >= diff/2, String.format("%d >= %d", alterations, diff/2));
			//Assert.assertTrue(alterations <= 2*diff, String.format("%d < %d", alterations, 2*diff));

		}
	}

	@Override
	@Test(dataProvider = "alterProbabilityParameters")
	public void alterProbability(
		final Integer ngenes,
		final Integer nchromosomes,
		final Integer npopulation,
		final Double p
	) {
		super.alterProbability(ngenes, nchromosomes, npopulation, p);
	}

	@Override
	@DataProvider
	public Object[][] alterProbabilityParameters() {
		return new Object[][] {
			// ngenes, nchromosomes, npopulation
			{1, 1,  150, 0.15},
			{1, 2,  150, 0.15},
			{1, 15, 150, 0.15},

			{2, 1,  150, 0.15},
			{2, 2,  150, 0.15},
			{2, 15, 150, 0.15},

			{3, 1,  150, 0.15},
			{3, 2,  150, 0.15},
			{3, 15, 150, 0.15},

			{4, 1,  150, 0.15},
			{4, 2,  150, 0.15},
			{4, 15, 150, 0.15},

			{5, 1,  150, 0.15},
			{5, 2,  150, 0.15},
			{5, 15, 150, 0.15},

			{180, 1,  150, 0.15},
			{180, 2,  150, 0.15},
			{180, 15, 150, 0.15},

			{180, 1,  150, 0.5},
			{180, 2,  150, 0.5},
			{180, 15, 150, 0.5},

			{180, 1,  150, 0.85},
			{180, 2,  150, 0.85},
			{180, 15, 150, 0.85}
		};
	}

	public static void main(String[] args) throws IOException {
		final var random = RandomGenerator.getDefault();
		final var range = DoubleRange.of(0, 5);
		final var histogram = Histogram.Builder.of(range.min(), range.max(), 20);
		final var distribution = Samplers.triangular(0.2);

		for (int i = 0; i < 100_000; ++i) {
			histogram.accept(distribution.sample(random, range));
		}

		histogram.build().print(System.out);

final var lengthSampler = Samplers.linear(0.3);
final var indexSampler = Samplers.linear(0.5);
final var random1 = ShiftMutator.RangeRandom.of(lengthSampler, indexSampler);
final var mutator = new ShiftMutator<DoubleGene, Double>(random1);

		new ShuffleMutator<>();
	}

}
