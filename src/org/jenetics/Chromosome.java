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

import java.io.Serializable;

import javolution.lang.Immutable;


/**
 * A chromosome is an array of genes.
 * 
 * @see ChromosomeFactory
 * @see <a href="http://en.wikipedia.org/wiki/Chromosome">Wikipdida: Chromosome</a>
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Chromosome.java,v 1.3 2008-07-05 20:28:11 fwilhelm Exp $
 */
public interface Chromosome<T extends Gene<?>> 
	extends Verifiable, Iterable<T>, Immutable, ChromosomeFactory<T>, Serializable
{
	
	/**
	 * Return the gene type of this chromosome.
	 * 
	 * @return the gene type of this chromosome.
	 */
	public Class<T> getType();
	
	/**
	 * Return the first gene of this chromosome.
	 * 
	 * @return the first gene of this chromosome.
	 */
	public T getGene();
	
	/**
	 * Return the gene on the specified index.
	 * 
	 * @param index The gene index.
	 * @return the wanted gene.
	 * @throws IndexOutOfBoundsException if the index is out of range 
	 * 		(index < 0 || index >= length()).
	 */
	public T getGene(final int index);
	
	/**
	 * Return the {@link Gene}s of this {@code Chromosome}.  Changes to the returned
	 * array doesn't effect this {@code Chromosome}.
	 * 
	 * @return an array of this {@code Chromosome}.
	 */
	public Array<T> getGenes();
		
	/**
	 * Create a new mutated copy of this {@link Chromosome}. The chromosome is mutated at
	 * the given {@code index}.
	 * 
	 * @param index Gene index.
	 * @return the newly created {@link Chromosome} with the mutated {@link Gene}.
	 * @throws IndexOutOfBoundsException if the index is out of range 
	 * 		    {@code (index < 0 || index >= length())}.
	 */
	public Chromosome<T> mutate(final int index);
	 
	/**
	 * Test whether this chromosome is valid. In general a chromosome is valid
	 * if all its {@link Gene}s are valid.
	 * 
	 * @return true if this chromosome is valid, false otherwise.
	 */
	@Override
	public boolean isValid();
	
	/**
	 * Returns the length of the Chromosome
	 * 
	 * @return Length of the Chromosome
	 */
	public int length();
	

}
