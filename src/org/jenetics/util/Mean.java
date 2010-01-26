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
package org.jenetics.util;

import org.jenetics.MeanAlterer;
import org.jenetics.NumberGene;

/**
 * A mixin interface for genes which can have a mean value. This mixin is 
 * required for the {@link MeanAlterer}.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Mean.java,v 1.1 2010-01-26 13:24:33 fwilhelm Exp $
 * 
 * @see MeanAlterer
 */
public interface Mean<T> {

	/**
	 * Return the (usually arithmetic) mean value of <code>this</code> and 
	 * {@code that}. For {@link NumberGene}s the mean is the arithmetic mean.
	 * 
	 * @param that the second value for calculating the mean.
	 * @return the mean value of <code>this</code> and <code>that</code>.
	 * @throws NullPointerException if the argument is {@code null}.
	 */
	public T mean(final T that);
	
}
