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
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import org.jenetics.util.Factory;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class BitGeneTest extends GeneTester<BitGene> {

	@Override
	protected Factory<BitGene> factory() {
		return () -> BitGene.of(RandomRegistry.getRandom().nextBoolean());
	}

	@Test
	public void testGetValue() {
		assertEquals(BitGene.FALSE.getBit(), false);
		assertEquals(BitGene.ZERO.getBit(), false);
		assertEquals(BitGene.TRUE.getBit(), true);
		assertEquals(BitGene.ONE.getBit(), true);
	}

	@Test
	public void testCompareTo() {
		assertEquals(BitGene.ZERO.compareTo(BitGene.FALSE), 0);
		assertTrue(BitGene.FALSE.compareTo(BitGene.ONE) < 0);
		assertTrue(BitGene.TRUE.compareTo(BitGene.ZERO) > 0);
	}

}
