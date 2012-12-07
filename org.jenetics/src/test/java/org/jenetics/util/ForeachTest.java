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

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2012-11-30 $</em>
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
