/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.util;

import java.util.function.Function;

/**
 * Interface for accumulating values of a given type. Here an usage example:
 *
 * [code]
 * final MinMax<Double> minMax = new MinMax<>();
 * final Variance<Double> variance = new Variance<>();
 * final Quantile<Double> quantile = new Quantile<>();
 *
 * final List<Double> values = ...;
 * accumulators.accumulate(values, minMax, variance, quantile);
 * [/code]
 *
 * @see accumulators
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date: 2013-02-13 $</em>
 */
public interface Accumulator<T> {

	/**
	 * Accumulate the given value.
	 *
	 * @param value the value to accumulate.
	 */
	public void accumulate(final T value);

	/**
	 * Return a view of this adapter with a different type {@code B}.
	 *
	 * Usage example:
	 * [code]
	 * // Convert a string on the fly into a double value.
	 * final Converter<String, Double> converter = new Converter<String, Double>() {
	 *         public Double convert(final String value) {
	 *             return Double.valueOf(value);
	 *         }
	 *     };
	 *
	 * // The values to accumulate
	 * final List<String> values = Arrays.asList("0", "1", "2", "3", "4", "5");
	 *
	 * final Accumulators.Min<Double> accumulator = new Accumulators.Min<Double>();
	 *
	 * // No pain to accumulate collections of a different type.
	 * Accumulators.accumulate(values, accumulator.map(converter));
	 * [/code]
	 *
	 * @param <B> the type of the returned adapter (view).
	 * @param mapper the mapper needed to map between the type of this
	 *        adapter and the adapter view type.
	 * @return the adapter view with the different type.
	 * @throws NullPointerException if the given {@code converter} is {@code null}.
	 */
	public default <B> Accumulator<B> map(final Function<? super B, ? extends T> mapper) {
		return value -> accumulate(mapper.apply(value));
	}

}
