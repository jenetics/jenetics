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

/**
 * This interface decouples the {@link Genotype} creation from the {@link Genotype}.
 * The GenotypeFactory creates a new (randomized) {@link Genotype} from a
 * specific type.
 * 
 * @see Genotype
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: GenotypeFactory.java,v 1.1 2008-03-25 18:31:54 fwilhelm Exp $
 */
public interface GenotypeFactory<T extends Gene<?>> {

	/**
	 * Create a random Genotype with the gene type T.
	 * 
	 * @return A randomly generated {@link Genotype}.
	 */
	public Genotype<T> newGenotype();
	
}
