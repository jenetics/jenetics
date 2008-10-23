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
 * @version $Id: ArrayUtils.java,v 1.13 2008-10-23 22:46:06 fwilhelm Exp $
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
     * @throws NullPointerException if the give array is {@code null}.
	 */
	public static <T> void swap(final T[] array, final int i, final int j) {
		Validator.notNull(array, "Array");
		
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
     * @throws NullPointerException if the give array is {@code null}.
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
     * @throws NullPointerException if the give array or comparator is 
     *         {@code null}.
	 */
	public static <T> void sort(
		final Array<T> array, final int from, final int to,
		final Comparator<? super T> comparator
	) {
		Validator.notNull(array, "Array");
		Validator.notNull(comparator, "Comparator");
		
		@SuppressWarnings("unchecked")
		final Comparator<Object> c = (Comparator<Object>)comparator;
		Arrays.sort(array._array, from, to, c);
	}
	
	/**
	 * Calls the sort method on the {@link Arrays} class.
	 * @see Arrays#sort(Object[], Comparator)
	 * 
     * @throws NullPointerException if the give array or comparator is 
     *         {@code null}.
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
     * @throws NullPointerException if the give array or comparator is 
     *         {@code null}.
	 */
	public static <T> void sort(final Array<T> array, final int from, final int to) {
		Arrays.sort(array._array, from, to);
	}
	
	/**
	 * Calls the sort method on the {@link Arrays} class.
	 * 
	 * @see Arrays#sort(Object[])
     * @throws NullPointerException if the give array is {@code null}.
	 */
	public static <T> void sort(final Array<T> array) {
		Arrays.sort(array._array, 0, array.length());
	}
	
//	public static void main(String[] args) {
//		Array<Integer> array = Array.newInstance(11);
//		for (int i = 0; i < array.length(); ++i) {
//			array.set(i, i);
//		}
//		
////		sort(array, 0, array.length(), new Comparator<Integer>() {
////			@Override public int compare(Integer o1, Integer o2) {
////				return o1.compareTo(o2);
////			}
////		});
//		
//		System.out.println(array.toString());
//		System.out.println(median(array));
//	}
	
	/**
	 * Finds the minimum and maximum value of the given array. 
	 * 
	 * @param <T> the comparable type.
	 * @param array the array to search.
	 * @return an array of size two. The first element contains the minimum and
	 *         the second element contains the maximum value. If the given array
	 *         has size zero, the min and max values of the returned array are 
	 *         {@code null}.
     * @throws NullPointerException if the give array is {@code null}.
	 */
	public static <T extends Object & Comparable<? super T>> Array<T> 
	minmax(final Array<T> array) 
	{
		Validator.notNull(array, "Array");
		
		final int size = array.length();
		
		T min = null;
		T max = null;
		int start = 0;
		
		if (size%2 == 0 && size > 0) {
			start = 2;
			if (array.get(0).compareTo(array.get(1)) < 0) {
				min = array.get(0);
				max = array.get(1);
			} else {
				min = array.get(1);
				max = array.get(0);
			}
		} else if (size%2 == 1) {
			start = 1;
			min = array.get(0);
			max = array.get(0);
		}
		
		for (int i = start; i < size; i += 2) {
			final T first = array.get(i);
			final T second = array.get(i + 1);
			
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
		 
		@SuppressWarnings("unchecked")
		Array<T> mm = Array.valueOf(min, max);
		return mm;
	}
	
	/**
	 * Returnthe <i>k</i>th smallest value of the {@code values} array. The input
	 * array will not be rearranged.
	 * 
	 * @param <T> the array element type.
	 * @param array the array.
	 * @param k searching the <i>k</i>th samllest value.
	 * @return the <i>k</i>th samllest value.
	 * @throws NullPointerException if the {@code array} or one of it's element 
	 *         is {@code null}.
	 * @throws IllegalArgumentException if {@code k < 0} or 
	 *         {@code k > values.length() - 1}.
	 */
	public static <T extends Object & Comparable<? super T>> T 
	select(final Array<T> array, final int k) 
	{
		Validator.notNull(array, "Array");
		if (k < 0) {
			throw new IllegalArgumentException("k is smaller than zero: " + k);
		}
		if (k > array.length() - 1) {
			throw new IllegalArgumentException(String.format(
				"k is greater than values.length() - 1 (%d): %d", 
				array.length() - 1, k
			));
		}

		//Init the pivot array. This avoids the rearrangement of the given array.
		final int[] pivot = new int[array.length()];
		for (int i = 0; i < pivot.length; ++i) {
			pivot[i] = i;
		}
		
		int l = 0;
		int ir = array.length() - 1;
		T value = null;
		while (value == null) {
			if (ir <= l + 1) {
				if (ir == l + 1 && array.get(pivot[ir]).compareTo(array.get(pivot[l])) < 0) {
					swap(pivot, l, ir);
				}
				value = array.get(pivot[k]);
			} else {
				final int mid = (l + ir) >> 1;
				swap(pivot, mid, l + 1);
				if (array.get(pivot[l]).compareTo(array.get(pivot[ir])) > 0) {
					swap(pivot, l, ir);
				}
				if (array.get(pivot[l + 1]).compareTo(array.get(pivot[ir])) > 0) {
					swap(pivot, l + 1, ir);
				}
				if (array.get(pivot[l]).compareTo(array.get(pivot[l + 1])) > 0) {
					swap(pivot, l, l + 1);
				}
				
				int i = l + 1;
				int j = ir;
				final T a = array.get(pivot[l + 1]);
				while (true) {
					do {
						++i;
					} while (array.get(pivot[i]).compareTo(a) < 0);
					do {
						--j;
					} while (array.get(pivot[j]).compareTo(a) > 0);
					if (j < i) {
						break;
					}
					swap(pivot, i, j);
				}
				
				array.set(pivot[l + 1], array.get(pivot[j]));
				array.set(pivot[j], a);
				if (j >= k) {
					ir = j -1;
				}
				if (j <= k) {
					l = i;
				}
			}
		}
		
		return value;
	}
	private static void swap(final int[] array, final int i, final int j) {
		final int temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}
	
	/**
	 * Finding the median of the give array. The input array will not be 
	 * rearranged.
	 * 
	 * @param <T> the array element type.
	 * @param array the array.
	 * @return the median
	 * @throws NullPointerException if the give array is {@code null}.
	 */
	public static <T extends Object & Comparable<? super T>> T 
	median(final Array<T> array) 
	{
		Validator.notNull(array, "Array");
		if (array.length() == 0) {
			throw new IllegalArgumentException("Array length is zero.");
		}
		
		T median = null;
		if (array.length() == 1) {
			median = array.get(0);
		} else if (array.length() == 2) {
			if (array.get(0).compareTo(array.get(1)) < 0) {
				median = array.get(0);
			} else {
				median = array.get(1);
			}
		} else {
			median = select(array, array.length()/2);
		}
		return median;
	}
	
	/**
	 * Randomize the {@code array} with the given {@link Random} object. The used
	 * shuffling algorithm is from D. Knuth TAOCP, Seminumerical Algorithms,
	 * Third edition, page 142, Algorithm S (Selection sampling technique).
	 * @param array the {@code array} to randomize.
	 * @param random the {@link Random} object to use for randomize.
	 * @param <T> the component type of the array to randomize.
	 * @throws NullPointerException if the give array is {@code null}.
	 */
	public static <T> void randomize(final T[] array, final Random random) {
		Validator.notNull(array, "Array");
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
	 * @param <T> the component type of the array to randomize.
	 * @throws NullPointerException if the give array is {@code null}.
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
     * @throws NullPointerException if the give array is {@code null}.
	 */
	public static <T> void reverse(final T[] array, final int from, final int to) {
		Validator.notNull(array, "Array");
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
     * @throws NullPointerException if the give array is {@code null}.
	 */
	public static <T> void reverse(final Array<T> array, final int from, final int to) {
		reverse(array._array, from, to);
	}
	
	/**
	 * Reverses the given array in place.
	 * 
	 * @param <T> the array type.
	 * @param array the array to reverse.
	 * @throws NullPointerException if the give array is {@code null}.
	 */
	public static <T> void reverse(final T[] array) {
		reverse(array, 0, array.length);
	}
	
	/**
	 * Reverses the given array in place.
	 * 
	 * @param <T> the array type.
	 * @param array the array to reverse.
	 * @throws NullPointerException if the give array is {@code null}.
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
	 * This example shows how this can be used in an concurrent environment:
	 * [code]
	 *     ConcurrentContext.enter();
	 *     try {
	 *        final int[] parts = ArrayUtils.partition(population.size(), _maxThreads);
	 *		
	 *        for (int i = 0; i < parts.length - 1; ++i) {
	 *            final int part = i;
	 *            ConcurrentContext.execute(new Runnable() {
	 *                public void run() {
	 *                    for (int j = parts[part + 1]; --j >= parts[part];) {
	 *                        population.get(j).evaluate();
	 *                    }
	 *                }
	 *            });
	 *        }
	 *     } finally {
	 *         ConcurrentContext.exit();
	 *     }
	 * [/code]
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

	/**
	 * Selects a random subset of size {@code k} from a set of size {@code n}.
	 * 
	 * @param n the size of the set.
	 * @param k the size of the subset.
	 * @param sub the sub set array.
	 * @param random the random number generator used.
	 * @throws NullPointerException if {@code random} is {@code null}.
	 * @throws IllegalArgumentException if {@code k <= 0}, {@code n < k} or
	 *         {@code sub.length < k}.
	 */
	public static void subset(final int n, final int k, final int sub[], final Random random) {
		Validator.notNull(random, "Random");
		if (k <= 0) {
			throw new IllegalArgumentException(String.format(
				"Subset size smaller or equal zero: %s", k
			));
		}
		if (n < k) {
			throw new IllegalArgumentException(String.format(
				"n smaller than k: %s < %s.", n, k
			));
		}
		if (sub.length < k) {
			throw new IllegalArgumentException(String.format(
				"Sub length must equal or greater than k: %s < %s", sub.length, k
			));
		}
		
		for (int i = 0; i < k; ++i) {
			sub[i] = (i*n)/k;
		}

		int l = 0;
		int ix = 0;
		for (int i = 0; i < k; ++i) {
			do {
				ix = uniform(1, n, random);
				l = (ix*k - 1)/n;
			} while (sub[l] >= ix);
			
			sub[l] = sub[l] + 1;
		}

		int m = 0;
		int ip = 0;
		int is = k;
		for (int i = 0; i < k; ++i) {
			m = sub[i];
			sub[i] = 0;

			if (m != (i*n)/k) {
				ip = ip + 1;
				sub[ip - 1] = m;
			}
		}

		int ihi = ip;
		int ids = 0;
		for (int i = 1; i <= ihi; ++i) {
			ip = ihi + 1 - i;
			l = 1 + (sub[ip - 1]*k - 1)/n;
			ids = sub[ip - 1] - ((l - 1)*n)/k;
			sub[ip - 1] = 0;
			sub[is - 1] = l;
			is = is - ids;
		}

		int ir = 0;
		int m0 = 0;
		for (int ll = 1; ll <= k; ++ll) {
			l = k + 1 - ll;
			
			if (sub[l - 1] != 0) {
				ir = l;
				m0 = 1 + ((sub[l - 1] - 1)*n)/k;
				m = (sub[l-1]*n)/k - m0 + 1;
			}

			ix = uniform(m0, m0 + m - 1, random);

			int i = l + 1;
			while (i <= ir && ix >= sub[i - 1]) {
				ix = ix + 1;
				sub[i-2] = sub[i-1];
				i = i + 1;
			}
			
			sub[i-2] = ix;
			m = m - 1;
		}
	}

	private static int uniform(final int a, final int b, final Random random) {
		int value = 0;
		
		if (a == b) {
			value = a - 1;
		} else {
			value = random.nextInt(b - a) + a;
		}
		
		return value;
	}

	
	public static void main(String[] args) {
		int[] set = new int[5];
		subset(10, set.length, set, new Random());
		System.out.println(Arrays.toString(set));
	}
	
	/**
	 * Returns the index of the first occurrence of the specified element in 
	 * the {@code array}, or -1 if the {@code array} does not contain the element. 
	 * 
	 * @param array the array to search.
	 * @param element the element to search for.
	 * @return the inde of the first occurrence of the specified element in the
	 *         given {@code array}, of -1 if the {@code array} does not contain
	 *         the element.
	 * @throws NullPointerException if the given {@code array} is {@code null}.
	 */
	public static int indexOf(final Object[] array, final Object element) {
		Validator.notNull(array, "Array");
		
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
	
	/**
	 * Returns the index of the first occurrence of the specified element in 
	 * the {@code array}, or -1 if the {@code array} does not contain the element. 
	 * 
	 * @param array the array to search.
	 * @param element the element to search for.
	 * @return the inde of the first occurrence of the specified element in the
	 *         given {@code array}, of -1 if the {@code array} does not contain
	 *         the element.
	 * @throws NullPointerException if the given {@code array} is {@code null}.
	 */
	public static <T> int indexOf(final Array<? extends T> array, final T element) {
		Validator.notNull(array, "Array");
		
		return indexOf(array._array, element);
	}
	
	public static <T> int indexOf(final T[] array, final Predicate<? super T> predicate) {
		Validator.notNull(array, "Array");
		Validator.notNull(predicate, "Predicate");
		
		int index = -1;
		
		for (int i = 0; i < array.length && index == -1; ++i) {
			if (predicate.evaluate(array[i])) {
				index = i;
			}
		}
		
		return index;
	}
	
	
	public static <T> int indexOf(final Array<? extends T> array, final Predicate<? super T> predicate) {
		return indexOf(array._array, predicate);
	}
	
}











