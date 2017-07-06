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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import org.jenetics.internal.util.Hash;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class Range<C extends Comparable<? super C>> extends Tuple2<C, C> {

	/**
	 * Create a new range object.
	 *
	 * @param min the minimum value of the domain.
	 * @param max the maximum value of the domain.
	 * @throws IllegalArgumentException if {@code min >= max}
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public Range(final C min, final C max) {
		super(requireNonNull(min, "Min value"), requireNonNull(max, "Max value"));
		if (min.compareTo(max) >= 0) {
			throw new IllegalArgumentException(format(
				"Min value must be smaller the max value: [%s, %s]", min, max
			));
		}
	}

	public C getMin() {
		return _1;
	}

	public C getMax() {
		return _2;
	}

	public boolean contains(final C value) {
		return _1.compareTo(value) <= 0 && _2.compareTo(value) >= 0;
	}

	@Override
	public int hashCode() {
		return Hash.of(Range.class).and(super.hashCode()).value();
	}

}
