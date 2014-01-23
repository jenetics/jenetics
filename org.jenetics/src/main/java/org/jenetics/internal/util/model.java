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
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
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

	@Retention(RUNTIME) @Target(TYPE)
	public @interface ValueType {
		Class<?> value();
	}

	@Retention(RUNTIME) @Target(TYPE)
	public @interface ModelType {
		Class<?> value();
	}

	@XmlRootElement(name = "java.lang.Integer")
	@XmlType(name = "java.lang.Integer")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class IntegerModel {

		@XmlAttribute int value;

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

		@XmlAttribute long value;

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

		@XmlAttribute double value;

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

		@XmlAttribute double value;

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

		@XmlAttribute long value;

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

	@XmlRootElement(name = "java.util.List")
	@XmlType(name = "java.util.List")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class ListModel {

		@XmlElement
		List<Object> entries = new ArrayList<>();

		public static final class Adapter extends XmlAdapter<ListModel, List<Object>> {
			@Override
			public ListModel marshal(final List<Object> values) {
				final ListModel model = new ListModel();
				for (Object value : values) {
					model.entries.add(Float64Model.Adapter.marshal((Float64) value));
				}
				return model;
			}

			@Override
			public List<Object> unmarshal(final ListModel models) {
				final List<Object> values = new ArrayList<>();
				for (Object model : models.entries) {
					values.add(Float64Model.Adapter.unmarshal((Float64Model)model));
				}
				return values;
			}
		}
	}

	/*
	@XmlJavaTypeAdapter(ModelAdapter.Adapter.class)
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class ModelAdapter {
		String name;
		Object value;

		public static class Adapter extends XmlAdapter<Element, ModelAdapter> {

			private ClassLoader classLoader;
			private DocumentBuilder documentBuilder;
			private JAXBContext jaxbContext;

			public Adapter() {
				classLoader = Thread.currentThread().getContextClassLoader();
			}

			public Adapter(JAXBContext jaxbContext) {
				this();
				this.jaxbContext = jaxbContext;
			}

			private DocumentBuilder getDocumentBuilder() throws Exception {
				// Lazy load the DocumentBuilder as it is not used for unmarshalling.
				if (null == documentBuilder) {
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					documentBuilder = dbf.newDocumentBuilder();
				}
				return documentBuilder;
			}

			private JAXBContext getJAXBContext(Class<?> type) throws Exception {
				if (null == jaxbContext) {
					// A JAXBContext was not set, so create a new one based  on the type.
					return JAXBContext.newInstance(type);
				}
				return jaxbContext;
			}

			@Override
			public Element marshal(ModelAdapter objectModel) throws Exception {
				if (null == objectModel) {
					return null;
				}

				// 1. Build the JAXBElement to wrap the instance of ModelAdapter.
				QName rootElement = new QName(objectModel.name);
				Object value = objectModel.value;
				Class<?> type = value.getClass();
				JAXBElement jaxbElement = new JAXBElement(rootElement, type, value);

				// 2.  Marshal the JAXBElement to a DOM element.
				Document document = getDocumentBuilder().newDocument();
				Marshaller marshaller = getJAXBContext(type).createMarshaller();
				marshaller.marshal(jaxbElement, document);
				Element element = document.getDocumentElement();

				// 3.  Set the type attribute based on the value's type.
				element.setAttribute("type", type.getName());
				return element;
			}

			@Override
			public ModelAdapter unmarshal(Element element) throws Exception {
				if (null == element) {
					return null;
				}

				// 1. Determine the values type from the type attribute.
				Class<?> type = classLoader.loadClass(element.getAttribute("type"));

				// 2. Unmarshal the element based on the value's type.
				DOMSource source = new DOMSource(element);
				Unmarshaller unmarshaller = getJAXBContext(type).createUnmarshaller();
				JAXBElement jaxbElement = unmarshaller.unmarshal(source, type);

				// 3. Build the instance of ModelAdapter
				ModelAdapter objectModel = new ModelAdapter();
				objectModel.name = element.getLocalName();
				objectModel.value = jaxbElement.getValue();
				return objectModel;
			}

		}

		public static final Adapter Adapter = new Adapter();

		public static final Function<ModelAdapter, Element>
			Marshaller = jaxb.marshaller(Adapter);

		public static final Function<Element, ModelAdapter>
			Unmarshaller = jaxb.unmarshaller(Adapter);
	}
	*/

}
