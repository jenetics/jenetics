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
 *  A {@link FitnessFunction} which always returns a given constant value.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 *
 * @param <T> the gene type.
 * @version $Id: ConstantFitnessFunction.java,v 1.3 2008-09-22 21:38:31 fwilhelm Exp $
 */
public class ConstantFitnessFunction<T extends Gene<?>, C extends Comparable<C>> 
	implements FitnessFunction<T, C> 
{
	private static final long serialVersionUID = 8766537513371578351L;
	
	private final C _value;
	
	public ConstantFitnessFunction(final C value) {
		this._value = value;
	}
	
	@Override
	public C evaluate(final Genotype<T> genotype) {
		return _value;
	}
	
}
