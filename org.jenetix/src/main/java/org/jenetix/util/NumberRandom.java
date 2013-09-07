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
package org.jenetix.util;


/**
 * This interface defines a random engine for (comparable) number types.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since @__new_version__@
 * @version @__new_version__@ &mdash; <em>$Date: 2013-05-22 $</em>
 */
public interface NumberRandom<N extends Comparable<? super N>> {

	/**
	 * Return a new random number, of type {@code N}, within the given range.
	 *
	 * @param min the minimal value of the random number (inclusively).
	 * @param max the maximum value of the random number (inclusively for
	 *        <i>integer</i> types and exclusively for <i>real</i> types).
	 * @return a new random number, of type {@code N}, within the given range.
	 * @throws IllegalArgumentException if {@code min > max}.
	 */
	public N next(final N min, final N max);

}