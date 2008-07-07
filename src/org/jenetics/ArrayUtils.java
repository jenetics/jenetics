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
package org.jenetics;

import static java.lang.Math.min;

import java.util.Random;

/**
 * Utility class concerning arrays.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: ArrayUtils.java,v 1.4 2008-07-07 21:17:40 fwilhelm Exp $
 */
public final class ArrayUtils {

	private ArrayUtils() {
	}
	
	/**
	 * Swap two elements of an given array.
	 * 
	 * @param <T> the array type.
	 * @param array the array
	 * @param i index of the first array element.
	 * @param j index of the second array element.
	 * @throws ArrayIndexOutOfBoundsException if one of the given indexes is out 
	 *         of bounds.
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
	 * @throws ArrayIndexOutOfBoundsException if one of the given indexes is out 
	 *         of bounds.
	 */
	public static <T> void swap(final Array<T> array, final int i, final int j) {
		final T temp = array.get(i);
		array.set(i, array.get(j));
		array.set(j, temp);
	}
	
	/**
	 * Randomize the {@code array} with the given {@link Random} object. The used
	 * shuffling algorithm is from D. Knuth TAOCP, Seminumerical Algorithms,
	 * Third edition, page 142, Algorithm S (Selection sampling technique).
	 * 
	 * @param <T> the component type of the array to randomize.
	 * @param random the {@link Random} object to use for randomize.
	 * @param array the {@code array} to randomize.
	 */
	public static <T> void randomize(final Random random, final T[] array) {
		for (int j = array.length - 1; j > 0; --j) {
			swap(array, j, random.nextInt(j + 1));
		}
	}
	
	/**
	 * Randomize the {@code array} with the given {@link Random} object. The used
	 * shuffling algorithm is from D. Knuth TAOCP, Seminumerical Algorithms,
	 * Third edition, page 142, Algorithm S (Selection sampling technique).
	 * 
	 * @param <T> the component type of the array to randomize.
	 * @param random the {@link Random} object to use for randomize.
	 * @param array the {@code array} to randomize.
	 */
	public static <T> void randomize(final Random random, final Array<T> array) {
		for (int j = array.length() - 1; j > 0; --j) {
			swap(array, j, random.nextInt(j + 1));
		}
	}
	
	/**
	 * Reverses the part of the array determined by the to indexes.
	 * 
	 * @param <T> the array type.
	 * @param array the array to reverse
	 * @param from the first index (inclusive)
	 * @param to the second index (exclusive)
	 */
	public static <T> void reverse(final T[] array, final int from, final int to) {
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
	 */
	public static <T> void reverse(final Array<T> array, final int from, final int to) {
		int i = from;
		int j = to;
		
		while (i < j) {
			--j;
			swap(array, i, j);
			++i;
		}
	}
	
	/**
	 * Reverses the given array.
	 * 
	 * @param <T> the array type.
	 * @param array the array to reverse.
	 */
	public static <T> void reverse(final T[] array) {
		reverse(array, 0, array.length);
	}
	
	public static <T> void reverse(final Array<T> array) {
		reverse(array, 0, array.length());
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
	
}



