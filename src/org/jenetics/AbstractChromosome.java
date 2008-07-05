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

import java.util.Iterator;
import java.util.RandomAccess;

import javolution.lang.Realtime;
import javolution.text.Text;

/**
 * The abstract base implementation of the Chromosome interface. The implementors
 * of this class must assure that the protected member <code>_genes</code> is not
 * <code>null</code> and the lenght of the <code>_genes</code> > 0.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: AbstractChromosome.java,v 1.4 2008-07-05 20:28:14 fwilhelm Exp $
 */
public abstract class AbstractChromosome<T extends Gene<?>> 
	implements Chromosome<T>, Realtime, RandomAccess
{
	private static final long serialVersionUID = 8283543816229993099L;

	/**
	 * Array of genes which forms the chromosome. This array must
	 * be initialized by the derived classes.
	 */
	protected Array<T> _genes = null;
	
	//
	private Boolean _valid = null;

	/**
	 * Default constructor of the AbstractChromosome.
	 */
	protected AbstractChromosome(final int length) {
		_genes = Array.newInstance(length);
	}
	
	protected AbstractChromosome(final Array<T> genes) {
		_genes = genes.copy();
	}
	
	@Override
	public T getGene(final int index) {
		return _genes.get(index);
	}
	
	@Override
	public T getGene() {
		return _genes.get(0);
	}

	/**
	 * Return a unmodifyabble array instance. A call of the 
	 * {@link Array#set(int, Object)} will throw an 
	 * {@link UnsupportedOperationException}.
	 */
	@Override
	public Array<T> getGenes() {
		return new ImmutableArray<T>(_genes);
	}
	
	@Override
	public boolean isValid() {
		boolean valid = true;
		if (_valid == null) {
			for (int i = 0; i < _genes.length() && valid; ++i) {
				valid = _genes.get(i).isValid();
			}
			_valid = valid ? Boolean.TRUE : Boolean.FALSE;
		} else {
			valid = _valid.booleanValue();
		}
		return valid;
	}
	
	@Override
	public Iterator<T> iterator() {
		return _genes.iterator();
	}
	
	@Override
	public int length() {
		return _genes.length();
	}
	
	/**
	 * Return the index of the first occurence of the given <code>gene</code>.
	 * 
	 * @param gene the {@link Gene} to search for.
	 * @return the index of the searched gene, or -1 if the given gene was not found.
	 */
	protected int firstIndexOf(final T gene) {
		int index = -1;
		for (int i = 0; i < length() && index == -1; ++i) {
			if (_genes.get(i).equals(gene)) {
				index = i;
			}
		}
		return index;
	}
	
	@Override
	public int hashCode() {
		int code = 17;
		for (T gene : _genes) {
			code += 37*gene.hashCode() + 17;
		}
		return code;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof AbstractChromosome)) {
			return false;
		}
		
		final AbstractChromosome<?> chromosome = (AbstractChromosome<?>)obj;
		boolean equals = length() == chromosome.length();
		for (int i = 0; equals && i < length(); ++i) {
			equals = _genes.get(i).equals(chromosome._genes.get(i));
		}
		return equals;
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




