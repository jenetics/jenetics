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

import org.jenetics.util.RandomRegistry;

import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

/**
 * Implementation of a BitGene.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: BitGene.java,v 1.6 2010-02-02 19:35:12 fwilhelm Exp $
 */
public class BitGene 
	implements Gene<Boolean, BitGene>, Comparable<BitGene>, XMLSerializable 
{
	private static final long serialVersionUID = 71303038065442905L;
	
	public static final BitGene TRUE = new BitGene(true);
	public static final BitGene FALSE = new BitGene(false);
	public static final BitGene ONE = TRUE;
	public static final BitGene ZERO = FALSE;
	
	private final boolean _value;
	
	protected BitGene(final boolean value) {
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
	
	public boolean booleanValue() {
		return _value;
	}
	
	@Override
	public Boolean getAllele() {
		return _value;
	}
	
	@Override
	public boolean isValid() {
		return true;
	}
	
	@Override
	public BitGene copy() {
		return _value ? TRUE : FALSE;
	}
	
	/**
	 * Create a new, <em>random</em> gene.
	 */
	@Override
	public BitGene newInstance() {
		final Random random = RandomRegistry.getRandom();
		return random.nextBoolean() ? TRUE : FALSE;
	}
	
	@Override
	public int hashCode() {
		return _value ? 7 : 13;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof BitGene)) {
			return false;
		}
		final BitGene gene = (BitGene)obj;
		return _value == gene._value;
	}
	
	@Override
	public int compareTo(final BitGene gene) {
		if (this == gene) {
			return 0;
		} else if (_value && !gene._value) {
			return 1;
		} else if (!_value && gene._value) {
			return -1;
		} else {
			return 0;
		}
	}

	@Override
	public String toString() {
		return getClass().getName() + ": " + _value;
	}
	
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



