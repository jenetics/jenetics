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
package io.jenetics.util;

import static io.jenetics.internal.util.Arrays.swap;

import java.util.Comparator;

/**
 * OLD 'ProxySorter' implementation.
 *
 * Implementations of this class doesn't sort the given array directly, instead
 * an index lookup array is returned which allows to access the array in
 * an sorted order. The arrays are sorted in descending order.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0
 */
public abstract class IndexSorter {

	// This value has been chosen after JMH benchmarking.
	//	Benchmark                                   Mode  Samples      Score  Score error  Units
	//	o.j.i.u.IndexSorterPerf.heapSort160         avgt       14   5560.895       80.158  ns/op
	//	o.j.i.u.IndexSorterPerf.heapSort250         avgt       14   9516.441      119.648  ns/op
	//	o.j.i.u.IndexSorterPerf.heapSort320         avgt       14  12722.461      103.487  ns/op
	//	o.j.i.u.IndexSorterPerf.heapSort80          avgt       14   2473.058       27.884  ns/op
	//	o.j.i.u.IndexSorterPerf.insertionSort160    avgt       14  10877.158      550.338  ns/op
	//	o.j.i.u.IndexSorterPerf.insertionSort250    avgt       14  25731.100      925.196  ns/op
	//	o.j.i.u.IndexSorterPerf.insertionSort320    avgt       14  41864.108     1801.247  ns/op
	//	o.j.i.u.IndexSorterPerf.insertionSort80     avgt       14   2643.726      165.315  ns/op
	//private static final int INSERTION_SORT_THRESHOLD = 80;
	private static final int INSERTION_SORT_THRESHOLD = 80;

	/**
	 * Sorting the given {@code array} by changing the given {@code indexes}.
	 * The order of the original {@code array} stays unchanged.
	 *
	 * @param array the array to sort
	 * @param indexes the index lookup array -
	 *        &forall; i &isin; [0, N): index[i] = i
	 * @param comparator the comparator used for sorting the array
	 * @param <T> the element type
	 * @return the given {@code indexes} which is now "sorted"
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public abstract <T> int[] sort(
		final Seq<? extends T> array,
		final int[] indexes,
		final Comparator<? super T> comparator
	);

	/**
	 * Sorting the given {@code array} by changing the given {@code indexes}.
	 * The order of the original {@code array} stays unchanged.
	 *
	 * @param array the array to sort
	 * @param indexes the index lookup array -
	 *        &forall; i &isin; [0, N): index[i] = i
	 * @param <C> the element type
	 * @return the given {@code indexes} which is now "sorted"
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public <C extends Comparable<? super C>> int[] sort(
		final Seq<? extends C> array,
		final int[] indexes
	) {
		return sort(array, indexes, Comparator.naturalOrder());
	}

	/**
	 * Sorting the given {@code array} by changing the given {@code indexes}.
	 * The order of the original {@code array} stays unchanged.
	 *
	 * @param array the array to sort
	 * @param indexes the index lookup array -
	 *        &forall; i &isin; [0, N): index[i] = i
	 * @param comparator the comparator used for sorting the array
	 * @param <T> the element type
	 * @return the given {@code indexes} which is now "sorted"
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public abstract <T> int[] sort(
		final T[] array,
		final int[] indexes,
		final Comparator<? super T> comparator
	);

	/**
	 * Sorting the given {@code array} by changing the given {@code indexes}.
	 * The order of the original {@code array} stays unchanged.
	 *
	 * @param array the array to sort
	 * @param indexes the index lookup array -
	 *        &forall; i &isin; [0, N): index[i] = i
	 * @param <C> the element type
	 * @return the given {@code indexes} which is now "sorted"
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public <C extends Comparable<? super C>> int[] sort(
		final C[] array,
		final int[] indexes
	) {
		return sort(array, indexes, Comparator.naturalOrder());
	}

	/**
	 * Sorting the given {@code array} by changing the given {@code indexes}.
	 * The order of the original {@code array} stays unchanged.
	 *
	 * @param array the array to sort
	 * @param indexes the index lookup array -
	 *        &forall; i &isin; [0, N): index[i] = i
	 * @param comparator the comparator used for comparing two int values
	 * @return the given {@code indexes} which is now "sorted"
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public abstract int[] sort(
		final int[] array,
		final int[] indexes,
		final IntComparator comparator
	);

	/**
	 * Sorting the given {@code array} by changing the given {@code indexes}.
	 * The order of the original {@code array} stays unchanged.
	 *
	 * @param array the array to sort
	 * @param indexes the index lookup array -
	 *        &forall; i &isin; [0, N): index[i] = i
	 * @return the given {@code indexes} which is now "sorted"
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public int[] sort(final int[] array, final int[] indexes) {
		return sort(array, indexes, Integer::compare);
	}

	/**
	 * Sorting the given {@code array} by changing the given {@code indexes}.
	 * The order of the original {@code array} stays unchanged.
	 *
	 * @param array the array to sort
	 * @param indexes the index lookup array -
	 *        &forall; i &isin; [0, N): index[i] = i
	 * @return the given {@code indexes} which is now "sorted"
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public abstract int[] sort(final long[] array, final int[] indexes);

	/**
	 * Sorting the given {@code array} by changing the given {@code indexes}.
	 * The order of the original {@code array} stays unchanged.
	 *
	 * @param array the array to sort
	 * @param indexes the index lookup array -
	 *        &forall; i &isin; [0, N): index[i] = i
	 * @return the given {@code indexes} which is now "sorted"
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public abstract int[] sort(final double[] array, final int[] indexes);


	/* *************************************************************************
	 * Static sorting methods.
	 * ************************************************************************/

	/**
	 * Return an {@code IndexSorter} suitable for the given array length.
	 *
	 * @param length the array length
	 * @return the suitable {@code IndexSorter}
	 */
	public static IndexSorter sorter(final int length) {
		return length < INSERTION_SORT_THRESHOLD
			? InsertionSorter.INSTANCE
			: HeapSorter.INSTANCE;
	}

	/**
	 * Sorting the given {@code array} by changing the given {@code indexes}.
	 * The order of the original {@code array} stays unchanged.
	 *
	 * @param array the array to sort
	 * @param comparator the comparator used for sorting the array
	 * @param <T> the element type
	 * @return the given {@code indexes} which is now "sorted"
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <T> int[] sort(
		final Seq<? extends T> array,
		final Comparator<? super T> comparator
	) {
		final IndexSorter sorter = array.size() < INSERTION_SORT_THRESHOLD
			? InsertionSorter.INSTANCE
			: HeapSorter.INSTANCE;

		return sorter.sort(array, indexes(array.size()), comparator);
	}

	/**
	 * Sorting the given {@code array} by changing the given {@code indexes}.
	 * The order of the original {@code array} stays unchanged.
	 *
	 * @param array the array to sort
	 * @param <C> the element type
	 * @return the given {@code indexes} which is now "sorted"
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <C extends Comparable<? super C>> int[]
	sort(final Seq<? extends C> array) {
		final IndexSorter sorter = array.size() < INSERTION_SORT_THRESHOLD
			? InsertionSorter.INSTANCE
			: HeapSorter.INSTANCE;

		return sorter.sort(array, indexes(array.size()));
	}

	/**
	 * Sorting the given {@code array} by changing the given {@code indexes}.
	 * The order of the original {@code array} stays unchanged.
	 *
	 * @param array the array to sort
	 * @param comparator the comparator used for sorting the array
	 * @param <T> the element type
	 * @return the given {@code indexes} which is now "sorted"
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <T> int[] sort(
		final T[] array,
		final Comparator<? super T> comparator
	) {
		final IndexSorter sorter = array.length < INSERTION_SORT_THRESHOLD
			? InsertionSorter.INSTANCE
			: HeapSorter.INSTANCE;

		return sorter.sort(array, indexes(array.length), comparator);
	}

	/**
	 * Sorting the given {@code array} by changing the given {@code indexes}.
	 * The order of the original {@code array} stays unchanged.
	 *
	 * @param array the array to sort
	 * @param <C> the element type
	 * @return the given {@code indexes} which is now "sorted"
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <C extends Comparable<? super C>> int[]
	sort(final C[] array) {
		final IndexSorter sorter = array.length < INSERTION_SORT_THRESHOLD
			? InsertionSorter.INSTANCE
			: HeapSorter.INSTANCE;

		return sorter.sort(array, indexes(array.length));
	}

	/**
	 * Return an new sorted index lookup array. The given array is not touched.
	 *
	 * @param array the array to sort.
	 * @return the index lookup array
	 */
	public static int[] sort(final int[] array) {
		final IndexSorter sorter = array.length < INSERTION_SORT_THRESHOLD
			? InsertionSorter.INSTANCE
			: HeapSorter.INSTANCE;

		return sorter.sort(array, indexes(array.length));
	}

	/**
	 * Return an new sorted index lookup array. The given array is not touched.
	 *
	 * @param array the array to sort.
	 * @return the index lookup array
	 */
	public static int[] sort(final long[] array) {
		final IndexSorter sorter = array.length < INSERTION_SORT_THRESHOLD
			? InsertionSorter.INSTANCE
			: HeapSorter.INSTANCE;

		return sorter.sort(array, indexes(array.length));
	}

	/**
	 * Return an new sorted index lookup array. The given array is not touched.
	 *
	 * @param array the array to sort.
	 * @return the index lookup array
	 */
	public static int[] sort(final double[] array) {
		final IndexSorter sorter = array.length < INSERTION_SORT_THRESHOLD
			? InsertionSorter.INSTANCE
			: HeapSorter.INSTANCE;

		return sorter.sort(array, indexes(array.length));
	}

	/**
	 * Create an initial indexes array of the given {@code length}.
	 *
	 * @param length the length of the indexes array
	 * @return the initialized indexes array
	 */
	public static int[] indexes(final int length) {
		return init(new int[length]);
	}

	/**
	 * Initializes the given {@code indexes} array.
	 *
	 * @param indexes the indexes array to initialize
	 * @return the initialized indexes array
	 * @throws NullPointerException if the given {@code indexes} array is
	 *         {@code null}
	 */
	public static int[] init(final int[] indexes) {
		for (int i = 0; i < indexes.length; ++i) {
			indexes[i] = i;
		}
		return indexes;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}

/**
 * Heap sort implementation.
 */
final class HeapSorter extends IndexSorter {

	static final HeapSorter INSTANCE = new HeapSorter();

	@Override
	public <T> int[] sort(
		final Seq<? extends T> array,
		final int[] indexes,
		final Comparator<? super T> comparator
	) {
		// Heapify
		for (int k = array.size()/2; k >= 0; --k) {
			sink(array, indexes, comparator, k, array.size());
		}

		// Sort down.
		for (int i = array.size(); --i >= 1;) {
			swap(indexes, 0, i);
			sink(array, indexes, comparator, 0, i);
		}

		return indexes;
	}

	private static <T> void sink(
		final Seq<? extends T> array,
		final int[] indexes,
		final Comparator<? super T> comparator,
		final int start,
		final int end
	) {
		int m = start;
		while (2*m < end) {
			int j = 2*m;
			if (j < end - 1 && comparator.compare(array.get(indexes[j]), array.get(indexes[j + 1])) > 0) ++j;
			if (comparator.compare(array.get(indexes[m]), array.get(indexes[j])) <= 0) break;
			swap(indexes, m, j);
			m = j;
		}
	}

	@Override
	public <T> int[] sort(
		final T[] array,
		final int[] indexes,
		final Comparator<? super T> comparator
	) {
		// Heapify
		for (int k = array.length/2; k >= 0; --k) {
			sink(array, indexes, comparator, k, array.length);
		}

		// Sort down.
		for (int i = array.length; --i >= 1;) {
			swap(indexes, 0, i);
			sink(array, indexes, comparator, 0, i);
		}

		return indexes;
	}

	private static <T> void sink(
		final T[] array,
		final int[] indexes,
		final Comparator<? super T> comparator,
		final int start,
		final int end
	) {
		int m = start;
		while (2*m < end) {
			int j = 2*m;
			if (j < end - 1 && comparator.compare(array[indexes[j]], array[indexes[j + 1]]) > 0) ++j;
			if (comparator.compare(array[indexes[m]], array[indexes[j]]) <= 0) break;
			swap(indexes, m, j);
			m = j;
		}
	}

	@Override
	public int[] sort(
		final int[] array,
		final int[] indexes,
		final IntComparator comparator
	) {
		// Heapify
		for (int k = array.length/2; k >= 0; --k) {
			sink(array, indexes, comparator, k, array.length);
		}

		// Sort down.
		for (int i = array.length; --i >= 1;) {
			swap(indexes, 0, i);
			sink(array, indexes, comparator, 0, i);
		}

		return indexes;
	}

	private static void sink(
		final int[] array,
		final int[] indexes,
		final IntComparator comparator,
		final int start,
		final int end
	) {
		int m = start;
		while (2*m < end) {
			int j = 2*m;
			if (j < end - 1 &&
				comparator.compare(array[indexes[j]], array[indexes[j + 1]]) > 0)
			{
				++j;
			}
			if (comparator.compare(array[indexes[m]], array[indexes[j]]) <= 0) {
				break;
			}
			swap(indexes, m, j);
			m = j;
		}
	}

	@Override
	public int[] sort(final long[] array, final int[] indexes) {
		// Heapify
		for (int k = array.length/2; k >= 0; --k) {
			sink(array, indexes, k, array.length);
		}

		// Sort down.
		for (int i = array.length; --i >= 1;) {
			swap(indexes, 0, i);
			sink(array, indexes, 0, i);
		}

		return indexes;
	}

	private static void sink(
		final long[] array,
		final int[] indexes,
		final int start,
		final int end
	) {
		int m = start;
		while (2*m < end) {
			int j = 2*m;
			if (j < end - 1 && array[indexes[j]] > array[indexes[j + 1]]) ++j;
			if (array[indexes[m]] <= array[indexes[j]]) break;
			swap(indexes, m, j);
			m = j;
		}
	}

	@Override
	public int[] sort(final double[] array, final int[] indexes) {
		// Heapify
		for (int k = array.length/2; k >= 0; --k) {
			sink(array, indexes, k, array.length);
		}

		// Sort down.
		for (int i = array.length; --i >= 1;) {
			swap(indexes, 0, i);
			sink(array, indexes, 0, i);
		}

		return indexes;
	}

	private static void sink(
		final double[] array,
		final int[] indexes,
		final int start,
		final int end
	) {
		int m = start;
		while (2*m < end) {
			int j = 2*m;
			if (j < end - 1 && array[indexes[j]] > array[indexes[j + 1]]) ++j;
			if (array[indexes[m]] <= array[indexes[j]]) break;
			swap(indexes, m, j);
			m = j;
		}
	}

}

/**
 * Insertion sort implementation.
 */
final class InsertionSorter extends IndexSorter {

	static final InsertionSorter INSTANCE = new InsertionSorter();

	@Override
	public <T> int[] sort(
		final Seq<? extends  T> array,
		final int[] indexes,
		final Comparator<? super T> comparator
	) {
		for (int i = 1, n = array.size(); i < n; ++i) {
			int j = i;
			while (j > 0) {
				if (comparator.compare(array.get(indexes[j - 1]), array.get(indexes[j])) < 0) {
					swap(indexes, j - 1, j);
				} else {
					break;
				}
				--j;
			}
		}

		return indexes;
	}

	@Override
	public <T> int[] sort(
		final T[] array,
		final int[] indexes,
		final Comparator<? super T> comparator
	) {
		for (int i = 1, n = array.length; i < n; ++i) {
			int j = i;
			while (j > 0) {
				if (comparator.compare(array[indexes[j - 1]], array[indexes[j]]) < 0) {
					swap(indexes, j - 1, j);
				} else {
					break;
				}
				--j;
			}
		}

		return indexes;
	}

	@Override
	public int[] sort(
		final int[] array,
		final int[] indexes,
		final IntComparator comparator
	) {
		for (int i = 1, n = array.length; i < n; ++i) {
			int j = i;
			while (j > 0) {
				if (comparator.compare(array[indexes[j - 1]], array[indexes[j]]) < 0) {
					swap(indexes, j - 1, j);
				} else {
					break;
				}
				--j;
			}
		}

		return indexes;
	}

	@Override
	public int[] sort(final long[] array, final int[] indexes) {
		for (int i = 1, n = array.length; i < n; ++i) {
			int j = i;
			while (j > 0) {
				if (array[indexes[j - 1]] < array[indexes[j]]) {
					swap(indexes, j - 1, j);
				} else {
					break;
				}
				--j;
			}
		}

		return indexes;
	}

	@Override
	public int[] sort(final double[] array, final int[] indexes) {
		for (int i = 1, n = array.length; i < n; ++i) {
			int j = i;
			while (j > 0) {
				if (array[indexes[j - 1]] < array[indexes[j]]) {
					swap(indexes, j - 1, j);
				} else {
					break;
				}
				--j;
			}
		}

		return indexes;
	}

}

@FunctionalInterface
interface IntComparator {

	/**
	 * Compares its two arguments for order. Returns a negative integer, zero,
	 * or a positive integer as the first argument is less than, equal to, or
	 * greater than the second.
	 *
	 * @param i the first integer
	 * @param j the second integer
	 * @return a negative integer, zero, or a positive integer as the first
	 *         argument is less than, equal to, or greater than the second
	 */
	int compare(final int i, final int j);

}
