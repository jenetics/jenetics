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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.jenetics.util.StaticObject;

/**
 * This object contains models for the java primitive/basic types and the
 * integer and float types of the JScience library.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 1.6 &mdash; <em>$Date$</em>
 * @since 1.6
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

	@XmlRootElement(name = "java.lang.Boolean")
	@XmlType(name = "java.lang.Boolean")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class BooleanModel {

		@XmlAttribute
		public boolean value;

		@ValueType(Boolean.class)
		@ModelType(BooleanModel.class)
		public static final class Adapter
			extends XmlAdapter<BooleanModel, Boolean>
		{
			@Override
			public BooleanModel marshal(final Boolean value) {
				final BooleanModel model = new BooleanModel();
				model.value = value;
				return model;
			}

			@Override
			public Boolean unmarshal(final BooleanModel model) {
				return model.value;
			}
		}

		public static final Adapter Adapter = new Adapter();

	}

	@XmlRootElement(name = "java.lang.Byte")
	@XmlType(name = "java.lang.Byte")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class ByteModel {

		@XmlAttribute
		public byte value;

		@ValueType(Byte.class)
		@ModelType(ByteModel.class)
		public static final class Adapter
			extends XmlAdapter<ByteModel, Byte>
		{
			@Override
			public ByteModel marshal(final Byte value) {
				final ByteModel model = new ByteModel();
				model.value = value;
				return model;
			}

			@Override
			public Byte unmarshal(final ByteModel model) {
				return model.value;
			}
		}

		public static final Adapter Adapter = new Adapter();

	}

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

	@XmlRootElement(name = "java.lang.Short")
	@XmlType(name = "java.lang.Short")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class ShortModel {

		@XmlAttribute
		public short value;

		@ValueType(Short.class)
		@ModelType(ShortModel.class)
		public static final class Adapter
			extends XmlAdapter<ShortModel, Short>
		{
			@Override
			public ShortModel marshal(final Short value) {
				final ShortModel model = new ShortModel();
				model.value = value;
				return model;
			}

			@Override
			public Short unmarshal(final ShortModel model) {
				return model.value;
			}
		}

		public static final Adapter Adapter = new Adapter();

	}

	@XmlRootElement(name = "java.lang.Integer")
	@XmlType(name = "java.lang.Integer")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class IntegerModel {

		@XmlAttribute
		public int value;

		@ValueType(Integer.class)
		@ModelType(IntegerModel.class)
		public static final class Adapter
			extends XmlAdapter<IntegerModel, Integer>
		{
			@Override
			public IntegerModel marshal(final Integer value) {
				final IntegerModel model = new IntegerModel();
				model.value = value;
				return model;
			}

			@Override
			public Integer unmarshal(final IntegerModel model) {
				return model.value;
			}
		}

		public static final Adapter Adapter = new Adapter();

	}

	@XmlRootElement(name = "java.lang.Long")
	@XmlType(name = "java.lang.Long")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class LongModel {

		@XmlAttribute
		public long value;

		@ValueType(Long.class)
		@ModelType(LongModel.class)
		public static final class Adapter
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

		public static final Adapter Adapter = new Adapter();

	}

	@XmlRootElement(name = "java.lang.Float")
	@XmlType(name = "java.lang.Float")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class FloatModel {

		@XmlAttribute
		public float value;

		@ValueType(Float.class)
		@ModelType(FloatModel.class)
		public static final class Adapter
			extends XmlAdapter<FloatModel, Float>
		{
			@Override
			public FloatModel marshal(final Float value) {
				final FloatModel model = new FloatModel();
				model.value = value;
				return model;
			}

			@Override
			public Float unmarshal(final FloatModel model) {
				return model.value;
			}
		}

		public static final Adapter Adapter = new Adapter();


	}

	@XmlRootElement(name = "java.lang.Double")
	@XmlType(name = "java.lang.Double")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class DoubleModel {

		@XmlAttribute
		public double value;

		@ValueType(Double.class)
		@ModelType(DoubleModel.class)
		public static final class Adapter
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

		public static final Adapter Adapter = new Adapter();

	}

	@XmlRootElement(name = "java.lang.String")
	@XmlType(name = "java.lang.String")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class StringModel {

		@XmlAttribute
		public String value;

		@ValueType(String.class)
		@ModelType(StringModel.class)
		public static final class Adapter
			extends XmlAdapter<StringModel, String>
		{
			@Override
			public StringModel marshal(final String value) {
				final StringModel model = new StringModel();
				model.value = value;
				return model;
			}

			@Override
			public String unmarshal(final StringModel model) {
				return model.value;
			}
		}

		public static final Adapter Adapter = new Adapter();

	}

}
