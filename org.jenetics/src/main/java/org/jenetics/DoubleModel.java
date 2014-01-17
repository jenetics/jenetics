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
package org.jenetics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.jenetics.internal.util.jaxb;

import org.jenetics.util.Function;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date$</em>
 * @since @__version__@
 */
@XmlRootElement(name = "java.lang.Double")
@XmlType(name = "java.lang.Double")
@XmlAccessorType(XmlAccessType.FIELD)
final class DoubleModel {

	@XmlAttribute
	public double value;

	public final static class Adapter
		extends XmlAdapter<DoubleModel, Double>
	{
		@Override
		public DoubleModel marshal(final Double value) {
			final DoubleModel model = new DoubleModel();
			model.value = value;
			return model;
		}

		@Override
		public Double unmarshal(final DoubleModel model) {
			return model.value;
		}
	}

	static final Adapter Adapter = new Adapter();

	static final Function<Double, DoubleModel> Marshaller =
		jaxb.marshaller(Adapter);

	static final Function<DoubleModel, Double> Unmarshaller =
		jaxb.unmarshaller(Adapter);

}
