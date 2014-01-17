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
@XmlRootElement(name = "java.lang.Long")
@XmlType(name = "java.lang.Long")
@XmlAccessorType(XmlAccessType.FIELD)
final class LongModel {

	@XmlAttribute
	public Long value;

	public final static class Adapter
		extends XmlAdapter<LongModel, Long>
	{
		@Override
		public LongModel marshal(final Long value) {
			final LongModel model = new LongModel();
			model.value = value;
			return model;
		}

		@Override
		public Long unmarshal(final LongModel model) {
			return model.value;
		}
	}

	static final Adapter Adapter = new Adapter();

	static final Function<Long, LongModel> Marshaller =
		jaxb.marshaller(Adapter);

	static final Function<LongModel, Long> Unmarshaller =
		jaxb.unmarshaller(Adapter);

}
