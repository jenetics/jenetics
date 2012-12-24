/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.BitSet;
import java.util.Random;

import javolution.context.LocalContext;

import org.jenetics.util.Factory;
import org.jenetics.util.IO;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.RandomRegistry;
import org.jscience.mathematics.number.LargeInteger;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class BitChromosomeTest extends ChromosomeTester<BitGene> {

	private final Factory<Chromosome<BitGene>>
	_factory = new BitChromosome(500, 0.3);
	@Override protected Factory<Chromosome<BitGene>> getFactory() {
		return _factory;
	}

	@Test
	public void invert() {
		BitChromosome c1 = new BitChromosome(100, 0.3);
		BitChromosome c2 = c1.copy();
		Assert.assertNotSame(c2, c1);
		Assert.assertEquals(c2, c1);

		BitChromosome c3 = c1.invert();
		for (int i = 0; i < c1.length(); ++i) {
			Assert.assertTrue(c1.getGene(i).getBit() != c3.getGene(i).getBit());
		}

		BitChromosome c4 = c3.invert();
		Assert.assertEquals(c4, c1);
	}

	@Test
	public void numValue() {
		BitChromosome c1 = new BitChromosome(10);

		int value = c1.intValue();
		assertEquals((short)value, c1.shortValue());
		assertEquals(value, c1.longValue());
		assertEquals((float)value, c1.floatValue());
		assertEquals((double)value, c1.doubleValue());
	}

	@Test
	public void intProbability() {
		BitChromosome c = new BitChromosome(10, 0);
		for (BitGene g : c) {
			assertFalse(g.getBit());
		}

		c = new BitChromosome(10, 1);
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

		BitChromosome c = new BitChromosome(bits);
		for (int i = 0; i < bits.length(); ++i) {
			assertEquals(c.getGene(i).getBit(), i % 2 == 0);
		}
	}

	@Test
	public void toBigInteger() {
		BitChromosome c = new BitChromosome(LargeInteger.valueOf(234902));

		LargeInteger i = c.toLargeInteger();
		assertEquals(i, LargeInteger.valueOf(234902));
		assertEquals(i.intValue(), 234902);
		assertEquals(i.longValue(), c.longValue());
		assertEquals(i.intValue(), c.intValue());

		byte[] data = new byte[3];
		c.toByteArray(data);
		BitChromosome c2 = new BitChromosome(data);
		LargeInteger i2 = c2.toLargeInteger();
		assertEquals(i2, LargeInteger.valueOf(234902));
	}

	@Test
	public void toBitSet() {
		BitChromosome c1 = new BitChromosome(34);
		BitChromosome c2 = new BitChromosome(34, c1.toBitSet());

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
		BitChromosome c = new BitChromosome(LargeInteger.valueOf(234902));
		String value = c.toCanonicalString();
		BitChromosome sc = new BitChromosome(value);

		Assert.assertEquals(sc, c);
	}

	@Test
	public void toStringToByteArray() {
		byte[] data = new byte[10];
		for (int i = 0; i < data.length; ++i) {
			data[i] = (byte)(Math.random()*256);
		}

		final String dataString = BitChromosome.toString(data);
		Reporter.log(dataString);

		final byte[] sdata = BitChromosome.toByteArray(dataString);
		Assert.assertEquals(sdata, data);
	}

	@Test
	public void objectSerializationCompatibility() throws IOException {
		final Random random = new LCG64ShiftRandom.ThreadSafe(0);
		LocalContext.enter();
		try {
			RandomRegistry.setRandom(random);
			final BitChromosome chromosome = new BitChromosome(5000, 0.5);

			final String resource = "/org/jenetics/BitChromosome.object";
			try (InputStream in = getClass().getResourceAsStream(resource)) {
				final Object object = IO.object.read(in);

				Assert.assertEquals(object, chromosome);
			}
		} finally {
			LocalContext.exit();
		}
	}

	@Test
	public void xmlSerializationCompatibility() throws IOException {
		final Random random = new LCG64ShiftRandom.ThreadSafe(0);
		LocalContext.enter();
		try {
			RandomRegistry.setRandom(random);
			final BitChromosome chromosome = new BitChromosome(5000, 0.5);

			final String resource = "/org/jenetics/BitChromosome.xml";
			try (InputStream in = getClass().getResourceAsStream(resource)) {
				final Object object = IO.xml.read(in);

				Assert.assertEquals(object, chromosome);
			}
		} finally {
			LocalContext.exit();
		}
	}


}







