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

import static org.jenetics.stat.StatisticsAssert.assertDistribution;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Random;

import javolution.context.LocalContext;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.stat.Histogram;
import org.jenetics.stat.UniformDistribution;
import org.jenetics.util.CharSeq;
import org.jenetics.util.Factory;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class CharacterGeneTest extends GeneTester<CharacterGene> {

	private final Factory<CharacterGene> _factory = CharacterGene.valueOf();
	@Override protected Factory<CharacterGene> getFactory() {
		return _factory;
	}

	@Test(invocationCount = 20, successPercentage = 95)
	public void newInstanceDistribution() {
		LocalContext.enter();
		try {
			RandomRegistry.setRandom(new Random(12345));

			final CharSeq characters = new CharSeq("0123456789");

			final Factory<CharacterGene> factory = CharacterGene.valueOf(characters);

			final Histogram<Long> histogram = Histogram.valueOf(0L, 10L, 10);

			final int samples = 100000;
			for (int i = 0; i < samples; ++i) {
				final CharacterGene g1 = factory.newInstance();
				final CharacterGene g2 = factory.newInstance();
				Assert.assertNotSame(g1, g2);

				histogram.accumulate(Long.valueOf(g1.getAllele().toString()));
				histogram.accumulate(Long.valueOf(g2.getAllele().toString()));
			}

			assertDistribution(histogram, new UniformDistribution<>(0L, 10L));
		} finally {
			LocalContext.exit();
		}
	}

    @Test
    public void testCharacterGene() {
        CharacterGene gene = CharacterGene.valueOf();
        assertTrue(gene.isValidCharacter(gene.getAllele()));
    }

    @Test
    public void testCharacterGeneCharacter() {
        CharacterGene gene = CharacterGene.valueOf('4');

        assertEquals(new Character('4'), gene.getAllele());
    }

    @Test
    public void testGetCharacter() {
        CharacterGene gene = CharacterGene.valueOf('6');

        assertEquals(new Character('6'), gene.getAllele());
    }

    @Test
    public void testCompareTo() {
        CharacterGene g1 = CharacterGene.valueOf('1');
        CharacterGene g2 = CharacterGene.valueOf('2');
        CharacterGene g3 = CharacterGene.valueOf('3');

        assertTrue(g1.compareTo(g2) < 0);
        assertTrue(g2.compareTo(g3) < 0);
        assertTrue(g3.compareTo(g2) > 0);
        assertTrue(g2.compareTo(g2) == 0);
    }

    @Test
    public void testIsValidCharacter() {
        for (Character c : CharacterGene.DEFAULT_CHARACTERS) {
            assertTrue(CharacterGene.valueOf(c).isValidCharacter(c));
        }
    }

    @Test
    public void testGetValidCharacters() {
        CharSeq cset = CharacterGene.DEFAULT_CHARACTERS;
        assertNotNull(cset);
        assertFalse(cset.isEmpty());
    }

}




