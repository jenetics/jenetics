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

import org.jenetics.util.Array;
import org.jenetics.util.ObjectTester;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public abstract class GeneTester<G extends Gene<?, G>> extends ObjectTester<G> {

	@Test
	public void equalsAllele() {
		final Array<G> same = newSameObjects(5);

		final G that = same.get(0);
		for (int i = 1; i < same.length(); ++i) {
			final G other = same.get(i);

			Assert.assertEquals(other.getAllele(), other.getAllele());
			Assert.assertEquals(other.getAllele(), that.getAllele());
			Assert.assertEquals(that.getAllele(), other.getAllele());
			Assert.assertFalse(other.getAllele().equals(null));
		}
	}

	@Test
	public void notEqualsAllele() {
		for (int i = 0; i < 10; ++i) {
			final G that = getFactory().newInstance();
			final G other = getFactory().newInstance();

			if (that.equals(other)) {
				Assert.assertTrue(other.getAllele().equals(that.getAllele()));
				Assert.assertEquals(that.getAllele().hashCode(), other.getAllele().hashCode());
			} else {
				Assert.assertFalse(other.getAllele().equals(that.getAllele()));
			}
		}
	}

	@Test
	public void copy() {
		for (int i = 0; i < 10; ++i) {
			final G gene = getFactory().newInstance();
			final Object copy = gene.copy();

			Assert.assertEquals(copy, gene);
			Assert.assertEquals(copy.hashCode(), gene.hashCode());
		}
	}

}



