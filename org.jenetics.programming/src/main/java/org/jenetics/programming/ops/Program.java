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
package org.jenetics.programming.ops;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.lang.reflect.Array;

import org.jenetix.util.Tree;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class Program<T> implements Op<T> {

	private final String _name;
	private final int _arity;
	private final Tree<? extends Op<T>, ?> _tree;


	public Program(final String name, final Tree<? extends Op<T>, ?> tree) {
		_name = requireNonNull(name);
		_tree = tree;
		_arity = tree.breadthFirstStream()
			.filter(t -> t.getValue() instanceof Var<?>)
			.mapToInt(v -> ((Var<?>)v.getValue()).index() + 1)
			.max()
			.orElse(0);
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
	public T apply(final T[] args) {
		if (args.length < arity() && !isTerminal()) {
			throw new IllegalArgumentException(format(
				"Arguments length is smaller the program arity: %d < %d",
				args.length, arity()
			));
		}

		return eval(_tree, args);
	}

	/**
	 * Evaluates the given operation tree with the given variables.
	 *
	 * @param <T> the argument type
	 * @param tree the operation tree
	 * @param variables the input variables
	 * @return the result of the operation tree evaluation
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	@SafeVarargs
	public static <T> T eval(
		final Tree<? extends Op<T>, ?> tree,
		final T... variables
	) {
		final Op<T> op = tree.getValue();

		@SuppressWarnings("unchecked")
		final T[] args = (T[])Array.newInstance(
			variables.getClass().getComponentType(),
			op.arity()
		);

		for (int i = 0; i < op.arity(); ++i) {
			final Tree<? extends Op<T>, ?> child = tree.getChild(i);

			if (child.getValue() instanceof Var<?>) {
				args[i] = child.getValue().apply(variables);
			} else {
				args[i] = eval(child, variables);
			}
		}

		return op.apply(args);
	}

	@Override
	public String toString() {
		return _name;
	}

}
