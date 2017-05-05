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

import java.util.Collection;
import java.util.Objects;
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

	public default Writer<T> using(final Writer<? super T> writer) {
		return (data, xml) -> {
			if (data != null) {
				writer.write(data, xml);
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
	 * @param property the elements to write
	 * @param <T> the writer base type
	 * @return a new writer instance
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <T> Writer<T> text(final Function<? super T, ?> property) {
		requireNonNull(property);

		return (data, writer) -> {
			if (data != null) {
				final Object value = property.apply(data);
				if (value != null) {
					writer.writeCharacters(value.toString());
				}
			}
		};
	}

	public static <T> Writer<T> text() {
		return text(Objects::toString);
	}

	/**
	 * Create a new {@code Writer}, which writes elements of the given
	 * {@code name} for each of the given properties.
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
	 * @param name the element name
	 * @param properties the elements to write
	 * @param <T> the writer base type
	 * @param <P> the element type.
	 * @return a new writer instance
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <T, P> Writer<T> elems(
		final String name,
		final Function<T, ? extends Iterable<? extends P>> properties
	) {
		requireNonNull(name);
		requireNonNull(properties);

		return (value, writer) -> {
			if (value != null) {
				final Iterable<? extends P> it = properties.apply(value);
				if (it != null) {
					for (P v : it) {
						if (v != null) {
							writer.writeStartElement(name);
							writer.writeCharacters(v.toString());
							writer.writeEndElement();
						}
					}
				}
			}
		};
	}

	/*
	public static <T, P> Writer<T> elems(
		final String name,
		final Function<T, ? extends Iterable<? extends P>> properties,
		final Writer<? super P> writer
	) {
		return (value, w) -> {
			if (value != null) {
				final Iterable<? extends P> it = properties.apply(value);
				if (it != null) {
					for (P v : it) {
						w.writeStartElement(name);
						if (v != null) {
							writer.write(v, w);
						}
						w.writeEndElement();
					}
				}
			}
		};
	}
	*/

	/**
	 * Create a new {@code Writer}, which writes a collection of values of a
	 * given type.
	 *
	 * @param name the root element name
	 * @param writer the child element writer
	 * @param <T> the element type
	 * @return a new writer instance
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <T> Writer<Collection<T>> elems(
		final String name,
		final Writer<? super T> writer
	) {
		requireNonNull(name);
		requireNonNull(writer);

		return (data, w) -> {
			if (data != null) {
				w.writeStartElement(name);

				for (T value : data) {
					if (value != null) {
						writer.write(value, w);
					}
				}
				w.writeEndElement();
			}
		};
	}

	/**
	 * Creates a new {@code Writer}, which writes the given {@code children} as
	 * sub-elements, defined by the given {@code childWriter}.
	 *
	 * @param children the sub-elements to write
	 * @param writer the sub-element writer
	 * @param <T> the writer base type
	 * @param <P> the element type.
	 * @return a new writer instance
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <T, P> Writer<T> elems(
		final Function<T, ? extends Iterable<? extends P>> children,
		final Writer<? super P> writer
	) {
		requireNonNull(children);
		requireNonNull(writer);

		return (value, baseWriter) -> {
			final Iterable<? extends P> values = children.apply(value);
			if (values != null) {
				for (P val : values) {
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
