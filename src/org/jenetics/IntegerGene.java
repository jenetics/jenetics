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

import org.jenetics.util.RandomRegistry;
import org.jscience.mathematics.number.Integer64;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: IntegerGene.java,v 1.5 2009-02-22 23:29:58 fwilhelm Exp $
 */
public class IntegerGene extends NumberGene<Integer64> 
	implements Mean<IntegerGene>, XMLSerializable 
{
	private static final long serialVersionUID = 262677052481286632L;
	
	protected IntegerGene() {
	}

	@Override
	public boolean isLargerThan(final NumberGene<Integer64> that) {
		return _value.isLargerThan(that._value);
	}

	@Override
	public IntegerGene plus(final NumberGene<Integer64> that) {
		final IntegerGene g = FACTORY.object();
		g.set(_value.plus(that._value), _min, _max);
		return g;
	}
	
	@Override
	public IntegerGene opposite() {
		final IntegerGene g = FACTORY.object();
		g.set(_value.opposite(), _min, _max);
		return g;
	}

	@Override
	public IntegerGene times(final NumberGene<Integer64> that) {
		final IntegerGene g = FACTORY.object();
		g.set(_value.times(that._value), _min, _max);
		return g;
	}
	
	public IntegerGene divide(final NumberGene<Integer64> that) {
		return divide(that._value);
	}

	public IntegerGene divide(final Integer64 that) {
		return newInstance(_value.divide(that));
	}
	
	@Override
	public IntegerGene mean(final IntegerGene that) {
		Integer64 sum = _value;
		sum = sum.plus(that._value);
		return newInstance(sum.divide(Integer64.valueOf(2)));
	}
	
	@Override
	public IntegerGene copy() {
		return valueOf(_value, _min, _max);
	}

	@Override
	public IntegerGene newInstance(final java.lang.Number number) {
		return valueOf(Integer64.valueOf(number.longValue()), _min, _max);
	}
	
	/**
	 * Create a new IntegerGene with the same limits and the given value.
	 * 
	 * @param number The value of the new NumberGene.
	 * @return The new NumberGene.
	 * @throws IllegalArgumentException if the gene value is not in the range
	 * 		(value < min || value > max).
	 */
	public IntegerGene newInstance(final long number) {
		return newInstance(Integer64.valueOf(number));
	}

	@Override
	public int compareTo(final NumberGene<Integer64> that) {
		return this.getAllele().compareTo(that.getAllele());
	}

	@Override
	public Text toText() {
		TextBuilder out = TextBuilder.newInstance();
		out.append("[").append(_value).append("]");
		return out.toText();
	}
	
	@Override
	public int hashCode() {
		int hash = 17;
		hash += super.hashCode()*37;
		return hash;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof IntegerGene)) {
			return false;
		}
		
		return super.equals(obj);
	}

	
	private static final ObjectFactory<IntegerGene> FACTORY = 
	new ObjectFactory<IntegerGene>() {
		@Override protected IntegerGene create() {
			return new IntegerGene();
		}
	};
	
	/**
	 * Create a new random IntegerGene with the given value and the given range. If the
	 * {@code value} isn't within the closed interval [min, max], no exception is thrown.
	 * In this case the method {@link IntegerGene#isValid()} returns {@code false}.
	 * 
	 * @param value the value of the IntegerGene.
	 * @param min the minimal valid value of this IntegerGene.
	 * @param max the maximal valid value of this IntegerGene.
	 * @return the new created IntegerGene with the given {@code value}.
	 * @throws IllegalArgumentException if min > max.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public static IntegerGene valueOf(
		final Integer64 value, 
		final Integer64 min, 
		final Integer64 max
	) {
		IntegerGene gene = FACTORY.object();
		gene.set(value, min, max);
		return gene;
	}
	
	/**
	 * Create a new random IntegerGene with the given value and the given range. If the
	 * {@code value} isn't within the closed interval [min, max], no exception is thrown.
	 * In this case the method {@link IntegerGene#isValid()} returns {@code false}.
	 * 
	 * @param value the value of the IntegerGene.
	 * @param min the minimal valid value of this IntegerGene.
	 * @param max the maximal valid value of this IntegerGene.
	 * @return the new created IntegerGene with the given {@code value}.
	 * @throws IllegalArgumentException if min > max.
	 */
	public static IntegerGene valueOf(long value, long min, long max) {
		return valueOf(
			Integer64.valueOf(value), 
			Integer64.valueOf(min), 
			Integer64.valueOf(max)
		);
	}
	
	/**
	 * Create a new random IntegerGene. It is guaranteed that the value of the IntegerGene
	 * lies in the closed interval [min, max].
	 * 
	 * @param min the minimal value of the IntegerGene to create.
	 * @param max the maximal value of the IntegerGene to create.
	 * @return the new created Integer gene.
	 * @throws IllegalArgumentException if min > max
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public static IntegerGene valueOf(final Integer64 min, final Integer64 max) {
		final Random random = RandomRegistry.getRandom();
		final int value = min.intValue() + 
						random.nextInt(max.intValue() - min.intValue() + 1);
		return valueOf(Integer64.valueOf(value), min, max);
	}
	
	/**
	 * Create a new random IntegerGene. It is guaranteed that the value of the IntegerGene
	 * lies in the closed interval [min, max].
	 * 
	 * @param min the minimal value of the IntegerGene to create.
	 * @param max the maximal value of the IntegerGene to create.
	 * @return the new created Integer gene.
	 * @throws IllegalArgumentException if min > max
	 */
	public static IntegerGene valueOf(final int min, final int max) {
		return valueOf(Integer64.valueOf(min), Integer64.valueOf(max));
	}
	
	static final XMLFormat<IntegerGene> XML = new XMLFormat<IntegerGene>(IntegerGene.class) {
		@Override
		public IntegerGene newInstance(final Class<IntegerGene> cls, final InputElement element)
			throws XMLStreamException
		{
			final long min = element.getAttribute("min", 0);
			final long max = element.getAttribute("max", 100);
			final long value = element.<Long>getNext();
			return IntegerGene.valueOf(value, min, max);
		}
		@Override
		public void write(final IntegerGene gene, final OutputElement element) 
			throws XMLStreamException 
		{
			element.setAttribute("min", gene.getMinValue().longValue());
			element.setAttribute("max", gene.getMaxValue().longValue());
			element.add(gene.getAllele().longValue());
		}
		@Override
		public void read(final InputElement element, final IntegerGene gene) 
			throws XMLStreamException 
		{
		}
	};
	
}







