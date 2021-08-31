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
package io.jenetics.prog.op;

import static java.util.Objects.requireNonNull;
import static io.jenetics.internal.util.Hashes.hash;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.1
 * @since 3.9
 */
final class Operation<T> implements Op<T>, Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private final String _name;
	private final int _arity;
	private final Function<T[], T> _function;

	Operation(
		final String name,
		final int arity,
		final Function<T[], T> function
	) {
		_name = requireNonNull(name);
		_function = requireNonNull(function);
		if (arity < 0) {
			throw new IllegalArgumentException(
				"Arity smaller than zero: " + arity
			);
		}

		_arity = arity;
	}

	@Override
	public String name() {
		return _name;
	}

	@Override
	public int arity() {
		return _arity;
	}

	@Override
	public T apply(final T[] values) {
		return _function.apply(values);
	}

	@Override
	public int hashCode() {
		return hash(_name, hash(_arity));
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof Operation<?> other &&
			Objects.equals(other._name, _name) &&
			other._arity == _arity;
	}

	@Override
	public String toString() {
		return _name;
	}

}
