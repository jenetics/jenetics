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

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public abstract class NumericChromosomeTester<
	N extends Number & Comparable<N>,
	G extends NumericGene<N, G>
>
	extends ChromosomeTester<G>
{


	@Test
	public void minMax() {
		@SuppressWarnings("unchecked")
		final NumericChromosome<N, G>
			c1 = (NumericChromosome<N, G>) factory().newInstance();

		@SuppressWarnings("unchecked")
		final NumericChromosome<N, G>
			c2 = (NumericChromosome<N, G>) factory().newInstance();


		assertMinMax(c1, c2);
		assertValid(c1);
		assertValid(c2);
	}

	@Test
	public void geneMinMax() {
		@SuppressWarnings("unchecked")
		final NumericChromosome<N, G>
			c = (NumericChromosome<N, G>) factory().newInstance();

		for (G gene : c) {
			Assert.assertSame(gene.getMin(), c.getMin());
			Assert.assertSame(gene.getMax(), c.getMax());
		}
	}

	@Test
	public void primitiveTypeAccess() {
		@SuppressWarnings("unchecked")
		final NumericChromosome<N, G>
			c = (NumericChromosome<N, G>) factory().newInstance();

		Assert.assertEquals(c.byteValue(), c.byteValue(0));
		Assert.assertEquals(c.shortValue(), c.shortValue(0));
		Assert.assertEquals(c.intValue(), c.intValue(0));
		Assert.assertEquals(c.floatValue(), c.floatValue(0));
		Assert.assertEquals(c.doubleValue(), c.doubleValue(0));
	}

	public void assertMinMax(
		final NumericChromosome<N, G> c1,
		final NumericChromosome<N, G> c2
	) {
		Assert.assertEquals(c1.getMin(), c2.getMin());
		Assert.assertEquals(c1.getMax(), c2.getMax());
	}

	public void assertValid(final NumericChromosome<N, G> c) {
		if (c.isValid()) {
			for (G gene: c) {
				Assert.assertTrue(gene.getAllele().compareTo(c.getMin()) >= 0);
				Assert.assertTrue(gene.getAllele().compareTo(c.getMax()) <= 0);
			}

		} else {
			for (G gene : c) {
				Assert.assertTrue(
					gene.getAllele().compareTo(c.getMin()) < 0 ||
						gene.getAllele().compareTo(c.getMax()) > 0
				);
			}
		}
	}
}
