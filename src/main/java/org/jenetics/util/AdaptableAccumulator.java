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

/**
 * This <i>mixin</i> allows an adapter to return a view of itself with an other
 * type.
 * </p>
 * 
 * Usage example:
 * [code]
 *     // Convert a string on the fly into a double value.
 *     final Converter<String, Double> converter = new Converter<String, Double>() {
 *             public Double convert(final String value) {
 *                 return Double.valueOf(value);
 *             }
 *         };
 *     
 *     // The values to accumulate
 *     final List<String> values = Arrays.asList("0", "1", "2", "3", "4", "5");
 *     
 *     // Min accumulator extends the AbstractAccumulator
 *     final Accumulators.Min<Double> accumulator = new Accumulators.Min<Double>();
 *     
 *     // No pain to accumulate collections of a different type.
 *     Accumulators.accumulate(values, accumulator.adapt(converter));
 * [/code]
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public abstract class AdaptableAccumulator<A> extends AbstractAccumulator<A> {
	
	protected AdaptableAccumulator() {
	}
	
	/**
	 * Return a view of this adapter with a different type {@code B}.
	 * 
	 * @param <B> the type of the returned adapter (view).
	 * @param converter the converter needed to map between the type of this
	 *        adapter and the adapter view type.
	 * @return the adapter view with the different type.
	 * @throws NullPointerException if the given {@code converter} is {@code null}.
	 */
	public <B> Accumulator<B> adapt(final Converter<B, A> converter) {
		return new AccumulatorAdapter<A, B>(this, converter);
	}
}
