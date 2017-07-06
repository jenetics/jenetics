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

import java.util.Random;

import org.jenetics.util.ISeq;
import org.jenetics.util.RandomRegistry;

import org.jenetix.util.FlatTree;
import org.jenetix.util.Tree;
import org.jenetix.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class ProgramChromosome<A> extends AbstractTreeChromosome<Op<A>, ProgramGene<A>> {

	private final ISeq<? extends Op<A>> _operations;
	private final ISeq<? extends Op<A>> _terminals;

	ProgramChromosome(
		final ISeq<ProgramGene<A>> genes,
		final ISeq<? extends Op<A>> operations,
		final ISeq<? extends Op<A>> terminals
	) {
		super(genes);
		_operations = requireNonNull(operations);
		_terminals = requireNonNull(terminals);
	}

	public ISeq<? extends Op<A>> getOperations() {
		return _operations;
	}

	public ISeq<? extends Op<A>> getTerminals() {
		return _terminals;
	}

	@Override
	public ProgramChromosome<A> newInstance(final ISeq<ProgramGene<A>> genes) {
		return of(genes, _operations, _terminals);
	}

	@Override
	public ProgramChromosome<A> newInstance() {
		return of(getRoot().depth(), _operations, _terminals);
	}

	public static <A, T extends Tree<? extends Op<A>, T>>
	ProgramChromosome<A> of(
		final T program,
		final ISeq<? extends Op<A>> operations,
		final ISeq<? extends Op<A>> terminals
	) {
		requireNonNull(operations);
		requireNonNull(terminals);
		check(program);

		final FlatTree<? extends Op<A>> flattened = FlatTree.of(program);
		final ISeq<ProgramGene<A>> genes = flattened.rootStream()
			.map(n -> new ProgramGene<>(
				n.getValue(), n.childOffset(), operations, terminals))
			.collect(ISeq.toISeq());

		return new ProgramChromosome<>(genes, operations, terminals);
	}

	public static <A, T extends Tree<? extends Op<A>, T>>
	void check(final T program) {
		requireNonNull(program);

		program.breathFirstStream().forEach(node -> {
			if (node.getValue().arity() != node.childCount()) {
				throw new IllegalArgumentException("");
			}
		});
	}

	public static <A> ProgramChromosome<A> of(
		final int depth,
		final ISeq<? extends Op<A>> operations,
		final ISeq<? extends Op<A>> terminals
	) {
		requireNonNull(operations);
		requireNonNull(terminals);

		final TreeNode<Op<A>> root = TreeNode.of();
		fill(depth, root, operations, terminals, RandomRegistry.getRandom());
		return of(root, operations, terminals);
	}

	private static <A> void fill(
		final int depth,
		final TreeNode<Op<A>> tree,
		final ISeq<? extends Op<A>> operations,
		final ISeq<? extends Op<A>> terminals,
		final Random random
	) {
		final Op<A> op = operations.get(random.nextInt(operations.size()));
		tree.setValue(op);

		if (depth > 1) {
			for (int i = 0; i < op.arity(); ++i) {
				final TreeNode<Op<A>> node = TreeNode.of();
				fill(depth - 1, node, operations, terminals, random);
				tree.attach(node);
			}
		} else {
			for (int i = 0; i < op.arity(); ++i) {
				final Op<A> term = terminals.get(random.nextInt(terminals.size()));
				final TreeNode<Op<A>> node = TreeNode.of(term);
				tree.attach(node);
			}
		}
	}

	public static <A> ProgramChromosome<A> of(
		final ISeq<ProgramGene<A>> genes,
		final ISeq<? extends Op<A>> operations,
		final ISeq<? extends Op<A>> terminals
	) {
		final TreeNode<Op<A>> program = unflatten(genes);
		return of(program, operations, terminals);
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

}
