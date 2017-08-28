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

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import io.jenetics.xml.stream.AutoCloseableXMLStreamWriter;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.9
 * @since 3.9
 */
public class EmptyXMLStreamWriter
	implements
		XMLStreamWriter,
		AutoCloseableXMLStreamWriter
{

	private final OutputStream _out;

	public EmptyXMLStreamWriter(final OutputStream out) {
		_out = requireNonNull(out);
	}

	@Override
	public void writeStartElement(String localName) throws XMLStreamException {
		try {
			_out.write(localName.length());
		} catch (IOException e) {
			throw new XMLStreamException(e);
		}
	}

	@Override
	public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
	}

	@Override
	public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
	}

	@Override
	public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
	}

	@Override
	public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
	}

	@Override
	public void writeEmptyElement(String localName) throws XMLStreamException {
	}

	@Override
	public void writeEndElement() throws XMLStreamException {
	}

	@Override
	public void writeEndDocument() throws XMLStreamException {
	}

	@Override
	public void close() throws XMLStreamException {
	}

	@Override
	public void flush() throws XMLStreamException {
	}

	@Override
	public void writeAttribute(String localName, String value) throws XMLStreamException {
	}

	@Override
	public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
	}

	@Override
	public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
	}

	@Override
	public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
	}

	@Override
	public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
	}

	@Override
	public void writeComment(String data) throws XMLStreamException {
	}

	@Override
	public void writeProcessingInstruction(String target) throws XMLStreamException {
	}

	@Override
	public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
	}

	@Override
	public void writeCData(String data) throws XMLStreamException {
	}

	@Override
	public void writeDTD(String dtd) throws XMLStreamException {
	}

	@Override
	public void writeEntityRef(String name) throws XMLStreamException {
	}

	@Override
	public void writeStartDocument() throws XMLStreamException {
	}

	@Override
	public void writeStartDocument(String version) throws XMLStreamException {
	}

	@Override
	public void writeStartDocument(String encoding, String version) throws XMLStreamException {
	}

	@Override
	public void writeCharacters(String text) throws XMLStreamException {
	}

	@Override
	public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
	}

	@Override
	public String getPrefix(String uri) throws XMLStreamException {
		return null;
	}

	@Override
	public void setPrefix(String prefix, String uri) throws XMLStreamException {
	}

	@Override
	public void setDefaultNamespace(String uri) throws XMLStreamException {
	}

	@Override
	public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
	}

	@Override
	public NamespaceContext getNamespaceContext() {
		return null;
	}

	@Override
	public Object getProperty(String name) throws IllegalArgumentException {
		return null;
	}
}
