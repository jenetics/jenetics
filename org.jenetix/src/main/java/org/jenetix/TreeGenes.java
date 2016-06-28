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

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collector;

import org.jenetics.internal.util.require;

import org.jenetics.util.ISeq;
import org.jenetics.util.Seq;

import org.jenetix.util.TreeNode;

/**
 * Static helper methods concerning {@code TreeGene}s.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class TreeGenes {
	private TreeGenes() {require.noInstance();}

	/**
	 * Return a {@link TreeNode} with {@code this} tree-gene as root.
	 *
	 * @param gene the root tree-gene
	 * @param chromosome the chromosome which {@code this} tree-gene is part of
	 * @return a {@link TreeNode} with {@code this} tree-gene as root
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 */
	public static <A, G extends TreeGene<A, G>> TreeNode<A>
	toTreeNode(final G gene, final Seq<? extends G> chromosome) {
		requireNonNull(chromosome);

		final TreeNode<A> root = TreeNode.of();
		TreeGenes.<A, G>fill(gene, root, chromosome);
		return root;
	}

	private static <A, G extends TreeGene<A, G>> void fill(
		final G gene,
		final TreeNode<A> parent,
		final Seq<? extends G> chromosome
	) {
		parent.setValue(gene.getAllele());

		gene.children(chromosome).forEachOrdered(child -> {
			final TreeNode<A> node = TreeNode.of();
			parent.attach(node);
			TreeGenes.<A, G>fill(child, node, chromosome);
		});
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
	 *     .collect(toTreeGeneISeq(MyTreeGene::new));
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
	static <A, G extends TreeGene<A, G>> Collector<TreeNode<A>, ?, ISeq<G>>
	toTreeGeneISeq(final BiFunction<A, int[], G> newGene) {
		requireNonNull(newGene);

		return Collector.of(
			(Supplier<List<TreeNode<A>>>)ArrayList::new,
			List::add,
			(left, right) -> { left.addAll(right); return left; },
			nodes -> {
				final Map<TreeNode<A>, Integer> indexes = toIndexed(nodes);

				return nodes.stream()
					.map(n -> newGene.apply(
							n.getValue(),
							toChildIndexes(n, indexes))
						)
					.collect(ISeq.toISeq());
			}
		);
	}

	private static <A> Map<A, Integer>
	toIndexed(final Collection<? extends A> values) {
		final Map<A, Integer> indexes = new IdentityHashMap<>();
		int index = 0;
		for (A value : values) {
			indexes.put(value, index++);
		}

		return indexes;
	}

	private static int[] toChildIndexes(
		final TreeNode<?> node,
		final Map<?, Integer> indexes
	) {
		return node.childStream()
			.mapToInt(indexes::get)
			.toArray();
	}

}
