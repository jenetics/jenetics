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
package io.jenetics.internal.util;

import java.util.Arrays;
import java.util.Objects;

/**
 * Static methods for simple and efficient hash-code calculation.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.3
 * @since 4.3
 */
public final class Hashes {

	private static final int P1 = 47;
	private static final int P2 = 103;
	private static final int P3 = 197;

	private Hashes() {
	}

	public static int hash(final Object value, final int seed) {
		return seed + P2*Objects.hashCode(value) + P3;
	}

	public static int hash(final Object value) {
		return hash(value, P1);
	}

	public static int hash(final Object[] value, final int seed) {
		return seed + P2*Arrays.hashCode(value) + P3;
	}

	public static int hash(final Object[] value) {
		return hash(value, P1);
	}

	public static int hash(final byte value, final int seed) {
		return seed + P2*Byte.hashCode(value) + P3;
	}

	public static int hash(final byte value) {
		return hash(value, P1);
	}

	public static int hash(final byte[] value, final int seed) {
		return seed + P2*Arrays.hashCode(value) + P3;
	}

	public static int hash(final byte[] value) {
		return hash(value, P1);
	}

	public static int hash(final char value, final int seed) {
		return seed + P2*Character.hashCode(value) + P3;
	}

	public static int hash(final char value) {
		return hash(value, P1);
	}

	public static int hash(final char[] value, final int seed) {
		return seed + P2*Arrays.hashCode(value) + P3;
	}

	public static int hash(final char[] value) {
		return hash(value, P1);
	}

	public static int hash(final short value, final int seed) {
		return seed + P2*Short.hashCode(value) + P3;
	}

	public static int hash(final short value) {
		return hash(value, P1);
	}

	public static int hash(final short[] value, final int seed) {
		return seed + P2*Arrays.hashCode(value) + P3;
	}

	public static int hash(final short[] value) {
		return hash(value, P1);
	}

	public static int hash(final int value, final int seed) {
		return seed + P2*Integer.hashCode(value) + P3;
	}

	public static int hash(final int value) {
		return hash(value, P1);
	}

	public static int hash(final int[] value, final int seed) {
		return seed + P2*Arrays.hashCode(value) + P3;
	}

	public static int hash(final int[] value) {
		return hash(value, P1);
	}

	public static int hash(final long value, final int seed) {
		return seed + P2*Long.hashCode(value) + P3;
	}

	public static int hash(final long value) {
		return hash(value, P1);
	}

	public static int hash(final long[] value, final int seed) {
		return seed + P2*Arrays.hashCode(value) + P3;
	}

	public static int hash(final long[] value) {
		return hash(value, P1);
	}

	public static int hash(final float value, final int seed) {
		return seed + P2*Float.hashCode(value) + P3;
	}

	public static int hash(final float value) {
		return hash(value, P1);
	}

	public static int hash(final float[] value, final int seed) {
		return seed + P2*Arrays.hashCode(value) + P3;
	}

	public static int hash(final float[] value) {
		return hash(value, P1);
	}

	public static int hash(final double value, final int seed) {
		return seed + P2*Double.hashCode(value) + P3;
	}

	public static int hash(final double value) {
		return hash(value, P1);
	}

	public static int hash(final double[] value, final int seed) {
		return seed + P2*Arrays.hashCode(value) + P3;
	}

	public static int hash(final double[] value) {
		return hash(value, P1);
	}

}
