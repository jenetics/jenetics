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

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@FunctionalInterface
public interface Writer<T> {

	/**
	 * Write the data of type {@code T} to the given XML stream writer.
	 *
	 * @param value the value to write
	 * @param writer the XML data sink
	 * @throws XMLStreamException if writing the data fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public void write(final T value, final XMLStreamWriter writer)
		throws XMLStreamException;

	/**
	 * Maps this writer to a different base type.
	 *
	 * @param mapper the mapper function
	 * @param <B> the new writer type
	 * @return a writer with changed type
	 */
	public default <B> Writer<B> map(final Function<? super B, ? extends T> mapper) {
		return (data, xml) -> {
			if (data != null) {
				final T value = mapper.apply(data);
				if (value != null) {
					write(value, xml);
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
	 * <pre>{@code
	 * final Writer<String> writer1 = elem("element",
	 *     attr("value");
	 * );
	 * final Writer<MyObject> writer2 = elem("element",
	 *     attr("value").map(object::getMyObject);
	 * );
	 * }</pre>
	 *
	 * @see #attr(String, Object)
	 *
	 * @param name the attribute name
	 * @param <T> the writer base type
	 * @return a new writer instance
	 * @throws NullPointerException if the attribute {@code name} is {@code null}
	 */
	public static <T> Writer<T> attr(final String name) {
		requireNonNull(name);

		return (data, xml) -> {
			if (data != null) {
				xml.writeAttribute(name, data.toString());
			}
		};
	}

	/**
	 * Writes the attribute with the given {@code name} and {@code value} to the
	 * current <em>outer</em> element.
	 *
	 * <pre>{@code
	 * final Writer<MyObject> = elem("element",
	 *     attr("version", "1.0"),
	 *     attr("value").map(MyObject::getValue)
	 * );
	 * }</pre>
	 *
	 * @param name the attribute name
	 * @param value the attribute value
	 * @param <T> the writer base type
	 * @return a new writer instance
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <T> Writer<T> attr(
		final String name,
		final Object value
	) {
		return attr(name).map(data -> value);
	}


	/* *************************************************************************
	 * Creating element writer.
	 * ************************************************************************/

	/**
	 * Create a new {@code Writer}, which writes a XML element with the given
	 * name and writes the given children into it.
	 *
	 * <pre>{@code
	 * final Writer<DoubleChromosome> = elem("double-chromosome",
	 *     attr("min", DoubleChromosome::getMin),
	 *     attr("max", DoubleChromosome::getMax),
	 *     attr("length", DoubleChromosome::length),
	 *     elems("allele", ch -> ch.toSeq().map(DoubleGene::getAllele))
	 * );
	 * }</pre>
	 *
	 * @param name the root element name
	 * @param children the XML child elements
	 * @param <T> the writer base type
	 * @return a new writer instance
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	@SafeVarargs
	public static <T> Writer<T> elem(
		final String name,
		final Writer<? super T>... children
	) {
		requireNonNull(name);
		requireNonNull(children);

		return (data, writer) -> {
			if (data != null) {
				writer.writeStartElement(name);
				for (Writer<? super T> child : children) {
					child.write(data, writer);
				}
				writer.writeEndElement();
			}
		};
	}


	/**
	 * Create a new {@code Writer}, which writes given property to the current
	 * outer element.
	 *
	 * <pre>{@code
	 * elem("bit-chromosome",
	 *     attr("length", BitChromosome::length),
	 *     attr("one-probability", BitChromosome::getOneProbability),
	 *     elem(BitChromosome::toCanonicalString)
	 * );
	 * }</pre>
	 *
	 * @param mapper the elements to write
	 * @param <T> the writer base type
	 * @return a new writer instance
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <T> Writer<T> text(final Function<? super T, ?> mapper) {
		return text().map(mapper);
	}

	public static <T> Writer<T> text() {
		return (data, writer) -> {
			if (data != null) {
				writer.writeCharacters(data.toString());
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
	public static <T> Writer<Iterable<T>> elems(final Writer<? super T> writer) {
		requireNonNull(writer);

		return (values, baseWriter) -> {
			if (values != null) {
				for (T val : values) {
					if (val != null) {
						writer.write(val, baseWriter);
					}
				}
			}
		};
	}

	public static <T> Writer<T> doc(final Writer<T> writer) {
		return (data, w) -> {
			w.writeStartDocument("UTF-8", "1.0");
			writer.write(data, w);
			w.writeEndDocument();
		};
	}

}
