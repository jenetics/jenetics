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

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jenetics.util.Seq;

/**
 * Abstract implementation of the {@link TreeGene} interface.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public abstract class AbstractTreeGene<A, G extends AbstractTreeGene<A, G>>
	implements TreeGene<A, G>
{

	/**
	 * The allele of the tree-gene.
	 */
	protected final A _value;

	/**
	 * The chromosome indexes of the tree-gene children.
	 */
	protected final int[] _children;

	/**
	 * Create a new {@code AbstractTreeGene} instance for the given parameters.
	 *
	 * @param value the tree-gene value (allele)
	 * @param children the gene indexes of the child genes. The given int[]
	 *        array is <b>not</b> copied and it's the responsibility of the
	 *        implementor to not change the indexes after gene creation.
	 */
	protected AbstractTreeGene(
		final A value,
		final int[] children
	) {
		_value = value;
		_children = requireNonNull(children);
	}

	@Override
	public Optional<G> getParent(final Seq<? extends G> genes) {
		// Find the index of 'this' gene in the gene sequence.
		final Optional<Integer> index = IntStream.range(0, genes.length())
			.filter(i -> genes.get(i) == this)
			.boxed()
			.findFirst();

		return index.flatMap(i -> parentFor(i, genes));
	}

	@SuppressWarnings("unchecked")
	private Optional<G>
	parentFor(final int child, final Seq<? extends G> genes) {
		return (Optional<G>)genes.stream()
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
	public G getChild(final int index, final IntFunction<? extends G> genes) {
		return genes.apply(_children[index]);
	}

	@Override
	public Stream<G> children(final Seq<? extends G> genes) {
		requireNonNull(genes);

		return IntStream.of(_children)
			.filter(i -> i >= 0 && i < genes.length())
			.mapToObj(genes::get);
	}

	@Override
	public int childCount() {
		return _children.length;
	}

	@Override
	public A getAllele() {
		return _value;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 31;
		hash += 31*Objects.hashCode(_value) + 17;
		hash += 31*Arrays.hashCode(_children) + 17;
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof AbstractTreeGene<?, ?> &&
			Objects.equals(((AbstractTreeGene<?, ?>)obj)._value, _value) &&
			Arrays.equals(((AbstractTreeGene<?, ?>)obj)._children, _children);
	}

	@Override
	public String toString() {
		return Objects.toString(_value);
	}

}
