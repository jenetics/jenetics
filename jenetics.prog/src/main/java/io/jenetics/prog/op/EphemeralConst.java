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
import static io.jenetics.internal.util.SerialIO.readNullableString;
import static io.jenetics.internal.util.SerialIO.writeNullableString;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serial;
import java.io.Serializable;
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
 * final Op<Double> val = EphemeralConst.of(random::nextDouble);
 * }</pre>
 *
 * <b>Serialization</b>
 * Although the {@code EphemeralConst} class implements the {@link Serializable}
 * interface, the serialization will fail if the <em>const</em> supplier is not
 * <em>serializable</em> as well. This can be achieved by <em>casting</em> the
 * supplier to a {@link Serializable}.
 * <pre>{@code
 * final Random random = new Random();
 * final EphemeralConst<Integer> object = EphemeralConst.of(
 *     "R",
 *     (Supplier<Integer> & Serializable)random::nextInt
 * );
 * }</pre>
 * The serialization of the <em>constant</em> will fail, if the lambda has to
 * capture variables form a <em>non</em>-serializable context (class). In such a
 * case it is advisable to create a dedicated supplier class.
 * <pre>{@code
 * final class RandomInt implements Supplier<Integer>, Serializable {
 *     private final Random rnd = new Random();
 *     private final int min;
 *     private final int max;
 *
 *     private RandomInt(final int min, final int max) {
 *         this.min = min;
 *         this.max = max;
 *     }
 *
 *     \@Override
 *     public Integer get() {
 *         return rnd.nextInt(max - min) + min;
 *     }
 * }
 * }</pre>
 *
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.0
 * @since 3.9
 */
public final class EphemeralConst<T>
	extends Val<T>
	implements Op<T>, Serializable
{

	@Serial
	private static final long serialVersionUID = 1L;

	private final Lazy<T> _value;
	private final Supplier<T> _supplier;

	private EphemeralConst(
		final String name,
		final Lazy<T> value,
		final Supplier<T> supplier
	) {
		super(name);
		_value = requireNonNull(value);
		_supplier = requireNonNull(supplier);
	}

	private EphemeralConst(final String name, final Supplier<T> supplier) {
		this(name, Lazy.of(supplier), supplier);
	}

	/**
	 * Return a newly created, uninitialized constant of type {@code T}.
	 *
	 * @return a newly created, uninitialized constant of type {@code T}
	 */
	@Override
	public Op<T> get() {
		return new EphemeralConst<>(name(), _supplier);
	}

	/**
	 * Fixes and returns the constant value.
	 *
	 * @since 5.0
	 *
	 * @return the constant value
	 */
	@Override
	public T value() {
		return _value.get();
	}

	@Override
	public String toString() {
		return name() != null
			? format("%s(%s)", name(), value())
			: Objects.toString(value());
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


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	@Serial
	private Object writeReplace() {
		return new SerialProxy(SerialProxy.EPHEMERAL_CONST, this);
	}

	@Serial
	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final ObjectOutput out) throws IOException {
		writeNullableString(name(), out);
		out.writeObject(value());
		out.writeObject(_supplier);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	static EphemeralConst read(final ObjectInput in)
		throws IOException, ClassNotFoundException
	{
		final String name = readNullableString(in);
		final Object value = in.readObject();
		final Supplier supplier = (Supplier)in.readObject();

		return new EphemeralConst(name, Lazy.ofValue(value), supplier);
	}

}
