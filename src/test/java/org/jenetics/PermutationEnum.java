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

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public enum PermutationEnum implements XMLSerializable {
	_1,
	_2,
	_3,
	_4,
	_5,
	_6,
	_7,
	_8,
	_9,
	_10,
	_11,
	_12,
	_13,
	_14,
	_15;
	
	
	static final XMLFormat<PermutationEnum> 
	XML = new XMLFormat<PermutationEnum>(PermutationEnum.class) 
	{		
		@Override
		public PermutationEnum newInstance(
			final Class<PermutationEnum> cls, final InputElement xml
		) 
			throws XMLStreamException 
		{
			return PermutationEnum.valueOf(xml.getText().toString());
		}
		@Override
		public void write(final PermutationEnum gene, final OutputElement xml) 
			throws XMLStreamException 
		{
			xml.addText(gene.name());
		}
		@Override
		public void read(final InputElement element, final PermutationEnum gene) {
		}
	};
	
}
