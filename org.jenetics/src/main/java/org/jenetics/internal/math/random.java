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

import static java.lang.Math.abs;
import static org.jenetics.internal.math.math.random.nextInt;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

import org.jenetics.util.StaticObject;

/**
 * Some random helper functions.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 1.4 &mdash; <em>$Date: 2014-08-01 $</em>
 */
public final class random extends StaticObject {
	private random() {}

	/*
     * Some additional random 'object' creation methods.
     */

	public static byte nextByte(final Random random) {
		return (byte)nextInt(random, Byte.MIN_VALUE, Byte.MAX_VALUE);
	}

	public static char nextCharacter(final Random random) {
		char c = '\0';
		do {
			c = (char)nextInt(random, Character.MIN_VALUE, Character.MAX_VALUE);
		} while (!Character.isLetterOrDigit(c));

		return c;
	}

	public static String nextString(final Random random, final int length) {
		final char[] chars = new char[length];
		for (int i = 0; i < length; ++i) {
			chars[i] = nextCharacter(random);
		}

		return new String(chars);
	}

	public static String nextString(final Random random) {
		return nextString(random, nextInt(random, 5, 20));
	}

	public static short nextShort(final Random random) {
		return (short)nextInt(random, Short.MIN_VALUE, Short.MAX_VALUE);
	}

	public static BigInteger nextBigInteger(final Random random) {
		return new BigInteger(100, random);
	}

	public static BigDecimal nextBigDecimal(final Random random) {
		final StringBuilder out = new StringBuilder();
		for (int i = 0; i < 10; ++i) {
			out.append(Long.toString(abs(random.nextLong())));
		}
		out.append(".");
		for (int i = 0; i < 20; ++i) {
			out.append(Long.toString(abs(random.nextLong())));
		}

		return new BigDecimal(out.toString());
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

}
