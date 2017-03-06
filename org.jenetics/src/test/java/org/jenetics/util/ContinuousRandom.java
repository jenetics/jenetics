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

import static java.lang.Math.min;

import java.util.Random;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class ContinuousRandom extends Random {

	private long _next;

	public ContinuousRandom(final long start) {
		_next = start;
	}

	@Override
	public long nextLong() {
		return _next++;
	}

	@Override
	public int nextInt() {
		return (int)nextLong();
	}

	@Override
	public boolean nextBoolean() {
		return (nextLong() & 0x8000000000000000L) != 0L;
	}

	@Override
	protected int next(final int bits) {
		return (int)(nextLong() >>> (Long.SIZE - bits));
	}

	@Override
	public void nextBytes(final byte[] bytes) {
		for (int i = 0, len = bytes.length; i < len;) {
			int n = min(len - i, Long.BYTES);

			for (long x = nextLong(); --n >= 0; x >>= Byte.SIZE) {
				bytes[i++] = (byte)x;
			}
		}
	}

}
