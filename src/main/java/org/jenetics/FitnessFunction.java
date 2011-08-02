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

import java.io.Serializable;

/**
 * The <code>FitnessFunction</code> represents the the <i>environment</i> a given
 * {@link Genotype} lives. The {@link Genotype} and the <code>FitnessFunction</code>
 * together form the {@link Genotype}.
 * <p/>
 * The <code>FitnessFunction</code> is shared by all <code>Phenotypes</code> and 
 * should be thread safe. Typically the <code>evaluate</code> method is reentrant.
 * 
 * @param <G> the gene type.
 * @param <C> the result type of the fitness function. The result type must be
 * 			  at least comparable to define a fitness order on the genotype.
 * @see FitnessScaler
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public interface FitnessFunction<
	G extends Gene<?, G>, 
	C extends Comparable<? super C>
> 
	extends Serializable 
{

	/**
	 * Evaluating the FitnessFunction. The returned value <em>must</em> not be 
	 * {@code null}.
	 * 
	 * @param genotype The FitnessFunction argument.
	 * @return The fitness value. The returned value <em>must</em> not be 
	 * 		 {@code null}.
	 * @throws NullPointerException if the given {@code genotype} is {@code null}.
	 */
	public C evaluate(final Genotype<G> genotype);
	
}
