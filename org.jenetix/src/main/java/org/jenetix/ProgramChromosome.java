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
package org.jenetix;

import static java.util.Objects.requireNonNull;
import static org.jenetix.util.FlatTreeNode.flatten;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jenetics.util.ISeq;

import org.jenetix.util.Tree;
import org.jenetix.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class ProgramChromosome<A> extends AbstractTreeChromosome<Op<A>, ProgramGene<A>> {

	private final ISeq<? extends Op<A>> _ops;
	private final Map<Integer, List<Op<A>>> _map = new HashMap<>();

	ProgramChromosome(final ISeq<ProgramGene<A>> genes) {
		super(genes);
		_ops = genes.get(0).getOps();
	}

	@Override
	public ProgramChromosome<A> newInstance(final ISeq<ProgramGene<A>> genes) {
		return new ProgramChromosome<>(
			flatten(unflatten(genes)).map(node ->
			    new ProgramGene<>(node.getValue(), _ops, node.getChildOffset())
			)
		);
	}

	private static <A> TreeNode<Op<A>> unflatten(final ISeq<ProgramGene<A>> genes) {
		return unflatten(TreeNode.of(), 0, genes);
	}

	private static <A> TreeNode<Op<A>> unflatten(
		final TreeNode<Op<A>> tree,
		final int index,
		final ISeq<ProgramGene<A>> genes
	) {
		if (index < genes.size()) {
			final ProgramGene<A> gene = genes.get(index);
			tree.setValue(gene.getAllele());

			int childOffset = 1;
			for (int i = 0; i < index; ++i) {
				childOffset += genes.get(i).childCount();
			}

			for (int i = 0; i < gene.childCount(); ++i) {
				final int childIndex = childOffset + 1;
				if (childIndex < genes.size()) {
					tree.attach(unflatten(TreeNode.of(), childIndex, genes));
				} else {
					tree.attach(TreeNode.of());
				}
			}
		}

		return tree;
	}

	@Override
	public ProgramChromosome<A> newInstance() {
		return null;
	}

	public static <A, T extends Tree<? extends Op<A>, T>>
	ProgramChromosome<A> of(
		final T tree,
		final ISeq<? extends Op<A>> operations,
		final ISeq<? extends Op<A>> terminals
	) {
		requireNonNull(tree);
		requireNonNull(operations);
		requireNonNull(terminals);

		return null;
	}

	public static <A> ProgramChromosome<A> of(
		final int size,
		final ISeq<? extends Op<A>> operations,
		final ISeq<? extends Op<A>> terminals
	) {
		requireNonNull(operations);
		requireNonNull(terminals);

		final TreeNode<Op<A>> root = TreeNode.of();

		return null;
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
				final Op<A> term = operations.get(random.nextInt(operations.size()));
				final TreeNode<Op<A>> node = TreeNode.of(term);
				tree.attach(node);
			}
		}
	}

}
