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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.jenetics.AbstractChromosome;
import org.jenetics.util.Factory;
import org.jenetics.util.ISeq;

import org.jenetix.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class TreeChromosome<A> extends AbstractChromosome<TreeGene<A>> {

	protected final Factory<TreeChromosome<A>> _factory;

	protected TreeChromosome(
		final ISeq<TreeGene<A>> genes,
		final Factory<TreeChromosome<A>> factory
	) {
		super(genes);
		_factory = requireNonNull(factory);
	}

	/**
	 * Return the root gene of this chromosome.
	 *
	 * @return the root tree gene of this chromosome
	 */
	public TreeGene<A> getRoot() {
		return getGene();
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
		return new TreeChromosome<>(genes, _factory);
	}

	@Override
	public TreeChromosome<A> newInstance() {
		return null;
	}

	public TreeNode<A> toTreeNode() {
		return getRoot().toTreeNode(this);
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

		return new TreeChromosome<>(genes, null);
	}

}
