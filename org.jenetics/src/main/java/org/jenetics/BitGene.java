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
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics;

import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import org.jenetics.util.Function;
import org.jenetics.util.RandomRegistry;

/**
 * Implementation of a BitGene.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date: 2012-11-06 $</em>
 */
public enum BitGene
	implements
		Gene<Boolean, BitGene>,
		XMLSerializable
{

	FALSE(false),
	TRUE(true);

	private static final long serialVersionUID = 2L;

	public static final BitGene ZERO = FALSE;
	public static final BitGene ONE = TRUE;

	private final boolean _value;

	private BitGene(final boolean value) {
		_value = value;
	}

	/**
	 * Return the value of the BitGene.
	 *
	 * @return The value of the BitGene.
	 */
	public final boolean getBit() {
		return _value;
	}

	/**
	 * Return the {@code boolean} value of this gene.
	 *
	 * @see #getAllele()
	 *
	 * @return the {@code boolean} value of this gene.
	 */
	public boolean booleanValue() {
		return _value;
	}

	@Override
	public Boolean getAllele() {
		return _value;
	}

	/**
	 * Return always {@code true}.
	 *
	 * @return always {@code true}.
	 */
	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public BitGene copy() {
		return this;
	}

	/**
	 * Create a new, <em>random</em> gene.
	 */
	@Override
	public BitGene newInstance() {
		return RandomRegistry.getRandom().nextBoolean() ? TRUE : FALSE;
	}

	@Override
	public String toString() {
		return Boolean.toString(_value);
	}


	/* *************************************************************************
	 *  Property access methods methods
	 * ************************************************************************/

	/**
	 * Converter for accessing the allele from a given gene.
	 */
	public static final Function<BitGene, Boolean> Allele =
		new Function<BitGene, Boolean>() {
			@Override public Boolean apply(final BitGene value) {
				return value._value;
			}
		};


	/* *************************************************************************
	 *  XML object serialization
	 * ************************************************************************/

	static final XMLFormat<BitGene>
	XML = new XMLFormat<BitGene>(BitGene.class)
	{
		private static final String VALUE = "value";

		@Override
		public BitGene newInstance(final Class<BitGene> cls, final InputElement element)
			throws XMLStreamException
		{
			final boolean value = element.getAttribute(VALUE, true);
			return value ? BitGene.TRUE : BitGene.FALSE;
		}
		@Override
		public void write(final BitGene gene, final OutputElement element)
			throws XMLStreamException
		{
			element.setAttribute(VALUE, gene._value);
		}
		@Override
		public void read(final InputElement element, final BitGene gene) {
		}
	};

}



