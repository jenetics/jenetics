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
package org.jenetics.performance;

import java.io.Serializable;

import org.jscience.mathematics.number.Float64;

import org.jenetics.BoltzmannSelector;
import org.jenetics.CharacterChromosome;
import org.jenetics.CharacterGene;
import org.jenetics.Chromosome;
import org.jenetics.Float64Gene;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.MeanAlterer;
import org.jenetics.Mutator;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.SinglePointCrossover;
import org.jenetics.util.Array;
import org.jenetics.util.Function;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-04-27 $</em>
 */
@Suite("GA")
public class GATest {

	private static final int LOOPS = 20;
	private static final int NGENES = 5;
	private static final int NCHROMOSOMES = 50;


	private static final class Float64GeneFF
		implements Function<Genotype<Float64Gene>, Float64>,
					Serializable
	{
		private static final long serialVersionUID = 1L;

		@Override
		public Float64 apply(final Genotype<Float64Gene> genotype) {
			return genotype.getChromosome().getGene().getAllele();
		}
	}

	private static final class CharacterGeneFF
		implements Function<Genotype<CharacterGene>, Float64>,
					Serializable
	{
		private static final long serialVersionUID = 1L;

		@Override
		public Float64 apply(final Genotype<CharacterGene> genotype) {
			return Float64.valueOf(
					genotype.getChromosome().getGene().getAllele().hashCode()
				);
		}

	}

	@Test(1)
	public TestCase float64Gene = new TestCase(
		String.format("Float64Gene[G=%s, C=%s]", NGENES, NCHROMOSOMES),
		LOOPS, NGENES*NCHROMOSOMES
	) {
		private final GenotypeBuilder _gtb = new GenotypeBuilder(); {
			_gtb.ngenes(NGENES);
			_gtb.nchromosomes(NCHROMOSOMES);
		}

		private GeneticAlgorithm<Float64Gene, Float64> _ga;

		@Override
		protected void beforeTest() {
			_ga = new GeneticAlgorithm<>(_gtb.build(), new Float64GeneFF());
			_ga.setAlterers(
				new MeanAlterer<Float64Gene>(),
				new SinglePointCrossover<Float64Gene>(),
				new Mutator<Float64Gene>(0.2)
			);
			_ga.setOffspringSelector(new RouletteWheelSelector<Float64Gene, Float64>());
			_ga.setSurvivorSelector(new BoltzmannSelector<Float64Gene, Float64>());
			_ga.setup();
		}

		@Override
		protected void test() {
			_ga.evolve(200);
		}
	};

	@Test(2)
	public TestCase characterGene = new TestCase(
		String.format("CharacterGene[G=%s, C=%s]", NGENES, NCHROMOSOMES),
		LOOPS, NGENES*NCHROMOSOMES
	) {
		private final Array<Chromosome<CharacterGene>>
		_chromosomes = new Array<>(NCHROMOSOMES);
		{_chromosomes.fill(new CharacterChromosome(NGENES));}

		private final Genotype<CharacterGene> _gt = Genotype.valueOf(_chromosomes.toISeq());

		private GeneticAlgorithm<CharacterGene, Float64> _ga;

		@Override
		protected void beforeTest() {
			_ga = new GeneticAlgorithm<>(_gt, new CharacterGeneFF());
			_ga.setAlterers(
				_ga.getAlterer(),
				new SinglePointCrossover<CharacterGene>(),
				new Mutator<CharacterGene>(0.2)
			);
			_ga.setOffspringSelector(new RouletteWheelSelector<CharacterGene, Float64>());
			_ga.setSurvivorSelector(new BoltzmannSelector<CharacterGene, Float64>());
			_ga.setup();
		}

		@Override
		protected void test() {
			_ga.evolve(200);
		}

	};

}





