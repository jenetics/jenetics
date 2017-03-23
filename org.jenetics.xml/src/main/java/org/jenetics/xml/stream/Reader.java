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
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.jenetics.xml.stream.XMLReader.Attr;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class Reader<T> {

	private final String _name;
	private final List<Attr> _attrs;

	private final List<XMLReader<?>> _children;
	private final Map<String, XMLReader<?>> _childMap = new HashMap<>();
	private final Function<Object[], T> _creator;

	Reader(
		final String name,
		final List<Attr> attrs,
		final List<XMLReader<?>> children,
		final Function<Object[], T> creator
	) {
		_name = requireNonNull(name);
		_attrs = requireNonNull(attrs);
		_creator = requireNonNull(creator);

		_children = requireNonNull(children);
		for (XMLReader<?> child : children) {
			_childMap.put(child.name(), child);
		}
	}

	public T read(final XMLStreamReader reader, final boolean lenient)
		throws XMLStreamException
	{
		final Map<String, Object> param = new HashMap<>();
		for (Attr attr : _attrs) {
			final Object value = reader.getAttributeValue(null, attr.name);
			param.put(attr.name, value);
		}

		while (reader.hasNext()) {
			switch (reader.next()) {
				case XMLStreamReader.START_ELEMENT:
					final XMLReader<?> child = _childMap.get(reader.getLocalName());
					try {
						// Special handling for XML list readers.
						if (child instanceof XMLListReader<?>) {
							@SuppressWarnings("unchecked")
							final List<Object> result = (List<Object>)param
								.computeIfAbsent(
									child.name(), key -> new ArrayList<>());

							result.add(
								((XMLListReader<?>)child)
									.adoptee()
									.read(reader, lenient)
							);

						} else if (child != null) {
							param.put(child.name(), child.read(reader, lenient));
						}
					} catch (XMLStreamException e) {
						if (!lenient) {
							throw e;
						}
					}

					break;
				case XMLStreamReader.END_ELEMENT:
					if (_name.equals(reader.getLocalName())) {
						final int size = _attrs.size() + _children.size();
						final Object[] args = new Object[size];

						for (int i = 0; i < _attrs.size(); ++i) {
							args[i] = param.get(_attrs.get(i).name);
						}
						for (int i = 0; i < _children.size(); ++i) {
							args[_attrs.size() + i] =
								param.get(_children.get(i).name());
						}

						return _creator.apply(args);
					}
			}
		}

		throw new XMLStreamException(format(
			"Premature end of file while reading '%s'.", _name
		));
	}


}
