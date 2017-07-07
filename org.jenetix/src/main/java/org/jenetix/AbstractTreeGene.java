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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;

import org.jenetics.util.ISeq;
import org.jenetix.util.Tree;

/**
 * Abstract implementation of the {@link TreeGene} interface. This class is
 * tightly coupled with the {@link AbstractTreeChromosome} class an they should
 * be always implemented in pairs.
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
	private final A _allele;
	private final int _childOffset;
	private final int _childCount;

	private ISeq<G> _genes;

	/**
	 * Creates a new tree-gene from the given data.
	 *
	 * @param allele the actual value (allele) of the tree-gene
	 * @param childOffset the offset index of the child in the containing
	 *        chromosome. If this node has no child, the value should be set
	 *        to zero.
	 * @param childCount the number of children of this gene
	 * @throws IllegalArgumentException if the {@code childCount} is smaller
	 *         than zero
	 */
	protected AbstractTreeGene(
		final A allele,
		final int childOffset,
		final int childCount
	) {
		if (childCount < 0) {
			throw new IllegalArgumentException(format(
				"Child count smaller than zero: %s", childCount
			));
		}

		_allele = allele;
		_childOffset = childOffset;
		_childCount = childCount;
	}

	@Override
	public ISeq<G> genes() {
		return _genes;
	}

	/**
	 * This method is used by the {@code AbstractTreeChromosome} to attach
	 * itself to this gene.
	 *
	 * @param genes the genes of the attached chromosome
	 */
	@Override
	public void bind(final ISeq<G> genes) {
		_genes = requireNonNull(genes);
	}

	@Override
	public int childOffset() {
		return _childOffset;
	}

	@Override
	public A getAllele() {
		return _allele;
	}

	/**
	 * Return the <em>parent</em> node of this tree node.
	 *
	 * @return the parent node, or {@code Optional.empty()} if this node is the
	 *         root of the tree
	 * @throws IllegalStateException if this gene is not part of a chromosome
	 */
	@Override
	public Optional<G> getParent() {
		checkTreeState();

		return _genes.stream()
			.filter(g -> g.childStream().anyMatch(this::identical))
			.findFirst();
	}

	void checkTreeState() {
		if (_genes == null) {
			throw new IllegalStateException(
				"Gene is not attached to a chromosome."
			);
		}
	}

	/**
	 * Return the child gene with the given index.
	 *
	 * @param index the child index
	 * @return the child node with the given index
	 * @throws IndexOutOfBoundsException  if the {@code index} is out of
	 *         bounds ({@code [0, childCount())})
	 * @throws IllegalStateException if this gene is not part of a chromosome
	 */
	@Override
	public G getChild(final int index) {
		checkTreeState();
		if (index < 0 || index >= childCount()) {
			throw new IndexOutOfBoundsException(format(
				"Child index out of bounds: %s", index
			));
		}

		assert _genes != null;
		return _genes.get(_childOffset + index);
	}

	@Override
	public int childCount() {
		return _childCount;
	}

	@Override
	public boolean isValid() {
		return _genes != null;
	}

	@Override
	public boolean identical(final Tree<?, ?> other) {
		return other instanceof AbstractTreeGene<?, ?> &&
			Objects.equals(((AbstractTreeGene<?, ?>)other)._allele, _allele) &&
			((AbstractTreeGene)other)._genes == _genes &&
			((AbstractTreeGene)other)._childOffset == _childOffset &&
			((AbstractTreeGene)other)._childCount == _childCount;
	}

	@Override
	public int hashCode() {
		int hash = 31;
		hash += 31*Objects.hashCode(_allele) + 17;
		hash += 31*_childOffset + 17;
		hash += 32*_childCount + 17;
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof AbstractTreeGene<?, ?> &&
			Objects.equals(((AbstractTreeGene<?, ?>)obj)._allele, _allele) &&
			((AbstractTreeGene)obj)._childOffset == _childOffset &&
			((AbstractTreeGene)obj)._childCount == _childCount;
	}

	@Override
	public String toString() {
		return Objects.toString(_allele);
	}

}
