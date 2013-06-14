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

import java.io.Serializable;
import java.util.Objects;

import javolution.lang.Reference;

/**
 * A final {@link Reference}. This class is used if you want to allow to set the
 * value of a {@link Reference} only once. If you try to set the references
 * value twice an {@link IllegalStateException} is thrown.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date: 2013-06-14 $</em>
 */
public final class FinalReference<T> implements Reference<T>, Serializable {
	private static final long serialVersionUID = 1L;

	private T _value = null;
	private boolean _initialized = false;

	/**
	 * Create a new final reference.
	 */
	public FinalReference() {
	}

	/**
	 * Create a new FinalReference with the given default value. The value of
	 * this reference can still be set, that means {@code isFinal() == false}.
	 *
	 * @param devault
	 */
	public FinalReference(final T devault) {
		_value = devault;
	}

	/**
	 * Test whether this {@link Reference} can be set without throwing an
	 * {@link IllegalStateException} or not.
	 *
	 * @return {@code true} if this {@link Reference} can't be set again,
	 *         false otherwise.
	 */
	public synchronized boolean isFinal() {
		return _initialized;
	}

	/**
	 * Set the reference value. If you try to set the reference value twice an
	 * {@link IllegalStateException} is thrown.
	 *
	 * @throws IllegalStateException if you try to set the reference value twice.
	 */
	@Override
	public synchronized void set(final T value) {
		if (_initialized) {
			throw new IllegalStateException("Value is already initialized.");
		}
		_value = value;
		_initialized = true;
	}

	@Override
	public synchronized T get() {
		return _value;
	}

	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).and(get()).value();
	}

	@Override
	public boolean equals(final Object object) {
		if (object == this) {
			return true;
		}
		if (!(object instanceof FinalReference<?>)) {
			return false;
		}

		final FinalReference<?> f = (FinalReference<?>)object;
		return eq(get(), f.get());
	}

	@Override
	public String toString() {
		return Objects.toString(get());
	}

}




