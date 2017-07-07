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

import org.jenetics.Gene;

import org.jenetics.util.ISeq;
import org.jenetix.util.Tree;

/**
 * Representation of tree shaped gene.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface TreeGene<A, G extends TreeGene<A, G>>
	extends
		Gene<A, G>,
		Tree<A, G>
{

	@Override
	public default A getValue() {
		return getAllele();
	}

	/**
	 * Return the genes of the underlying chromosome, where this tree gene is
	 * part of. For an unattached gene, {@code null} is returned.
	 *
	 * @see #attachTo(ISeq)
	 *
	 * @return the genes of the underlying chromosome
	 */
	public ISeq<G> genes();

	/**
	 * Return the index of the first child node in the underlying gene array.
	 * {@code -1} is returned if {@code this} node is a leaf.
	 *
	 * @return Return the index of the first child node in the underlying node
	 *         array, or {@code -1} if {@code this} node is a leaf
	 */
	public int childOffset();

	/**
	 * This method is used by the {@code AbstractTreeChromosome} to attach
	 * itself to this gene. Once set, the genes can be queried with
	 * {@link #genes()}.
	 *
	 * @see #genes()
	 *
	 * @param genes the genes of the attached chromosome
	 */
	public void attachTo(final ISeq<G> genes);

	/**
	 * Return a new tree gene with the given allele and the <em>local</em> tree
	 * structure.
	 *
	 * @param allele the actual gene allele
	 * @param childOffset the offset of the first node child within the
	 *        chromosome
	 * @param childCount the number of children of the new tree gene
	 * @return a new tree gene with the given parameters
	 * @throws IllegalArgumentException  if the {@code childCount} is smaller
	 *         than zero
	 */
	public G newInstance(
		final A allele,
		final int childOffset,
		final int childCount
	);

}
