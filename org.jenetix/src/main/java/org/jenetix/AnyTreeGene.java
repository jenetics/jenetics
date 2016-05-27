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
import org.jenetics.util.Factory;

import org.jenetix.util.TreeNode;

/**
 * @param <A> the allele type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class AnyTreeGene<A>
	implements TreeGene<A, AnyTreeGene<A>>
{

	private final A _value;
	private final int[] _children;
	private final Factory<A> _factory;

	/**
	 * Create a new {@code TreeGene} instance for the given parameters.
	 *
	 * @param value the tree-gene value (allele)
	 * @param children the gene indexes of the child genes
	 * @param factory the allele factor used for creating new {@code TreeGene}
	 *        instances
	 */
	private AnyTreeGene(
		final A value,
		final int[] children,
		final Factory<A> factory
	) {
		_value = value;
		_children = requireNonNull(children);
		_factory = requireNonNull(factory);
	}

	/**
	 * Return the root gene from the given {@code chromosome}
	 *
	 * @param chromosome the chromosome from where to fetch the root gene
	 * @return the root tree gene
	 * @throws NullPointerException if the given {@code chromosome} is
	 *        {@code null}
	 */
	public static <A> AnyTreeGene<A>
	getRoot(final Chromosome<AnyTreeGene<A>> chromosome) {
		return chromosome.getGene();
	}

	@Override
	public Optional<AnyTreeGene<A>>
	getParent(final Chromosome<AnyTreeGene<A>> chromosome) {
		final Optional<Integer> index = IntStream.range(0, chromosome.length())
			.filter(i -> chromosome.getGene(i) == this)
			.mapToObj(Integer::valueOf)
			.findFirst();

		return index.flatMap(i -> parentFor(i, chromosome));
	}

	private Optional<AnyTreeGene<A>> parentFor(
		final int child,
		final Chromosome<AnyTreeGene<A>> chromosome
	) {
		return chromosome.stream()
			.filter(g -> contains(g._children, child))
			.findFirst();
	}

	private static boolean contains(final int[] array, final int value) {
		boolean found = false;
		for (int i = 0; i < array.length && !found; ++i) {
			found = array[i] == value;
		}
		return found;
	}

	@Override
	public AnyTreeGene<A> getChild(
		final int index,
		final Chromosome<AnyTreeGene<A>> chromosome
	) {
		return chromosome.getGene(_children[index]);
	}

	@Override
	public Stream<AnyTreeGene<A>>
	children(final Chromosome<AnyTreeGene<A>> chromosome) {
		requireNonNull(chromosome);

		return IntStream.of(_children)
			.filter(i -> i >= 0 && i < chromosome.length())
			.mapToObj(chromosome::getGene);
	}

	@Override
	public int childCount() {
		return _children.length;
	}

	/**
	 * Return a {@link TreeNode} with {@code this} tree-gene as root.
	 *
	 * @param chromosome the chromosome which {@code this} tree-gene is part of
	 * @return a {@link TreeNode} with {@code this} tree-gene as root
	 */
	@Override
	public TreeNode<A> toTreeNode(final Chromosome<AnyTreeGene<A>> chromosome) {
		final TreeNode<A> root = TreeNode.of();
		fill(this, root, chromosome);
		return root;
	}

	private static <A> void fill(
		final AnyTreeGene<A> gene,
		final TreeNode<A> parent,
		final Chromosome<AnyTreeGene<A>> chromosome
	) {
		parent.setValue(gene.getAllele());

		gene.children(chromosome).forEachOrdered(g -> {
			final TreeNode<A> node = TreeNode.of();
			parent.add(node);
			fill(g, node, chromosome);
		});
	}

	@Override
	public A getAllele() {
		return _value;
	}

	@Override
	public AnyTreeGene<A> newInstance() {
		return newInstance(_factory.newInstance());
	}

	@Override
	public AnyTreeGene<A> newInstance(final A value) {
		return AnyTreeGene.of(value, _children, _factory);
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
	 * @param children the gene indexes of the child genes
	 * @param factory the allele factor used for creating new {@code TreeGene}
	 *        instances
	 * @param <A> the allele type
	 * @return a new {@code TreeGene} instance
	 */
	public static <A> AnyTreeGene<A> of(
		final A value,
		final int[] children,
		final Factory<A> factory
	) {
		return new AnyTreeGene<>(value, children, factory);
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
	public static <A> AnyTreeGene<A> toTreeGene(
		final TreeNode<A> node,
		final ToIntFunction<TreeNode<A>> index,
		final Factory<A> factory
	) {
		final int[] indexes = node.childStream()
			.mapToInt(index)
			.toArray();

		return AnyTreeGene.of(
			node.getValue(),
			indexes,
			factory
		);
	}

}
