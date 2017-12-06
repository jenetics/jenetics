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
package io.jenetics.ext.util;

/**
 * This interface imposes a total ordering on the component objects of each
 * class that implements it. This ordering is referred to as the components's
 * natural ordering, and the components's {@code compareTo} method is referred
 * to as its natural comparison method.
 *
 * @param <T> the type of objects which holds the comparable components
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface ComponentComparable<T> {

	/**
	 * Return the number of components.
	 *
	 * @return the number of components
	 */
	public int size();

	/**
	 * Compares the {@code this} vector with the {@code other} at the given
	 * component {@code index}.
	 *
	 * @param other the other vector
	 * @param index the component index
	 * @return a negative integer, zero, or a positive integer as
	 *        {@code this[index]} is less than, equal to, or greater than
	 *        {@code other[index]}
	 * @throws NullPointerException if the {@code other} object is {@code null}
	 */
	public int compareTo(final T other, final int index);

}
