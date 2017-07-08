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

import static java.util.Objects.requireNonNull;

import java.util.Random;

import org.jenetics.util.ISeq;
import org.jenetics.util.RandomRegistry;

import org.jenetix.util.FlatTree;
import org.jenetix.util.TreeNode;

/**
 * Helper methods for creating program trees.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Programs {
	private Programs() {
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
	 */
	public static <A> TreeNode<Op<A>> of(
		final int depth,
		final ISeq<? extends Op<A>> operations,
		final ISeq<? extends Op<A>> terminals
	) {
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
		final Op<A> op = operations.get(random.nextInt(operations.size()));
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

			offsets[i] = op.arity() == 0 ? -1 : offset;
			offset += nodes.get(i).getValue().arity();
		}

		return offsets;
	}

}
