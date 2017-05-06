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

import static java.lang.Math.min;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public abstract class Reader<T> {

	static enum Type {
		ELEM, ATTR, LIST, TEXT
	}

	private final String _name;
	private final Type _type;

	Reader(final String name, final Type type) {
		_name = requireNonNull(name);
		_type = requireNonNull(type);
	}

	/**
	 * Read the given type from the underlying XML stream {@code reader}.
	 *
	 * @param reader the underlying XML stream {@code reader}
	 * @return the read type, maybe {@code null}
	 * @throws XMLStreamException if an error occurs while reading the value
	 */
	public abstract T read(final XMLStreamReader reader)
		throws XMLStreamException;

	/**
	 * Create a new reader with the new type {@code B}.
	 *
	 * @param mapper the mapper function
	 * @param <B> the target type of the new reader
	 * @return a new reader
	 */
	public <B> Reader<B> map(final Function<? super T, ? extends B> mapper) {
		requireNonNull(mapper);

		return new Reader<B>(_name, _type) {
			@Override
			public B read(final XMLStreamReader reader)
				throws XMLStreamException
			{
				return mapper.apply(Reader.this.read(reader));
			}
		};
	}

	/**
	 * Return the name of the element processed by this reader.
	 *
	 * @return the element name the reader is processing
	 */
	public String name() {
		return _name;
	}

	Type type() {
		return _type;
	}

	@Override
	public String toString() {
		return format("Reader[%s]", name());
	}

	public static List<String> attrs(final String... attrs) {
		return Arrays.asList(attrs);
	}

	public static Reader<String> attr(final String name) {
		return new AttrReader(name);
	}

	public static <T> Reader<T> elem(
		final String name,
		final Function<Object[], T> mapper,
		final Reader<?>... children
	) {
		requireNonNull(name);
		requireNonNull(mapper);
		requireNonNull(children);

		return new NodeReader<>(name, mapper, asList(children), Type.ELEM);
	}

	@SuppressWarnings("unchecked")
	public static <T> Reader<T> elem(final String name, final Reader<?>... children) {
		return elem(name, v -> (T)v[0], children);
	}

	public static <T> Reader<T> text(final Function<String, ? extends T> mapper) {
		return text().map(mapper);
	}

	public static Reader<String> text() {
		return new NodeReader("", v -> ((Object[])v)[0], emptyList(), Type.TEXT);
		/*
		return elem("", v -> {
			System.out.println("TEXT: " + v[0]);
			return (String)v[0];
		});
		*/
	}

	public static <T> Reader<List<T>> elems(final Reader<? extends T> reader) {
		System.out.println("SETUP_ELEMES: " + reader);
		return new NodeReader(reader.name(), v -> {
			System.out.println("LIST: " + Arrays.toString((Object[])v));
			return ((Object[])v)[0];
		}, asList(reader), Type.LIST);
		//return new ListReader<T>(reader);
	}


	/**
	 * Create a new {@code XMLReader} with the given elements.
	 *
	 * @param creator creates the final object from the read arguments
	 * @param name the element name
	 * @param attrs the element attributes
	 * @param children the child element readers
	 * @param <T> the object type
	 * @return the reader for the given element
	 */
	public static <T> Reader<T> of(
		final Function<Object[], T> creator,
		final String name,
		final List<String> attrs,
		final Reader<?>... children
	) {
		return new NodeReader<T>(name, creator, asList(children), Type.ELEM);
	}

	/**
	 * Create a new {@code XMLReader} with the given elements.
	 * <pre>{@code
	 * XMLReader.of(
	 *     a -> Link.of((String)a[0], (String)a[1], (String)a[2]),
	 *     "link", attr("href"),
	 *     XMLReader.of("text"),
	 *     XMLReader.of("type")
	 * )
	 * }</pre>
	 *
	 * @param creator creates the final object from the read arguments
	 * @param name the element name
	 * @param children the child element readers
	 * @param <T> the object type
	 * @return the reader for the given element
	 */
	static <T> Reader<T> of(
		final Function<Object[], T> creator,
		final String name,
		final Reader<?>... children
	) {
		return of(creator, name, emptyList(), children);
	}

	@SuppressWarnings("unchecked")
	static <T> Reader<T> of(final String name, final Reader<T> reader) {
		//return r -> reader.read(r);
		return of(a -> (T)a[0], name, reader);
	}

	/**
	 * Create a reader for a leaf element with the given {@code name}.
	 *
	 * @param name the element
	 * @return the reader for the given element
	 */
	static Reader<String> of(final String name) {
		return null;
		//return new TextReader(name);
	}

	/**
	 * Return a reader which reads a list of elements.
	 *
	 * @param reader the basic element reader
	 * @param <T> the object type
	 * @return the reader for the given elements
	 */
	static <T> Reader<List<T>> ofList(final Reader<T> reader) {
		return null; //new ListReader<T>(reader);
	}

}

/**
 * Reader implementation for reading the attribute of the current node.
 */
final class AttrReader extends Reader<String> {

	AttrReader(final String name) {
		super(name, Type.ATTR);
	}

	@Override
	public String read(final XMLStreamReader reader) throws XMLStreamException {
		return reader.getAttributeValue(null, name());
	}

}

/**
 * The main XML reader implementation.
 *
 * @param <T> the object type
 */
final class NodeReader<T> extends Reader<T> {

	private static interface Result {
		void put(final Object value);
		Reader<?> reader();
		int index();
		Object value();

		static Map<String, Result> results(final List<Reader<?>> readers) {
			final Map<String, Result> results = new HashMap<>();
			for (int i = 0; i < readers.size(); ++i) {
				final Reader<?> reader = readers.get(i);
				results.put(
					reader.name(),
					reader.type() == Type.LIST
						? new ListResult(reader, i)
						: reader.type() == Type.TEXT
							? new TextResult(reader, i)
							: new ValueResult(reader, i)
					);
			}

			return results;
		}
	}

	private static final class ValueResult implements Result {
		private final Reader<?> _reader;
		private final int _index;
		private Object _value;

		ValueResult(final Reader<?> reader, final int index) {
			_reader = reader;
			_index = index;
		}

		@Override
		public void put(final Object value) {
			_value = value;
		}

		@Override
		public Reader<?> reader() {
			return _reader;
		}

		@Override
		public int index() {
			return _index;
		}

		@Override
		public Object value() {
			return _value;
		}
	}

	private static final class TextResult implements Result {
		private final Reader<?> _reader;
		private final int _index;
		private final StringBuilder _value = new StringBuilder();

		TextResult(final Reader<?> reader, final int index) {
			_reader = reader;
			_index = index;
		}

		@Override
		public void put(final Object value) {
			_value.append(value);
		}

		@Override
		public Reader<?> reader() {
			return _reader;
		}

		@Override
		public int index() {
			return _index;
		}

		@Override
		public String value() {
			return _value.toString();
		}
	}

	private static final class ListResult implements Result {
		private final Reader<?> _reader;
		private final int _index;
		private final List<Object> _value = new ArrayList<>();

		ListResult(final Reader<?> reader, final int index) {
			_reader = reader;
			_index = index;
		}

		@Override
		public void put(final Object value) {
			_value.add(value);
		}

		@Override
		public Reader<?> reader() {
			return _reader;
		}

		@Override
		public int index() {
			return _index;
		}

		@Override
		public List<Object> value() {
			return _value;
		}
	}


	private final Function<Object[], T> _creator;
	private final List<Reader<?>> _children;

	/*s
	private final Map<String, Reader<?>> _childMap = new HashMap<>();
	private final List<Reader<?>> _attributes = new ArrayList<>();
	private final Function<Object[], T> _creator;

	private final Map<Reader<?>, Integer> _index = new HashMap<>();
*/

	NodeReader(
		final String name,
		final Function<Object[], T> creator,
		final List<Reader<?>> children,
		final Type type
	) {
		super(name, type);
		_creator = requireNonNull(creator);
		_children = requireNonNull(children);

		//elem("root", attr("size"), text());
		// <root size="3">foo</root>

	}

	@Override
	public T read(final XMLStreamReader xml)
		throws XMLStreamException
	{
		//final Map<String, List<Object>> lists = new HashMap<>();
		//final List<Object> values = new ArrayList<>();

		final Map<String, Result> results = Result.results(_children);

		for (Result result: results.values()) {
			if (result.reader().type() == Type.ATTR) {
				result.put(result.reader().read(xml));
			}
		}

		final StringBuilder text = new StringBuilder();
		while (xml.hasNext()) {
			switch (xml.next()) {
				case XMLStreamReader.START_ELEMENT:
					System.out.println("START: " + xml.getLocalName());

					final Result result = results.get(xml.getLocalName());
					if (result == null) break;

					System.out.println("PUT: " + xml.getLocalName() + "; " + result.reader());
					final Object obj = result.reader().read(xml);
					System.out.println("READ: " + result.reader().name() + "--" + obj);
					result.put(obj);



					/*
					if (child.type() == Type.LIST) {
						lists
							.computeIfAbsent(child.name(), k -> new ArrayList<>())
							.add(((ListReader<?>)child).adoptee().read(xml));

						values.add(((ListReader<?>)child).adoptee().read(xml));
					} else if (child.type() == Type.ELEM) {
						values.add(child.read(xml));
					}
					*/
					break;
				case XMLStreamReader.CHARACTERS:
				case XMLStreamReader.CDATA:
					for (Result r: results.values()) {
						System.out.println("READER: " + r.reader() + ":" + r.reader().type());
						if (r.reader().type() == Type.TEXT) {
							System.out.println("TEXT: " + xml.getText());
							r.put(xml.getText());
						}
					}
					break;
				case XMLStreamReader.END_ELEMENT:
					System.out.println("END: " + xml.getLocalName());
					if (name().equals(xml.getLocalName())) {
						final Object[] array = new Object[results.size()];
						for (Result r : results.values()) {
							array[r.index()] = r.value();
						}

						/*
						final List<Object> list = results.values().stream()
							.map(Result::value)
							.collect(Collectors.toList());

						list.add(text.toString());
						*/
						return _creator.apply(array);

						//values.add(text.toString());
						//return _creator.apply(values.toArray());
					}
			}
		}

		throw new XMLStreamException(format(
			"Premature end of file while reading '%s'.", name()
		));
	}

}

///**
// * List element reader.
// *
// * @param <T> the object type.
// */
//final class ListReader<T> extends Reader<List<T>> {
//
//	private final Reader<? extends T> _adoptee;
//
//	ListReader(final Reader<? extends T> adoptee) {
//		super(adoptee.name(), Type.LIST);
//		_adoptee = requireNonNull(adoptee);
//	}
//
//	Reader<? extends T> adoptee() {
//		return _adoptee;
//	}
//
//	@Override
//	public List<T> read(final XMLStreamReader xml)
//		throws XMLStreamException
//	{
//		/*
//		final List<Object> values = new ArrayList<>();
//
//		while (xml.hasNext()) {
//			switch (xml.next()) {
//				case XMLStreamReader.START_ELEMENT:
//					if (_adoptee.name().equals(xml.getLocalName())) {
//						values.add(_adoptee.read(xml));
//					}
//					break;
//				case XMLStreamReader.END_ELEMENT:
//					if (name().equals(xml.getLocalName())) {
//						values.add(text.toString());
//						return _creator.apply(values.toArray());
//					}
//			}
//		}
//		*/
//		return null;
//	}
//
//}
