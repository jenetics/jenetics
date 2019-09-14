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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.xml;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.1
 * @since 4.1
 */
public class XMLStreamWriterTest {

	static void write(final IntegerChromosome ch, final XMLStreamWriter xml)
		throws XMLStreamException
	{
		xml.writeStartElement("int-chromosome");
		xml.writeAttribute("length", Integer.toString(ch.length()));
		xml.writeStartElement("min");
		xml.writeCharacters(ch.getMin().toString());
		xml.writeEndElement();
		xml.writeStartElement("max");
		xml.writeCharacters(ch.getMax().toString());
		xml.writeEndElement();
		xml.writeStartElement("alleles");
		for (IntegerGene gene : ch) {
			xml.writeStartElement("allele");
			xml.writeCharacters(gene.getAllele().toString());
			xml.writeEndElement();
		}
		xml.writeEndElement();
		xml.writeEndElement();
	}

	public static void main(final String[] args) throws Exception {
		final IntegerChromosome ch = IntegerChromosome.of(
			Integer.MIN_VALUE, Integer.MAX_VALUE, 3
		);

		final XMLOutputFactory factory = XMLOutputFactory.newFactory();
		final XMLStreamWriter xml = factory.createXMLStreamWriter(System.out);
		try {
			write(ch, xml);
		} finally {
			xml.close();
		}
	}

	/*
<int-chromosome length="3">
    <min>-2147483648</min>
    <max>2147483647</max>
    <alleles>
        <allele>-1878762439</allele>
        <allele>-957346595</allele>
        <allele>-88668137</allele>
    </alleles>
</int-chromosome>
	 */

}
