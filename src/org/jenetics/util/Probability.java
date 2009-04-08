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

import javolution.context.ObjectFactory;
import javolution.lang.Realtime;
import javolution.text.Text;
import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import org.jscience.mathematics.structure.Structure;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Probability.java,v 1.3 2009-04-08 20:20:47 fwilhelm Exp $
 */
public final class Probability extends Number
	implements Comparable<Probability>, XMLSerializable, 
				Realtime, Structure<Probability>
{
	private static final long serialVersionUID = -3532667665043234910L;
	
	public static final double MAX_VALUE = 1;
	public static final double MIN_VALUE = 0;
	public static final Probability ONE = new Probability(1);
	public static final Probability ZERO = new Probability(0);
	
	private double _probability;
	
	Probability() {
	}
	
	Probability(final double p) {
		_probability = p;
	}

	@Override
	public Probability copy() {
		return valueOf(_probability);
	}
	
	@Override
	public double doubleValue() {
		return _probability;
	}
	
	@Override
	public float floatValue() {
		return (float)_probability;
	}

	@Override
	public int intValue() {
		return (int)Math.round(_probability);
	}

	@Override
	public long longValue() {
		return Math.round(_probability);
	}

	public boolean isLargerThan(final Probability p) {
		return _probability > p._probability;
	}
	
	public boolean isLargerThan(final double p) {
		return _probability > p;
	}

	public Probability opposite() {
		return valueOf(1.0 - _probability);
	}
	
	/**
	 * @throws NullPointerException if <code>p</code> is null.
	 */
	@Override
	public int compareTo(final Probability p) {
		if (p == null) {
			throw new NullPointerException("Probability must not be null. ");
		}
		
		int compare = 0;
		if (_probability > p._probability) {
			compare = 1;
		} else if (_probability < p._probability){
			compare = -1;
		} 
		return compare;
	}

	@Override
	public int hashCode() {
		long bits = Double.doubleToLongBits(_probability);
		return (int)(bits ^ (bits >>> 32));
	}
	
	@Override 
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Probability)) {
			return false;
		}
		
		final Probability p = (Probability)obj;
		return Double.doubleToLongBits(_probability) == 
			Double.doubleToLongBits(p._probability);
	}

	@Override
	public String toString() {
		return toText().toString();
	}
	
	@Override
	public Text toText() {
		return Text.valueOf(_probability);
	}
	
	private static final ObjectFactory<Probability> 
	FACTORY = new ObjectFactory<Probability>() {
		@Override protected Probability create() {
			return new Probability();
		}
	};
	
	/**
	 * Create a new Probability object with the value <code>p</code>.
	 * 
	 * @param p the probability; must be within the interval [0..1].
	 * @return a new Probability object with the value <code>p</code>.
	 * @throws IllegalArgumentException if the value <code>p</code> is not
	 *         within the interval [0..1]
	 */
	public static Probability valueOf(final double p) {
		if (p < 0 || p > 1) {
			throw new IllegalArgumentException("Invalid Probability: " + p);
		}
		Probability prop = FACTORY.object();
		prop._probability = p;
		return prop;
	}
	
	public static Probability valueOf(final String p) {
		return valueOf(Double.valueOf(p));
	}
	
	static final XMLFormat<Probability> XML = new XMLFormat<Probability>(Probability.class) {
		private static final String PROBABILITY = "probability";
		
		@Override
		public Probability newInstance(final Class<Probability> cls, final InputElement xml) 
			throws XMLStreamException 
		{
			final double probability = xml.getAttribute(PROBABILITY, 0);
			return Probability.valueOf(probability);
		}
		@Override
		public void write(final Probability p, final OutputElement xml) 
			throws XMLStreamException 
		{
			xml.setAttribute(PROBABILITY, p._probability);
		}
		@Override
		public void read(final InputElement xml, final Probability p) 
			throws XMLStreamException 
		{
		}
		
	};

}





