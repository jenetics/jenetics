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

import java.time.LocalDate;

import org.jenetics.AnyChromosome;
import org.jenetics.AnyGene;
import org.jenetics.Genotype;
import org.jenetics.Phenotype;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.engine.Codec;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.util.RandomRegistry;

/**
 * This example tries to find a monday with the highest day of month.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class LastMonday {

	// First monday of 2015.
	private static final LocalDate MIN_MONDAY = LocalDate.of(2015, 1, 5);

	// The used Codec.
	private static final Codec<LocalDate, AnyGene<LocalDate>> CODEC = Codec.of(
		Genotype.of(AnyChromosome.of(LastMonday::nextRandomMonday)),
		gt -> gt.getGene().getAllele()
	);

	// Supplier of random 'LocalDate' objects. The implementation is responsible
	// for guaranteeing the desired allele restriction. In this case we will
	// generate only mondays.
	private static LocalDate nextRandomMonday() {
		return MIN_MONDAY.plusWeeks(RandomRegistry.getRandom().nextInt(1000));
	}

	// The fitness function: find a monday at the end of the month.
	private static int fitness(final LocalDate date) {
		return date.getDayOfMonth();
	}

	public static void main(final String[] args) {
		final Engine<AnyGene<LocalDate>, Integer> engine = Engine
			.builder(LastMonday::fitness, CODEC)
			.offspringSelector(new RouletteWheelSelector<>())
			.build();

		final Phenotype<AnyGene<LocalDate>, Integer> best = engine.stream()
			.limit(50)
			.collect(EvolutionResult.toBestPhenotype());

		System.out.println(best);
	}

}
