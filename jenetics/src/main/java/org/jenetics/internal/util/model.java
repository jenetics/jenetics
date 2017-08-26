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
package org.jenetics.internal.util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * This object contains models not defined as native XML type.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 1.6
 * @since 2.0
 */
public final class model {
	private model() {require.noInstance();}

	@XmlRootElement(name = "char")
	@XmlType(name = "char")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class CharacterModel {

		@XmlValue
		public String value;

		public static final class Adapter
			extends XmlAdapter<CharacterModel, Character>
		{
			@Override
			public CharacterModel marshal(final Character value) {
				final CharacterModel model = new CharacterModel();
				model.value = value.toString();
				return model;
			}

			@Override
			public Character unmarshal(final CharacterModel model) {
				return model.value.charAt(0);
			}
		}

		public static final Adapter ADAPTER = new Adapter();

	}

	@XmlRootElement(name = "indexed-object")
	@XmlType(name = "org.jenetics.IndexedObject")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class IndexedObject {

		@XmlAttribute(required = true)
		public int index;

		@XmlElement(name = "value", required = true, nillable = false)
		public Object value;

	}

}
