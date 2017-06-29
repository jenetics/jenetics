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
import java.util.function.BiFunction;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.jenetics.Gene;
import org.jenetics.util.ISeq;
import org.jenetics.util.Seq;

import org.jenetix.util.Tree;
import org.jenetix.util.TreeNode;

/**
 * Representation of a tree-node.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface TreeGene<A, G extends TreeGene<A, G>> extends Gene<A, G> {

	public class Tr {

	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public default Tree<G, ?> asTree(final Seq<? extends G> genes) {
		return new Tree() {
			@Override
			public G getValue() {
				return null;
			}

			@Override
			public Optional<Tree<G, ?>> getParent() {
				return TreeGene.this.getParent(genes).map(p -> p.asTree(genes));
			}

			@Override
			public Tree<G, ?> getChild(int index) {
				return null;
			}

			@Override
			public int childCount() {
				return 0;
			}
		};
	}

	public default G foo() {
		final Tree<G, ?> tree = asTree(null);

		tree.isAncestor(tree);

		return asTree(null).getParent()
			.map(p -> p.getValue())
			.orElse(null);
	}

	/**
	 * Return the (optional) parent gene of this tree-gene.
	 *
	 * @param genes the gene sequence from where to fetch the gene
	 * @return the parent gene, if available
	 * @throws NullPointerException if the given {@code genes} sequence is
	 *        {@code null}
	 */
	public Optional<G> getParent(final Seq<? extends G> genes);

	/**
	 * Returns the child at the specified index in this node's child array.
	 *
	 * @param index   an index into this node's child array
	 * @param genes the gene sequence from where to fetch the gene
	 * @return the tree-gene in this node's child array at the specified index
	 * @throws ArrayIndexOutOfBoundsException  if the {@code index} is out of
	 *         bounds
	 * @throws NullPointerException if the given {@code genes} sequence is
	 *        {@code null}
	 */
	public G getChild(final int index, final Seq<? extends G> genes);

	/**
	 * Return the children stream of the {@code this} tree-gene.
	 *
	 * @param genes the gene sequence from where to fetch the gene
	 * @return the node children
	 * @throws NullPointerException if the given {@code gene} sequence is
	 *        {@code null}
	 */
	public Stream<G> children(final Seq<? extends G> genes);

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
	 * Return a new {@link TreeNode} with represents {@code this} tree-gene.
	 *
	 * @param genes the gene sequence which {@code this} tree-gene is part of
	 * @return a {@link TreeNode} with {@code this} tree-gene as root
	 * @throws NullPointerException if the given {@code genes} sequence is
	 *        {@code null}
	 */
	@SuppressWarnings("unchecked")
	public default TreeNode<A> toTreeNode(final Seq<? extends G> genes) {
		return TreeGenes.<A, G>toTreeNode((G)this, genes);
	}

	/**
	 * Return a collector, which collects a {@link TreeNode} stream into a
	 * sequence of {@link TreeGene}s. The collection process is also referred as
	 * <em>node linearization</em>.
	 *
	 * <pre>{@code
	 * final TreeNode<Integer> root =
	 * TreeNode.of(0)
	 *     .add(TreeNode.of(-1)
	 *         .add(TreeNode.of(-2))
	 *         .add(TreeNode.of(-3)))
	 *     .add(TreeNode.of(1)
	 *         .add(TreeNode.of(2))
	 *          .add(TreeNode.of(3)));
	 *
	 * final ISeq<MyTreeGene> linearizedTree = root
	 *     .breathFirstStream()
	 *     // It is assumed that 'MyTreeGene' has a (Integer, int[]) constructor.
	 *     .collect(toLinearizedGenes(MyTreeGene::new));
	 * }</pre>
	 *
	 * @param newGene the factory function, which creates a new tree-gene
	 *        instance from the given allele and children indexes. The index
	 *        values fits the the returned tree-gene sequence.
	 * @param <A> the allele type
	 * @param <G> the gene type
	 * @return a linearized {@code TreeGene} representation of the
	 *         <em>collected</em> {@code TreeNode} stream
	 * @throws NullPointerException if the given gene factory is {@code null}
	 */
	public static <A, G extends TreeGene<A, G>> Collector<TreeNode<A>, ?, ISeq<G>>
	toLinearizedGenes(final BiFunction<A, int[], G> newGene) {
		return TreeGenes.toTreeGeneISeq(newGene);
	}

}
