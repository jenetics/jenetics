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
package org.jenetics.xml.stream;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static org.jenetics.xml.stream.Lists.immutable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Simplifies the usage of the {@link XMLStreamReader}.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
abstract class AbstractReader<T> implements Reader<T> {

	private final String _name;
	private final List<String> _attrs;

	AbstractReader(final String name, final List<String> attrs) {
		_name = requireNonNull(name);
		_attrs = immutable(attrs);
	}

	/**
	 * Return the name of the element processed by this reader.
	 *
	 * @return the element name the reader is processing
	 */
	@Override
	public String name() {
		return _name;
	}

	/**
	 * Return the list of element attributes to read.
	 *
	 * @return the list of element attributes to read
	 */
	@Override
	public List<String> attrs() {
		return _attrs;
	}

	@Override
	public String toString() {
		return format("Reader[%s]", name());
	}

}

/**
 * The main XML reader implementation.
 *
 * @param <T> the object type
 */
final class ReaderImpl<T> extends AbstractReader<T> {

	private final List<Reader<?>> _children;
	private final Map<String, Reader<?>> _childMap = new HashMap<>();
	private final Function<Object[], T> _creator;

	ReaderImpl(
		final String name,
		final List<String> attrs,
		final List<Reader<?>> children,
		final Function<Object[], T> creator
	) {
		super(name, attrs);
		_creator = requireNonNull(creator);

		_children = requireNonNull(children);
		for (Reader<?> child : children) {
			_childMap.put(child.name(), child);
		}
	}

	@Override
	public T read(final XMLStreamReader reader)
		throws XMLStreamException
	{
		final Map<String, Object> param = new HashMap<>();
		for (String attr : attrs()) {
			final Object value = reader.getAttributeValue(null, attr);
			param.put(attr, value);
		}

		while (reader.hasNext()) {
			switch (reader.next()) {
				case XMLStreamReader.START_ELEMENT:
					final Reader<?> child = _childMap.get(reader.getLocalName());
					// Special handling for XML list readers.
					if (child instanceof ListReader<?>) {
						@SuppressWarnings("unchecked")
						final List<Object> result = (List<Object>)param
							.computeIfAbsent(
								child.name(), key -> new ArrayList<>());

						result.add(
							((ListReader<?>)child)
								.adoptee()
								.read(reader)
						);

					} else if (child != null) {
						param.put(child.name(), child.read(reader));
					}
					break;
				case XMLStreamReader.END_ELEMENT:
					if (name().equals(reader.getLocalName())) {
						final int size = attrs().size() + _children.size();
						final Object[] args = new Object[size];

						for (int i = 0; i < attrs().size(); ++i) {
							args[i] = param.get(attrs().get(i));
						}
						for (int i = 0; i < _children.size(); ++i) {
							args[attrs().size() + i] =
								param.get(_children.get(i).name());
						}

						return _creator.apply(args);
					}
			}
		}

		throw new XMLStreamException(format(
			"Premature end of file while reading '%s'.", name()
		));
	}

}

/**
 * Special reader implementation for reading text content of leaf nodes.
 */
final class TextReader extends AbstractReader<String> {

	TextReader(final String name, final List<String> attrs) {
		super(name, attrs);
	}

	@Override
	public String read(final XMLStreamReader reader)
		throws XMLStreamException
	{
		final StringBuilder result = new StringBuilder();
		while (reader.hasNext()) {
			switch (reader.next()) {
				case XMLStreamReader.CHARACTERS:
				case XMLStreamReader.CDATA:
					result.append(reader.getText());
					break;
				case XMLStreamReader.END_ELEMENT:
					if (name().equals(reader.getLocalName())) {
						return result.toString();
					}
			}
		}

		throw new XMLStreamException(format(
			"Premature end of file while reading '%s'.", name()
		));
	}

}

/**
 * List element reader.
 *
 * @param <T> the object type.
 */
final class ListReader<T> extends AbstractReader<List<T>> {

	private final Reader<T> _adoptee;

	ListReader(final Reader<T> adoptee) {
		super(adoptee.name(), emptyList());
		_adoptee = requireNonNull(adoptee);
	}

	Reader<T> adoptee() {
		return _adoptee;
	}

	@Override
	public List<T> read(final XMLStreamReader reader)
		throws XMLStreamException
	{
		throw new UnsupportedOperationException();
	}

}
