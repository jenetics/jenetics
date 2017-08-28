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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static io.jenetics.xml.stream.Writer.elem;
import static io.jenetics.xml.stream.Writer.text;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import io.jenetics.xml.stream.Reader;
import io.jenetics.xml.stream.Writer;

/**
 * The sample class contains the results of one test run of all parameters. This
 * class is <i>mutable</i> and <b>not</b> thread safe.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.0
 * @since 3.4
 */
public final class Sample implements Serializable {

	private static final long serialVersionUID = 1L;

	private final double[] _values;

	private Sample(final double[] values) {
		_values = requireNonNull(values);
	}

	/**
	 * Return the number of {@code double} <i>slots</i> the {@code Sample} class
	 * stores.
	 *
	 * @return the number of {@code double} <i>slots</i>
	 */
	public int size() {
		return _values.length;
	}

	/**
	 * Insert the given value at the next free <i>slot</i> (position).
	 *
	 * @param value the value to insert
	 * @throws IndexOutOfBoundsException if all sample values has been set
	 * @throws IllegalArgumentException if the given value is not a number,
	 *         {@code Double.isNaN(value)} returns {@code true}
	 */
	void add(final double value) {
		if (Double.isNaN(value)) {
			throw new IllegalArgumentException();
		}

		_values[nextIndex()] = value;
	}

	/**
	 * Return the index of the next free {@code double} <i>slot</i>. If all
	 * <i>slots</i> are occupied, {@code -1} is returned.
	 *
	 * @return the index of the next free {@code double} <i>slot</i>, or
	 *         {@code -1} if all <i>slots</i> are occupied
	 */
	public int nextIndex() {
		int index = -1;
		for (int i = 0; i <  _values.length && index == -1; ++i) {
			if (Double.isNaN(_values[i])) {
				index = i;
			}
		}

		return index;
	}

	/**
	 * Test whether all sample <i>slots</i> are occupied.
	 *
	 * @return {@code true} if all sample <i>slots</i> are occupied,
	 *         {@code false} otherwise
	 */
	public boolean isFull() {
		return nextIndex() == -1;
	}

	/**
	 * Return the sample {@code double} values (slots).
	 *
	 * @return the sample {@code double} values (slots).
	 */
	public double[] values() {
		return _values.clone();
	}

	/**
	 * Returns a sequential {@link DoubleStream} with the <i>slot</i> values of
	 * this {@code Sample} object.
	 *
	 * @return a {@code DoubleStream} for the sample values
	 */
	public DoubleStream stream() {
		return Arrays.stream(_values);
	}

	/**
	 * Return the <i>slot</i> value at the given position.
	 *
	 * @param index the value index
	 * @return the sample value at the given {@code index}
	 * @throws IndexOutOfBoundsException if the {@code index} is out of range
	 *         {@code (index < 0 || index >= size())}
	 */
	public double get(final int index) {
		return _values[index];
	}

	/**
	 * Create a new {@code Sample} object, with all slots available, with the
	 * same size as {@code this} one.
	 *
	 * @return a new empty {@code Sample} object
	 */
	public Sample newSample() {
		return of(size());
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(_values);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Sample &&
			Arrays.equals(_values, ((Sample)obj)._values);
	}

	@Override
	public String toString() {
		return Arrays.toString(_values);
	}

	/**
	 * Create a new {@code Sample} object with the {@code values}.
	 *
	 * @param values the slot values of the created {@code Sample} object
	 * @return a new {@code Sample} object with the {@code values}
	 * @throws IllegalArgumentException if the length of the given {@code values}
	 *         is zero
	 */
	public static Sample of(final double[] values) {
		if (values.length == 0) {
			throw new IllegalArgumentException(
				"The given values must not be empty."
			);
		}

		return new Sample(values);
	}

	/**
	 * Create a new {@code Sample} object of the given {@code length}.
	 *
	 * @param length the length of the new {@code Sample} object
	 * @return a new {@code Sample} object of the given {@code length}
	 * @throws IllegalArgumentException if the given {@code length} is smaller
	 *         then one
	 */
	public static Sample of(final int length) {
		if (length < 1) {
			throw new IllegalArgumentException(format(
				"Length must not be smaller than one, but was %d.", length
			));
		}

		final double[] values = new double[length];
		Arrays.fill(values, Double.NaN);
		return of(values);
	}

	/* *************************************************************************
	 *  XML reader/writer
	 * ************************************************************************/

	public static final Writer<Sample> WRITER = elem(
			"sample", text().map(s -> DoubleStream.of(s.values())
				.mapToObj(Double::toString).collect(Collectors.joining(" ")))
		);


	public static final Reader<Sample> READER = Reader.elem(
		(Object[] v) -> Sample.of(
			Stream.of((String[])v[0])
				.mapToDouble(Double::parseDouble)
				.toArray()
		),
		"sample",
		Reader.text().map(s -> s.split("\\s"))
	);

}
