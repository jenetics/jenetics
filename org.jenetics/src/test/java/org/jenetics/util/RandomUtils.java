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

import static org.jenetics.util.math.random;

import java.util.Random;

import org.jscience.mathematics.number.Float64;
import org.jscience.mathematics.number.Integer64;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class RandomUtils {

	private static final Object This = RandomUtils.class;

	private RandomUtils() {
	}

	private static Random random() {
		return RandomRegistry.getRandom();
	}

	public static String nextString(final int length) {
		final Random random = RandomRegistry.getRandom();

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
		final int surrogateStart = 0xD800;
		return (char)(random().nextInt(surrogateStart - 1) + 1);
	}

	public static short nextShort() {
		return (short)random.nextInt(random(), Short.MIN_VALUE, Short.MAX_VALUE);
	}

	public static Integer64 nextInteger64() {
		return Integer64.valueOf(random().nextLong());
	}

	public static Float64 nextFloat64() {
		return Float64.valueOf(random().nextDouble());
	}

	/*
	 * Factory instances.
	 */

	public static final Factory<Boolean> BooleanFactory = lambda.factory(
		random(), "nextBoolean"
	);

	public static final Factory<Byte> ByteFactory = lambda.factory(
		This, "nextByte"
	);

	public static final Factory<Character> CharacterFactory = lambda.factory(
		This, "nextCharacter"
	);

	public static final Factory<Short> ShortFactory = lambda.factory(
		This, "nextShort"
	);

	public static final Factory<Integer> IntegerFactory = lambda.factory(
		random(), "nextInt"
	);

	public static final Factory<Long> LongFactory = lambda.factory(
		random(), "nextLong"
	);

	public static final Factory<Float> FloatFactory = lambda.factory(
		random(), "nextFloat"
	);

	public static final Factory<Double> DoubleFactory = lambda.factory(
		random(), "nextDouble"
	);

	public static final Factory<Integer64> Integer64Factory = lambda.factory(
		This, "nextInteger64"
	);

	public static final Factory<Float64> Float64Factory = lambda.factory(
		This, "nextFloat64"
	);

	public static final Factory<String> StringFactory = lambda.factory(
		This, "nextString"
	);


	public static <T> ISeq<T> ISeq(final int size, final Factory<T> factory) {
		return new Array<T>(size).fill(factory).toISeq();
	}
}
