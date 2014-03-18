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

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.jenetics.util.Function;
import org.jenetics.util.StaticObject;

/**
 * This object contains models for the java primitive/basic types and the
 * integer and float types of the JScience library.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 1.6 &mdash; <em>$Date$</em>
 * @since 2.0
 */
public final class model extends StaticObject {
	private model() {}

	@Retention(RUNTIME)
	@Target(TYPE)
	public @interface ValueType {
		Class<?> value();
	}

	@Retention(RUNTIME)
	@Target(TYPE)
	public @interface ModelType {
		Class<?> value();
	}

	/* ************************************************************************
	 * Java primitive type models.
	 **************************************************************************/

	@XmlRootElement(name = "char")
	@XmlType(name = "char")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class CharacterModel {

		@XmlValue
		public String value;

		@ValueType(Character.class)
		@ModelType(CharacterModel.class)
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

		public static final Adapter Adapter = new Adapter();

	}

	public static final Function<Character, String> CharacterToString =
		new Function<Character, String>() {
			@Override
			public String apply(final Character value) {
				return value.toString();
			}
		};

	public static final Function<String, Character> StringToCharacter =
		new Function<String, Character>() {
			@Override
			public Character apply(final String value) {
				return value.charAt(0);
			}
		};
}
