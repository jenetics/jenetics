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

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@XmlJavaTypeAdapter(Sample.Model.Adapter.class)
public final class Sample {

	private final double[] _values;

	private Sample(final double[] values) {
		_values = requireNonNull(values);
	}

	public int size() {
		return _values.length;
	}

	public void add(final double value) {
		if (value == Double.NaN) {
			throw new IllegalArgumentException();
		}

		_values[nextIndex()] = value;
	}

	public int nextIndex() {
		int index = -1;
		for (int i = 0; i <  _values.length && index == -1; ++i) {
			if (Double.isNaN(_values[i])) {
				index = i;
			}
		}

		return index;
	}

	public boolean isFull() {
		return nextIndex() == -1;
	}

	public double[] getValues() {
		return _values.clone();
	}

	public Sample newSample() {
		return of(size());
	}

	public static Sample of(final double[] values) {
		return new Sample(values);
	}

	public static Sample of(final int length) {
		final double[] values = new double[length];
		Arrays.fill(values, Double.NaN);

		return of(values);
	}

	@Override
	public String toString() {
		return Arrays.toString(_values);
	}


	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "sample")
	@XmlType(name = "org.jenetics.tool.Sample")
	@XmlAccessorType(XmlAccessType.FIELD)
	static final class Model {

		@XmlList
		public double[] sample;

		public static final class Adapter extends XmlAdapter<Model, Sample> {
			@Override
			public Model marshal(final Sample sample) {
				final Model model = new Model();
				model.sample = sample._values;
				return model;
			}

			@Override
			public Sample unmarshal(final Model model) {
				return Sample.of(model.sample);
			}
		}
	}

}
