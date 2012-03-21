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

import static org.jenetics.util.object.eq;
import static org.jenetics.util.object.hashCodeOf;
import static org.jenetics.util.object.str;

import java.io.Serializable;

import javolution.lang.Reference;

/**
 * A final {@link Reference}. This class is used if you want to allow to set the
 * value of a {@link Reference} only once. If you try to set the references
 * value twice an {@link IllegalStateException} is thrown.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version $Id$
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
		return str(get());
	}

}




