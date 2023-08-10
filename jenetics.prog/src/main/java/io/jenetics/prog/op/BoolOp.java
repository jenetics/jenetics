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

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.TreeNode;

/**
 * This class contains basic and secondary boolean operations.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.0
 * @since 5.0
 */
public enum BoolOp implements Op<Boolean> {

	/**
	 * Conjunction. <em>This operation has arity 2.</em>
	 */
	AND("and", 2, v -> v[0] && v[1]),

	/**
	 * Disjunction. <em>This operation has arity 2.</em>
	 */
	OR("or", 2, v -> v[0] || v[1]),

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
	EQU("equ", 2, v -> (v[0] && v[1]) || (!v[0] && !v[1]));

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

	BoolOp(
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

	/**
	 * Evaluates the operation with the given arguments.
	 *
	 * @see #apply(Boolean[])
	 *
	 * @param args the operation arguments
	 * @return the evaluated operation
	 */
	public boolean eval(final boolean... args) {
		final Boolean[] v = new Boolean[args.length];
		for (int i = 0; i < args.length; ++i) {
			v[i] = args[i];
		}

		return apply(v);
	}

	@Override
	public String toString() {
		return _name;
	}


	/**
	 * Converts the string representation of an operation to the operation
	 * object. It is used for converting the string representation of a tree to
	 * an operation tree. If you use it that way, you should not forget to
	 * re-index the tree variables.
	 *
	 * <pre>{@code
	 * final TreeNode<Op<Boolean>> tree = TreeNode.parse(
	 *     "and(or(x,y),not(y))",
	 *     BoolOp::toBoolOp
	 * );
	 *
	 * assert Program.eval(tree, false, false) == false;
	 * Var.reindex(tree);
	 * assert Program.eval(tree, false, false) == true;
	 * }</pre>
	 *
	 * @since 5.0
	 *
	 * @see Var#reindex(TreeNode)
	 * @see Program#eval(Tree, Object[])
	 *
	 * @param string the string representation of an operation which should be
	 *        converted
	 * @return the operation, converted from the given string
	 * @throws IllegalArgumentException if the given {@code value} doesn't
	 *         represent a mathematical expression
	 * @throws NullPointerException if the given string {@code value} is
	 *         {@code null}
	 */
	public static Op<Boolean> toBoolOp(final String string) {
		requireNonNull(string);

		final Op<Boolean> result;
		final Optional<Const<Boolean>> cop = toConst(string);
		if (cop.isPresent()) {
			result = cop.orElseThrow(AssertionError::new);
		} else {
			final Optional<Op<Boolean>> mop = toOp(string);
			result = mop.isPresent()
				? mop.orElseThrow(AssertionError::new)
				: Var.parse(string);
		}

		return result;
	}

	static Optional<Const<Boolean>> toConst(final String string) {
		return tryParseBoolean(string)
			.map(Const::of);
	}

	private static Optional<Boolean> tryParseBoolean(final String value) {
        return switch (value) {
            case "true", "1" -> Optional.of(true);
            case "false", "0" -> Optional.of(false);
            default -> Optional.empty();
        };
	}

	private static Optional<Op<Boolean>> toOp(final String string) {
		return Stream.of(values())
			.filter(op -> Objects.equals(op._name, string))
			.map(op -> (Op<Boolean>)op)
			.findFirst();
	}

}
