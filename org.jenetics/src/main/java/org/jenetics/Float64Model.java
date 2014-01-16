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

import org.jscience.mathematics.number.Float64;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date$</em>
 * @since @__version__@
 */
@XmlRootElement(name = "org.jscience.mathematics.number.Float64")
@XmlType(name = "org.jscience.mathematics.number.Float64")
@XmlAccessorType(XmlAccessType.FIELD)
final class Float64Model {
	@XmlAttribute public double value;

	Float64Model(final double value) {
		this.value = value;
	}

	public final static class Adapter
		extends XmlAdapter<Float64Model, Float64>
	{
		@Override
		public Float64Model marshal(final Float64 value) {
			return new Float64Model(value.doubleValue());
		}

		@Override
		public Float64 unmarshal(final Float64Model model) {
			return Float64.valueOf(model.value);
		}
	}

	static final Adapter ADAPTER = new Adapter();
}
