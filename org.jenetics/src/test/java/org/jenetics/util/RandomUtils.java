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
package org.jenetics.util;

import static java.lang.Math.abs;
import static org.jenetics.util.lambda.factory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

import org.jenetics.util.math.random;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-04-04 $</em>
 */
public class RandomUtils {

	private static final Object This = RandomUtils.class;

	private RandomUtils() {
	}

	private static Random random() {
		return RandomRegistry.getRandom();
	}

	public static String nextString(final int length) {
		final StringBuilder chars = new StringBuilder(length);
		for (int i = 0; i < length; ++i) {
			chars.append(nextCharacter());
		}

		return chars.toString();
	}

	public static String nextString() {
		return nextString(random().nextInt(20) + 5);
	}

	public static byte nextByte() {
		return (byte)random.nextInt(random(), Byte.MIN_VALUE, Byte.MAX_VALUE);
	}

	public static char nextCharacter() {
		char c = '\0';
		do {
			c = (char)random.nextInt(random(), Character.MIN_VALUE, Character.MAX_VALUE);
		} while (!Character.isLetterOrDigit(c));

		return c;
	}

	public static short nextShort() {
		return (short)random.nextInt(random(), Short.MIN_VALUE, Short.MAX_VALUE);
	}

	public static BigInteger nextBigInteger() {
		return new BigInteger(100, random());
	}

	public static BigDecimal nextBigDecimal() {
		final StringBuilder out = new StringBuilder();
		for (int i = 0; i < 10; ++i) {
			out.append(Long.toString(abs(random().nextLong())));
		}
		out.append(".");
		for (int i = 0; i < 20; ++i) {
			out.append(Long.toString(abs(random().nextLong())));
		}

		return new BigDecimal(out.toString());
	}

	/*
	 * Factory instances.
	 */

	public static final Factory<Boolean> BooleanFactory = factory(random(), "nextBoolean");

	public static final Factory<Byte> ByteFactory = Factory("nextByte");

	public static final Factory<Character> CharacterFactory = Factory("nextCharacter");

	public static final Factory<Short> ShortFactory = Factory("nextShort");

	public static final Factory<Integer> IntegerFactory = factory(random(), "nextInt");

	public static final Factory<Long> LongFactory = factory(random(), "nextLong");

	public static final Factory<Float> FloatFactory = factory(random(), "nextFloat");

	public static final Factory<Double> DoubleFactory = factory(random(), "nextDouble");

	public static final Factory<BigInteger> BigIntegerFactory = Factory("nextBigInteger");

	public static final Factory<BigDecimal> BigDecimalFactory = Factory("nextBigDecimal");

	public static final Factory<String> StringFactory = Factory("nextString");

	private static <T> Factory<T> Factory(final String name) {
		return lambda.factory(This, name);
	}

	public static <T> ISeq<T> ISeq(final int size, final Factory<T> factory) {
		return new Array<T>(size).fill(factory).toISeq();
	}
}
