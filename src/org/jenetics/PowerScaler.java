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

import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import org.jscience.mathematics.number.Float64;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: PowerScaler.java,v 1.2 2008-08-26 22:29:33 fwilhelm Exp $
 */
public class PowerScaler implements FitnessScaler<Float64>, XMLSerializable {
	private static final long serialVersionUID = -5895077899454677843L;
	
	public static final PowerScaler SQR_SCALER = new PowerScaler(2);
	public static final PowerScaler SQRT_SCALER = new PowerScaler(0.5);

	private final double _exponent;
	
	public PowerScaler(final double exponent) {
		this._exponent = exponent;
	}

	@Override
	public Float64 scale(final Float64 value) {
		return value.pow(_exponent);
	}

	static final XMLFormat<PowerScaler> 
	XML = new XMLFormat<PowerScaler>(PowerScaler.class) {
		@Override
		public PowerScaler newInstance(final Class<PowerScaler> cls, final InputElement xml) 
			throws XMLStreamException 
		{
			final double exponent = xml.getAttribute("exponent", 1.0);
			return new PowerScaler(exponent);
		}
		@Override
		public void write(final PowerScaler ps, final OutputElement xml)
			throws XMLStreamException 
		{
			xml.setAttribute("exponent", ps._exponent);
		}
		@Override
		public void read(final InputElement xml, final PowerScaler ps) 
			throws XMLStreamException 
		{	
		}
	};
}
