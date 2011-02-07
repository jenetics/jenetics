/*
 * Java Genetic Algorithm Library (@!identifier!@).
 * Copyright (c) @!year!@ Franz Wilhelmstötter
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
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 * 	 
 */
package org.jenetics;

import static org.jenetics.util.ObjectUtils.eq;
import static org.jenetics.util.ObjectUtils.hashCodeOf;
import static org.jenetics.util.Predicates.nil;
import static org.jenetics.util.Validator.nonNull;

import java.util.ListIterator;
import java.util.RandomAccess;

import javolution.lang.Realtime;
import javolution.text.Text;

import org.jenetics.util.Array;
import org.jenetics.util.Converter;
import org.jenetics.util.Validator.Verify;

/**
 * The abstract base implementation of the Chromosome interface. The implementors
 * of this class must assure that the protected member <code>_genes</code> is not
 * <code>null</code> and the lenght of the <code>genes</code> > 0.
 * 
 * @param <G> the gene type.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public abstract class AbstractChromosome<G extends Gene<?, G>> 
	implements 
		Chromosome<G>, 
		Realtime, 
		RandomAccess
{
	private static final long serialVersionUID = 1;

	/**
	 * Array of genes which forms the chromosome. This array must
	 * be initialized by the derived classes.
	 */
	protected transient Array<G> _genes = null;
	
	/**
	 * Indicates whether this chromosome is valid or not. If the variable is
	 * {@code null} the validation state hasn't been calculated yet.
	 */
	protected transient Boolean _valid = null;

	/**
	 * Create a new chromosome with the given length.
	 * 
	 * @param length the {@code length} of the new chromosome.
	 * @throws IllegalArgumentException if the {@code length} is smaller than 
	 *         one.
	 */
	protected AbstractChromosome(final int length) {
		if (length < 1) {
			throw new IllegalArgumentException(String.format(
				"Chromosome length < 1: %d", length
			));
		}
		
		_genes = new Array<G>(length);
	}
	
	/**
	 * Create a new {@code AbstractChromosome} from the given {@code genes}
	 * array. The genes array is not copied, but sealed, so changes to the given 
	 * genes array doesn't effect the genes of this chromosome.
	 * 
	 * @param genes the genes that form the chromosome.
	 * @throws NullPointerException if the given gene array is {@code null}.
	 * @throws IllegalArgumentException if the length of the gene array is
	 * 		  smaller than one.
	 */
	protected AbstractChromosome(final Array<G> genes) {
		nonNull(genes, "Gene array");
		assert (genes.indexOf(nil()) == -1) : "Found at least on null gene.";
		
		if (genes.length() < 1) {
			throw new IllegalArgumentException(String.format(
				"Chromosome length < 1: %d", genes.length()
			));
		}
		
		_genes = genes.seal();
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
	public Array<G> toArray() {
		return _genes.seal();
	}
	
	@Override
	public boolean isValid() {
		if (_valid == null) {
			_valid = _genes.foreach(new Verify()) == -1;
		}
		
		return _valid;
	}
	
	@Override
	public ListIterator<G> iterator() {
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
	 * 		  found.
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
	public Text toText() {
		return Text.valueOf(_genes.toString());
	}
	
	@Override
	public String toString() {
		return _genes.toString();
	}
	
	
	/* *************************************************************************
	 *  Property access methods
	 * ************************************************************************/
	
	/**
	 * Return a {@link Converter} which returns the first {@link Gene} from this
	 * {@link Chromosome}.
	 */
	static <G extends Gene<?, G>, C extends Chromosome<G>> 
	Converter<C, G> gene() {
		return new Converter<C, G>() {
			@Override public G convert(final C value) {
				return value.getGene();
			}
		};
	}
	
	/**
	 * Return a {@link Converter} which returns the {@link Gene} with the given
	 * {@code index} from this {@link Chromosome}.
	 */
	static <G extends Gene<?, G>, C extends Chromosome<G>> 
	Converter<C, G> gene(final int index) {
		return new Converter<C, G>() {
			@Override public G convert(final C value) {
				return value.getGene(index);
			}
		};
	}

	/**
	 * Return a {@link Converter} which returns the gene array from this
	 * {@link Chromosome}.
	 */
	static <G extends Gene<?, G>, C extends AbstractChromosome<G>> 
	Converter<C, Array<G>> genes() {
		return new Converter<C, Array<G>>() {
			@Override public Array<G> convert(final C value) {
				return value.toArray();
			}
		};
	}
	
}




