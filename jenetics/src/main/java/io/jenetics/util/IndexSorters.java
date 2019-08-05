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

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class IndexSorters {

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
	static final int INSERTION_SORT_THRESHOLD = 80;

	private IndexSorters() {
	}

	static <T> int[] sort(
		final T array,
		final int length,
		final ProxyComparator<? super T> comp
	) {
		return length < INSERTION_SORT_THRESHOLD
			? InsertionIndexSorter.INSTANCE.sort(array, length, comp)
			: HeapProxySorter.INSTANCE.sort(array, length, comp);
	}

	/**
	 * Create an initial indexes array of the given {@code length}.
	 *
	 * @param length the length of the indexes array
	 * @return the initialized indexes array
	 */
	static int[] indexes(final int length) {
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
	static int[] init(final int[] indexes) {
		for (int i = 0; i < indexes.length; ++i) {
			indexes[i] = i;
		}
		return indexes;
	}

}
