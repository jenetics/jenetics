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
package org.jenetics;

import static java.util.Objects.requireNonNull;
import static org.jenetics.util.functions.Null;
import static org.jenetics.util.object.Verify;
import static org.jenetics.util.object.eq;
import static org.jenetics.util.object.hashCodeOf;

import java.util.Iterator;
import java.util.RandomAccess;

import org.jenetics.util.Function;
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
 * @version 1.0 &mdash; <em>$Date: 2013-06-14 $</em>
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
		requireNonNull(genes, "Gene array");
		assert (genes.indexWhere(Null) == -1) : "Found at least on null gene.";

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
	public G getGene() {
		return _genes.get(0);
	}

	@Override
	public ISeq<G> toSeq() {
		return _genes;
	}

	@Override
	public boolean isValid() {
		if (_valid == null) {
			_valid = _genes.forAll(Verify);
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


	/* *************************************************************************
	 *  Property access methods
	 * ************************************************************************/

	/**
	 * Return a {@link Function} which returns the first {@link Gene} from this
	 * {@link Chromosome}.
	 */
	static <G extends Gene<?, G>, C extends Chromosome<G>>
	Function<C, G> gene() {
		return new Function<C, G>() {
			@Override public G apply(final C value) {
				return value.getGene();
			}
		};
	}

	/**
	 * Return a {@link Function} which returns the {@link Gene} with the given
	 * {@code index} from this {@link Chromosome}.
	 */
	static <G extends Gene<?, G>, C extends Chromosome<G>>
	Function<C, G> gene(final int index) {
		return new Function<C, G>() {
			@Override public G apply(final C value) {
				return value.getGene(index);
			}
		};
	}

	/**
	 * Return a {@link Function} which returns the gene array from this
	 * {@link Chromosome}.
	 */
	static <G extends Gene<?, G>, C extends Chromosome<G>>
	Function<C, ISeq<G>> genes() {
		return new Function<C, ISeq<G>>() {
			@Override public ISeq<G> apply(final C value) {
				return value.toSeq();
			}
		};
	}

}




