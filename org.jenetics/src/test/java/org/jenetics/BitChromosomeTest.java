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
import static org.testng.Assert.assertTrue;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.Random;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.internal.util.bit;

import org.jenetics.util.Factory;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class BitChromosomeTest extends ChromosomeTester<BitGene> {

	@Override
	protected Factory<Chromosome<BitGene>> factory() {
		return () -> BitChromosome.of(500, 0.3);
	}

	@Test(invocationCount = 20, successPercentage = 90)
	public void newInstance() {
		final int size = 50_000;
		final BitChromosome base = BitChromosome.of(size, 0.5);

		for (int i = 0; i < 100; ++i) {
			final BitChromosome other = base.newInstance();
			Assert.assertNotEquals(other, base);

			Assert.assertEquals(other.bitCount(), size/2.0, size/100.0);
		}
	}

	@Test
	public void chromosomeProbability() {
		final byte[] data = new byte[1234];
		RandomRegistry.getRandom().nextBytes(data);

		final BitChromosome c = new BitChromosome(data);
		Assert.assertEquals(
			c.getOneProbability(),
			(double)bit.count(data)/(double)(data.length*8)
		);
	}

	@Test
	public void seqTypes() {
		final BitChromosome c = BitChromosome.of(100, 0.3);

		Assert.assertEquals(c.toSeq().getClass(), BitGeneISeq.class);
		Assert.assertEquals(c.toSeq().copy().getClass(), BitGeneMSeq.class);
		Assert.assertEquals(c.toSeq().copy().toISeq().getClass(), BitGeneISeq.class);
	}

	@Test
	public void invert() {
		final BitChromosome c1 = BitChromosome.of(100, 0.3);
		final BitChromosome c3 = c1.invert();

		for (int i = 0; i < c1.length(); ++i) {
			Assert.assertTrue(c1.getGene(i).getBit() != c3.getGene(i).getBit());
		}

		BitChromosome c4 = c3.invert();
		Assert.assertEquals(c4, c1);
	}

	@Test
	public void numValue() {
		BitChromosome c1 = BitChromosome.of(10);

		int value = c1.intValue();
		assertEquals((short)value, c1.shortValue());
		assertEquals(value, c1.longValue());
		assertEquals((float)value, c1.floatValue());
		assertEquals((double)value, c1.doubleValue());
	}

	@Test
	public void intProbability() {
		BitChromosome c = BitChromosome.of(10, 0);
		for (BitGene g : c) {
			assertFalse(g.getBit());
		}

		c = BitChromosome.of(10, 1);
		for (BitGene g : c) {
			assertTrue(g.getBit());
		}
	}

	@Test
	public void bitChromosomeBitSet() {
		BitSet bits = new BitSet(10);
		for (int i = 0; i < 10; ++i) {
			bits.set(i, i % 2 == 0);
		}

		BitChromosome c = BitChromosome.of(bits);
		for (int i = 0; i < bits.length(); ++i) {
			assertEquals(c.getGene(i).getBit(), i % 2 == 0);
		}
	}

	@Test
	public void ones() {
		final BitChromosome c = BitChromosome.of(1000, 0.5);

		final int ones = (int)c.ones().count();
		assertEquals(ones, c.bitCount());
		assertTrue(c.ones().allMatch(c::get));
	}

	@Test
	public void zeros() {
		final BitChromosome c = BitChromosome.of(1000, 0.5);

		final int zeros = (int)c.zeros().count();
		assertEquals(zeros, c.length() - c.bitCount());
		assertTrue(c.zeros().allMatch(i -> !c.get(i)));
	}

	@Test(invocationCount = 5)
	public void toBigInteger() {
		final LCG64ShiftRandom random = new LCG64ShiftRandom();
		final BigInteger value = new BigInteger(1056, random);
		final BitChromosome chromosome = BitChromosome.of(value);

		assertEquals(chromosome.toBigInteger(), value);
	}

	@Test
	public void toBitSet() {
		BitChromosome c1 = BitChromosome.of(34);
		BitChromosome c2 = BitChromosome.of(c1.toBitSet(), 34);

		for (int i = 0; i < c1.length(); ++i) {
			assertEquals(c1.getGene(i).getBit(), c2.getGene(i).getBit());
		}
	}

	@Test
	public void toByteArray() {
		byte[] data = new byte[16];
		for (int i = 0; i < data.length; ++i) {
			data[i] = (byte)(Math.random()*256);
		}
		BitChromosome bc = new BitChromosome(data);

		Assert.assertEquals(bc.toByteArray(), data);

	}

	@Test
	public void toCanonicalString() {
		BitChromosome c = BitChromosome.of(BigInteger.valueOf(234902));
		String value = c.toCanonicalString();
		BitChromosome sc = BitChromosome.of(value);

		Assert.assertEquals(sc, c);
	}

	@Test
	public void toStringToByteArray() {
		byte[] data = new byte[10];
		for (int i = 0; i < data.length; ++i) {
			data[i] = (byte)(Math.random()*256);
		}

		final String dataString = bit.toByteString(data);
		Reporter.log(dataString);

		final byte[] sdata = bit.fromByteString(dataString);
		Assert.assertEquals(sdata, data);
	}

	@Test
	public void fromBitSet() {
		final Random random = new Random();
		final BitSet bits = new BitSet(2343);
		for (int i = 0; i < bits.size(); ++i) {
			bits.set(i, random.nextBoolean());
		}

		final BitChromosome c = BitChromosome.of(bits);
		Assert.assertEquals(c.toByteArray(), bits.toByteArray());
	}

	@Test
	public void fromByteArrayBitSet() {
		final Random random = new Random();
		final byte[] bytes = new byte[234];
		random.nextBytes(bytes);

		final BitSet bits = BitSet.valueOf(bytes);
		final BitChromosome c = BitChromosome.of(bits);
		Assert.assertEquals(c.toByteArray(), bytes);
		Assert.assertEquals(bits.toByteArray(), bytes);
	}

	@Test(dataProvider = "bitCountProbability")
	public void bitCount(final Double p) {
		final int size = 1_000;
		final BitChromosome base = BitChromosome.of(size, p);

		for (int i = 0; i < 1_000; ++i) {
			final BitChromosome other = base.newInstance();

			int bitCount = 0;
			for (BitGene gene : other) {
				if (gene.booleanValue()) {
					++bitCount;
				}
			}

			Assert.assertEquals(other.bitCount(), bitCount);
		}
	}

	@Test(dataProvider = "bitCountProbability")
	public void bitSetBitCount(final Double p) {
		final int size = 1_000;
		final BitChromosome base = BitChromosome.of(size, p);

		for (int i = 0; i < 1_000; ++i) {
			final BitChromosome other = base.newInstance();
			Assert.assertEquals(other.toBitSet().cardinality(), other.bitCount());
		}
	}

	@DataProvider(name = "bitCountProbability")
	public Object[][] getBitCountProbability() {
		return new Object[][] {
			{0.01}, {0.1}, {0.125}, {0.333}, {0.5}, {0.75}, {0.85}, {0.999}
		};
	}

}
