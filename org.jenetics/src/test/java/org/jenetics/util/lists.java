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

import java.util.List;
import java.util.Random;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class lists extends StaticObject {
	private lists() {}


	/**
	 * Randomize the {@code list} using the given {@link Random} object. The used
	 * shuffling algorithm is from D. Knuth TAOCP, Seminumerical Algorithms,
	 * Third edition, page 142, Algorithm S (Selection sampling technique).
	 *
	 * @param list the {@code array} to randomize.
	 * @param <T> the component type of the array to randomize.
	 * @throws NullPointerException if the give list is {@code null}.
	 */
	public static <T> void shuffle(final List<T> list) {
		shuffle(list, RandomRegistry.getRandom());
	}

	/**
	 * Randomize the {@code list} using the given {@link Random} object. The used
	 * shuffling algorithm is from D. Knuth TAOCP, Seminumerical Algorithms,
	 * Third edition, page 142, Algorithm S (Selection sampling technique).
	 *
	 * @param list the {@code array} to randomize.
	 * @param random the {@link Random} object to use for randomize.
	 * @param <T> the component type of the array to randomize.
	 * @throws NullPointerException if the give list or the random object is
	 *          {@code null}.
	 */
	public static <T> void shuffle(final List<T> list, final Random random) {
		for (int j = list.size() - 1; j > 0; --j) {
			swap(list, j, random.nextInt(j + 1));
		}
	}

	/**
	 * Swap two elements of an given list.
	 *
	 * @param <T> the list type.
	 * @param list the array
	 * @param i index of the first list element.
	 * @param j index of the second list element.
	 * @throws IndexOutOfBoundsException if <tt>i &lt; 0</tt> or
	 *			<tt>j &lt; 0</tt> or <tt>i &gt; a.length</tt> or
	 *			<tt>j &gt; a.length</tt>
	 * @throws NullPointerException if the give list is {@code null}.
	 */
	public static <T> void swap(final List<T> list, final int i, final int j) {
		final T old = list.get(i);
		list.set(i, list.get(j));
		list.set(j, old);
	}
}
