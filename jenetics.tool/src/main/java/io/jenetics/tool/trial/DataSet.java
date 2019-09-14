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
import static io.jenetics.xml.stream.Writer.elems;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import io.jenetics.util.ISeq;

import io.jenetics.xml.stream.Reader;
import io.jenetics.xml.stream.Writer;

/**
 * Collection of sample {@code Data} objects.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.0
 * @since 3.4
 */
public final class DataSet implements Serializable {

	private static final long serialVersionUID = 1L;

	private final ISeq<Data> _sets;

	private DataSet(final ISeq<Data> sets) {
		_sets = requireNonNull(sets);

		if (_sets.isEmpty()) {
			throw new IllegalArgumentException(
				"Data set names must not be empty."
			);
		}
	}

	public ISeq<Data> values() {
		return _sets;
	}

	public Data get(final String name) {
		return _sets.stream()
			.filter(d -> d.getName().equals(name))
			.findFirst()
			.get();
	}

	public int nextParamIndex() {
		final ISeq<Integer> indexes = _sets.map(Data::nextParamIndex);
		if (!indexes.forAll(i -> indexes.get(0).equals(i))) {
			throw new IllegalStateException("Inconsistent state.");
		}

		return indexes.get(0);
	}

	public int dataSize() {
		return _sets.get(0).dataSize();
	}

	public void add(final double[] values) {
		if (values.length != _sets.length()) {
			throw new IllegalArgumentException(format(
				"Expected %d values, but got %d.", _sets.length(), values.length
			));
		}

		for (int i = 0; i < values.length; ++i) {
			_sets.get(i).currentSample().add(values[i]);
		}
	}

	/**
	 * Create a new {@code DataSet} object with the given number of parameters
	 * and the data set names.
	 *
	 * @param parameterCount the number of parameters one data sample consist of
	 * @param dataSetNames the names of the created {@code Data} sets
	 * @return a new data set object
	 */
	public static DataSet of(
		final int parameterCount,
		final String... dataSetNames
	) {
		return new DataSet(
			Arrays.stream(dataSetNames)
				.map(name -> Data.of(name, parameterCount))
				.collect(ISeq.toISeq())
		);
	}


	/* *************************************************************************
	 *  XML reader/writer
	 * ************************************************************************/

	public static final Writer<DataSet> WRITER = elem(
		"data-set",
		elems(Data.WRITER).map(d -> d._sets)
	);

	@SuppressWarnings("unchecked")
	public static final Reader<DataSet> READER = Reader.elem(
		(Object[] v) -> new DataSet(ISeq.of((List<Data>)v[0])),
		"data-set",
		Reader.elems(Data.READER)
	);

}
