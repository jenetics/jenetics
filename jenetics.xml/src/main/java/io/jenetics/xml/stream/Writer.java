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

import java.util.function.Function;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * XML writer interface, used for writing objects in XML format. The following
 * XML will show the marshaled representation of an {@code IntegerChromosome}.
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
 * } </pre>
 *
 * The XML has been written by the following {@code Writer} definition.
 *
 * {@snippet lang="java":
 * final Writer<IntegerChromosome> writer =
 *     elem("int-chromosome",
 *         attr("length").map(ch -> ch.length()),
 *         elem("min", Writer.<Integer>text().map(ch -> ch.getMin())),
 *         elem("max", Writer.<Integer>text().map(ch -> ch.getMax())),
 *         elem("alleles",
 *             elems("allele",  Writer.<Integer>text())
 *                 .map(ch -> ISeq.of(ch).map(g -> g.getAllele()))
 *         )
 *     );
 * }
 *
 * How to write the XML writing is shown by the next code snippet.
 *
 * {@snippet lang="java":
 * final IntegerChromosome ch = IntegerChromosome.of(MIN_VALUE, MAX_VALUE, 3);
 * try (AutoCloseableXMLStreamWriter xml = XML.writer(out, indent)) {
 *     write(ch, xml);
 * }
 * }
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.9
 * @since 3.9
 */
@FunctionalInterface
public interface Writer<T> {

	/**
	 * Write the data of type {@code T} to the given XML stream writer.
	 *
	 * @param xml the underlying {@code XMLStreamWriter}, where the value is
	 *        written to
	 * @param data the value to write
	 * @throws XMLStreamException if writing the data fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	void write(final XMLStreamWriter xml, final T data)
		throws XMLStreamException;

	/**
	 * Maps this writer to a different base type. Mapping to a different data
	 * type is necessary when you are going to write <em>sub</em>-objects of
	 * your basic data type {@code T}. E.g. the chromosome length or the
	 * {@code min} and {@code max} value of an {@code IntegerChromosome}.
	 *
	 * @param mapper the mapper function
	 * @param <B> the new data type of returned writer
	 * @return a writer with changed type
	 */
	default <B> Writer<B> map(final Function<? super B, ? extends T> mapper) {
		return (xml, data) -> {
			if (data != null) {
				final T value = mapper.apply(data);
				if (value != null) {
					write(xml, value);
				}
			}
		};
	}


	/* *************************************************************************
	 * *************************************************************************
	 * Static factory methods.
	 * *************************************************************************
	 * ************************************************************************/

	/* *************************************************************************
	 * Creating attribute writer.
	 * ************************************************************************/

	/**
	 * Writes the attribute with the given {@code name} to the current
	 * <em>outer</em> element.
	 *
	 * {@snippet lang="java":
	 * final Writer<String> writer1 = elem("element", attr("attribute"));
	 * }
	 *
	 * @see #attr(String, Object)
	 *
	 * @param name the attribute name
	 * @param <T> the writer base type
	 * @return a new writer instance
	 * @throws NullPointerException if the attribute {@code name} is {@code null}
	 */
	static <T> Writer<T> attr(final String name) {
		requireNonNull(name);

		return (xml, data) -> {
			if (data != null) {
				xml.writeAttribute(name, data.toString());
			}
		};
	}

	/**
	 * Writes the attribute with the given {@code name} and a constant
	 * {@code value} to the current <em>outer</em> element.
	 *
	 * {@snippet lang="java":
	 * final Writer<MyObject> writer = elem("element", attr("version", "1.0"));
	 * }
	 *
	 * @param name the attribute name
	 * @param value the attribute value
	 * @param <T> the writer base type
	 * @return a new writer instance
	 * @throws NullPointerException if one of the {@code name} is {@code null}
	 */
	static <T> Writer<T> attr(
		final String name,
		final Object value
	) {
		return attr(name).map(data -> value);
	}


	/* *************************************************************************
	 * Creating element writer.
	 * ************************************************************************/

	/**
	 * Create a new {@code Writer}, which writes an XML element with the given
	 * name and writes the given children into it.
	 *
	 * @param name the root element name
	 * @param children the XML child elements
	 * @param <T> the writer base type
	 * @return a new writer instance
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	@SafeVarargs
	static <T> Writer<T> elem(
		final String name,
		final Writer<? super T>... children
	) {
		requireNonNull(name);
		requireNonNull(children);

		return (xml, data) -> {
			if (data != null) {
				xml.writeStartElement(name);
				for (Writer<? super T> child : children) {
					child.write(xml, data);
				}
				xml.writeEndElement();
			}
		};
	}

	/**
	 * Create a new text {@code Writer}, which writes the given data as string
	 * to the outer element.
	 *
	 * @param <T> the data type, which is written as string to the outer element
	 * @return a new text writer
	 */
	static <T> Writer<T> text() {
		return (xml, data) -> {
			if (data != null) {
				xml.writeCharacters(data.toString());
			}
		};
	}

	/**
	 * Creates a new {@code Writer}, which writes the given {@code children} as
	 * sub-elements, defined by the given {@code childWriter}.
	 *
	 * @param name the enclosing element name used for each data value
	 * @param writer the sub-element writer
	 * @param <T> the writer base type
	 * @return a new writer instance
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	static <T> Writer<Iterable<T>> elems(
		final String name,
		final Writer<? super T> writer
	) {
		requireNonNull(name);
		requireNonNull(writer);

		return (xml, data) -> {
			if (data != null) {
				for (T value : data) {
					if (value != null) {
						xml.writeStartElement(name);
						writer.write(xml, value);
						xml.writeEndElement();
					}
				}
			}
		};
	}

	/**
	 * Creates a new {@code Writer}, which writes the given {@code children} as
	 * sub-elements, defined by the given {@code childWriter}.
	 *
	 * @param writer the sub-element writer
	 * @param <T> the writer base type
	 * @return a new writer instance
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	static <T> Writer<Iterable<T>> elems(final Writer<? super T> writer) {
		requireNonNull(writer);

		return (xml, data) -> {
			if (data != null) {
				for (T value : data) {
					if (value != null) {
						writer.write(xml, value);
					}
				}
			}
		};
	}

	/**
	 * Adds an XML prolog element written by the given {@code writer}. The default
	 * values for encoding and version are set to "UTF-8" and "1.0", respectively.
	 *
	 * <pre> {@code
	 * <?xml version="1.0" encoding="UTF-8"?>
	 * } </pre>
	 *
	 * @param writer the root element writer
	 * @param <T> the writer data type
	 * @return a new writer instance
	 */
	static <T> Writer<T> doc(final Writer<? super T> writer) {
		return (xml, data) -> {
			xml.writeStartDocument("UTF-8", "1.0");
			writer.write(xml, data);
			xml.writeEndDocument();
		};
	}


	/* *************************************************************************
	 * Service lookup
	 * ************************************************************************/


	/*
	public static abstract class Provider<T> {
		private static final Map<Class<?>, Object>
			PROVIDERS = new ConcurrentHashMap<>();

		public abstract Class<T> type();
		public abstract Writer<T> writer();

		@SuppressWarnings({"unchecked", "rawtypes"})
		public static <T> Optional<Provider<T>> of(final Class<T> type) {
			requireNonNull(type);

			return (Optional<Provider<T>>)PROVIDERS.computeIfAbsent(type, t -> {
				final ServiceLoader<Provider> loader =
					ServiceLoader.load(Provider.class);

				return StreamSupport.stream(loader.spliterator(), false)
					.filter(p -> p.type() == type)
					.findFirst();
			});
		}
	}
	*/

}
