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
package io.jenetics.tool.trial;

import static java.util.Objects.requireNonNull;
import static io.jenetics.xml.stream.Writer.attr;
import static io.jenetics.xml.stream.Writer.elem;
import static io.jenetics.xml.stream.Writer.elems;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import io.jenetics.util.ISeq;
import io.jenetics.xml.stream.Reader;
import io.jenetics.xml.stream.Writer;

/**
 * Collection of parameters the function under test is tested with.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.0
 * @since 3.4
 */
public final class Params<T> implements Iterable<T>, Serializable {

	private static final long serialVersionUID = 1L;

	private final String _name;
	private final ISeq<T> _params;

	private Params(final String name, final ISeq<T> params) {
		_name = requireNonNull(name);
		_params = requireNonNull(params);
	}

	/**
	 * Return the name of the parameter collection.
	 *
	 * @return the name of the parameter collection
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Return the parameter with the given {@code index}.
	 *
	 * @param index the parameter index
	 * @return the parameter with the given {@code index}
	 * @throws IndexOutOfBoundsException if the {@code index} is out of range
	 *         {@code (index < 0 || index >= size())}
	 */
	public T get(final int index) {
		return _params.get(index);
	}

	/**
	 * Return the number of parameters this collection contains.
	 *
	 * @return the number of parameters
	 */
	public int size() {
		return _params.size();
	}

	/**
	 * Return the parameter values.
	 *
	 * @return the parameter values
	 */
	public ISeq<T> values() {
		return _params;
	}

	/**
	 * Return the parameter values as stream.
	 *
	 * @return the parameter values as stream
	 */
	public Stream<T> stream() {
		return _params.stream();
	}

	@Override
	public Iterator<T> iterator() {
		return _params.iterator();
	}

	@Override
	public int hashCode() {
		return _params.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Params<?> other &&
			_params.equals(other._params);
	}

	@Override
	public String toString() {
		return _params.toString();
	}

	/**
	 * Return a new parameters object.
	 *
	 * @param name the name of the parameters
	 * @param params the actual parameters
	 * @param <T> the parameter type
	 * @throws NullPointerException if one of the parameters is {@code null}
	 * @return a new parameters object
	 */
	public static <T> Params<T> of(
		final String name,
		final ISeq<T> params
	) {
		return new Params<>(name, params);
	}


	/* *************************************************************************
	 *  XML reader/writer
	 * ************************************************************************/

	public static <T> Writer<Params<T>> writer(final Writer<? super T> writer) {
		return elem(
			"params",
			attr("name").map(Params::getName),
			elems("param", writer)
		);
	}

	@SuppressWarnings("unchecked")
	public static <T> Reader<Params<T>> reader(final Reader<? extends T> reader) {
		return Reader.elem(
			(Object[] v) -> Params.of(
				(String)v[0],
				ISeq.of((List<T>)v[1])
			),
			"params",
			Reader.attr("name"),
			Reader.elems(Reader.elem("param", reader))
		);
	}

}
