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

import org.jenetics.util.Validator;


/**
 *  A {@link FitnessFunction} which always returns a given constant value.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 *
 * @param <G> the gene type.
 * @version $Id: ConstantFitnessFunction.java,v 1.5 2009-12-16 10:32:30 fwilhelm Exp $
 */
public class ConstantFitnessFunction<G extends Gene<?, G>, C extends Comparable<C>> 
	implements FitnessFunction<G, C> 
{
	private static final long serialVersionUID = 8766537513371578351L;
	
	private final C _value;
	
	/**
	 * Create a new <i>constant</i> fitness function with the given value. This
	 * value is returned for every {@link #evaluate(Genotype)} call.
	 * 
	 * @param value the constant value.
	 * @throws NullPointerException if the given {@code value} is {@code null}.
	 */
	public ConstantFitnessFunction(final C value) {
		_value = Validator.notNull(value, "Constant value");
	}
	
	/**
	 * Always return the given constant value.
	 */
	@Override
	public C evaluate(final Genotype<G> genotype) {
		return _value;
	}
	
}
