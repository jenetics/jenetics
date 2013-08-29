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

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class ForeachTest {

	public interface P1<T> {
		public boolean apply(final T value);
	}

	public interface P2<T> {
		public Boolean apply(final T value);
	}

	public static void main(final String[] args) throws Exception {
		Thread.sleep(100);
		final int iterations = 2_000_000_000;

		long begin = System.currentTimeMillis();
		iterate1(iterations, new P1<Long>() {
			@Override public boolean apply(Long value) {
				return value.intValue() < iterations;
			}
		});
		long end = System.currentTimeMillis();
		System.out.println("P1: " + (end - begin));


		begin = System.currentTimeMillis();
		iterate2(iterations, new P2<Long>() {
			@Override public Boolean apply(Long value) {
				return value.intValue() < iterations ? Boolean.TRUE : Boolean.FALSE;
			}
		});
		end = System.currentTimeMillis();
		System.out.println("P2: " + (end - begin));

	}


	static long iterate1(final long iterations, final P1<Long> predicate) {
		long i = 0;
		while (i < iterations && predicate.apply(i)) {
			i += 1;

		}

		return i;
	}

	static long iterate2(final long iterations, final P2<Long> predicate) {
		long i = 0;
		while (i < iterations && predicate.apply(i)) {
			i += 1;
		}

		return i;
	}

}
