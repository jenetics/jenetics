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
import java.util.function.Function;

import org.testng.annotations.Test;

import org.jenetics.util.Factory;
import org.jenetics.util.ObjectTester;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
@Test
public class PhenotypeTest extends ObjectTester<Phenotype<DoubleGene, Double>> {

	private final Function<Genotype<DoubleGene>, Double> _ff =
		(Function<Genotype<DoubleGene>, Double>  & Serializable)
			gt -> sin(toRadians(gt.getGene().getAllele()));

	private final Factory<Genotype<DoubleGene>> _genotype = Genotype.of(
		DoubleChromosome.of(0, 1, 50),
		DoubleChromosome.of(0, 1, 500),
		DoubleChromosome.of(0, 1, 100),
		DoubleChromosome.of(0, 1, 50)
	);

	@Override
	protected Factory<Phenotype<DoubleGene, Double>> factory() {
		return () -> Phenotype.of(_genotype.newInstance(), 0, _ff).evaluate();
	}

}
