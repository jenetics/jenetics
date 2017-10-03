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
package io.jenetics.xml.stream;

import static java.util.Objects.requireNonNull;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.9
 * @since 3.9
 */
class XMLWriterProxy implements AutoCloseableXMLStreamWriter {
	private final XMLStreamWriter _adoptee;

	XMLWriterProxy(final XMLStreamWriter writer) {
		_adoptee = requireNonNull(writer);
	}

	@Override
	public void writeStartElement(final String localName)
		throws XMLStreamException
	{
		_adoptee.writeStartElement(localName);
	}

	@Override
	public void writeStartElement(
		final String namespaceURI,
		final String localName
	)
		throws XMLStreamException
	{
		_adoptee.writeStartElement(namespaceURI, localName);
	}

	@Override
	public void writeStartElement(
		final String prefix,
		final String localName,
		final String namespaceURI
	)
		throws XMLStreamException
	{
		_adoptee.writeStartElement(prefix, localName, namespaceURI);
	}

	@Override
	public void writeEmptyElement(
		final String namespaceURI,
		final String localName
	)
		throws XMLStreamException
	{
		_adoptee.writeEmptyElement(namespaceURI, localName);
	}

	@Override
	public void writeEmptyElement(
		final String prefix,
		final String localName,
		final String namespaceURI
	)
		throws XMLStreamException
	{
		_adoptee.writeEmptyElement(prefix, localName, namespaceURI);
	}

	@Override
	public void writeEmptyElement(final String localName)
		throws XMLStreamException
	{
		_adoptee.writeEmptyElement(localName);
	}

	@Override
	public void writeEndElement() throws XMLStreamException {
		_adoptee.writeEndElement();
	}

	@Override
	public void writeEndDocument() throws XMLStreamException {
		_adoptee.writeEndDocument();
	}

	@Override
	public void close() throws XMLStreamException {
		_adoptee.close();
	}

	@Override
	public void flush() throws XMLStreamException {
		_adoptee.flush();
	}

	@Override
	public void writeAttribute(final String localName, final String value)
		throws XMLStreamException
	{
		_adoptee.writeAttribute(localName, value);
	}

	@Override
	public void writeAttribute(
		final String prefix,
		final String namespaceURI,
		final String localName,
		final String value
	)
		throws XMLStreamException
	{
		_adoptee.writeAttribute(prefix, namespaceURI, localName, value);
	}

	@Override
	public void writeAttribute(
		final String namespaceURI,
		final String localName,
		final String value
	)
		throws XMLStreamException
	{
		_adoptee.writeAttribute(namespaceURI, localName, value);
	}

	@Override
	public void writeNamespace(final String prefix, final String namespaceURI)
		throws XMLStreamException
	{
		_adoptee.writeNamespace(prefix, namespaceURI);
	}

	@Override
	public void writeDefaultNamespace(final String namespaceURI)
		throws XMLStreamException
	{
		_adoptee.writeDefaultNamespace(namespaceURI);
	}

	@Override
	public void writeComment(final String data) throws XMLStreamException {
		_adoptee.writeComment(data);
	}

	@Override
	public void writeProcessingInstruction(final String target)
		throws XMLStreamException
	{
		_adoptee.writeProcessingInstruction(target);
	}

	@Override
	public void writeProcessingInstruction(
		final String target,
		final String data
	)
		throws XMLStreamException
	{
		_adoptee.writeProcessingInstruction(target, data);
	}

	@Override
	public void writeCData(final String data) throws XMLStreamException {
		_adoptee.writeCData(data);
	}

	@Override
	public void writeDTD(final String dtd) throws XMLStreamException {
		_adoptee.writeDTD(dtd);
	}

	@Override
	public void writeEntityRef(final String name) throws XMLStreamException {
		_adoptee.writeEntityRef(name);
	}

	@Override
	public void writeStartDocument() throws XMLStreamException {
		_adoptee.writeStartDocument();
	}

	@Override
	public void writeStartDocument(final String version) throws XMLStreamException {
		_adoptee.writeStartDocument(version);
	}

	@Override
	public void writeStartDocument(final String encoding, final String version)
		throws XMLStreamException
	{
		_adoptee.writeStartDocument(encoding, version);
	}

	@Override
	public void writeCharacters(final String text) throws XMLStreamException {
		_adoptee.writeCharacters(text);
	}

	@Override
	public void writeCharacters(
		final char[] text,
		final int start,
		final int len
	)
		throws XMLStreamException
	{
		_adoptee.writeCharacters(text, start, len);
	}

	@Override
	public String getPrefix(final String uri) throws XMLStreamException {
		return _adoptee.getPrefix(uri);
	}

	@Override
	public void setPrefix(final String prefix, final String uri)
		throws XMLStreamException
	{
		_adoptee.setPrefix(prefix, uri);
	}

	@Override
	public void setDefaultNamespace(final String uri) throws XMLStreamException {
		_adoptee.setDefaultNamespace(uri);
	}

	@Override
	public void setNamespaceContext(final NamespaceContext context)
		throws XMLStreamException
	{
		_adoptee.setNamespaceContext(context);
	}

	@Override
	public NamespaceContext getNamespaceContext() {
		return _adoptee.getNamespaceContext();
	}

	@Override
	public Object getProperty(final String name)
		throws IllegalArgumentException
	{
		return _adoptee.getProperty(name);
	}

}
