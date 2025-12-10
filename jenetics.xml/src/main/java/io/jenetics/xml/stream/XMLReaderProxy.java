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
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.9
 * @since 3.9
 */
class XMLReaderProxy implements AutoCloseableXMLStreamReader {

	private final XMLStreamReader _adoptee;

	XMLReaderProxy(final XMLStreamReader reader) {
		_adoptee = requireNonNull(reader);
	}

	@Override
	public Object getProperty(final String name) throws IllegalArgumentException {
		return _adoptee.getProperty(name);
	}

	@Override
	public int next() throws XMLStreamException {
		return _adoptee.next();
	}

	@Override
	public void require(
		final int type,
		final String namespaceURI,
		final String localName
	)
		throws XMLStreamException
	{
		_adoptee.require(type, namespaceURI, localName);
	}

	@Override
	public String getElementText() throws XMLStreamException {
		return _adoptee.getElementText();
	}

	@Override
	public int nextTag() throws XMLStreamException {
		return _adoptee.nextTag();
	}

	@Override
	public boolean hasNext() throws XMLStreamException {
		return _adoptee.hasNext();
	}

	@Override
	public void close() throws XMLStreamException {
		_adoptee.close();
	}

	@Override
	public String getNamespaceURI(final String prefix) {
		return _adoptee.getNamespaceURI(prefix);
	}

	@Override
	public boolean isStartElement() {
		return _adoptee.isStartElement();
	}

	@Override
	public boolean isEndElement() {
		return _adoptee.isEndElement();
	}

	@Override
	public boolean isCharacters() {
		return _adoptee.isCharacters();
	}

	@Override
	public boolean isWhiteSpace() {
		return _adoptee.isWhiteSpace();
	}

	@Override
	public String getAttributeValue(
		final String namespaceURI,
		final String localName
	) {
		return _adoptee.getAttributeValue(namespaceURI, localName);
	}

	@Override
	public int getAttributeCount() {
		return _adoptee.getAttributeCount();
	}

	@Override
	public QName getAttributeName(final int index) {
		return _adoptee.getAttributeName(index);
	}

	@Override
	public String getAttributeNamespace(final int index) {
		return _adoptee.getAttributeNamespace(index);
	}

	@Override
	public String getAttributeLocalName(final int index) {
		return _adoptee.getAttributeLocalName(index);
	}

	@Override
	public String getAttributePrefix(final int index) {
		return _adoptee.getAttributePrefix(index);
	}

	@Override
	public String getAttributeType(final int index) {
		return _adoptee.getAttributeType(index);
	}

	@Override
	public String getAttributeValue(final int index) {
		return _adoptee.getAttributeValue(index);
	}

	@Override
	public boolean isAttributeSpecified(final int index) {
		return _adoptee.isAttributeSpecified(index);
	}

	@Override
	public int getNamespaceCount() {
		return _adoptee.getNamespaceCount();
	}

	@Override
	public String getNamespacePrefix(final int index) {
		return _adoptee.getNamespacePrefix(index);
	}

	@Override
	public String getNamespaceURI(final int index) {
		return _adoptee.getNamespaceURI(index);
	}

	@Override
	public NamespaceContext getNamespaceContext() {
		return _adoptee.getNamespaceContext();
	}

	@Override
	public int getEventType() {
		return _adoptee.getEventType();
	}

	@Override
	public String getText() {
		return _adoptee.getText();
	}

	@Override
	public char[] getTextCharacters() {
		return _adoptee.getTextCharacters();
	}

	@Override
	public int getTextCharacters(
		final int sourceStart,
		final char[] target,
		final int targetStart,
		final int length
	)
		throws XMLStreamException
	{
		return _adoptee.getTextCharacters(sourceStart, target, targetStart, length);
	}

	@Override
	public int getTextStart() {
		return _adoptee.getTextStart();
	}

	@Override
	public int getTextLength() {
		return _adoptee.getTextLength();
	}

	@Override
	public String getEncoding() {
		return _adoptee.getEncoding();
	}

	@Override
	public boolean hasText() {
		return _adoptee.hasText();
	}

	@Override
	public Location getLocation() {
		return _adoptee.getLocation();
	}

	@Override
	public QName getName() {
		return _adoptee.getName();
	}

	@Override
	public String getLocalName() {
		return _adoptee.getLocalName();
	}

	@Override
	public boolean hasName() {
		return _adoptee.hasName();
	}

	@Override
	public String getNamespaceURI() {
		return _adoptee.getNamespaceURI();
	}

	@Override
	public String getPrefix() {
		return _adoptee.getPrefix();
	}

	@Override
	public String getVersion() {
		return _adoptee.getVersion();
	}

	@Override
	public boolean isStandalone() {
		return _adoptee.isStandalone();
	}

	@Override
	public boolean standaloneSet() {
		return _adoptee.standaloneSet();
	}

	@Override
	public String getCharacterEncodingScheme() {
		return _adoptee.getCharacterEncodingScheme();
	}

	@Override
	public String getPITarget() {
		return _adoptee.getPITarget();
	}

	@Override
	public String getPIData() {
		return _adoptee.getPIData();
	}

}
