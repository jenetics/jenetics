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
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.jscience.mathematics.number.Float64;
import org.jscience.mathematics.number.Integer64;

import org.jenetics.util.Function;
import org.jenetics.util.StaticObject;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date: 2014-01-23 $</em>
 * @since @__version__@
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

		public static final Function<Integer, IntegerModel> Marshaller =
			jaxb.marshaller(Adapter);

		public static final Function<IntegerModel, Integer> Unmarshaller =
			jaxb.unmarshaller(Adapter);

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

		public static final Function<Long, LongModel> Marshaller =
			jaxb.marshaller(Adapter);

		public static final Function<LongModel, Long> Unmarshaller =
			jaxb.unmarshaller(Adapter);

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

		public static final Function<Double, DoubleModel> Marshaller =
			jaxb.marshaller(Adapter);

		public static final Function<DoubleModel, Double> Unmarshaller =
			jaxb.unmarshaller(Adapter);

	}

	@XmlRootElement(name = "org.jscience.mathematics.number.Float64")
	@XmlType(name = "org.jscience.mathematics.number.Float64")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class Float64Model {

		@XmlAttribute
		public double value;

		@ValueType(Float64.class)
		@ModelType(Float64Model.class)
		public static final class Adapter
			extends XmlAdapter<Float64Model, Float64>
		{
			@Override
			public Float64Model marshal(final Float64 value) {
				final Float64Model model = new Float64Model();
				model.value = value.doubleValue();
				return model;
			}

			@Override
			public Float64 unmarshal(final Float64Model model) {
				return Float64.valueOf(model.value);
			}
		}

		public static final Adapter Adapter = new Adapter();

		public static final Function<Float64, Float64Model> Marshaller =
			jaxb.marshaller(Adapter);

		public static final Function<Float64Model, Float64> Unmarshaller =
			jaxb.unmarshaller(Adapter);

	}

	@XmlRootElement(name = "org.jscience.mathematics.number.Integer64")
	@XmlType(name = "org.jscience.mathematics.number.Integer64")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class Integer64Model {

		@XmlAttribute
		public long value;

		@ValueType(Integer64.class)
		@ModelType(Integer64Model.class)
		public static final class Adapter
			extends XmlAdapter<Integer64Model, Integer64>
		{
			@Override
			public Integer64Model marshal(final Integer64 value) {
				final Integer64Model model = new Integer64Model();
				model.value = value.longValue();
				return model;
			}

			@Override
			public Integer64 unmarshal(final Integer64Model model) {
				return Integer64.valueOf(model.value);
			}
		}

		public static final Adapter Adapter = new Adapter();

		public static final Function<Integer64, Integer64Model>
			Marshaller = jaxb.marshaller(Adapter);

		public static final Function<Integer64Model, Integer64>
			Unmarshaller = jaxb.unmarshaller(Adapter);

	}

}
