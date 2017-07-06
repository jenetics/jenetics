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
package org.jenetics;

/**
 * Base interface for genes where the alleles are bound by a minimum and a
 * maximum value.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.6
 * @version 3.0
 */
public interface BoundedGene<
	A extends Comparable<? super A>,
	G extends BoundedGene<A, G>
>
	extends Gene<A, G>, Comparable<G>
{

	/**
	 * Return the allowed min value.
	 *
	 * @return The allowed min value.
	 */
	public A getMin();

	/**
	 * Return the allowed max value.
	 *
	 * @return The allowed max value.
	 */
	public A getMax();

	@Override
	public default boolean isValid() {
		return
			getAllele().compareTo(getMin()) >= 0 &&
			getAllele().compareTo(getMax()) <= 0;
	}

	@Override
	public default int compareTo(final G other) {
		return getAllele().compareTo(other.getAllele());
	}

	/**
	 * Create a new gene from the given {@code value} and the current bounds.
	 *
	 * @param value the value of the new gene.
	 * @return a new gene with the given value.
	 */
	public G newInstance(final A value);

}
