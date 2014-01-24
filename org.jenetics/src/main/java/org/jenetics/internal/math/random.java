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
package org.jenetics.internal.math;

import static org.jenetics.util.math.random.nextDouble;
import static org.jenetics.util.math.random.nextInt;
import static org.jenetics.util.math.random.nextLong;

import java.util.Random;

import org.jscience.mathematics.number.Float64;
import org.jscience.mathematics.number.Integer64;

import org.jenetics.util.Factory;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.StaticObject;

/**
 * Some random helper functions.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 1.4 &mdash; <em>$Date: 2014-01-24 $</em>
 */
public final class random extends StaticObject {
	private random() {}

	public static char nextPrintableChar(final Random random) {
		final int low = 33;
		final int high = 127;
		return (char)(random.nextInt(high - low) + low);
	}

	/*
	 * Conversion methods used by the 'Random' engine from the JDK.
	 */

	public static float toFloat(final int a) {
		return (a >>> 8)/((float)(1 << 24));
	}

	public static float toFloat(final long a) {
		return (int)(a >>> 40)/((float)(1 << 24));
	}

	public static double toDouble(final long a) {
		return (((a >>> 38) << 27) + (((int)a) >>> 5))/(double)(1L << 53);
	}

	public static double toDouble(final int a, final int b) {
		return (((long)(a >>> 6) << 27) + (b >>> 5))/(double)(1L << 53);
	}

	/*
	 * Conversion methods used by the Apache Commons BitStreamGenerator.
	 */

	public static float toFloat2(final int a) {
		return (a >>> 9)*0x1.0p-23f;
	}

	public static float toFloat2(final long a) {
		return (int)(a >>> 41)*0x1.0p-23f;
	}

	public static double toDouble2(final long a) {
		return (a & 0xFFFFFFFFFFFFFL)*0x1.0p-52d;
	}

	public static double toDouble2(final int a, final int b) {
		return (((long)(a >>> 6) << 26) | (b >>> 6))*0x1.0p-52d;
	}

	public static Factory<Byte> ByteFactory(
		final Random random,
		final byte min, final byte max
	) {
		return new Factory<Byte>() {
			@Override public Byte newInstance() {
				return (byte)nextInt(random, min, max);
			}
		};
	}

	public static Factory<Byte> ByteFactory(final byte min, final byte max) {
		return ByteFactory(RandomRegistry.getRandom(), min, max);
	}

	public static Factory<Character> CharacterFactory(final Random random) {
		return new Factory<Character>() {
			@Override public Character newInstance() {
				return nextPrintableChar(random);
			}
		};
	}

	public static Factory<Character> CharacterFactory() {
		return CharacterFactory(RandomRegistry.getRandom());
	}


	public static Factory<Long> LongFactory(
		final Random random,
		final long min, final long max
	) {
		return new Factory<Long>() {
			@Override public Long newInstance() {
				return nextLong(random, min, max);
			}
		};
	}

	public static Factory<Long> LongFactory(final long min, final long max) {
		return LongFactory(RandomRegistry.getRandom(), min, max);
	}


	public static Factory<Float64> Float64Factory(
		final Random random,
		final double min, final double max
	) {
		return new Factory<Float64>() {
			@Override public Float64 newInstance() {
				return Float64.valueOf(nextDouble(random, min, max));
			}
		};
	}

	public static Factory<Float64> Float64Factory(final double min, final double max) {
		return Float64Factory(RandomRegistry.getRandom(), min, max);
	}

	public static Factory<Double> DoubleFactory(
		final Random random,
		final double min, final double max
	) {
		return new Factory<Double>() {
			@Override public Double newInstance() {
				return nextDouble(random, min, max);
			}
		};
	}

	public static Factory<Double> DoubleFactory(final double min, final double max) {
		return DoubleFactory(RandomRegistry.getRandom(), min, max);
	}

	public static Factory<Integer64> Integer64Factory(
		final Random random,
		final long min, final long max
	) {
		return new Factory<Integer64>() {
			@Override public Integer64 newInstance() {
				return Integer64.valueOf(nextLong(random, min, max));
			}
		};
	}

	public static Factory<Integer64> Integer64Factory(final long min, final long max) {
		return Integer64Factory(RandomRegistry.getRandom(), min, max);
	}

}


