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

import java.util.List;
import java.util.Random;

import org.jenetics.internal.util.require;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class lists {
	private lists() {require.noInstance();}


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
