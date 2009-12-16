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


/**
 * Interface for scaling the 'raw' fitness of a given chromosome. 
 * 
 * @param <C> the result type of the fitness function to scale. The scaled value
 *            must (of course) of the same type.
 * @see FitnessFunction
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: FitnessScaler.java,v 1.6 2009-12-16 10:32:30 fwilhelm Exp $
 */
public interface FitnessScaler<C extends Comparable<C>> extends Serializable {
	
	/**
	 * Return the scaled fitness value. The returned value <em>must</em> not be 
	 * {@code null}.
	 * 
	 * @param value the fitness value to scale.
	 * @return The scaled fitness value. The returned value <em>must</em> not be 
	 *        {@code null}.
	 * @throws NullPointerException if the given {@code value} is {@code null}.
	 */
	public C scale(final C value);
	
}
