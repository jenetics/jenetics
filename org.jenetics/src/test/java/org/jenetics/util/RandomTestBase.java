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
package org.jenetics.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2012-12-05 $</em>
 */
public abstract class RandomTestBase {

	@Test(dataProvider = "PRNG22")
	public void sameBooleanSequence(final Random rand1, final Random rand2) {
		for (int i = 0; i < 1234; ++i) {
			Assert.assertEquals(rand1.nextBoolean(), rand2.nextBoolean());
		}
	}

	@Test(dataProvider = "PRNG22")
	public void sameByteLongSequence(final Random rand1, final Random rand2) {
		final int size = 123*8;
		final byte[] bytes1 = new byte[size];

		for (int i = 0; i < 1234; ++i) {
			rand1.nextBytes(bytes1);
		}

		final int loops = (size/8)*1234;
		for (int i = 0; i < loops; ++i) {
			rand2.nextLong();
		}

		for (int i = 0; i < 1234; ++i) {
			Assert.assertEquals(rand1.nextLong(), rand2.nextLong());
		}
	}

	@Test(dataProvider = "PRNG22")
	public void sameByteLongValueSequence(final Random rand1, final Random rand2) {
		final byte[] bytes = new byte[8];
		for (int i = 0; i < 1234; ++i) {
			rand1.nextBytes(bytes);
			arrays.reverse(bytes);

			Assert.assertEquals(bit.toLong(bytes), rand2.nextLong());
		}
	}

	@Test(dataProvider = "PRNG22")
	public void sameByteSequence(final Random rand1, final Random rand2) {
		final int size = 3413;
		final byte[] bytes1 = new byte[size];
		final byte[] bytes2 = new byte[size];

		for (int i = 0; i < 1234; ++i) {
			rand1.nextBytes(bytes1);
			rand2.nextBytes(bytes2);

			Assert.assertEquals(bytes1, bytes2);
		}
	}

	@Test(dataProvider = "PRNG22")
	public void sameIntSequence(final Random rand1, final Random rand2) {
		for (int i = 0; i < 1234; ++i) {
			Assert.assertEquals(rand1.nextInt(), rand2.nextInt());
		}
	}

	@Test(dataProvider = "PRNG22")
	public void sameIntRangeSequence(final Random rand1, final Random rand2) {
		final int range = 8282828;
		for (int i = 0; i < 1234; ++i) {
			Assert.assertEquals(rand1.nextInt(range), rand2.nextInt(range));
		}
	}

	@Test(dataProvider = "PRNG22")
	public void sameLongSequence(final Random rand1, final Random rand2) {
		for (int i = 0; i < 1234; ++i) {
			Assert.assertEquals(rand1.nextLong(), rand2.nextLong());
		}
	}

	@Test(dataProvider = "PRNG22")
	public void sameFloatSequence(final Random rand1, final Random rand2) {
		for (int i = 0; i < 1234; ++i) {
			Assert.assertEquals(rand1.nextFloat(), rand2.nextFloat());
		}
	}

	@Test(dataProvider = "PRNG22")
	public void sameDoubleSequence(final Random rand1, final Random rand2) {
		for (int i = 0; i < 1234; ++i) {
			Assert.assertEquals(rand1.nextDouble(), rand2.nextDouble());
		}
	}

	@Test(dataProvider = "PRNG22")
	public void sameGaussianSequence(final Random rand1, final Random rand2) {
		for (int i = 0; i < 1234; ++i) {
			Assert.assertEquals(rand1.nextGaussian(), rand2.nextGaussian());
		}
	}

	@Test(dataProvider = "PRNG22")
	public void equals(final Random rand1, final Random rand2) {
		Assert.assertNotSame(rand2, rand1);
		Assert.assertEquals(rand2, rand1);

		for (int i = 0; i < 666; ++i) {
			rand1.nextLong();
		}
		Assert.assertNotEquals(rand1, rand2);

		for (int i = 0; i < 666; ++i) {
			rand2.nextLong();
		}
		Assert.assertEquals(rand2, rand1);
	}

	@Test(dataProvider = "PRNG22")
	public void hashCode(final Random rand1, final Random rand2) {
		Assert.assertNotSame(rand2, rand1);
		Assert.assertEquals(rand2, rand1);
		Assert.assertEquals(rand2.hashCode(), rand1.hashCode());

		for (int i = 0; i < 666; ++i) {
			rand1.nextLong();
		}
		Assert.assertNotEquals(rand1, rand2);
		Assert.assertNotEquals(rand2.hashCode(), rand1.hashCode());

		for (int i = 0; i < 666; ++i) {
			rand2.nextLong();
		}
		Assert.assertEquals(rand2, rand1);
		Assert.assertEquals(rand2.hashCode(), rand1.hashCode());
	}

	@DataProvider(name = "PRNG22")
	protected abstract Object[][] getPRNG22();

	@Test(dataProvider = "PRNG3")
	public void serialize(final Random rand1)
		throws IOException, ClassNotFoundException
	{
		for (int i = 0; i < 12734; ++i) {
			rand1.nextLong();
		}

		final Random rand2 = RandomTestBase.toSerialized(rand1);
		Assert.assertNotSame(rand2, rand1);
		Assert.assertTrue(
			rand1.getClass().isAssignableFrom(rand2.getClass()),
			String.format("Must be of type %s.", rand1.getClass())
		);

		for (int i = 0; i < 2489248; ++i) {
			Assert.assertEquals(rand2.nextLong(), rand1.nextLong());
		}

	}

	@DataProvider(name = "PRNG3")
	protected abstract Object[][] getPRNG3();


	public static Random toSerialized(final Random random)
		throws IOException, ClassNotFoundException
	{
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final ObjectOutputStream out = new ObjectOutputStream(bytes);
		out.writeObject(random);
		out.flush();

		final ObjectInputStream in = new ObjectInputStream(
			new ByteArrayInputStream(bytes.toByteArray())
		);

		return (Random)in.readObject();
	}


}
