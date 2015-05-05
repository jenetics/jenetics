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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.util.Factory;
import org.jenetics.util.ObjectTester;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class GenotypeTest extends ObjectTester<Genotype<DoubleGene>> {

	private final Factory<Genotype<DoubleGene>> _factory = Genotype.of(
		DoubleChromosome.of(0, 1, 50),
		DoubleChromosome.of(0, 1, 500),
		DoubleChromosome.of(0, 1, 100),
		DoubleChromosome.of(0, 1, 50)
	);
	@Override protected Factory<Genotype<DoubleGene>> factory() {
		return _factory;
	}

	@Test
	public void factoryTest() {
		final Genotype<DoubleGene> factory = (Genotype<DoubleGene>)_factory;
		final Genotype<DoubleGene> gt = _factory.newInstance();

		Assert.assertEquals(factory.length(), gt.length());
		Assert.assertEquals(factory.getNumberOfGenes(), gt.getNumberOfGenes());
		for (int i = 0; i < factory.length(); ++i) {
			Assert.assertEquals(
				factory.getChromosome(i).length(),
				gt.getChromosome(i).length()
			);
		}
	}

	@Test
	public void testGenotypeGenotypeOfT() {
		final BitChromosome c1 = BitChromosome.of(12);
		final BitChromosome c2 = BitChromosome.of(12);
		final Genotype<BitGene> g2 = Genotype.of(c1, c2, c2);
		final Genotype<BitGene> g4 = g2;

		assertEquals(g2, g4);
		assertEquals(g2.hashCode(), g4.hashCode());
	}

	@Test
	public void testSetGetChromosome() {
		LongChromosome c1 = LongChromosome.of(0, 100, 10);
		LongChromosome c2 = LongChromosome.of(0, 100, 10);
		@SuppressWarnings("unused")
		LongChromosome c3 = LongChromosome.of(0, 100, 10);
		@SuppressWarnings("unused")
		Genotype<LongGene> g = Genotype.of(c1, c2);
	}


	@Test
	public void testCreate() {
		LongChromosome c1 = LongChromosome.of(0, 100, 10);
		LongChromosome c2 = LongChromosome.of(0, 100, 10);
		Genotype<LongGene> g1 = Genotype.of(c1, c2);
		Genotype<LongGene> g2 = g1.newInstance();

		assertFalse(g1 == g2);
		assertFalse(g1.equals(g2));
	}

    @Test
    public void numberOfGenes() {
		final Genotype<DoubleGene> genotype = Genotype.of(
			DoubleChromosome.of(0.0, 1.0, 8),
			DoubleChromosome.of(1.0, 2.0, 10),
			DoubleChromosome.of(0.0, 10.0, 9),
			DoubleChromosome.of(0.1, 0.9, 5)
		);
		Assert.assertEquals(genotype.getNumberOfGenes(), 32);
    }

	@Test
	public void newInstance() {
		final Genotype<DoubleGene> gt1 = Genotype.of(
			//Rotation
			DoubleChromosome.of(DoubleGene.of(-Math.PI, Math.PI)),

			//Translation
			DoubleChromosome.of(DoubleGene.of(-300, 300), DoubleGene.of(-300, 300)),

			//Shear
			DoubleChromosome.of(DoubleGene.of(-0.5, 0.5), DoubleGene.of(-0.5, 0.5))
		);

		final Genotype<DoubleGene> gt2 = gt1.newInstance();

		Assert.assertEquals(gt1.length(), gt2.length());
		for (int i = 0; i < gt1.length(); ++i) {
			Chromosome<DoubleGene> ch1 = gt1.getChromosome(i);
			Chromosome<DoubleGene> ch2 = gt2.getChromosome(i);
			Assert.assertEquals(ch1.length(), ch2.length());
		}
	}

}
