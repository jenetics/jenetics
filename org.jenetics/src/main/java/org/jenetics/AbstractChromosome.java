/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics;

import static org.jenetics.util.object.eq;
import static org.jenetics.util.object.hashCodeOf;
import static org.jenetics.util.object.nonNull;

import java.util.Iterator;
import java.util.RandomAccess;
import java.util.function.Function;

import org.jenetics.util.ISeq;

/**
 * The abstract base implementation of the Chromosome interface. The implementors
 * of this class must assure that the protected member <code>_genes</code> is not
 * <code>null</code> and the length of the <code>genes</code> > 0.
 *
 * @param <G> the gene type.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date: 2013-02-13 $</em>
 */
public abstract class AbstractChromosome<G extends Gene<?, G>>
	implements
		Chromosome<G>,
		RandomAccess
{
	private static final long serialVersionUID = 1;

	/**
	 * Array of genes which forms the chromosome. This array must
	 * be initialized by the derived classes.
	 */
	protected transient ISeq<G> _genes = null;

	/**
	 * Indicates whether this chromosome is valid or not. If the variable is
	 * {@code null} the validation state hasn't been calculated yet.
	 */
	protected transient Boolean _valid = null;

	/**
	 * Create a new {@code AbstractChromosome} from the given {@code genes}
	 * array. The genes array is not copied, but sealed, so changes to the given
	 * genes array doesn't effect the genes of this chromosome.
	 *
	 * @param genes the genes that form the chromosome.
	 * @throws NullPointerException if the given gene array is {@code null}.
	 * @throws IllegalArgumentException if the length of the gene array is
	 *          smaller than one.
	 */
	protected AbstractChromosome(final ISeq<G> genes) {
		nonNull(genes, "Gene array");
		assert (genes.indexWhere(g -> g == null) == -1) : "Found at least on null gene.";

		if (genes.length() < 1) {
			throw new IllegalArgumentException(String.format(
				"Chromosome length < 1: %d", genes.length()
			));
		}

		_genes = genes;
	}

	@Override
	public G getGene(final int index) {
		return _genes.get(index);
	}

	@Override
	public ISeq<G> toSeq() {
		return _genes;
	}

	@Override
	public boolean isValid() {
		if (_valid == null) {
			_valid = _genes.forall(g -> g.isValid());
		}

		return _valid;
	}

	@Override
	public Iterator<G> iterator() {
		return _genes.iterator();
	}

	@Override
	public int length() {
		return _genes.length();
	}

	/**
	 * Return the index of the first occurrence of the given <code>gene</code>.
	 *
	 * @param gene the {@link Gene} to search for.
	 * @return the index of the searched gene, or -1 if the given gene was not
	 *         found.
	 */
	protected int indexOf(final Object gene) {
		return _genes.indexOf(gene);
	}

	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).and(_genes).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		final AbstractChromosome<?> chromosome = (AbstractChromosome<?>)obj;
		return eq(_genes, chromosome._genes);
	}

	@Override
	public String toString() {
		return _genes.toString();
	}

}




