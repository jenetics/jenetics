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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.function.Supplier;

import io.jenetics.internal.util.Lazy;

/**
 * Implementation of an <em>ephemeral</em> constant. It causes the insertion of
 * a <em>mutable</em> constant into the operation tree. Every time this terminal
 * is chosen a, different value is generated which is then used for that
 * particular terminal, and which will remain fixed for the given tree. The main
 * usage would be to introduce random terminal values.
 *
 * <pre>{@code
 * final Random random = ...;
 * final Op<Double> val = EphemeralConst.of(random::nextDouble());
 * }</pre>
 *
 *  @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.9
 * @since 3.9
 */
public final class EphemeralConst<T> implements Op<T> {

	private final String _name;
	private final Supplier<T> _supplier;
	private final Lazy<T> _value;

	private EphemeralConst(final String name, final Supplier<T> supplier) {
		_name = name;
		_supplier = requireNonNull(supplier);
		_value = Lazy.of(_supplier);
	}

	@Override
	public String name() {
		return _name;
	}

	@Override
	public int arity() {
		return 0;
	}

	/**
	 * Return a newly created, uninitialized constant of type {@code T}.
	 *
	 * @return a newly created, uninitialized constant of type {@code T}
	 */
	@Override
	public Op<T> get() {
		return new EphemeralConst<>(_name, _supplier);
	}

	@Override
	public T apply(final T[] ts) {
		return _value.get();
	}

	@Override
	public int hashCode() {
		int hash = 17;
		hash += 31*Objects.hashCode(_name) + 37;
		hash += 31*Objects.hashCode(_value.get()) + 37;
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof EphemeralConst<?> &&
			Objects.equals(((EphemeralConst)obj)._name, _name) &&
			Objects.equals(((EphemeralConst)obj)._value.get(), _value.get());
	}

	@Override
	public String toString() {
		return _name != null
			? format("%s(%s)", _name, _value.get())
			: Objects.toString(_value.get());
	}

	/**
	 * Create a new ephemeral constant with the given {@code name} and value
	 * {@code supplier}. For every newly created operation tree, a new constant
	 * value is chosen for this terminal operation. The value is than kept
	 * constant for this tree.
	 *
	 * @param name the name of the ephemeral constant
	 * @param supplier the value supplier
	 * @param <T> the constant type
	 * @return a new ephemeral constant
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <T> EphemeralConst<T> of(
		final String name,
		final Supplier<T> supplier
	) {
		return new EphemeralConst<>(requireNonNull(name), supplier);
	}

	/**
	 * Create a new ephemeral constant with the given value {@code supplier}.
	 * For every newly created operation tree, a new constant value is chosen
	 * for this terminal operation. The value is than kept constant for this tree.
	 *
	 * @param supplier the value supplier
	 * @param <T> the constant type
	 * @return a new ephemeral constant
	 * @throws NullPointerException if the {@code supplier} is {@code null}
	 */
	public static <T> EphemeralConst<T> of(final Supplier<T> supplier) {
		return new EphemeralConst<>(null, supplier);
	}

}
