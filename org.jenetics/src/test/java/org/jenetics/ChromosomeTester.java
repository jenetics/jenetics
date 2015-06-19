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

import org.jenetics.util.ISeq;
import org.jenetics.util.ObjectTester;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public abstract class ChromosomeTester<G extends Gene<?, G>>
	extends ObjectTester<Chromosome<G>>
{

	@Test
	public void getGene() {
		final Chromosome<G> c = factory().newInstance();
		final ISeq<G> genes = c.toSeq();

		Assert.assertEquals(c.getGene(), genes.get(0));
		for (int i = 0; i < genes.length(); ++i) {
			Assert.assertSame(c.getGene(i), genes.get(i));
		}
	}

	@Test
	public void newInstanceFromArray() {
		for (int i = 0; i < 100; ++i) {
			final Chromosome<G> c1 = factory().newInstance();
			final ISeq<G> genes = c1.toSeq();
			final Chromosome<G> c2 = c1.newInstance(genes);

			Assert.assertEquals(c2, c1);
			Assert.assertEquals(c1, c2);
		}
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void newInstanceFromNullArray() {
		final Chromosome<G> c = factory().newInstance();
		c.newInstance(null);
	}

	@Test
	public void newInstanceFromRandom() {
		for (int i = 0; i < 100; ++i) {
			final Chromosome<G> c1 = factory().newInstance();
			final Chromosome<G> c2 = c1.newInstance();

			Assert.assertEquals(c2.length(), c1.length());
			if (c1.equals(c2)) {
				Assert.assertEquals(c2.toSeq(), c1.toSeq());
			}
		}
	}

	@Test
	public void iterator() {
		final Chromosome<G> c = factory().newInstance();
		final ISeq<G> a = c.toSeq();

		int index = 0;
		for (G gene : c) {
			Assert.assertEquals(gene, a.get(index));
			Assert.assertEquals(gene, c.getGene(index));

			++index;
		}
	}

	@Test
	public void length() {
		final Chromosome<G> c = factory().newInstance();
		final ISeq<G> a = c.toSeq();

		Assert.assertEquals(c.length(), a.length());
	}

}
