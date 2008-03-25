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
import javolution.xml.stream.XMLStreamException;

/**
 *  A {@link FitnessFunction} which always returns a given constant value.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 *
 * @param <T> the gene type.
 * @version $Id: ConstantFitnessFunction.java,v 1.1 2008-03-25 18:31:55 fwilhelm Exp $
 */
public class ConstantFitnessFunction<T extends Gene<?>> implements FitnessFunction<T> {
	private static final long serialVersionUID = 8766537513371578351L;
	
	private final double _value;
	
	public ConstantFitnessFunction() {
		this(0);
	}
	
	public ConstantFitnessFunction(final double value) {
		this._value = value;
	}
	
	@Override
	public double evaluate(final Genotype<T> genotype) {
		return _value;
	}

	
	@SuppressWarnings("unchecked")
	static final XMLFormat<ConstantFitnessFunction> 
	XML = new XMLFormat<ConstantFitnessFunction>(ConstantFitnessFunction.class) {
		@Override
		public ConstantFitnessFunction newInstance(
			final Class<ConstantFitnessFunction> cls, final InputElement xml
		)  throws XMLStreamException {
			final double value = xml.getAttribute("value", 1.0);
			return new ConstantFitnessFunction(value);
		}
		@Override
		public void write(final ConstantFitnessFunction ff, final OutputElement xml) 
			throws XMLStreamException 
		{
			xml.setAttribute("value", ff._value);
		}
		@Override
		public void read(final InputElement xml, final ConstantFitnessFunction a) {
		}
	};
}
