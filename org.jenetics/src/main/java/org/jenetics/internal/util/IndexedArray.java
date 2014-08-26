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
package org.jenetics.internal.util;

import static java.lang.System.arraycopy;
import static org.jenetics.internal.util.IndexSorter.indexes;

import java.util.Arrays;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-08-26 $</em>
 */
public class IndexedArray {

	private final double[] _array;
	private final int[] _indexes;

	public IndexedArray(final double[] array, final int[] indexes) {
		_array = array;
		_indexes = indexes;
	}

	public IndexedArray(final double[] array) {
		this(array, indexes(array.length));
	}

	public double get(final int index) {
		return _array[_indexes[index]];
	}

	public IndexedArray sort() {
		arraycopy(IndexSorter.sort(_array), 0, _indexes, 0, _array.length);
		return this;
	}

	public IndexedArray revert() {
		array.revert(_indexes);
		return this;
	}

	public int[] toIndexArray() {
		return _indexes;
	}

	public double[] toArray() {
		final double[] result = new double[_array.length];
		for (int i = 0; i < result.length; ++i) {
			result[i] = get(i);
		}

		return result;
	}

	public String toIndexString() {
		return Arrays.toString(_indexes);
	}

	@Override
	public String toString() {
		return Arrays.toString(toArray());
	}

}
