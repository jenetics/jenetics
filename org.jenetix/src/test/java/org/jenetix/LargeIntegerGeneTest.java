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
package org.jenetix;

import static org.testng.Assert.assertEquals;

import org.jscience.mathematics.number.LargeInteger;
import org.testng.annotations.Test;

import org.jenetics.NumberGeneTester;
import org.jenetics.util.Factory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-05-23 $</em>
 */
public class LargeIntegerGeneTest
	extends NumberGeneTester<LargeInteger, LargeIntegerGene>
{

	private final Factory<LargeIntegerGene> _factory = LargeIntegerGene.Builder.build(
		LargeInteger.ZERO,
		LargeInteger.valueOf("101010101010101010101010101010101010101010101010")
	);

	@Override protected Factory<LargeIntegerGene> getFactory() {
		return _factory;
	}

	@Test
	public void mean() {
		final LargeInteger min = LargeInteger.valueOf(
			"-1010101010101010101010101010101010101010101010101010101010101010"
		);
		final LargeInteger max = LargeInteger.valueOf(
			"1010101010101010101010101010101010101010101010101010101010101010"
		);
		final LargeIntegerGene template = LargeIntegerGene.Builder.build(min, max);

		for (int i = 1; i < 500; ++i) {
			final LargeIntegerGene a = template.newInstance(i - 50);
			final LargeIntegerGene b = template.newInstance((i - 100)*3);
			final LargeIntegerGene c = a.mean(b);

			assertEquals(a.getMin(), min);
			assertEquals(a.getMax(), max);
			assertEquals(b.getMin(), min);
			assertEquals(b.getMax(), max);
			assertEquals(c.getMin(), min);
			assertEquals(c.getMax(), max);

			assertEquals(c.getAllele().longValue(), ((i - 50) + ((i - 100)*3))/2);
		}
	}

}
