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

import java.io.Serializable;

import javolution.lang.Reference;

/**
 * A final {@link Reference}. This class is used if you want to allow to set the
 * value of a {@link Reference} only once. If you try to set the references 
 * value twice an {@link IllegalStateException} is thrown.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: FinalReference.java 330 2010-02-16 12:48:21Z fwilhelm $
 */
public final class FinalReference<T> implements Reference<T>, Serializable {
	private static final long serialVersionUID = -7316538710472411065L;
	
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
	public boolean isFinal() {
		return _initialized;
	}
	
	/**
	 * Set the reference value. If you try to set the reference value twice an
	 * {@link IllegalStateException} is thrown.
	 * 
	 * @throws IllegalStateException if you try to set the reference value twice.
	 */
	@Override
	public void set(final T value) {
		if (_initialized) {
			throw new IllegalStateException("Value is already initialized.");
		}
		_value = value;
		_initialized = true;
	}
	
	@Override
	public T get() {
		return _value;
	}
	
	@Override
	public int hashCode() {
		return _value != null ? _value.hashCode() : 0;
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
		return f._value != null ? f._value.equals(_value) : _value == null;
	}
	
	@Override
	public String toString() {
		return _value != null ? _value.toString() : "null";
	}
	
}




