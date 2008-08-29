/*
 * Java Genetic Algorithm Library (@!identifier!@).
 * Copyright (c) @!year!@ Franz Wilhelmstötter
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

import static java.lang.Math.min;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.RandomAccess;


/**
 * Utility class concerning arrays.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: ArrayUtils.java,v 1.3 2008-08-29 21:18:15 fwilhelm Exp $
 */
public final class ArrayUtils {

	private ArrayUtils() {
	}
	
	/**
	 * Returns a fixed-size list backed by the specified array. (Changes to
	 * the returned list "write through" to the array.) The returned list is
	 * fixed size, serializable and implements {@link RandomAccess}.
	 *
	 * @param array the array by which the list will be backed
	 * @return a list view of the specified array
	 * @throws NullPointerException if the given {@code array} is {@code null}.
	 */	
	public static <T> List<T> asList(final Array<T> array) {
		Validator.notNull(array, "Array");
		return new org.jenetics.util.ArrayList<T>(array._array);
	}
	
	/**
	 * Swap two elements of an given array.
	 * 
	 * @param <T> the array type.
	 * @param array the array
	 * @param i index of the first array element.
	 * @param j index of the second array element.
     * @throws ArrayIndexOutOfBoundsException if <tt>i &lt; 0</tt> or
     *         <tt>j &lt; 0</tt> or <tt>i &gt; a.length</tt> or
     *         <tt>j &gt; a.length</tt>
	 */
	public static <T> void swap(final T[] array, final int i, final int j) {
		final T temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}
	
	/**
	 * Swap two elements of an given array.
	 * 
	 * @param <T> the array type.
	 * @param array the array
	 * @param i index of the first array element.
	 * @param j index of the second array element.
     * @throws ArrayIndexOutOfBoundsException if <tt>i &lt; 0</tt> or
     *         <tt>j &lt; 0</tt> or <tt>i &gt; a.length</tt> or
     *         <tt>j &gt; a.length</tt>
	 */
	public static <T> void swap(final Array<T> array, final int i, final int j) {
		swap(array._array, i, j);
	}
	
	/**
	 * Calls the sort method on the {@link Arrays} class.
	 * 
	 * @see Arrays#sort(Object[], int, int, Comparator)
     * @throws IllegalArgumentException if <tt>from &gt; to</tt>
     * @throws ArrayIndexOutOfBoundsException if <tt>from &lt; 0</tt> or
     *         <tt>to &gt; a.length</tt>
	 */
	public static <T> void sort(
		final Array<T> array, final int from, final int to,
		final Comparator<? super T> comparator
	) {
		@SuppressWarnings("unchecked")
		final Comparator<Object> c = (Comparator<Object>)comparator;
		Arrays.sort(array._array, from, to, c);
	}
	
	/**
	 * Calls the sort method on the {@link Arrays} class.
	 * @see Arrays#sort(Object[], Comparator)
	 */
	public static <T> void sort(final Array<T> array, final Comparator<? super T> comparator) {
		sort(array, 0, array.length(), comparator);
	}
	
	/**
	 * Calls the sort method on the {@link Arrays} class.
	 * @see Arrays#sort(Object[], int, int)
     * @throws IllegalArgumentException if <tt>from &gt; to</tt>
     * @throws ArrayIndexOutOfBoundsException if <tt>from &lt; 0</tt> or
     *         <tt>to &gt; a.length</tt>
	 */
	public static <T> void sort(final Array<T> array, final int from, final int to) {
		Arrays.sort(array._array, from, to);
	}
	
	/**
	 * Calls the sort method on the {@link Arrays} class.
	 * 
	 * @see Arrays#sort(Object[])
	 */
	public static <T> void sort(final Array<T> array) {
		Arrays.sort(array._array, 0, array.length());
	}
	
//	public static void main(String[] args) {
//		Array<Integer> array = Array.newInstance(10);
//		for (int i = 0; i < array.length(); ++i) {
//			array.set(i, array.length() - i);
//		}
//		
//		sort(array, 0, array.length(), new Comparator<Integer>() {
//			@Override public int compare(Integer o1, Integer o2) {
//				return o1.compareTo(o2);
//			}
//		});
//		
//		System.out.println(Arrays.toString(array._array));
//	}
	
	/**
	 * Finds the minimum and maximum value of the given array. 
	 * 
	 * @param <T> the comparable type.
	 * @param values the array to search.
	 * @return an array of size two. The first element contains the minimum and
	 *         the second element contains the maximum value. If the given array
	 *         has size zero, the min and max values of the returned array are 
	 *         {@code null}.
	 */
	public static <T extends Comparable<T>> Array<T> minmax(final Array<T> values) {
		final int size = values.length();
		
		T min = null;
		T max = null;
		int start = 0;
		
		if (size%2 == 0 && size > 0) {
			start = 2;
			if (values.get(0).compareTo(values.get(1)) < 0) {
				min = values.get(0);
				max = values.get(1);
			} else {
				min = values.get(1);
				max = values.get(0);
			}
		} else if (size%2 == 1) {
			start = 1;
			min = values.get(0);
			max = values.get(0);
		}
		
		for (int i = start; i < size; i += 2) {
			final T first = values.get(i);
			final T second = values.get(i + 1);
			
			if (first.compareTo(second) < 0) {
				if (first.compareTo(min) < 0) {
					min = first;
				}
				if (second.compareTo(max) > 0) {
					max = second;
				}
			} else {
				if (second.compareTo(min) < 0) {
					min = second;
				}
				if (first.compareTo(max) > 0) {
					max = first;
				}
			}
		}
		
		final Array<T> mm = Array.newInstance(2);
		mm.set(0, min);
		mm.set(1, max);
		return mm;  
	}
	
	public static <T extends Comparable<T>> T median(final Array<T> values) {
		
		
		return null;
	}
	
	/**
	 * Randomize the {@code array} with the given {@link Random} object. The used
	 * shuffling algorithm is from D. Knuth TAOCP, Seminumerical Algorithms,
	 * Third edition, page 142, Algorithm S (Selection sampling technique).
	 * @param array the {@code array} to randomize.
	 * @param random the {@link Random} object to use for randomize.
	 * 
	 * @param <T> the component type of the array to randomize.
	 */
	public static <T> void randomize(final T[] array, final Random random) {
		for (int j = array.length - 1; j > 0; --j) {
			swap(array, j, random.nextInt(j + 1));
		}
	}
	
	/**
	 * Randomize the {@code array} with the given {@link Random} object. The used
	 * shuffling algorithm is from D. Knuth TAOCP, Seminumerical Algorithms,
	 * Third edition, page 142, Algorithm S (Selection sampling technique).
	 * @param array the {@code array} to randomize.
	 * @param random the {@link Random} object to use for randomize.
	 * 
	 * @param <T> the component type of the array to randomize.
	 */
	public static <T> void randomize(final Array<T> array, final Random random) {
		randomize(array._array, random);
	}
	
	/**
	 * Reverses the part of the array determined by the to indexes.
	 * 
	 * @param <T> the array type.
	 * @param array the array to reverse
	 * @param from the first index (inclusive)
	 * @param to the second index (exclusive)
     * @throws IllegalArgumentException if <tt>from &gt; to</tt>
     * @throws ArrayIndexOutOfBoundsException if <tt>from &lt; 0</tt> or
     *         <tt>to &gt; a.length</tt>
	 */
	public static <T> void reverse(final T[] array, final int from, final int to) {
		rangeCheck(array.length, from, to);
		int i = from;
		int j = to;
		
		while (i < j) {
			--j;
			swap(array, i, j);
			++i;
		}
	}
	
	/**
	 * Reverses the part of the array determined by the to indexes.
	 * 
	 * @param <T> the array type.
	 * @param array the array to reverse
	 * @param from the first index (inclusive)
	 * @param to the second index (exclusive)
     * @throws IllegalArgumentException if <tt>from &gt; to</tt>
     * @throws ArrayIndexOutOfBoundsException if <tt>from &lt; 0</tt> or
     *         <tt>to &gt; a.length</tt>
	 */
	public static <T> void reverse(final Array<T> array, final int from, final int to) {
		reverse(array._array, from, to);
	}
	
	/**
	 * Reverses the given array in place.
	 * 
	 * @param <T> the array type.
	 * @param array the array to reverse.
	 */
	public static <T> void reverse(final T[] array) {
		reverse(array, 0, array.length);
	}
	
	/**
	 * Reverses the given array in place.
	 * 
	 * @param <T> the array type.
	 * @param array the array to reverse.
	 */
	public static <T> void reverse(final Array<T> array) {
		reverse(array._array);
	}
	
	private static void rangeCheck(int length, int from, int to) {
		if (from > to) {
			throw new IllegalArgumentException(
				"fromIndex(" + from + ") > toIndex(" + to+ ")"
			);
		}
		if (from < 0) {
			throw new ArrayIndexOutOfBoundsException(from);
		}
		if (to > length) {
			throw new ArrayIndexOutOfBoundsException(to);
		}
	}
	
	/**
	 * Return a array with the indexes of the partitions of an array with the given size.
	 * The length of the returned array is {@code min(size, prts) + 1}.
	 * <p/>
	 * Some examples:
	 * <pre>
	 *     partition(10, 3): [0, 3, 6, 10]
	 *     partition(15, 6): [0, 2, 4, 6, 9, 12, 15]
	 *     partition(5, 10): [0, 1, 2, 3, 4, 5]
	 * </pre>
	 * 
	 * The following examples prints the start index (inclusive) and the end
	 * index (exclusive) of the {@code partition(15, 6)}.
	 * [code]
	 *     int[] parts = partition(15, 6);
	 *     for (int i = 0; i < parts.length - 1; ++i) {
	 *         System.out.println(i + ": " + parts[i] + "\t" + parts[i + 1]); 
	 *     }
	 * [/code]
	 * <pre>
	 *     0: 0    2
	 *     1: 2    4
	 *     2: 4    6
	 *     3: 6    9
	 *     4: 9    12
	 *     5: 12   15	
	 * </pre>
	 * 
	 * @param size the size of the array to partition.
	 * @param prts the number of parts the (virtual) array should be partitioned.
	 * @return the partition array with the length of {@code min(size, prts) + 1}.
	 * @throws IllegalArgumentException if {@code size} or {@code p} is less than one.
	 */
	public static int[] partition(final int size, final int prts) {
		if (size < 1) {
			throw new IllegalArgumentException(
				"Size must greater than zero: " + size
			);
		}
		if (prts < 1) {
			throw new IllegalArgumentException(
				"Number of partitions must greater than zero: " + prts
			);
		}
		
		final int parts = min(size, prts);
		final int[] partition = new int[parts + 1];
		
		final int bulk = size != 0 ? size/parts : 0;
		final int rest = size != 0 ? size%parts : 0;
		assert ((bulk*parts + rest) == size);
		
		for (int i = 0, n = parts - rest; i < n; ++i) {
			partition[i] = i*bulk;
		}
		for (int i = 0, n = rest + 1; i < n; ++i) {
			partition[parts - rest + i] = (parts - rest)*bulk + i*(bulk + 1);
		}
		
		return partition;
	}	
	
	static int indexOf(final Object[] array, final Object element) {
		int index = -1;
		if (element != null) {
			for (int i = 0; i < array.length && index == -1; ++i) {
				if (element.equals(array[i])) {
					index = i;
				}
			}
		} else {
			for (int i = 0; i < array.length && index == -1; ++i) {
				if (array[i] == null) {
					index = i;
				}
			}	
		}
		return index;
	}
	
}



