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

import static org.jenetics.util.Validator.notNull;
import javolution.text.Text;
import javolution.text.TextBuilder;

import org.jscience.mathematics.number.Number;

/**
 * Abstract base class for implementing concrete NumberGenes.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: NumberGene.java,v 1.9 2009-02-24 21:43:30 fwilhelm Exp $
 */
public abstract class NumberGene<N extends Number<N>, G extends NumberGene<N, G>> 
	extends Number<G> implements Gene<N, G>, Mean<G>
{
	private static final long serialVersionUID = 9160065700657586640L;

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
	
	@Override
	public G copy() {
		return newInstance(_value);
	}
	
	/**
	 * Create a new NumberGene with the same limits and the given value.
	 * 
	 * @param value The value of the new NumberGene.
	 * @return The new NumberGene.
	 * @throws IllegalArgumentException if the gene value is not in the range
	 * 		(value < min || value > max).
	 * @throws NullPointerException if the given {@code value} is {@code null}.
	 */
	public abstract G newInstance(final java.lang.Number value); 
		
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
		notNull(value, "Gene value");
		notNull(min, "Min value");
		notNull(max, "Max value");
		
		if (min.isGreaterThan(max)) {
			throw new IllegalArgumentException(String.format(
				"Max value must be greater than min value. [%s, %s]", min, max
			));
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
	 * Return the number value of this gene.
	 * 
	 * @return the number value of this gene.
	 */
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
	public N getMin() {
		return _min;
	}
	
	/**
	 * Return the allowed max value.
	 * 
	 * @return The allowed max value.
	 */
	public N getMax() {
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
	public boolean isLargerThan(final G that) {
		return _value.isLargerThan(that._value);
	}

	@Override
	public G plus(final G that) {
		return newInstance(_value.plus(that._value));
	}

	@Override
	public G opposite() {
		return newInstance(_value.opposite());
	}
	
	@Override
	public G times(final G that) {
		return newInstance(_value.times(that._value));
	}
    
	@Override
	public int compareTo(final G that) {
		return _value.compareTo(that._value);
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
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		}
		
		final NumberGene<?, ?> gene = (NumberGene<?, ?>)obj;
		return _value.equals(gene._value) && 
				_min.equals(gene._min) && 
				_max.equals(gene._max);
	}
	
	@Override
	public Text toText() {
		TextBuilder out = new TextBuilder();
		out.append("[").append(_value).append("]");
		return out.toText();
	}
	
}






