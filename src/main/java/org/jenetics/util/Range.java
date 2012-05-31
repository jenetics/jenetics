/*
 * Java Genetic Algorithm Library (@!identifier!@).
 * Copyright (c) @!year!@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.util;

import static org.jenetics.util.object.hashCodeOf;
import static org.jenetics.util.object.nonNull;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &ndash; <em>$Revision$</em>
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
		super(nonNull(min, "Min value"), nonNull(max, "Max value"));
		if (min.compareTo(max) >= 0) {
			throw new IllegalArgumentException(String.format(
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
		return hashCodeOf(Range.class).and(super.hashCode()).value();
	}

}
