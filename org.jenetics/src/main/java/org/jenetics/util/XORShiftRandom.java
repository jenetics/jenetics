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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

/**
 * <p>
 * The given implementation of the {@link Random} class is not thread safe.
 * </p>
 * <q align="justified" cite="http://www.nr.com/"><em>
 * This generator was discovered and characterized by George Marsaglia
 * [<a href="http://www.jstatsoft.org/v08/i14/paper">Xorshift RNGs</a>]. In just
 * three XORs and three shifts (generally fast operations) it produces a full
 * period of 2<sup>64</sup> - 1 on 64 bits. (The missing value is zero, which
 * perpetuates itself and must be avoided.) High and low bits pass Diehard.
 * </em></q>
 * <p align="left">
 * <strong>Numerical Recipes 3rd Edition: The Art of Scientific Computing</strong>
 * <br/>
 * <em>Chapter 7. Random Numbers; Page 345</em>
 * <br/>
 * <small>Cambridge University Press New York, NY, USA ©2007</small>
 * <br/>
 * ISBN:0521880688 9780521880688
 * <br/>
 * [<a href="http://www.nr.com/">http://www.nr.com/</a>].
 * <p/>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.1
 * @version 1.1 &mdash; <em>$Date: 2012-11-19 $</em>
 */
public class XORShiftRandom extends Random implements Cloneable {
	private static final long serialVersionUID = 1L;

	/**
	 * This field can be used to initial the {@link RandomRegistry} with a fast
	 * and thread safe random engine of this type; each thread gets a <i>local</i>
	 * copy of the {@code XORShiftRandom} engine.
	 * 
	 * [code]
	 * RandomRegistry.setRandom(XORShiftRandom.INSTANCE);
	 * [/code]
	 */
	public static final ThreadLocal<XORShiftRandom>
	INSTANCE = new ThreadLocal<XORShiftRandom>() {
		@Override protected XORShiftRandom initialValue() {
			return new XORShiftRandom();
		}
	};

	private long _x;

	public XORShiftRandom() {
		this(System.nanoTime());
	}

	public XORShiftRandom(final long seed) {
		_x = seed == 0 ? 0xdeadbeef : seed;
	}

	@Override
	public long nextLong() {
//		The other suggested shift values are:
//			21, 35, 4
//			20, 41, 5
//			17, 31, 8
//			11, 29, 14
//			14, 29, 11
//			30, 35, 13
//			21, 37, 4
//			21, 43, 4
//			23, 41, 18

		_x ^= (_x << 21);
		_x ^= (_x >>> 35);
		_x ^= (_x << 4);
		return _x;
	}

	@Override
	protected int next(final int bits) {
		return (int)(nextLong() >>> (64 - bits));
	}
	
	@Override
	public String toString() {
		return String.format("%s[%d]", getClass().getName(), _x);
	}
	
	@Override
	public XORShiftRandom clone() {
		try {
			return (XORShiftRandom)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError(String.format(
				"Cloning of %s not supported.", getClass()
			));
		}
	}
	
	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private void writeObject(final ObjectOutputStream out)
		throws IOException
	{
		out.defaultWriteObject();

		out.writeLong(_x);
	}

	private void readObject(final ObjectInputStream in)
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		_x = in.readLong();
	}
	
}

