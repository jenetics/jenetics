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

import static org.jenetics.Checker.checkNull;

import org.jscience.mathematics.number.Number;

/**
 * Abstract base class for implementing concrete NumberGenes.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: NumberGene.java,v 1.1 2008-03-25 18:31:57 fwilhelm Exp $
 */
public abstract class NumberGene<N extends Number<N>> 
	extends Number<NumberGene<N>> implements Gene<N> 
{
	/**
	 * The minimum value of this <code>NumberGene</code>.
	 */
	protected N _min;
	
	/**
	 * The maximum value of this <code>NumberGene</code>.
	 */
	protected N _max;
	
	/**
	 * The value of this <code>NumberGene</code>.
	 */
	protected N _value;
	
	protected NumberGene() {
	}
	
	/**
	 * Set the <code>NumerGene</code>.
	 * 
	 * @param value The value of the number gene.
	 * @param min The allowed min value of the gene.
	 * @param max The allows max value of the gene.
	 * @throws NullPointerException if one of the given number is null.
	 * @throws IllegalArgumentException if min > max.
	 */
	protected void set(final N value, final N min, final N max) {
		checkNull(value, "Gene value");
		checkNull(min, "Min value");
		checkNull(max, "Max value");
		
		if (min.isGreaterThan(max)) {
			throw new IllegalArgumentException(
				"Max value must be greater than min value."
			);
		}

		this._min = min;
		this._max = max;
		this._value = value;
	}
	
	/**
	 * Test whether this is a valid NumberGene and its value is within the
	 * interval closed interval [min, max].
	 * 
	 * @return if this gene is valid, which means the gene value is within the
	 *         closed interval [min, max].
	 */
	@Override
	public boolean isValid() {
		return _value.compareTo(_min) >= 0 && _value.compareTo(_max) <= 0;
	}
	
	/**
	 * Create a new NumberGene with the same limits and the given value.
	 * 
	 * @param value The value of the new NumberGene.
	 * @return The new NumberGene.
	 * @throws IllegalArgumentException if the gene value is not in the range
	 * 		(value < min || value > max).
	 */
	public abstract NumberGene<N> newInstance(final N value); 
	
	public N getNumber() {
		return _value;
	}
	
	@Override
	public N getAllele() {
		return _value;
	}
	
	/**
	 * Return the allowed min value.
	 * 
	 * @return The allowed min value.
	 */
	public N getMinValue() {
		return _min;
	}
	
	/**
	 * Return the allowed max value.
	 * 
	 * @return The allowed max value.
	 */
	public N getMaxValue() {
		return _max;
	}
	
    @Override
	public double doubleValue() {
    	return _value.doubleValue();
    }
    
    @Override
	public long longValue() {
    	return _value.longValue();
    }
    
	@Override
	public int hashCode() {
		int code = 37;
		code += 17*_value.hashCode() + 37;
		code += 17*_min.hashCode() + 37;
		code += 17*_max.hashCode() + 37;
		
		return code;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof NumberGene)) {
			return false;
		}
		
		final NumberGene<?> gene = (NumberGene<?>)obj;
		return _value.equals(gene._value) && 
			_min.equals(gene._min) && 
			_max.equals(gene._max);
	}
	
}






