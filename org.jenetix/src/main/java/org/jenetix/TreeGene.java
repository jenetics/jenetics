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

import java.util.Objects;
import java.util.Optional;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jenetics.Chromosome;
import org.jenetics.Gene;
import org.jenetics.util.Factory;

import org.jenetix.util.TreeNode;

/**
 * @param <A> the allele type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class TreeGene<A>
	implements Gene<A, TreeGene<A>>
{

	private final A _value;
	private final int _parent;
	private final int[] _children;
	private final Factory<A> _factory;

	/**
	 * Create a new {@code TreeGene} instance for the given parameters.
	 *
	 * @param value the tree-gene value (allele)
	 * @param parent the gene index (within the chromosome) of the parent gene
	 * @param children the gene indexes of the child genes
	 * @param factory the allele factor used for creating new {@code TreeGene}
	 *        instances
	 */
	private TreeGene(
		final A value,
		final int parent,
		final int[] children,
		final Factory<A> factory
	) {
		_value = value;
		_parent = parent;
		_children = requireNonNull(children);
		_factory = requireNonNull(factory);
	}

	/**
	 * Return the (optional) parent gene of this tree-gene.
	 *
	 * @param chromosome the chromosome from where to fetch the gene
	 * @return the parent gene, if available
	 * @throws NullPointerException if the given {@code chromosome} is
	 *        {@code null}
	 */
	public Optional<TreeGene<A>>
	getParent(final Chromosome<TreeGene<A>> chromosome) {
		return _parent < 0 || _parent >= chromosome.length()
			? Optional.empty()
			: Optional.of(chromosome.getGene(_parent));
	}

	/**
	 * Return the children stream of the {@code this} tree-gene.
	 *
	 * @param chromosome the chromosome from where to fetch the gene
	 * @return the node children
	 * @throws NullPointerException if the given {@code chromosome} is
	 *        {@code null}
	 */
	public Stream<TreeGene<A>>
	children(final Chromosome<TreeGene<A>> chromosome) {
		requireNonNull(chromosome);

		return IntStream.of(_children)
			.filter(i -> i >= 0 && i < chromosome.length())
			.mapToObj(chromosome::getGene);
	}

	/**
	 * Test whether {@code this} gene is a leaf.
	 *
	 * @return {@code true} if {@code this} gene is a leaf, {@code false}
	 *         otherwise
	 */
	public boolean isLeaf() {
		return _children.length == 0;
	}

	@Override
	public A getAllele() {
		return _value;
	}

	@Override
	public TreeGene<A> newInstance() {
		return newInstance(_factory.newInstance());
	}

	@Override
	public TreeGene<A> newInstance(final A value) {
		return TreeGene.of(value, _parent, _children, _factory);
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public String toString() {
		return Objects.toString(_value);
	}


	/* *************************************************************************
	 * Static factory methods.
	 **************************************************************************/

	/**
	 * Create a new {@code TreeGene} instance for the given parameters.
	 *
	 * @param value the tree-gene value (allele)
	 * @param parent the gene index (within the chromosome) of the parent gene
	 * @param children the gene indexes of the child genes
	 * @param factory the allele factor used for creating new {@code TreeGene}
	 *        instances
	 * @param <A> the allele type
	 * @return a new {@code TreeGene} instance
	 */
	public static <A> TreeGene<A> of(
		final A value,
		final int parent,
		final int[] children,
		final Factory<A> factory
	) {
		return new TreeGene<>(value, parent, children, factory);
	}

	/**
	 * Converts the given tree {@code node} into a {@code TreeGene}.
	 *
	 * @param node the tree {@code node} to convert
	 * @param index the index function which returns the gene index within the
	 *        {@link TreeChromosome} for a given tree {@code node}.
	 * @param factory the allele factor used for creating new {@code TreeGene}
	 *        instances
	 * @param <A> the allele type
	 * @return a new {@code TreeGene} instance
	 */
	public static <A> TreeGene<A> toTreeGene(
		final TreeNode<A> node,
		final ToIntFunction<TreeNode<A>> index,
		final Factory<A> factory
	) {
		final int[] indexes = node.children()
			.mapToInt(index)
			.toArray();

		return TreeGene.of(
			node.getValue(),
			node.getParent()
				.map(index::applyAsInt)
				.orElse(-1),
			indexes,
			factory
		);
	}

}
