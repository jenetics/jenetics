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
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static javax.xml.stream.XMLStreamConstants.CDATA;
import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;


/**
 * XML reader class, used for reading objects in XML format. The {@code Reader}
 * needed for creating an {@code IntegerChromosome} from it's XML representation,
 * <pre> {@code
 * <int-chromosome length="3">
 *     <min>-2147483648</min>
 *     <max>2147483647</max>
 *     <alleles>
 *         <allele>-1878762439</allele>
 *         <allele>-957346595</allele>
 *         <allele>-88668137</allele>
 *     </alleles>
 * </int-chromosome>
 * }</pre>
 * , will look like in the following code snippet:
 *
 * <pre>{@code
 * final Reader<IntegerChromosome> reader =
 *     elem("int-chromosome",
 *         attr("length").map(Integer::parseInt),
 *         elem("min", text().map(Integer::parseInt)),
 *         elem("max", text().map(Integer::parseInt)),
 *         elem("alleles",
 *             elems(elem("allele", text().map(Integer::parseInt)))
 *         ),
 *         (Object[] v) -> {
 *             final int length = (int)v[0];
 *             final int min = (int)v[1];
 *             final int max = (int)v[2];
 *             final List<Integer> alleles = (List<Integer>)v[3];
 *             assert alleles.size() == length;
 *
 *             return IntegerChromosome.of(
 *                 alleles.stream()
 *                     .map(value -> IntegerGene.of(value, min, max)
 *                     .toArray(IntegerGene[]::new)
 *             );
 *         }
 *     );
 * }</pre>
 *
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
		return format("Reader[%s, %s]", name(), type());
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

		return new ElemReader<>(name, mapper, asList(children), Type.ELEM);
	}

	@SuppressWarnings("unchecked")
	public static <T> Reader<T> elem(final String name, final Reader<?>... children) {
		return elem(name, v -> v.length > 0 ? (T)v[0] : null, children);
	}

	public static Reader<String> text() {
		return new TextReader();
	}

	public static <T> Reader<List<T>> elems(final Reader<? extends T> reader) {
		return new ListReader<T>(reader);
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
	public String read(final XMLStreamReader xml) throws XMLStreamException {
		xml.require(START_ELEMENT, null, null);
		return xml.getAttributeValue(null, name());
	}

}

/**
 * Reader implementation for reading the text of the current node.
 */
final class TextReader extends Reader<String> {

	TextReader() {
		super("", Type.TEXT);
	}

	@Override
	public String read(final XMLStreamReader xml) throws XMLStreamException {
		final StringBuilder out = new StringBuilder();

		int type = xml.getEventType();
		do {
			out.append(xml.getText());
		} while (xml.hasNext() && (type = xml.next()) == CHARACTERS || type == CDATA);


		return out.toString();
	}
}

final class ListReader<T> extends Reader<List<T>> {

	private final Reader<? extends T> _adoptee;

	ListReader(final Reader<? extends T> adoptee) {
		super(adoptee.name(), Type.LIST);
		_adoptee = adoptee;
	}

	@Override
	public List<T> read(final XMLStreamReader xml) throws XMLStreamException {
		xml.require(START_ELEMENT, null, name());
		return Collections.singletonList(_adoptee.read(xml));
	}
}

/**
 * The main XML reader implementation.
 *
 * @param <T> the object type
 */
final class ElemReader<T> extends Reader<T> {

	private final Function<Object[], T> _creator;
	private final List<Reader<?>> _children;

	ElemReader(
		final String name,
		final Function<Object[], T> creator,
		final List<Reader<?>> children,
		final Type type
	) {
		super(name, type);
		if (children.stream().filter(r -> r.type() == Type.TEXT).count() > 1) {
			throw new IllegalArgumentException("Found more than one TEXT reader.");
		}

		_creator = requireNonNull(creator);
		_children = requireNonNull(children);
	}

	@Override
	public T read(final XMLStreamReader xml)
		throws XMLStreamException
	{
		xml.require(START_ELEMENT, null, name());

		final Map<String, ReaderResult> results = ReaderResult.of(_children);
		final ReaderResult text = results.values().stream()
			.filter(r -> r.reader().type() == Type.TEXT)
			.findFirst()
			.orElse(null);

		for (ReaderResult result: results.values()) {
			if (result.reader().type() == Type.ATTR) {
				result.put(result.reader().read(xml));
			}
		}

		if (xml.hasNext()) {
			xml.next();

			boolean hasNext = false;
			do {
				switch (xml.getEventType()) {
					case START_ELEMENT:
						final ReaderResult result = results.get(xml.getLocalName());
						if (result != null) {
							result.put(result.reader().read(xml));
							if (xml.hasNext()) {
								hasNext = true;
								xml.next();
							} else {
								hasNext = false;
							}
						}

						break;
					case CHARACTERS:
					case CDATA:
						if (text != null) {
							text.put(text.reader().read(xml));
							hasNext = true;
						}
						break;
					case END_ELEMENT:
						if (name().equals(xml.getLocalName())) {
							final Object[] array = new Object[results.size()];
							for (ReaderResult r : results.values()) {
								array[r.index()] = r.value();
							}
							return _creator.apply(array);
						}
				}

			} while (hasNext);
		}

		throw new XMLStreamException(format(
			"Premature end of file while reading '%s'.", name()
		));
	}

}

interface ReaderResult {
	void put(final Object value);
	Reader<?> reader();
	int index();
	Object value();

	static Map<String, ReaderResult> of(final List<Reader<?>> readers) {
		final Map<String, ReaderResult> results = new HashMap<>();


		for (int i = 0; i < readers.size(); ++i) {
			final Reader<?> reader = readers.get(i);
			final ReaderResult result; switch (reader.type()) {
				case TEXT: result = new ValueResult(reader, i); break;
				case LIST: result = new ListResult(reader, i); break;
				default: result = new ValueResult(reader, i);
			}

			results.put(reader.name(), result);
		}

		return results;
	}
}

final class ValueResult implements ReaderResult {
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

final class TextResult implements ReaderResult {
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

final class ListResult implements ReaderResult {
	private final Reader<?> _reader;
	private final int _index;
	private final List<Object> _value = new ArrayList<>();

	ListResult(final Reader<?> reader, final int index) {
		_reader = reader;
		_index = index;
	}

	@Override
	public void put(final Object value) {
		if (value instanceof List<?>) {
			_value.addAll((List<?>)value);
		} else {
			_value.add(value);
		}
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
