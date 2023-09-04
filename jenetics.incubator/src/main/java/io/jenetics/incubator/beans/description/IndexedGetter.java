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
package io.jenetics.incubator.beans.description;

/**
 * Represents the <em>getter</em> function for <em>indexed</em> objects, e.g.
 * array or {@code List} objects.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.2
 * @since 7.2
 */
@FunctionalInterface
public interface IndexedGetter {

	/**
	 * Return the value from a given <em>indexed</em> {@code object} with the
	 * given {@code index}.
	 *
	 * @param object the <em>indexed</em> object ({@code Object[]} or {@code List}
	 * @param index the array/list index
	 * @return the value at the given {@code index}
	 */
	Object get(final Object object, final int index);

}
