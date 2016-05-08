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
package org.jenetics.tool.trial;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jenetics.util.ISeq;

/**
 * Collection of sample {@code Data} objects.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.4
 * @since 3.4
 */
@XmlJavaTypeAdapter(DataSet.Model.Adapter.class)
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
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "data-set")
	@XmlType(name = "org.jenetics.tool.trial.DataSet")
	@XmlAccessorType(XmlAccessType.FIELD)
	static final class Model {

		@XmlElement(name = "data")
		public List<Data> dataSet;

		public static final class Adapter extends XmlAdapter<Model, DataSet> {
			@Override
			public Model marshal(final DataSet data) {
				final Model model = new Model();
				model.dataSet = data.values().asList();
				return model;
			}

			@Override
			public DataSet unmarshal(final Model model) {
				return new DataSet(ISeq.of(model.dataSet));
			}
		}

	}

}
