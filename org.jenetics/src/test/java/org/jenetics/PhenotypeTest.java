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

import org.jscience.mathematics.number.Float64;

import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import org.jenetics.util.ObjectTester;
import org.jenetics.util.functions;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-04-27 $</em>
 */
public class PhenotypeTest extends ObjectTester<Phenotype<Float64Gene, Float64>> {

	private static final class FF
		implements Function<Genotype<Float64Gene>, Float64>,
					Serializable
	{
		private static final long serialVersionUID = 2793605351118238308L;
		@Override public Float64 apply(final Genotype<Float64Gene> genotype) {
			final Float64Gene gene = genotype.getChromosome().getGene(0);
			return Float64.valueOf(sin(toRadians(gene.doubleValue())));
		}
	}

	private final Factory<Genotype<Float64Gene>> _genotype = Genotype.valueOf(
			new Float64Chromosome(0, 1, 50),
			new Float64Chromosome(0, 1, 500),
			new Float64Chromosome(0, 1, 100),
			new Float64Chromosome(0, 1, 50)
		);
	private final Function<Genotype<Float64Gene>, Float64> _ff = new FF();
	private final Function<Float64, Float64> _scaler = functions.Identity();
	private final Factory<Phenotype<Float64Gene, Float64>>
	_factory = new Factory<Phenotype<Float64Gene, Float64>>() {
		@Override public Phenotype<Float64Gene, Float64> newInstance() {
			return Phenotype.valueOf(_genotype.newInstance(), _ff, _scaler, 0);
		}
	};

	@Override protected Factory<Phenotype<Float64Gene, Float64>> getFactory() {
		return _factory;
	}

}
