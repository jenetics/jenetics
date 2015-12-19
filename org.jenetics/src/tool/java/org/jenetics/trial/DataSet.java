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
package org.jenetics.trial;

import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@XmlJavaTypeAdapter(DataSet.Model.Adapter.class)
public final class DataSet {

	private final int _paramCount;
	private final ISeq<Data> _sets;

	private DataSet(final int paramCount, final ISeq<Data> sets) {
		_paramCount = paramCount;
		_sets = requireNonNull(sets);

		if (_sets.isEmpty()) {
			throw new IllegalArgumentException(
				"Data set names must not be empty."
			);
		}
	}

	public int getParamCount() {
		return _paramCount;
	}

	public ISeq<Data> getSets() {
		return _sets;
	}

	public static DataSet of(final int paramCount, final String... dataSetNames) {
		return new DataSet(
			paramCount,
			Arrays.stream(dataSetNames)
				.map(name -> Data.of(name, singletonList(Sample.of(paramCount))))
				.collect(ISeq.toISeq())
		);
	}

	@XmlRootElement(name = "data-set")
	@XmlType(name = "org.jenetics.tool.DataSet")
	@XmlAccessorType(XmlAccessType.FIELD)
	static final class Model {

		@XmlAttribute(name = "param-count")
		public int paramCount;

		@XmlElement(name = "data")
		public List<Data> dataSet;

		public static final class Adapter extends XmlAdapter<Model, DataSet> {
			@Override
			public Model marshal(final DataSet data) {
				final Model model = new Model();
				model.paramCount = data.getParamCount();
				model.dataSet = data.getSets().asList();
				return model;
			}

			@Override
			public DataSet unmarshal(final Model model) {
				return new DataSet(model.paramCount, ISeq.of(model.dataSet));
			}
		}

	}

}
