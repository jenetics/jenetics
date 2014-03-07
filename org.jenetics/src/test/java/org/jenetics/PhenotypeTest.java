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

import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import java.io.Serializable;

import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import org.jenetics.util.ObjectTester;
import org.jenetics.util.functions;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-02-17 $</em>
 */
public class PhenotypeTest extends ObjectTester<Phenotype<DoubleGene, Double>> {

	private static final class FF
		implements Function<Genotype<DoubleGene>, Double>,
					Serializable
	{
		private static final long serialVersionUID = 2793605351118238308L;
		@Override public Double apply(final Genotype<DoubleGene> genotype) {
			final DoubleGene gene = genotype.getChromosome().getGene(0);
			return sin(toRadians(gene.getAllele()));
		}
	}

	private final Factory<Genotype<DoubleGene>> _genotype = Genotype.of(
		DoubleChromosome.of(0, 1, 50),
		DoubleChromosome.of(0, 1, 500),
		DoubleChromosome.of(0, 1, 100),
		DoubleChromosome.of(0, 1, 50)
	);
	private final Function<Genotype<DoubleGene>, Double> _ff = new FF();
	private final Function<Double, Double> _scaler = functions.Identity();
	private final Factory<Phenotype<DoubleGene, Double>>
	_factory = new Factory<Phenotype<DoubleGene, Double>>() {
		@Override public Phenotype<DoubleGene, Double> newInstance() {
			return Phenotype.of(_genotype.newInstance(), _ff, _scaler, 0).evaluate();
		}
	};

	@Override protected Factory<Phenotype<DoubleGene, Double>> getFactory() {
		return _factory;
	}

}
