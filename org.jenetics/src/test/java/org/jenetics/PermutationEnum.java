/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics;

import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-04-27 $</em>
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
