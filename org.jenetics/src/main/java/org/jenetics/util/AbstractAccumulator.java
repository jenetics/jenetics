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
import static org.jenetics.util.object.eq;
import static org.jenetics.util.object.hashCodeOf;

/**
 * Abstract implementation of the {@link Accumulator} interface which defines a
 * {@code samples} property which is incremented by the {@link #accumulate(Object)}
 * method.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date$</em>
 */
public abstract class AbstractAccumulator<T>
	implements
		Accumulator<T>,
		Cloneable
{

	/**
	 * The number of accumulated samples.
	 */
	protected long _samples = 0;

	protected AbstractAccumulator() {
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

		final AbstractAccumulator<?> accumulator = (AbstractAccumulator<?>)obj;
		return eq(_samples, accumulator._samples);
	}

	@Override
	public String toString() {
		return format(
				"%s[samples=%d]", getClass().getName(), _samples
			);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected AbstractAccumulator<T> clone() {
		try {
			return (AbstractAccumulator<T>)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}

}
