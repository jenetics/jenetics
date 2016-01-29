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

import static java.util.Objects.requireNonNull;
import static org.jenetics.tool.trial.SampleSummary.toSampleSummary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@XmlJavaTypeAdapter(Data.Model.Adapter.class)
public final class Data {

	private final String _name;
	private final List<Sample> _samples = new ArrayList<>();

	private Data(final String name, final List<Sample> samples) {
		if (samples.isEmpty()) {
			throw new IllegalArgumentException("Sample list must not be empty.");
		}

		_name = requireNonNull(name);
		_samples.addAll(samples);
	}

	public String getName() {
		return _name;
	}

	public int dataSize() {
		return _samples.size();
	}

	public int sampleSize() {
		return _samples.get(0).size();
	}

	public Sample currentSample() {
		Sample sample = _samples.get(_samples.size() - 1);
		if (sample.isFull()) {
			sample = sample.newSample();
			_samples.add(sample);
		}

		return sample;
	}

	public int nextParamIndex() {
		return currentSample().nextIndex();
	}

	public SampleSummary summary() {
		return _samples.stream()
			.collect(toSampleSummary(sampleSize()));
	}

	public static Data of(final String name, final List<Sample> samples) {
		return new Data(name, samples);
	}


	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "data")
	@XmlType(name = "org.jenetics.tool.Data")
	@XmlAccessorType(XmlAccessType.FIELD)
	final static class Model {

		@XmlAttribute
		public String name;

		@XmlElement(name = "sample")
		public List<String> samples;

		public static final class Adapter extends XmlAdapter<Model, Data> {
			@Override
			public Model marshal(final Data data) {
				final Model model = new Model();
				model.name = data._name;
				model.samples = data._samples.stream()
					.map(s -> s.stream().mapToObj(Double::toString)
								.collect(Collectors.joining(" ")))
					.collect(Collectors.toList());
				return model;
			}

			@Override
			public Data unmarshal(final Model model) {
				return Data.of(
					model.name,
					model.samples.stream()
						.map(s -> Arrays.stream(s.split("\\s"))
							.mapToDouble(Double::parseDouble).toArray())
						.map(Sample::of)
						.collect(Collectors.toList())
				);
			}
		}

	}
}
