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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.ext;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static io.jenetics.internal.util.Hashes.hash;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import io.jenetics.util.BaseSeq;
import io.jenetics.util.ISeq;

/**
 * Abstract implementation of the {@link TreeGene} interface..
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 6.0
 * @since 3.9
 */
public abstract class AbstractTreeGene<A, G extends AbstractTreeGene<A, G>>
	implements TreeGene<A, G>, Serializable
{

	private static final long serialVersionUID = 1L;

	/**
	 * The allele of the tree-gene.
	 */
	private final A _allele;
	private final int _childOffset;
	private final int _childCount;

	private BaseSeq<G> _genes;

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

	/**
	 * Return the whole flattened tree values in breadth-first order. This method
	 * will always return the same {@code ISeq} instance.
	 *
	 * @return the whole flattened tree values
	 */
	@Override
	public ISeq<G> flattenedNodes() {
		return ISeq.of(_genes);
	}

	@Override
	public G root() {
		return _genes.get(0);
	}

	@Override
	public boolean isRoot() {
		return root() == this;
	}

	@Override
	public int size() {
		return isRoot() ? _genes.length() : TreeGene.super.size();
	}

	protected void checkTreeState() {
		if (_genes == null) {
			throw new IllegalStateException(
				"Gene is not attached to a chromosome."
			);
		}
	}

	/**
	 * This method is used by the {@code AbstractTreeChromosome} to attach
	 * itself to this gene.
	 *
	 * @param genes the genes of the attached chromosome
	 */
	protected void bind(final BaseSeq<G> genes) {
		_genes = requireNonNull(genes);
	}

	@Override
	public int childOffset() {
		return _childOffset;
	}

	@Override
	public A allele() {
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
	public Optional<G> parent() {
		checkTreeState();

		return _genes.stream()
			.filter(g -> g.childStream().anyMatch(this::identical))
			.findFirst();
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
	public G childAt(final int index) {
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
	public int hashCode() {
		return hash(_allele, hash(_childOffset, hash(_childCount)));
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof AbstractTreeGene &&
			Objects.equals(((AbstractTreeGene)obj)._allele, _allele) &&
			((AbstractTreeGene)obj)._childOffset == _childOffset &&
			((AbstractTreeGene)obj)._childCount == _childCount;
	}

	@Override
	public String toString() {
		return Objects.toString(_allele);
	}

}
