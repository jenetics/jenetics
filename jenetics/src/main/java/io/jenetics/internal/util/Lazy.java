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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.internal.util;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Class for lazy value initialization.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 5.0
 */
public final class Lazy<T> implements Supplier<T>, Serializable {

	@Serial
	private static final long serialVersionUID = 2L;

	private final transient Supplier<T> _supplier;

	private T _value;
	private transient volatile boolean _evaluated;

	private Lazy(
		final T value,
		final boolean evaluated,
		final Supplier<T> supplier
	) {
		_value = value;
		_evaluated = evaluated;
		_supplier = supplier;
	}

	private Lazy(final Supplier<T> supplier) {
		this(null, false, requireNonNull(supplier));
	}

	@Override
	public T get() {
		return _evaluated ? _value : evaluate();
	}

	/**
	 * Return the evaluation state of the {@code Lazy} variable.
	 *
	 * @return {@code true} is the {@code Lazy} variable has been evaluated,
	 *         {@code false} otherwise
	 */
	public boolean isEvaluated() {
		return _supplier == null || _evaluated || _evaluated();
	}

	private synchronized boolean _evaluated() {
		return _evaluated;
	}

	private synchronized T evaluate() {
		if (!_evaluated) {
			_value = _supplier.get();
			_evaluated = true;
		}

		return _value;
	}

    @Override
    public int hashCode() {
        return Objects.hashCode(get());
    }

    @Override
    public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof Lazy<?> other &&
			Objects.equals(other.get(), get());
    }

    @Override
    public String toString() {
		return format("Lazy[%s]", isEvaluated() ? get() : "?");
    }

	/**
	 * Create a new lazy value initialization.
	 *
	 * @param supplier the lazy value supplier
	 * @param <T> the value type
	 * @return a new lazy value initialization
	 * @throws java.lang.NullPointerException if the given supplier is
	 *         {@code null}
	 */
	public static <T> Lazy<T> of(final Supplier<T> supplier) {
		return new Lazy<>(supplier);
	}

	/**
	 * Create a new {@code Lazy} object with the given {@code value}. This
	 * method allows creating a <em>lazy</em> object with the given
	 * {@code value}.
	 *
	 * @since 3.7
	 *
	 * @param value the value this {@code Lazy} object is initialized with
	 * @param <T> the value type
	 * @return return a new lazy value with the given value
	 */
	public static <T> Lazy<T> ofValue(final T value) {
		return new Lazy<>(value, true, null);
	}


	/**************************************************************************
	 *  Java object serialization
	 *************************************************************************/

	static final class SerialProxy implements Externalizable  {

		@Serial
		private static final long serialVersionUID = 1L;

		private Lazy<?> _object;

		public SerialProxy() {
		}

		SerialProxy(final Lazy<?> object) {
			_object = object;
		}

		@Serial
		private Object readResolve() {
			return _object;
		}

		@Override
		public void writeExternal(final ObjectOutput out) throws IOException {
			out.writeObject(_object.get());
		}

		@Override
		public void readExternal(ObjectInput in)
			throws IOException, ClassNotFoundException
		{
			_object = Lazy.ofValue(in.readObject());
		}
	}

	@Serial
	private Object writeReplace() {
		return new SerialProxy(this);
	}

	@Serial
	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

}
