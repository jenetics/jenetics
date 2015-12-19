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
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@XmlJavaTypeAdapter(Measurement.Model.Adapter.class)
public class Measurement<T> {

	private final String _name;
	private final String _description;

	private final ISeq<T> _parameters;
	private final DataSet _dataSet;

	public Measurement(
		final String name,
		final String description,
		final ISeq<T> parameters,
		final String... dataSetNames
	) {
		_name = name;
		_description = description;
		_parameters = requireNonNull(parameters);
		_dataSet = DataSet.of(parameters.size(), dataSetNames);
	}

	public Optional<String> getName() {
		return Optional.ofNullable(_name);
	}

	public Optional<String> getDescription() {
		return Optional.ofNullable(_description);
	}

	public ISeq<T> getParameters() {
		return _parameters;
	}

	public void execute(final Function<T, double[]> function) {
		/*
		final T param = _parameters.get(_data.get(0).next().nextIndex());
		final double[] results = function.apply(param);

		for (int i = 0; i < results.length; ++i) {
			_data.get(i).next().add(results[i]);
		}
		*/
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

	public static void main(final String[] args) throws Exception {
		final Measurement<String> measurement = new Measurement<String>(
			"Some name", "Some description",
			ISeq.of("p1", "p2", "p3", "p4", "p5"),
			"fitness", "generation"
		);

		measurement.write(System.out);
	}

	static double[] foo(final String parameter) {
		return null;
	}

	@XmlRootElement(name = "measurement")
	@XmlType(name = "org.jenetics.tool.Measurement")
	@XmlAccessorType(XmlAccessType.FIELD)
	static final class Model {

		@XmlAttribute
		public String name;

		@XmlAttribute
		public String description;

		@XmlElement(name = "params", required = true, nillable = false)
		public List params;

		@XmlElement(name = "data-set", required = true, nillable = false)
		public DataSet dataSet;

		public static final class Adapter extends XmlAdapter<Model, Measurement> {
			@Override
			public Model marshal(final Measurement data) {
				final Model model = new Model();
				model.name = data._name;
				model.description = data._description;
				model.params = (List) data.getParameters().stream()
					.map(p -> org.jenetics.internal.util.jaxb.Marshaller(p).apply(p))
					.collect(Collectors.toList());
				model.dataSet = data._dataSet;
				return model;
			}


			@Override
			public Measurement unmarshal(final Model model) {
				return null;
			}
		}
	}

}
