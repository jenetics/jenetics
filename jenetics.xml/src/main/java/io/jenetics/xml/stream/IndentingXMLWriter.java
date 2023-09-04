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

import java.util.ArrayDeque;
import java.util.Deque;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * {@link XMLStreamWriter} proxy for writing XML indentations.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.9
 * @since 3.9
 */
final class IndentingXMLWriter extends XMLWriterProxy {

	private enum State {
		SEEN_NOTHING,
		SEEN_ELEMENT,
		SEEN_DATA
	}

	private static final String NEW_LINE = System.lineSeparator();

	private final String _indent;

	private State _state;
	private final Deque<State> _states = new ArrayDeque<>();
	private int _depth;

	IndentingXMLWriter(final XMLStreamWriter writer, final String indent) {
		super(writer);
		_state = State.SEEN_NOTHING;
		_indent = indent;
		_depth = 0;
	}

	private void onStartElement() throws XMLStreamException {
		_states.push(State.SEEN_ELEMENT);
		_state = State.SEEN_NOTHING;
		if (_depth > 0) {
			super.writeCharacters(NEW_LINE);
		}

		doIndent();
		++_depth;
	}

	private void onEndElement() throws XMLStreamException {
		--_depth;
		if (_state == State.SEEN_ELEMENT) {
			super.writeCharacters(NEW_LINE);
			doIndent();
		}

		_state = _states.pop();
	}

	private void onEmptyElement() throws XMLStreamException {
		_state = State.SEEN_ELEMENT;
		if(_depth > 0) {
			super.writeCharacters(NEW_LINE);
		}

		doIndent();
	}

	private void doIndent() throws XMLStreamException {
		if (_depth > 0) {
			for(int i = 0; i < _depth; ++i) {
				super.writeCharacters(_indent);
			}
		}

	}

	@Override
	public void writeStartDocument() throws XMLStreamException {
		super.writeStartDocument();
		super.writeCharacters(NEW_LINE);
	}

	@Override
	public void writeStartDocument(final String version)
		throws XMLStreamException
	{
		super.writeStartDocument(version);
		super.writeCharacters(NEW_LINE);
	}

	@Override
	public void writeStartDocument(final String encoding, final String version)
		throws XMLStreamException
	{
		super.writeStartDocument(encoding, version);
		super.writeCharacters(NEW_LINE);
	}

	@Override
	public void writeStartElement(final String localName)
		throws XMLStreamException
	{
		onStartElement();
		super.writeStartElement(localName);
	}

	@Override
	public void writeStartElement(
		final String namespaceURI,
		final String localName
	)
		throws XMLStreamException
	{
		onStartElement();
		super.writeStartElement(namespaceURI, localName);
	}

	@Override
	public void writeStartElement(
		final String prefix,
		final String localName,
		final String namespaceURI
	)
		throws XMLStreamException
	{
		onStartElement();
		super.writeStartElement(prefix, localName, namespaceURI);
	}

	@Override
	public void writeEmptyElement(
		final String namespaceURI,
		final String localName
	)
		throws XMLStreamException
	{
		onEmptyElement();
		super.writeEmptyElement(namespaceURI, localName);
	}

	@Override
	public void writeEmptyElement(
		final String prefix,
		final String localName,
		final String namespaceURI
	)
		throws XMLStreamException
	{
		onEmptyElement();
		super.writeEmptyElement(prefix, localName, namespaceURI);
	}

	@Override
	public void writeEmptyElement(final String localName)
		throws XMLStreamException
	{
		onEmptyElement();
		super.writeEmptyElement(localName);
	}

	@Override
	public void writeEndElement() throws XMLStreamException {
		onEndElement();
		super.writeEndElement();
	}

	@Override
	public void writeCharacters(final String text) throws XMLStreamException {
		_state = State.SEEN_DATA;
		super.writeCharacters(text);
	}

	@Override
	public void writeCharacters(
		final char[] text,
		final int start,
		final int len
	)
		throws XMLStreamException
	{
		_state = State.SEEN_DATA;
		super.writeCharacters(text, start, len);
	}

	@Override
	public void writeCData(final String data) throws XMLStreamException {
		_state = State.SEEN_DATA;
		super.writeCData(data);
	}
}
