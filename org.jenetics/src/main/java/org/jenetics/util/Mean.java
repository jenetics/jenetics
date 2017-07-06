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
package org.jenetics.util;

/**
 * A mixin interface for genes which can have a mean value. This mixin is
 * required for the {@link org.jenetics.MeanAlterer}.
 *
 * @see org.jenetics.MeanAlterer
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.6
 */
public interface Mean<T> {

	/**
	 * Return the (usually arithmetic) mean value of {@code this} and
	 * {@code that}. For {@link org.jenetics.NumericGene}s the mean is the
	 * arithmetic mean.
	 *
	 * @param that the second value for calculating the mean.
	 * @return the mean value of {@code this} and {@code that}.
	 * @throws NullPointerException if the argument is {@code null}.
	 */
	public T mean(final T that);

}
