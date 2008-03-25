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

import java.util.Random;

import javolution.context.ObjectFactory;
import javolution.text.Text;
import javolution.text.TextBuilder;
import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import org.jscience.mathematics.number.Float64;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: DoubleGene.java,v 1.1 2008-03-25 18:31:55 fwilhelm Exp $
 */
public class DoubleGene extends NumberGene<Float64> 
	implements Mean<DoubleGene>, XMLSerializable 
{
	private static final long serialVersionUID = 2531451920309748752L;	
	
	protected DoubleGene() {
	}
	
	@Override
	public boolean isLargerThan(final NumberGene<Float64> that) {
		return _value.isLargerThan(that._value);
	}

	@Override
	public DoubleGene plus(final NumberGene<Float64> that) {
		DoubleGene g = FACTORY.object();
		g.set(_value.plus(that._value), _min, _max);
		return g;
	}

	@Override
	public DoubleGene opposite() {
		DoubleGene g = FACTORY.object();
		g.set(_value.opposite(), _min, _max);
		return g;
	}
	
	@Override
	public DoubleGene times(final NumberGene<Float64> that) {
		DoubleGene g = FACTORY.object();
		g.set(_value.times(that._value), _min, _max);
		return g;
	}
	
	public DoubleGene divide(final NumberGene<Float64> that) {
		return divide(that._value);
	}

	public DoubleGene divide(final Float64 that) {
		return newInstance(_value.divide(that));
	}
	

	@Override
	public DoubleGene mean(final DoubleGene that) {
		Float64 sum = _value;
		sum = sum.plus(that._value);
		return newInstance(sum.divide(Float64.valueOf(2)));
	}
	
	@Override
	public DoubleGene copy() {
		return valueOf(_value, _min, _max);
	}

	@Override
	public DoubleGene newInstance(final Float64 number) {
		return valueOf(number, _min, _max);
	}
	
	/**
	 * Create a new DoubleGene with the same limits and the given value.
	 * 
	 * @param number The value of the new NumberGene.
	 * @return The new NumberGene.
	 * @throws IllegalArgumentException if the gene value is not in the range
	 * 		(value < min || value > max).
	 */
	public DoubleGene newInstance(final double number) {
		return newInstance(Float64.valueOf(number));
	}

	@Override
	public int compareTo(final NumberGene<Float64> that) {
		return this.getAllele().compareTo(that.getAllele());
	}

	@Override
	public Text toText() {
		TextBuilder out = new TextBuilder();
		out.append("[").append(_value).append("]");
		return out.toText();
	}
	
	@Override
	public int hashCode() {
		int hash = 15	;
		hash += super.hashCode()*37;
		return hash;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof DoubleGene)) {
			return false;
		}
		
		return super.equals(obj);
	}
	
	private static final ObjectFactory<DoubleGene> 
	FACTORY = new ObjectFactory<DoubleGene>() {
		@Override protected DoubleGene create() {
			return new DoubleGene();
		}
	};
	
	/**
	 * Create a new random DoubleGene with the given value and the given range. If the
	 * {@code value} isn't within the closed interval [min, max], no exception is thrown.
	 * In this case the method {@link DoubleGene#isValid()} returns {@code false}.
	 * 
	 * @param value the value of the DoubleGene.
	 * @param min the minimal valid value of this DoubleGene.
	 * @param max the maximal valid value of this DoubleGene.
	 * @return the new created DoubleGene with the given {@code value}.
	 * @throws IllegalArgumentException if min > max.
	 */
	public static DoubleGene valueOf(final Float64 value, final Float64 min, final Float64 max) {
		DoubleGene gene = FACTORY.object();
		gene.set(value, min, max);
		return gene;
	}
	
	/**
	 * Create a new random DoubleGene with the given value and the given range. If the
	 * {@code value} isn't within the closed interval [min, max], no exception is thrown.
	 * In this case the method {@link DoubleGene#isValid()} returns {@code false}.
	 * 
	 * @param value the value of the DoubleGene.
	 * @param min the minimal valid value of this DoubleGene.
	 * @param max the maximal valid value of this DoubleGene.
	 * @return the new created DoubleGene with the given {@code value}.
	 * @throws IllegalArgumentException if min > max.
	 */
	public static DoubleGene valueOf(final double value, final double min, final double max) {
		return valueOf(
			Float64.valueOf(value),
			Float64.valueOf(min), 
			Float64.valueOf(max)
		);
	}
	
	/**
	 * Create a new random DoubleGene. It is guaranteed that the value of the DoubleGene
	 * lies in the closed interval [min, max].
	 * 
	 * @param min the minimal value of the DoubleGene to create.
	 * @param max the maximal value of the DoubleGene to create.
	 * @return the new created Integer gene.
	 * @throws IllegalArgumentException if min > max
	 */
	public static DoubleGene valueOf(final Float64 min, final Float64 max) {
		final Random random = RandomRegistry.getRandom();
		final double value = min.doubleValue() + random.nextDouble()*(max.doubleValue() - min.doubleValue());
		return valueOf(Float64.valueOf(value), min, max);
	}
	
	/**
	 * Create a new random DoubleGene. It is guaranteed that the value of the DoubleGene
	 * lies in the closed interval [min, max].
	 * 
	 * @param min the minimal value of the DoubleGene to create.
	 * @param max the maximal value of the DoubleGene to create.
	 * @return the new created Integer gene.
	 * @throws IllegalArgumentException if min > max
	 */
	public static DoubleGene valueOf(final double min, final double max) {
		return valueOf(Float64.valueOf(min), Float64.valueOf(max));
	}
	
	static final XMLFormat<DoubleGene> XML = new XMLFormat<DoubleGene>(DoubleGene.class) {
		@Override
		public DoubleGene newInstance(final Class<DoubleGene> cls, final InputElement element)
			throws XMLStreamException
		{
			final double min = element.getAttribute("min", 0.0);
			final double max = element.getAttribute("max", 1.0);
			final double value = element.<Double>getNext();
			return DoubleGene.valueOf(min, max, value);
		}
		@Override
		public void write(final DoubleGene gene, final OutputElement element) 
			throws XMLStreamException 
		{
			element.setAttribute("min", gene.getMinValue().doubleValue());
			element.setAttribute("max", gene.getMaxValue().doubleValue());
			element.add(gene.getAllele().doubleValue());
		}
		@Override
		public void read(InputElement element, DoubleGene gene) throws XMLStreamException {
		}
	};

}




