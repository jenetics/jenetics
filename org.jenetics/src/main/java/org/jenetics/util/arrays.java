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

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Static helper methods concerning arrays.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0 &mdash; <em>$Date: 2014-03-31 $</em>
 */
public final class arrays extends StaticObject {
	private arrays() {}


	/**
	 * Unified method for calculating the hash code of every {@link Seq}
	 * implementation. The hash code is defined as followed:
	 *
	 * [code]
	 * int hashCode = 1;
	 * final Iterator&lt;E&gt; it = seq.iterator();
	 * while (it.hasNext()) {
	 *     final E obj = it.next();
	 *     hashCode = 31*hashCode + (obj == null ? 0 : obj.hashCode());
	 * }
	 * [/code]
	 *
	 * @see Seq#hashCode()
	 * @see List#hashCode()
	 *
	 * @param seq the sequence to calculate the hash code for.
	 * @return the hash code of the given sequence.
	 */
	public static int hashCode(final Seq<?> seq) {
		int hash = 1;
		for (Object element : seq) {
			hash = 31*hash + (element == null ? 0: element.hashCode());
		}
		return hash;
	}

	/**
	 * Unified method for compare to sequences for equality.
	 *
	 * @see Seq#equals(Object)
	 *
	 * @param seq the sequence to test for equality.
	 * @param obj the object to test for equality with the sequence.
	 * @return {@code true} if the given objects are sequences and contain the
	 *          same objects in the same order, {@code false} otherwise.
	 */
	public static boolean equals(final Seq<?> seq, final Object obj) {
		if (obj == seq) {
			return true;
		}
		if (!(obj instanceof Seq<?>)) {
			return false;
		}

		final Seq<?> other = (Seq<?>)obj;
		boolean equals = (seq.length() == other.length());
		for (int i = seq.length(); equals && --i >= 0;) {
			final Object element = seq.get(i);
			if (element != null) {
				equals = element.equals(other.get(i));
			} else {
				equals = other.get(i) == null;
			}
		}
		return equals;
	}

	/**
	 * Calls the sort method on the {@link Arrays} class.
	 *
	 * @param <T> the array element type
	 * @param array the array to sort
	 * @return the sorted input array, for command chaining
	 * @throws NullPointerException if the give array is {@code null}.
	 * @throws UnsupportedOperationException if the array is sealed
	 * 		  ({@code array.isSealed() == true}).
	 */
	public static <T extends Object & Comparable<? super T>> MSeq<T>
	sort(final MSeq<T> array)
	{
		Collections.sort(array.asList());
		return array;
	}

	/**
	 * Test whether the given array is sorted in ascending order.
	 *
	 * @param <T> the array element type
	 * @param seq the array to test.
	 * @return {@code true} if the given {@code array} is sorted in ascending
	 *         order, {@code false} otherwise.
	 * @throws NullPointerException if the given array or one of it's element is
	 *         {@code null}.
	 */
	public static <T extends Object & Comparable<? super T>>
	boolean isSorted(final Seq<T> seq)
	{
		boolean sorted = true;
		for (int i = 0, n = seq.length() - 1; i < n && sorted; ++i) {
			sorted = seq.get(i).compareTo(seq.get(i + 1)) <= 0;
		}

		return sorted;
	}

	/**
	 * Test whether the given array is sorted in ascending order. The order of
	 * the array elements is defined by the given comparator.
	 *
	 * @param <T> the array element type
	 * @param seq the array to test.
	 * @param comparator the comparator which defines the order.
	 * @return {@code true} if the given {@code array} is sorted in ascending
	 *         order, {@code false} otherwise.
	 * @throws NullPointerException if the given array or one of it's element or
	 *         the comparator is {@code null}.
	 */
	public static <T> boolean isSorted(
		final Seq<T> seq, final Comparator<? super T> comparator
	) {
		boolean sorted = true;
		for (int i = 0, n = seq.length() - 1; i < n && sorted; ++i) {
			sorted = comparator.compare(seq.get(i), seq.get(i + 1)) <= 0;
		}

		return sorted;
	}

	/**
	 * Return a array with the indexes of the partitions of an array with the
	 * given size. The length of the returned array is {@code min(size, prts) + 1}.
	 * <p>
	 * Some examples:
	 * <pre>
	 * 	 partition(10, 3): [0, 3, 6, 10]
	 * 	 partition(15, 6): [0, 2, 4, 6, 9, 12, 15]
	 * 	 partition(5, 10): [0, 1, 2, 3, 4, 5]
	 * </pre>
	 *
	 * The following examples prints the start index (inclusive) and the end
	 * index (exclusive) of the {@code partition(15, 6)}.
	 * [code]
	 * int[] parts = partition(15, 6);
	 * for (int i = 0; i &lt; parts.length - 1; ++i) {
	 *     System.out.println(i + ": " + parts[i] + "\t" + parts[i + 1]);
	 * }
	 * [/code]
	 * <pre>
	 * 	 0: 0 	2
	 * 	 1: 2 	4
	 * 	 2: 4 	6
	 * 	 3: 6 	9
	 * 	 4: 9 	12
	 * 	 5: 12	15
	 * </pre>
	 *
	 * This example shows how this can be used in an concurrent environment:
	 * [code]
	 * try (final Concurrency c = Concurrency.start()) {
	 *     final int[] parts = arrays.partition(population.size(), _maxThreads);
	 *
	 *     for (int i = 0; i &lt; parts.length - 1; ++i) {
	 *         final int part = i;
	 *         c.execute(new Runnable() { @Override public void run() {
	 *             for (int j = parts[part + 1]; --j &gt;= parts[part];) {
	 *                 population.get(j).evaluate();
	 *             }
	 *         }});
	 *     }
	 * }
	 * [/code]
	 *
	 * @param size the size of the array to partition.
	 * @param parts the number of parts the (virtual) array should be partitioned.
	 * @return the partition array with the length of {@code min(size, parts) + 1}.
	 * @throws IllegalArgumentException if {@code size} or {@code p} is less than one.
	 */
	public static int[] partition(final int size, final int parts) {
		if (size < 1) {
			throw new IllegalArgumentException(
				"Size must greater than zero: " + size
			);
		}
		if (parts < 1) {
			throw new IllegalArgumentException(
				"Number of partitions must greater than zero: " + parts
			);
		}

		final int pts = Math.min(size, parts);
		final int[] partition = new int[pts + 1];

		final int bulk = size/pts;
		final int rest = size%pts;
		assert ((bulk*pts + rest) == size);

		for (int i = 0, n = pts - rest; i < n; ++i) {
			partition[i] = i*bulk;
		}
		for (int i = 0, n = rest + 1; i < n; ++i) {
			partition[pts - rest + i] = (pts - rest)*bulk + i*(bulk + 1);
		}

		return partition;
	}

	/**
	 * Iterates over all elements of the given {@code array} as long as the
	 * {@code predicate} returns {@code true} (which means <i>continue</i>) and
	 * returns the index the iteration has been interrupted. -1 is returned if
	 * all elements were visited.
	 * <p>
	 * Can be used to check all array elements for nullness.
	 *
	 * [code]
	 * public void foo(final Integer[] values) {
	 *     arrays.forEach(values, new Validator.NonNull());
	 *     ...
	 * }
	 * [/code]
	 *
	 * @param <T> the array element type
	 * @param <R> the returned type of the applied function
	 * @param array the array to iterate.
	 * @param f the function to apply to every element.
	 * @throws NullPointerException if one of the elements are {@code null}.
	 */
	public static <T, R> void forEach(
		final T[] array,
		final Function<? super T, ? extends R> f
	) {
		requireNonNull(array, "Array");
		requireNonNull(f, "Predicate");

		for (int i = 0; i < array.length; ++i) {
			f.apply(array[i]);
		}
	}

	/**
	 * Iterates over all elements of the given {@code values}
	 *
	 * @param <T> the element type
	 * @param <R> the returned type of the applied function
	 * @param values the values to iterate.
	 * @param f the function to apply to each element.
	 * @throws NullPointerException if one of the elements are {@code null}.
	 */
	public static <T, R> void forEach(
		final Iterable<? extends T> values,
		final Function<? super T, ? extends R> f
	) {
		requireNonNull(values, "Array");
		requireNonNull(f, "Function");

		for (final T value : values) {
			f.apply(value);
		}
	}

	/**
	 * Map the array from type A to an other array of type B.
	 *
	 * @param <A> the source type.
	 * @param <B> the target type.
	 * @param a the source array.
	 * @param b the target array. If the given array is to short a new array
	 *        with the right size is created, mapped and returned. If the given
	 *        array is long enough <i>this</i> array is returned.
	 * @param converter the converter needed for mapping from type A to type B.
	 * @return the mapped array. If {@code b} is long enough {@code b} is
	 *         returned otherwise a new created array.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public static <A, B> B[] map(
		final A[] a,
		final B[] b,
		final Function<? super A, ? extends B> converter
	) {
		requireNonNull(a, "Source array");
		requireNonNull(b, "Target array");
		requireNonNull(converter, "Converter");

		B[] result = b;
		if (b.length < a.length) {
			@SuppressWarnings("unchecked")
			final B[] r = (B[])java.lang.reflect.Array.newInstance(
				b.getClass().getComponentType(), a.length
			);
			result = r;
		}

		for (int i = 0; i < result.length; ++i) {
			result[i] = converter.apply(a[i]);
		}

		return result;
	}

}
