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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static io.jenetics.stat.StatisticsAssert.assertUniformDistribution;
import static io.jenetics.util.RandomRegistry.using;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import java.util.Random;

import org.testng.annotations.Test;

import io.jenetics.testfixtures.stat.Histogram;
import io.jenetics.util.CharSeq;
import io.jenetics.util.Factory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class CharacterGeneTest extends GeneTester<CharacterGene> {

	@Override
	protected Factory<CharacterGene> factory() {
		return CharacterGene::of;
	}

	@Test
	public void equalsVerifier() {
		EqualsVerifier.forClass(CharacterGene.class)
			.suppress(Warning.NULL_FIELDS)
			.verify();
	}

	@Test(invocationCount = 20, successPercentage = 95)
	public void newInstanceDistribution() {
		using(new Random(12345), r -> {
			final CharSeq characters = new CharSeq("0123456789");

			final Factory<CharacterGene> factory = CharacterGene.of(characters);

			final Histogram<Long> histogram = Histogram.ofLong(0L, 10L, 10);

			final int samples = 100000;
			for (int i = 0; i < samples; ++i) {
				final CharacterGene g1 = factory.newInstance();
				final CharacterGene g2 = factory.newInstance();
				assertThat(g1).isNotSameAs(g2);

				histogram.accept(Long.valueOf(g1.allele().toString()));
				histogram.accept(Long.valueOf(g2.allele().toString()));
			}

			assertUniformDistribution(histogram);
		});
	}

	@Test
	public void testCharacterGene() {
		CharacterGene gene = CharacterGene.of();
		assertTrue(gene.isValidCharacter(gene.allele()));
	}

	@Test
	public void testCharacterGeneCharacter() {
		CharacterGene gene = CharacterGene.of('4');

		assertEquals(Character.valueOf('4'), gene.allele());
	}

	@Test
	public void testGetCharacter() {
		CharacterGene gene = CharacterGene.of('6');

		assertEquals(Character.valueOf('6'), gene.allele());
	}

	@Test
	public void testCompareTo() {
		CharacterGene g1 = CharacterGene.of('1');
		CharacterGene g2 = CharacterGene.of('2');
		CharacterGene g3 = CharacterGene.of('3');

		assertTrue(g1.compareTo(g2) < 0);
		assertTrue(g2.compareTo(g3) < 0);
		assertTrue(g3.compareTo(g2) > 0);
		assertEquals(0, g2.compareTo(g2));
	}

	@Test
	public void testIsValidCharacter() {
		for (Character c : CharacterGene.DEFAULT_CHARACTERS) {
			assertTrue(CharacterGene.of(c).isValidCharacter(c));
		}
	}

	@Test
	public void testGetValidCharacters() {
		CharSeq cset = CharacterGene.DEFAULT_CHARACTERS;
		assertNotNull(cset);
		assertFalse(cset.isEmpty());
	}

}
