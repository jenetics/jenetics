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

import java.util.Optional;
import java.util.stream.Stream;

import org.jenetics.Chromosome;
import org.jenetics.Gene;

import org.jenetix.util.TreeNode;

/**
 * Representation of a tree-node.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface TreeGene<A, G extends TreeGene<A, G>> extends Gene<A, G> {

	/**
	 * Return the (optional) parent gene of this tree-gene.
	 *
	 * @param chromosome the chromosome from where to fetch the gene
	 * @return the parent gene, if available
	 * @throws NullPointerException if the given {@code chromosome} is
	 *        {@code null}
	 */
	public Optional<G> getParent(final Chromosome<? extends G> chromosome);

	/**
	 * Returns the child at the specified index in this node's child array.
	 *
	 * @param index   an index into this node's child array
	 * @param chromosome the chromosome from where to fetch the gene
	 * @return the tree-gene in this node's child array at the specified index
	 * @throws ArrayIndexOutOfBoundsException  if the {@code index} is out of
	 *         bounds
	 */
	public G getChild(final int index, final Chromosome<? extends G> chromosome);

	/**
	 * Return the children stream of the {@code this} tree-gene.
	 *
	 * @param chromosome the chromosome from where to fetch the gene
	 * @return the node children
	 * @throws NullPointerException if the given {@code chromosome} is
	 *        {@code null}
	 */
	public Stream<G> children(final Chromosome<? extends G> chromosome);

	/**
	 * Test whether {@code this} gene is a leaf.
	 *
	 * @return {@code true} if {@code this} gene is a leaf, {@code false}
	 *         otherwise
	 */
	public default boolean isLeaf() {
		return childCount() == 0;
	}

	/**
	 * Returns the number of children of {@code this} gene.
	 *
	 * @return the number of children of {@code this} gene
	 */
	public int childCount();

	/**
	 * Return a {@link TreeNode} with {@code this} tree-gene as root.
	 *
	 * @param chromosome the chromosome which {@code this} tree-gene is part of
	 * @return a {@link TreeNode} with {@code this} tree-gene as root
	 * @throws NullPointerException if the given {@code chromosome} is
	 *        {@code null}
	 */
	@SuppressWarnings("unchecked")
	public default TreeNode<A> toTreeNode(final Chromosome<? extends G> chromosome) {
		return TreeGenes.<A, G>toTreeNode((G)this, chromosome);
	}

}
