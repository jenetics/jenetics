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

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents the program variables. The {@code Var} operation is a termination
 * operation, which just returns the value with the defined index of the input
 * variable array. It is essentially an orthogonal projection of the
 * <em>n</em>-dimensional input space to the <em>1</em>-dimensional result space.
 *
 * <pre>{@code
 * final ISeq<? extends Op<Double>> operations = ISeq.of(...);
 * final ISeq<? extends Op<Double>> terminals = ISeq.of(
 *     Var.of("x", 0), Var.of("y", 1)
 * );
 * }</pre>
 *
 * The example above shows how to define the terminal operations for a GP, which
 * tries to optimize a 2-dimensional function.
 *
 * <pre>{@code
 * static double error(final ProgramChromosome<Double> program) {
 *     final double x = ...;
 *     final double y = ...;
 *     final double result = program.apply(x, y);
 *     ...
 *
 *     return ...;
 * }
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since 3.9
 */
public final class Var<T> implements Op<T>, Serializable {

	private static final long serialVersionUID = 1L;

	private final String _name;
	private final int _index;

	/**
	 * Create a new variable with the given {@code name} and projection
	 * {@code index}.
	 *
	 * @param name the variable name. Used when printing the operation tree
	 *        (program)
	 * @param index the projection index
	 * @throws IllegalArgumentException if the projection {@code index} is
	 *         smaller than zero
	 * @throws NullPointerException if the given variable {@code name} is
	 *         {@code null}
	 */
	private Var(final String name, final int index) {
		_name = requireNonNull(name);
		if (index < 0) {
			throw new IndexOutOfBoundsException(
				"Index smaller than zero: " + index
			);
		}
		_index = index;
	}

	/**
	 * The projection index of the variable.
	 *
	 * @return the projection index of the variable
	 */
	public int index() {
		return _index;
	}

	@Override
	public String name() {
		return _name;
	}

	@Override
	public int arity() {
		return 0;
	}

	@Override
	public T apply(final T[] variables) {
		return variables[_index];
	}

	@Override
	public int hashCode() {
		int hash = 17;
		hash += 31*Objects.hashCode(_name) + 37;
		hash += 31*_index + 37;
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof Var<?> &&
			Objects.equals(((Var)obj)._name, _name) &&
			((Var)obj)._index == _index;
	}

	@Override
	public String toString() {
		return _name;
	}

	/**
	 * Create a new variable with the given {@code name} and projection
	 * {@code index}.
	 *
	 * @param name the variable name. Used when printing the operation tree
	 *        (program)
	 * @param index the projection index
	 * @param <T> the variable type
	 * @return a new variable with the given {@code name} and projection
	 *         {@code index}
	 */
	public static <T> Var<T> of(final String name, final int index) {
		return new Var<>(name, index);
	}

}
