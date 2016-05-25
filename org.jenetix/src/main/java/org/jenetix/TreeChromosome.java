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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.jenetics.AbstractChromosome;
import org.jenetics.util.Factory;
import org.jenetics.util.ISeq;
import org.jenetics.util.IntRange;

import org.jenetix.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class TreeChromosome<A> extends AbstractChromosome<TreeGene<A>> {

	private final IntRange _childCount;
	private final IntRange _depth;

	protected TreeChromosome(
		final IntRange childCount,
		final IntRange depth,
		final ISeq<TreeGene<A>> genes
	) {
		super(genes);

		_childCount = requireNonNull(childCount);
		_depth = requireNonNull(depth);
	}

	/**
	 * Return the root gene
	 *
	 * @return the root tree gene
	 */
	public TreeGene<A> getRoot() {
		return _genes.get(0);
	}

	/**
	 * Return the parent gene of the given tree {@code gene}.
	 *
	 * @param gene the {@code gene} from where to fetch the parent
	 * @return the parent gene of the given tree {@code gene}
	 * @throws NullPointerException if the given {@code gene} is {@code null}
	 */
	public Optional<TreeGene<A>> getParent(final TreeGene<A> gene) {
		return gene.getParent(this);
	}

	/**
	 * Return the child tree nodes from the given {@code gene}.
	 *
	 * @param gene the {@code gene} from where to fetch the child tree-nodes
	 * @return the child nodes of the given tree {@code gene}
	 * @throws NullPointerException if the given {@code gene} is {@code null}
	 */
	public Stream<TreeGene<A>> children(final TreeGene<A> gene) {
		return gene.children(this);
	}

	@Override
	public TreeGene<A> getGene(final int index) {
		return _genes.get(index);
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public TreeChromosome<A> newInstance(final ISeq<TreeGene<A>> genes) {
		return new TreeChromosome<>(_childCount, _depth, genes);
	}

	@Override
	public TreeChromosome<A> newInstance() {
		return null;
	}

	public TreeNode<A> toTree() {
		final TreeNode<A> root = TreeNode.of();
		toTree(getGene(0), root);
		return root;
	}

	private void toTree(final TreeGene<A> gene, final TreeNode<A> parent) {
		requireNonNull(gene);
		parent.setValue(gene.getAllele());

		gene.children(this).forEachOrdered(g -> {
			final TreeNode<A> node = TreeNode.of();
			parent.add(node);
			toTree(g, node);
		});
	}

	/* *************************************************************************
	 * Static factory methods.
	 **************************************************************************/

	/**
	 * Create a new {@code TreeChromosome} from the given tree-node.
	 *
	 * @param tree source tree
	 * @param factory the allele factor used for creating new {@code TreeGene}
	 *        instances
	 * @param <A> the allele (tree value) type
	 * @return a new tree-chromosome
	 */
	public static <A> TreeChromosome<A> of(
		final TreeNode<A> tree,
		final Factory<A> factory
	) {
		requireNonNull(tree);
		requireNonNull(factory);

		final ISeq<TreeNode<A>> nodes = tree
			.breathFirstStream()
			.collect(ISeq.toISeq());

		final Map<TreeNode<A>, Integer> indexes = new LinkedHashMap<>();
		for (int i = 0; i < nodes.length(); ++i) {
			indexes.put(nodes.get(i), i);
		}

		final ISeq<TreeGene<A>> genes = nodes
			.map(node -> TreeGene.toTreeGene(node, indexes::get, factory));

		return new TreeChromosome<>(IntRange.of(1, 23), IntRange.of(2, 23), genes);
	}

}
