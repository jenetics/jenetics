/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import javolution.context.ObjectFactory;
import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.jscience.mathematics.number.Float64;
import org.jscience.mathematics.structure.GroupMultiplicative;

import org.jenetics.util.Function;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.math;

/**
 * Implementation of the NumberGene which holds a 64 bit floating point number.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date$</em>
 */
public final class Float64Gene
	extends NumberGene<Float64, Float64Gene>
	implements GroupMultiplicative<Float64Gene>
{
	private static final long serialVersionUID = 1L;

	Float64Gene() {
	}

	@Override
	protected Float64 box(final java.lang.Number value) {
		return Float64.valueOf(value.doubleValue());
	}

	public Float64Gene divide(final Float64Gene gene) {
		return newInstance(_value.divide(gene._value));
	}

	@Override
	public Float64Gene inverse() {
		return newInstance(_value.inverse());
	}

	@Override
	public Float64Gene mean(final Float64Gene that) {
		return newInstance((_value.doubleValue() + that._value.doubleValue())/2.0);
	}


	/* *************************************************************************
	 *  Property access methods
	 * ************************************************************************/

	/**
	 * Converter for accessing the value from a given number gene.
	 */
	public static final Function<Float64Gene, Float64> Allele =
		new Function<Float64Gene, Float64>() {
			@Override public Float64 apply(final Float64Gene value) {
				return value._value;
			}
		};

	/**
	 * Converter for accessing the allele from a given number gene.
	 */
	public static final Function<Float64Gene, Float64> Value = Allele;

	/**
	 * Converter for accessing the allowed minimum from a given number gene.
	 */
	public static final Function<Float64Gene, Float64> Min =
		new Function<Float64Gene, Float64>() {
			@Override public Float64 apply(final Float64Gene value) {
				return value._min;
			}
		};

	/**
	 * Converter for accessing the allowed minimum from a given number gene.
	 */
	public static final Function<Float64Gene, Float64> Max =
		new Function<Float64Gene, Float64>() {
			@Override public Float64 apply(final Float64Gene value) {
				return value._max;
			}
		};

	/* *************************************************************************
	 *  Factory methods
	 * ************************************************************************/

	/**
	 * Create a new valid, <em>random</em> gene.
	 */
	@Override
	public Float64Gene newInstance() {
		return valueOf(_min, _max);
	}

	/**
	 * Create a new Float64Gene with the same limits and the given value.
	 *
	 * @param value The value of the new NumberGene.
	 * @return The new NumberGene.
	 */
	public Float64Gene newInstance(final double value) {
		return valueOf(Float64.valueOf(value), _min, _max);
	}

	@Override
	public Float64Gene newInstance(final Float64 value) {
		return valueOf(value, _min, _max);
	}


	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	private static final ObjectFactory<Float64Gene> FACTORY =
		new ObjectFactory<Float64Gene>() {
			@Override protected Float64Gene create() {
				return new Float64Gene();
			}
		};

	/**
	 * Create a new random Float64Gene with the given value and the given range.
	 * If the {@code value} isn't within the closed interval [min, max], no
	 * exception is thrown. In this case the method {@link Float64Gene#isValid()}
	 * returns {@code false}.
	 *
	 * @param value the value of the gene.
	 * @param min the minimal valid value of this gene.
	 * @param max the maximal valid value of this gene.
	 * @return the new created gene with the given {@code value}.
	 */
	public static Float64Gene valueOf(
		final double value,
		final double min,
		final double max
	) {
		return valueOf(
			Float64.valueOf(value),
			Float64.valueOf(min),
			Float64.valueOf(max)
		);
	}

	/**
	 * Create a new random Float64Gene with the given value and the given range.
	 * If the {@code value} isn't within the closed interval [min, max], no
	 * exception is thrown. In this case the method {@link Float64Gene#isValid()}
	 * returns {@code false}.
	 *
	 * @param value the value of the gene.
	 * @param min the minimal valid value of this gene.
	 * @param max the maximal valid value of this gene.
	 * @return the new created gene with the given {@code value}.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public static Float64Gene valueOf(
		final Float64 value,
		final Float64 min,
		final Float64 max
	) {
		Float64Gene gene = FACTORY.object();
		gene.set(value, min, max);
		return gene;
	}

	/**
	 * Create a new random Float64Gene. It is guaranteed that the value of the
	 * DoubleGene lies in the closed interval [min, max].
	 *
	 * @param min the minimal value of the Float64Gene to create.
	 * @param max the maximal value of the Float64Gene to create.
	 * @return the new created gene.
	 */
	public static Float64Gene valueOf(final double min, final double max) {
		return valueOf(Float64.valueOf(min), Float64.valueOf(max));
	}

	/**
	 * Create a new random Float64Gene. It is guaranteed that the value of the
	 * Float64Gene lies in the closed interval [min, max].
	 *
	 * @param min the minimal value of the Float64Gene to create.
	 * @param max the maximal value of the Float64Gene to create.
	 * @return the new created gene.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public static Float64Gene valueOf(
		final Float64 min,
		final Float64 max
	) {
		final Random random = RandomRegistry.getRandom();
		final Float64 value = Float64.valueOf(
				math.random.nextDouble(random, min.doubleValue(), max.doubleValue())
			);

		return valueOf(value, min, max);
	}


	/* *************************************************************************
	 *  XML object serialization
	 * ************************************************************************/

	static final XMLFormat<Float64Gene>
	XML = new XMLFormat<Float64Gene>(Float64Gene.class)
	{
		private static final String MIN = "min";
		private static final String MAX = "max";

		@Override
		public Float64Gene newInstance(
			final Class<Float64Gene> cls, final InputElement element
		)
			throws XMLStreamException
		{
			final double min = element.getAttribute(MIN, 0.0);
			final double max = element.getAttribute(MAX, 1.0);
			final double value = element.<Double>getNext();
			return Float64Gene.valueOf(value, min, max);
		}
		@Override
		public void write(final Float64Gene gene, final OutputElement element)
			throws XMLStreamException
		{
			element.setAttribute(MIN, gene.getMin().doubleValue());
			element.setAttribute(MAX, gene.getMax().doubleValue());
			element.add(gene.getAllele().doubleValue());
		}
		@Override
		public void read(InputElement element, Float64Gene gene)
			throws XMLStreamException
		{
		}
	};

	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private void writeObject(final ObjectOutputStream out)
		throws IOException
	{
		out.defaultWriteObject();

		out.writeDouble(_value.doubleValue());
		out.writeDouble(_min.doubleValue());
		out.writeDouble(_max.doubleValue());
	}

	private void readObject(final ObjectInputStream in)
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		set(
			Float64.valueOf(in.readDouble()),
			Float64.valueOf(in.readDouble()),
			Float64.valueOf(in.readDouble())
		);
	}

}




