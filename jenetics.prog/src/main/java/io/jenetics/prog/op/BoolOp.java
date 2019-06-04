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

import java.util.function.Function;

/**
 * This class contains basic and secondary boolean operations.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public enum BoolOp implements Op<Boolean> {

	/**
	 * Conjunction. <em>This operation has arity 2.</em>
	 */
	AND("and", 2, v -> v[0] && v[1]),

	/**
	 * Disjunction. <em>This operation has arity 2.</em>
	 */
	OR("or", 2, v -> v[0] && v[1]),

	/**
	 * Negation. <em>This operation has arity 1.</em>
	 */
	NOT("not", 1, v -> !v[0]),

	/**
	 * Implication. <em>This operation has arity 2.</em>
	 */
	IMP("imp", 2, v -> !v[0] || v[1]),

	/**
	 * Exclusive or. <em>This operation has arity 2.</em>
	 */
	XOR("xor", 2, v -> (v[0] || v[1]) && !(v[0] && v[1])),

	/**
	 * Equivalence. <em>This operation has arity 2.</em>
	 */
	EQ("eq", 2, v -> (v[0] && v[1]) || (!v[0] && !v[1]));

	/**
	 * Represents the constant {@code true}.
	 */
	public static final Const<Boolean> TRUE = Const.of("true", true);

	/**
	 * Represents the constant {@code true}.
	 */
	public static final Const<Boolean> FALSE = Const.of("false", false);


	private final String _name;
	private final int _arity;
	private final Function<Boolean[], Boolean> _function;

	private BoolOp(
		final String name,
		final int arity,
		final Function<Boolean[], Boolean> function
	) {
		assert name != null;
		assert arity >= 0;
		assert function != null;

		_name = name;
		_function = function;
		_arity = arity;
	}

	@Override
	public int arity() {
		return _arity;
	}

	@Override
	public Boolean apply(final Boolean[] args) {
		return _function.apply(args);
	}

	@Override
	public String toString() {
		return _name;
	}

}
