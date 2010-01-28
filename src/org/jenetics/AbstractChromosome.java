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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *     
 */
package org.jenetics;

import java.util.ListIterator;
import java.util.RandomAccess;

import javolution.lang.Realtime;
import javolution.text.Text;

import org.jenetics.util.Array;
import org.jenetics.util.Validator;

/**
 * The abstract base implementation of the Chromosome interface. The implementors
 * of this class must assure that the protected member <code>_genes</code> is not
 * <code>null</code> and the lenght of the <code>_genes</code> > 0.
 * 
 * @param <G> the gene type.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: AbstractChromosome.java,v 1.22 2010-01-28 19:34:14 fwilhelm Exp $
 */
public abstract class AbstractChromosome<G extends Gene<?, G>> 
	implements Chromosome<G>, Realtime, RandomAccess
{
	private static final long serialVersionUID = 8283543816229993099L;

	/**
	 * Array of genes which forms the chromosome. This array must
	 * be initialized by the derived classes.
	 */
	protected Array<G> _genes = null;
	
	private Boolean _valid = null;

	/**
	 * Create a new chromosome
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
	 * array. The genes array is copied, so changes to the given genes array
	 * doesn't effect the genes of this chromosome.
	 * 
	 * @param genes the genes that form the chromosome.
	 * @throws NullPointerException if the given gene array is {@code null}.
	 * @throws IllegalArgumentException if the length of the gene array is
	 *         smaller than one.
	 */
	protected AbstractChromosome(final Array<G> genes) {
		Validator.nonNull(genes, "Gene array");
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
			boolean valid = true;
			for (int i = 0; i < _genes.length() && valid; ++i) {
				valid = _genes.get(i).isValid();
			}
			_valid = valid;
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
	 * @return the index of the searched gene, or -1 if the given gene was not found.
	 */
	protected int indexOf(final G gene) {
		return _genes.indexOf(gene);
	}
	
	@Override
	public int hashCode() {
		int code = 17;
		for (G gene : _genes) {
			code += 37*gene.hashCode() + 17;
		}
		return code;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof AbstractChromosome<?>)) {
			return false;
		}
		
		final AbstractChromosome<?> chromosome = (AbstractChromosome<?>)obj;
		return _genes.equals(chromosome._genes);
	}

	@Override
	public Text toText() {
		return Text.valueOf(_genes.toString());
	}
	
	@Override
	public String toString() {
		return _genes.toString();
	}
	
}




