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

import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.util.jaxb.marshal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

import javax.xml.bind.Marshaller;
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
 * Represents an function testing measurement environment.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@XmlJavaTypeAdapter(Measurement.Model.Adapter.class)
public final class Measurement<T> {

	private final String _name;
	private final String _description;

	private final Params<T> _params;
	private final DataSet _dataSet;

	private Measurement(
		final String name,
		final String description,
		final Params<T> params,
		final DataSet dataSet
	) {
		_name = requireNonNull(name);
		_description = description;
		_params = requireNonNull(params);
		_dataSet = requireNonNull(dataSet);
	}

	public String getName() {
		return _name;
	}

	public Optional<String> getDescription() {
		return Optional.ofNullable(_description);
	}

	public Params<T> getParams(Measurement<T> this) {
		return _params;
	}

	public int sampleSize() {
		return _dataSet.sampleSize();
	}

	public void sample(final Function<T, double[]> function) {
		_params.get()
			.subSeq(_dataSet.nextParamIndex())
			.forEach(p -> _dataSet.add(function.apply(p)));
	}

	public void write(final OutputStream out)
		throws IOException
	{
		try {
			final Marshaller marshaller = jaxb.context().createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(marshal(this), out);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	public static <T> Measurement<T> of(
		final String name,
		final String description,
		final Params<T> params,
		final String... dataSetNames
	) {
		return new Measurement<T>(
			name,
			description,
			params,
			DataSet.of(params.size(), dataSetNames)
		);
	}

	public static <T> Measurement<T> read(final InputStream in)
		throws IOException
	{
		return null;
	}

	public static void main(final String[] args) throws Exception {
		final Measurement<String> measurement = Measurement.of(
			"Some name", "Some description",
			Params.of("Strings", ISeq.of("p1", "p2", "p3", "p4", "p5")),
			"fitness", "generation"
		);

		final Random random = new Random();

		for (int i = 0; i < 10; ++i) {
			measurement.sample(p -> {
				return new double[] {
					random.nextDouble(), random.nextDouble()
				};
			});
		}

		measurement.write(System.out);
	}


	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "measurement")
	@XmlType(name = "org.jenetics.tool.Measurement")
	@XmlAccessorType(XmlAccessType.FIELD)
	@SuppressWarnings({"unchecked", "rawtypes"})
	static final class Model {

		@XmlAttribute
		public String name;

		@XmlAttribute
		public String description;

		@XmlElement(name = "params", required = true, nillable = false)
		public Params params;

		@XmlElement(name = "data-set", required = true, nillable = false)
		public DataSet dataSet;

		public static final class Adapter
			extends XmlAdapter<Model, Measurement>
		{
			@Override
			public Model marshal(final Measurement data) {
				final Model model = new Model();
				model.name = data._name;
				model.description = data._description;
				model.params = data.getParams();
				model.dataSet = data._dataSet;
				return model;
			}

			@Override
			public Measurement unmarshal(final Model model) {
				return new Measurement(
					model.name,
					model.description,
					model.params,
					model.dataSet
				);
			}
		}
	}

}
