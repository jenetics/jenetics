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
package io.jenetics.prog.op;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.lang.reflect.Array;
import java.util.Random;

import io.jenetics.ext.util.FlatTree;
import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.TreeNode;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;

/**
 * This class composes a given operation tree to a new operation. which can
 * serve as a sub <em>program</em> in an other operation tree.
 *
 * @param <T> the argument type of the operation
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.9
 * @since 3.9
 */
public class Program<T> implements Op<T> {

	private final String _name;
	private final int _arity;
	private final Tree<? extends Op<T>, ?> _tree;

	/**
	 * Create a new program with the given name and the given operation tree.
	 * The arity of the program is calculated from the given operation tree and
	 * set to the maximal arity of the operations of the tree.
	 *
	 * @param name the program name
	 * @param tree the operation tree
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 * @throws IllegalArgumentException if the given operation tree is invalid,
	 *         which means there is at least one node where the operation arity
	 *         and the node child count differ.
	 */
	public Program(final String name, final Tree<? extends Op<T>, ?> tree) {
		_name = requireNonNull(name);
		_tree = requireNonNull(tree);
		check(tree);
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
		if (args.length < arity()) {
			throw new IllegalArgumentException(format(
				"Arguments length is smaller than program arity: %d < %d",
				args.length, arity()
			));
		}

		return eval(_tree, args);
	}

	/**
	 * Convenient method, which lets you apply the program function without
	 * explicitly create a wrapper array.
	 *
	 * @see #apply(Object[])
	 *
	 * @param args the function arguments
	 * @return the evaluated value
	 * @throws NullPointerException if the given variable array is {@code null}
	 * @throws IllegalArgumentException if the length of the arguments array
	 *         is smaller than the program arity
	 */
	@SafeVarargs
	public final T eval(final T... args) {
		return apply(args);
	}

	@Override
	public String toString() {
		return _name;
	}


	/* *************************************************************************
	 * Static helper methods.
	 * ************************************************************************/

	/**
	 * Evaluates the given operation tree with the given variables.
	 *
	 * @param <T> the argument type
	 * @param tree the operation tree
	 * @param variables the input variables
	 * @return the result of the operation tree evaluation
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if the length of the variable array
	 *         is smaller than the program arity
	 */
	@SafeVarargs
	public static <T> T eval(
		final Tree<? extends Op<T>, ?> tree,
		final T... variables
	) {
		requireNonNull(tree);
		requireNonNull(variables);

		final Op<T> op = tree.getValue();
		return op.isTerminal()
			? op.apply(variables)
			: op.apply(
					tree.childStream()
						.map(child -> eval(child, variables))
						.toArray(size -> newArray(variables.getClass(), size))
				);
	}

	@SuppressWarnings("unchecked")
	private static <T> T[] newArray(final Class<?> arrayType, final int size) {
		return (T[])Array.newInstance(arrayType.getComponentType(), size);
	}

	/**
	 * Validates the given program tree.
	 *
	 * @param program the program to validate
	 * @throws NullPointerException if the given {@code program} is {@code null}
	 * @throws IllegalArgumentException if the given operation tree is invalid,
	 *         which means there is at least one node where the operation arity
	 *         and the node child count differ.
	 */
	public static void check(final Tree<? extends Op<?>, ?> program) {
		requireNonNull(program);
		program.forEach(Program::checkArity);
	}

	private static void checkArity(final Tree<? extends Op<?>, ?> node) {
		if (node.getValue() != null &&
			node.getValue().arity() != node.childCount())
		{
			throw new IllegalArgumentException(format(
				"Op arity != child count: %d != %d",
				node.getValue().arity(), node.childCount()
			));
		}
	}

	/**
	 * Create a new program tree from the given (non) terminal operations with
	 * the desired depth. The created program tree is a <em>full</em> tree.
	 *
	 * @param depth the desired depth of the program tree
	 * @param operations the list of <em>non</em>-terminal operations
	 * @param terminals the list of terminal operations
	 * @param <A> the operational type
	 * @return a new program tree
	 * @throws NullPointerException if one of the given operations is
	 *        {@code null}
	 * @throws IllegalArgumentException if the given tree depth is smaller than
	 *         zero
	 */
	public static <A> TreeNode<Op<A>> of(
		final int depth,
		final ISeq<? extends Op<A>> operations,
		final ISeq<? extends Op<A>> terminals
	) {
		if (depth < 0) {
			throw new IllegalArgumentException(
				"Tree depth is smaller than zero: " + depth
			);
		}
		if (!operations.forAll(o -> !o.isTerminal())) {
			throw new IllegalArgumentException(
				"Operation list contains terminal op."
			);
		}
		if (!terminals.forAll(o -> o.isTerminal())) {
			throw new IllegalArgumentException(
				"Terminal list contains non-terminal op."
			);
		}

		final TreeNode<Op<A>> root = TreeNode.of();
		fill(depth, root, operations, terminals, RandomRegistry.getRandom());
		return root;
	}

	private static <A> void fill(
		final int level,
		final TreeNode<Op<A>> tree,
		final ISeq<? extends Op<A>> operations,
		final ISeq<? extends Op<A>> terminals,
		final Random random
	) {
		final Op<A> op = level == 0
			? terminals.get(random.nextInt(terminals.size()))
			: operations.get(random.nextInt(operations.size()));

		tree.setValue(op);

		if (level > 1) {
			for (int i = 0; i < op.arity(); ++i) {
				final TreeNode<Op<A>> node = TreeNode.of();
				fill(level - 1, node, operations, terminals, random);
				tree.attach(node);
			}
		} else {
			for (int i = 0; i < op.arity(); ++i) {
				final Op<A> term = terminals.get(random.nextInt(terminals.size()));
				tree.attach(TreeNode.of(term));
			}
		}
	}

	/**
	 * Creates a valid program tree from the given flattened sequence of
	 * op nodes. The given {@code operations} and {@code termination} nodes are
	 * used for <em>repairing</em> the program tree, if necessary.
	 *
	 * @param nodes the flattened, possible corrupt, program tree
	 * @param terminals the usable non-terminal operation nodes to use for
	 *        reparation
	 * @param <A> the operation argument type
	 * @return a new valid program tree build from the flattened program tree
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if the {@code nodes} sequence is empty
	 */
	public static <A> TreeNode<Op<A>> toTree(
		final ISeq<? extends FlatTree<? extends Op<A>, ?>> nodes,
		final ISeq<? extends Op<A>> terminals
	) {
		if (nodes.isEmpty()) {
			throw new IllegalArgumentException("Tree nodes must not be empty.");
		}

		final Op<A> op = requireNonNull(nodes.get(0).getValue());
		final TreeNode<Op<A>> tree = TreeNode.of(op);
		return toTree(
			tree,
			0,
			nodes,
			offsets(nodes),
			terminals,
			RandomRegistry.getRandom()
		);
	}

	private static <A> TreeNode<Op<A>> toTree(
		final TreeNode<Op<A>> root,
		final int index,
		final ISeq<? extends FlatTree<? extends Op<A>, ?>> nodes,
		final int[] offsets,
		final ISeq<? extends Op<A>> terminals,
		final Random random
	) {
		if (index < nodes.size()) {
			final FlatTree<? extends Op<A>, ?> node = nodes.get(index);
			final Op<A> op = node.getValue();

			for (int i  = 0; i < op.arity(); ++i) {
				assert offsets[index] != -1;

				final TreeNode<Op<A>> treeNode = TreeNode.of();
				if (offsets[index] + i < nodes.size()) {
					treeNode.setValue(nodes.get(offsets[index] + i).getValue());
				} else {
					treeNode.setValue(terminals.get(random.nextInt(terminals.size())));
				}

				toTree(
					treeNode,
					offsets[index] + i,
					nodes,
					offsets,
					terminals,
					random
				);
				root.attach(treeNode);
			}
		}

		return root;
	}

	/**
	 * Create the offset array for the given nodes. The offsets are calculated
	 * using the arity of the stored operations.
	 *
	 * @param nodes the flattened tree nodes
	 * @return the offset array for the given nodes
	 */
	static int[]
	offsets(final ISeq<? extends FlatTree<? extends Op<?>, ?>> nodes) {
		final int[] offsets = new int[nodes.size()];

		int offset = 1;
		for (int i = 0; i < offsets.length; ++i) {
			final Op<?> op = nodes.get(i).getValue();

			offsets[i] = op.isTerminal() ? -1 : offset;
			offset += op.arity();
		}

		return offsets;
	}

}
