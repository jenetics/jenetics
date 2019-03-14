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

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.TreeNode;

/**
 * Some {@code Op} helper functions.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Ops {
	private Ops() {
	}

	/**
	 * Re-indexes the variables of the given operation {@code tree}. If the
	 * operation tree is created from it's string representation, the indices
	 * of the variables ({@link Var}), are all set to zero, since it needs the
	 * whole tree for setting the indices correctly. The mapping from the node
	 * string to the {@link Op} object, on the other hand, is a <em>local</em>
	 * operation. This method gives you the possibility to fix the indices of
	 * the variables. The indices of the variables are assigned according it's
	 * <em>natural</em> order.
	 *
	 * <pre>{@code
	 * final TreeNode<Op<Double>> tree = TreeNode.parse(
	 *     "add(mul(x,y),sub(y,x))",
	 *     MathOp::toMathOp
	 * );
	 *
	 * assert Program.eval(tree, 10.0, 5.0) == 100.0;
	 * Ops.reindexVars(tree);
	 * assert Program.eval(tree, 10.0, 5.0) == 45.0;
	 * }</pre>
	 * The example above shows a use-case of this method. If you parse a tree
	 * string and convert it to an operation tree, you have to re-index the
	 * variables first. If not, you will get the wrong result when evaluating
	 * the tree. After the re-indexing you will get the correct result of 45.0.
	 *
	 * @see MathOp#toMathOp(String)
	 * @see Program#eval(Tree, Object[])
	 *
	 * @param tree the tree where the variable indices needs to be fixed
	 * @param <V> the operation value type
	 */
	public static <V> void reindexVars(final TreeNode<Op<V>> tree) {
		final SortedSet<Var<V>> vars = tree.stream()
			.filter(node -> node.getValue() instanceof Var)
			.map(node -> (Var<V>)node.getValue())
			.collect(Collectors.toCollection(TreeSet::new));

		int index = 0;
		final Map<Var<V>, Integer> indexes = new HashMap<>();
		for (Var<V> var : vars) {
			indexes.put(var, index++);
		}

		for (TreeNode<Op<V>> node : tree) {
			final Op<V> op = node.getValue();
			if (op instanceof Var) {
				node.setValue(Var.of(op.name(), indexes.get(op)));
			}
		}
	}

}
