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

import org.jenetics.util.MSeq;
import org.jenetics.util.ObjectTester;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public abstract class GeneTester<G extends Gene<?, G>> extends ObjectTester<G> {

	@Test
	public void newInstance() {
		for (int i = 0; i < 1000; ++i) {
			final G gene = factory().newInstance();
			Assert.assertNotNull(gene);
			Assert.assertNotNull(gene.getAllele());
			Assert.assertTrue(gene.isValid());

			final G gene2 = gene.newInstance();
			Assert.assertNotNull(gene2);
			Assert.assertNotNull(gene2.getAllele());
			Assert.assertTrue(gene2.isValid());
		}
	}

	@Test
	public void equalsAllele() {
		final MSeq<G> same = newEqualObjects(5);

		final G that = same.get(0);
		for (int i = 1; i < same.length(); ++i) {
			final G other = same.get(i);

			Assert.assertEquals(other.getAllele(), other.getAllele());
			Assert.assertEquals(other.getAllele(), that.getAllele());
			Assert.assertEquals(that.getAllele(), other.getAllele());
		}
	}

	@Test
	public void alleleNotNull() {
		for (int i = 0; i < 1000; ++i) {
			Assert.assertNotNull(factory().newInstance().getAllele());
		}
	}

	@Test
	public void notEqualsAlleleNull() {
		final Object that = factory().newInstance().getAllele();
		Assert.assertFalse(that.equals(null));
	}

	@Test
	public void notEqualsAllele() {
		for (int i = 0; i < 1000; ++i) {
			final Object that = factory().newInstance().getAllele();
			final Object other = factory().newInstance().getAllele();

			if (that.equals(other)) {
				Assert.assertTrue(other.equals(that));
				Assert.assertEquals(that.hashCode(), other.hashCode());
			} else {
				Assert.assertFalse(other.equals(that));
				Assert.assertFalse(that.equals(other));
			}
		}
	}

}
