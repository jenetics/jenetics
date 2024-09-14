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
package io.jenetics.incubator.combinatorial;

/**
 * This interface allows iterating over subset elements. The following example
 * shows how to iterate over the indexes using a cursor.
 * {@snippet class="CombinatorialSnippets" region="Cursor.loop"}
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.1
 * @since 8.1
 */
public interface Cursor {

	/**
	 * Writes the current value to the given {@code index} array and moves the
	 * cursor forward.
	 * {@snippet class="CombinatorialSnippets" region="Cursor.loop"}
	 *
	 * @param index the {@code int[]} array where the index values are written
	 *        to
	 * @return {@code true} if an index value has been written to the output
	 *         {@code index} array
	 * @throws ArrayIndexOutOfBoundsException if the given {@code index} is too
	 *         small to hold the next subset
	 * @throws NullPointerException if the given parameter is {@code null}
	 */
	boolean next(int[] index);

	/**
	 * Return the next index value or {@code null} if no further value is
	 * available.
	 * {@snippet class="CombinatorialSnippets" region="Cursor.loop2"}
	 *
	 * @return the next element or {@code null} if no further value is available
	 */
	default int[] next() {
		final var index = new int[size()];
		return next(index) ? index : null;
	}

	/**
	 * Return the length of the <em>index</em> array {@code this} cursor works
	 * with.
	 *
	 * @return the index array length
	 */
	int size();

}
