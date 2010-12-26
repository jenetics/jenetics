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

import org.jenetics.util.Array;
import org.jenetics.util.Factory;
import org.jenetics.util.Verifiable;

import javolution.lang.Immutable;


/**                                                              
 * A chromosome is an array of genes.
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Chromosome">Wikipdida: Chromosome</a>
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public interface Chromosome<T extends Gene<?, T>> 
	extends 
		Verifiable, 
		Iterable<T>, 
		Immutable, 
		Factory<Chromosome<T>>, 
		Serializable
{ 
	
	/**
	 * A factory method which creates a new {@link Chromosome} of specific type 
	 * and the given {@code genes}.
	 * 
	 * @param genes the genes of the new chromosome. The given genes array is
	 *        not copied, it is sealed instead.
	 * @return A new {@link Chromosome} of the same type with the given genes.
	 * @throws NullPointerException if the given {@code gene}s are {@code null}.
	 */
	public Chromosome<T> newInstance(final Array<T> genes);
	
	/**
	 * Return the first gene of this chromosome.  Each chromosome must contain
	 * at least one gene.
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
	 *         (index < 1 || index >= length()).
	 */
	public T getGene(final int index);

	/**
	 * Returns the length of the Chromosome. The minimal length of a
	 * chromosome is one.
	 * 
	 * @return Length of the Chromosome
	 */
	public int length();
	
	/**
	 * Return a unmodifiable array of the genes of this chromosome. A call of 
	 * the {@link Array#set(int, Object)} will throw an 
	 * {@link UnsupportedOperationException}. To get a mutable version of the
	 * genes array you have to call {@code toArray().copy()}
	 * 
	 * @return an immutable gene array.
	 */
	public Array<T> toArray();

}

