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

import org.jenetics.util.Array;
import org.jenetics.util.Factory;

/**
 * This interface decouples the {@link Chromosome} creation from the {@link Chromosome}.
 * The ChromosomeFactory creates a new (randomized) {@link Chromosome} from a
 * specific type.
 * 
 * @see Chromosome
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: ChromosomeFactory.java 330 2010-02-16 12:48:21Z fwilhelm $
 */
public interface ChromosomeFactory<T extends Gene<?, T>> 
	extends Factory<Chromosome<T>> 
{

	/**
	 * A factory method which creates a new {@link Chromosome} of specific type 
	 * and the given {@code genes}.
	 * 
	 * @return A new {@link Chromosome} of the same type with the given genes.
	 * @throws NullPointerException if the given {@code gene}s are {@code null}.
	 */
	public Chromosome<T> newInstance(final Array<T> genes);

	
}
