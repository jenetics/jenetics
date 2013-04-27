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

import static org.jenetics.util.object.eq;
import static org.jenetics.util.object.hashCodeOf;
import static org.jenetics.util.object.nonNull;

/**
 * Abstract implementation of the {@link Accumulator} interface which defines a
 * {@code samples} property which is incremented by the {@link #accumulate(Object)}
 * method.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date$</em>
 */
public abstract class MappedAccumulator<T>
	implements
		Accumulator<T>,
		Cloneable
{

	/**
	 * The number of accumulated samples.
	 */
	protected long _samples = 0;

	protected MappedAccumulator() {
	}

	/**
	 * Return the number of samples accumulated so far.
	 *
	 * @return the number of samples accumulated so far.
	 */
	public long getSamples() {
		return _samples;
	}

	@Override
	public void accumulate(final T value) {
		++_samples;
	}

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
	public <B> MappedAccumulator<B> map(final Function<? super B, ? extends T> mapper) {
		nonNull(mapper, "Mapper");
		return new MappedAccumulator<B>() {
			@Override
			public void accumulate(final B value) {
				MappedAccumulator.this.accumulate(mapper.apply(value));
			}
		};
	}

	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).and(_samples).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		final MappedAccumulator<?> accumulator = (MappedAccumulator<?>)obj;
		return eq(_samples, accumulator._samples);
	}

	@Override
	public String toString() {
		return String.format(
				"%s[samples=%d]", getClass().getName(), _samples
			);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected MappedAccumulator<T> clone() {
		try {
			return (MappedAccumulator<T>)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}

}
