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
	private final A _allele;
	private final int _childrenOffset;

	private AbstractTreeChromosome<A, G> _chromosome;

	protected AbstractTreeGene(
		final A allele,
		final int childrenOffset
	) {
		if (childrenOffset < 0) {
			throw new IllegalArgumentException(
				"Children offset smaller than zero: " + childrenOffset
			);
		}

		_allele = requireNonNull(allele);
		_childrenOffset = childrenOffset;
	}

	protected AbstractTreeGene(final A allele) {
		this(allele, 0);
	}

	final void attachTo(final AbstractTreeChromosome<A, G> chromosome) {
		_chromosome = requireNonNull(chromosome);
	}

	public int getChildrenOffset() {
		return _childrenOffset;
	}

	@Override
	public A getAllele() {
		return _allele;
	}

	@Override
	public A getValue() {
		return _allele;
	}

	@Override
	public Optional<G> getParent() {
		checkTreeState();

		return _chromosome.stream()
			.filter(g -> g.childStream().anyMatch(c -> c == this))
			.findFirst();
	}

	void checkTreeState() {
		if (_chromosome == null) {
			throw new IllegalStateException(
				"Gene is not attached to a chromosome."
			);
		}
	}

	@Override
	public G getChild(final int index) {
		checkTreeState();
		if (index < 0 || index >= childCount()) {
			throw new IndexOutOfBoundsException(format(
				"Child index out of bounds: %s", index
			));
		}

		assert _chromosome != null;
		return _chromosome.getGene(_childrenOffset + index);
	}

	@Override
	public int childCount() {
		return 0;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 31;
		hash += 31*Objects.hashCode(_allele) + 17;
		hash += 31*_childrenOffset + 17;
		hash += 31*System.identityHashCode(_chromosome) + 17;
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof AbstractTreeGene<?, ?> &&
			Objects.equals(((AbstractTreeGene<?, ?>)obj)._allele, _allele) &&
			((AbstractTreeGene)obj)._chromosome == _chromosome &&
			((AbstractTreeGene)obj)._childrenOffset == _childrenOffset;
	}

	@Override
	public String toString() {
		return Objects.toString(_allele);
	}

}
