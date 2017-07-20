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
package org.jenetics;

import org.jenetics.internal.util.require;

import org.jenetics.util.ISeq;

/**
 * Defining all JAXB marshalling classes in this package.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.5
 * @since 3.5
 */
final class JAXBRegistry {
	private JAXBRegistry() {require.noInstance();}

	/**
	 * The JAXB classes of this package.
	 */
	public static final ISeq<Class<?>> CLASSES = ISeq.of(
		BitGene.Model.class,
		EnumGene.Model.class,
		CharacterGene.Model.class,
		IntegerGene.Model.class,
		LongGene.Model.class,
		DoubleGene.Model.class,

		BitChromosome.Model.class,
		CharacterChromosome.Model.class,
		IntegerChromosome.Model.class,
		LongChromosome.Model.class,
		DoubleChromosome.Model.class,
		PermutationChromosome.Model.class,

		Genotype.Model.class,
		Phenotype.Model.class,
		Population.Model.class

//		BoltzmannSelector.Model.class,
//		ExponentialRankSelector.Model.class,
//		LinearRankSelector.Model.class,
//		MonteCarloSelector.class,
//		RouletteWheelSelector.class,
//		StochasticUniversalSelector.class,
//		TournamentSelector.Model.class,
//		TruncationSelector.class,
//
//		CompositeAlterer.Model.class,
//		GaussianMutator.Model.class,
//		MeanAlterer.Model.class,
//		MultiPointCrossover.Model.class,
//		Mutator.Model.class,
//		PartiallyMatchedCrossover.Model.class,
//		SinglePointCrossover.Model.class,
//		SwapMutator.Model.class
	);
}
