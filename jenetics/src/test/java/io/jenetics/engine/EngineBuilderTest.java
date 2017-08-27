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
package io.jenetics.engine;

import java.util.function.Function;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.Alterer;
import io.jenetics.BoltzmannSelector;
import io.jenetics.DoubleChromosome;
import io.jenetics.DoubleGene;
import io.jenetics.GaussianMutator;
import io.jenetics.Genotype;
import io.jenetics.Optimize;
import io.jenetics.RouletteWheelSelector;
import io.jenetics.Selector;
import io.jenetics.util.Factory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class EngineBuilderTest {

	@Test
	public void build() {
		final Function<Genotype<DoubleGene>, Double> fitnessFunction =
			gt -> gt.getGene().getAllele();
		final Function<Double, Double> fitnessScaler = c -> c;
		final Factory<Genotype<DoubleGene>> genotypeFactory =
			Genotype.of(DoubleChromosome.of(0, 1));
		final Selector<DoubleGene, Double> survivorsSelector = new RouletteWheelSelector<>();
		final Selector<DoubleGene, Double> offspringSelector = new BoltzmannSelector<>();
		final Alterer<DoubleGene, Double> alterer = new GaussianMutator<>();
		final Optimize optimize = Optimize.MINIMUM;
		final double offspringFraction = 0.5;
		final int populationSize = 500;
		final int phenotypeAge = 340;

		final Engine<DoubleGene, Double> engine = Engine
			.builder(fitnessFunction, genotypeFactory)
			.fitnessScaler(fitnessScaler)
			.offspringSelector(offspringSelector)
			.survivorsSelector(survivorsSelector)
			.alterers(alterer)
			.optimize(optimize)
			.offspringFraction(offspringFraction)
			.populationSize(populationSize)
			.maximalPhenotypeAge(phenotypeAge)
			.build();

		Assert.assertEquals(engine.getFitnessScaler(), fitnessScaler);
		Assert.assertEquals(engine.getFitnessFunction(), fitnessFunction);
		Assert.assertEquals(engine.getOffspringSelector(), offspringSelector);
		Assert.assertEquals(engine.getSurvivorsSelector(), survivorsSelector);
		Assert.assertEquals(engine.getAlterer(), alterer);
		Assert.assertEquals(engine.getOptimize(), optimize);
		Assert.assertEquals(engine.getOffspringCount(), (int)(offspringFraction*populationSize));
		Assert.assertEquals(engine.getOffspringCount() + engine.getSurvivorsCount(), populationSize);
		Assert.assertEquals(engine.getPopulationSize(), populationSize);
		Assert.assertEquals(engine.getMaximalPhenotypeAge(), phenotypeAge);
	}

	@Test
	public void offspringFractionZero() {
		final Function<Genotype<DoubleGene>, Double> fitnessFunction =
			gt -> gt.getGene().getAllele();
		final Factory<Genotype<DoubleGene>> genotypeFactory =
			Genotype.of(DoubleChromosome.of(0, 1));

		final Engine<DoubleGene, Double> engine = Engine
			.builder(fitnessFunction, genotypeFactory)
			.offspringFraction(0)
			.build();

		engine.stream()
			.limit(10)
			.collect(EvolutionResult.toBestEvolutionResult());
	}

	@Test
	public void offspringFractionOne() {
		final Function<Genotype<DoubleGene>, Double> fitnessFunction =
			gt -> gt.getGene().getAllele();
		final Factory<Genotype<DoubleGene>> genotypeFactory =
			Genotype.of(DoubleChromosome.of(0, 1));

		final Engine<DoubleGene, Double> engine = Engine
			.builder(fitnessFunction, genotypeFactory)
			.offspringFraction(1)
			.build();

		engine.stream()
			.limit(10)
			.collect(EvolutionResult.toBestEvolutionResult());
	}

	@Test
	public void survivorsFraction() {
		final Function<Genotype<DoubleGene>, Double> fitnessFunction =
			gt -> gt.getGene().getAllele();
		final Factory<Genotype<DoubleGene>> genotypeFactory =
			Genotype.of(DoubleChromosome.of(0, 1));

		final Engine<DoubleGene, Double> engine = Engine
			.builder(fitnessFunction, genotypeFactory)
			.survivorsFraction(0.3)
			.build();

		Assert.assertEquals(engine.getSurvivorsCount(), 15);
		Assert.assertEquals(engine.getOffspringCount(), 35);
	}

	@Test
	public void survivorsFractionZero() {
		final Function<Genotype<DoubleGene>, Double> fitnessFunction =
			gt -> gt.getGene().getAllele();
		final Factory<Genotype<DoubleGene>> genotypeFactory =
			Genotype.of(DoubleChromosome.of(0, 1));

		final Engine<DoubleGene, Double> engine = Engine
			.builder(fitnessFunction, genotypeFactory)
			.survivorsFraction(0)
			.build();

		engine.stream()
			.limit(10)
			.collect(EvolutionResult.toBestEvolutionResult());
	}

	@Test
	public void survivorsFractionOne() {
		final Function<Genotype<DoubleGene>, Double> fitnessFunction =
			gt -> gt.getGene().getAllele();
		final Factory<Genotype<DoubleGene>> genotypeFactory =
			Genotype.of(DoubleChromosome.of(0, 1));

		final Engine<DoubleGene, Double> engine = Engine
			.builder(fitnessFunction, genotypeFactory)
			.survivorsFraction(1)
			.build();

		engine.stream()
			.limit(10)
			.collect(EvolutionResult.toBestEvolutionResult());
	}

	@Test
	public void survivorsSize() {
		final Function<Genotype<DoubleGene>, Double> fitnessFunction =
			gt -> gt.getGene().getAllele();
		final Factory<Genotype<DoubleGene>> genotypeFactory =
			Genotype.of(DoubleChromosome.of(0, 1));

		final Engine<DoubleGene, Double> engine = Engine
			.builder(fitnessFunction, genotypeFactory)
			.survivorsSize(15)
			.build();

		Assert.assertEquals(engine.getSurvivorsCount(), 15);
		Assert.assertEquals(engine.getOffspringCount(), 35);
	}

	@Test
	public void offspringSize() {
		final Function<Genotype<DoubleGene>, Double> fitnessFunction =
			gt -> gt.getGene().getAllele();
		final Factory<Genotype<DoubleGene>> genotypeFactory =
			Genotype.of(DoubleChromosome.of(0, 1));

		final Engine<DoubleGene, Double> engine = Engine
			.builder(fitnessFunction, genotypeFactory)
			.offspringSize(35)
			.build();

		Assert.assertEquals(engine.getSurvivorsCount(), 15);
		Assert.assertEquals(engine.getOffspringCount(), 35);
	}

}
