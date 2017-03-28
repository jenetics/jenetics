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

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface Reader<T> {


	/**
	 * Return the name of the element processed by this reader.
	 *
	 * @return the element name the reader is processing
	 */
	public String name();

	/**
	 * Return the list of element attributes to read.
	 *
	 * @return the list of element attributes to read
	 */
	public List<String> attrs();

	/**
	 * Read the given type from the underlying XML stream {@code reader}.
	 *
	 * @param reader the underlying XML stream {@code reader}
	 * @return the read type, maybe {@code null}
	 * @throws XMLStreamException if an error occurs while reading the value
	 */
	public abstract T read(final XMLStreamReader reader)
		throws XMLStreamException;


	public static List<String> attrs(final String... attrs) {
		return Arrays.asList(attrs);
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
		return new ReaderImpl<T>(name, attrs, asList(children), creator);
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

	/**
	 * Create a reader for a leaf element with the given {@code name}.
	 *
	 * @param name the element
	 * @return the reader for the given element
	 */
	static Reader<String> of(final String name) {
		return new TextReader(name, emptyList());
	}

	/**
	 * Return a reader which reads a list of elements.
	 *
	 * @param reader the basic element reader
	 * @param <T> the object type
	 * @return the reader for the given elements
	 */
	static <T> Reader<List<T>> ofList(final Reader<T> reader) {
		return new ListReader<T>(reader);
	}

}
