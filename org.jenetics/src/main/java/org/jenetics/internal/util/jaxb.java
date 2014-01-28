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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.jscience.mathematics.number.Float64;
import org.jscience.mathematics.number.Integer64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.jenetics.internal.util.model.BooleanModel;
import org.jenetics.internal.util.model.ByteModel;
import org.jenetics.internal.util.model.CharacterModel;
import org.jenetics.internal.util.model.DoubleModel;
import org.jenetics.internal.util.model.Float64Model;
import org.jenetics.internal.util.model.FloatModel;
import org.jenetics.internal.util.model.Integer64Model;
import org.jenetics.internal.util.model.IntegerModel;
import org.jenetics.internal.util.model.LongModel;
import org.jenetics.internal.util.model.ModelType;
import org.jenetics.internal.util.model.ShortModel;
import org.jenetics.internal.util.model.StringModel;
import org.jenetics.internal.util.model.ValueType;

import org.jenetics.util.Function;
import org.jenetics.util.StaticObject;

/**
 * JAXB helper methods.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date$</em>
 * @since @__version__@
 */
public class jaxb extends StaticObject {
	private jaxb() {}

	public static final JAXBContext CONTEXT = newContext();

	private static JAXBContext newContext() {
		try {
			return JAXBContext.newInstance(
				"org.jenetics:org.jenetics.internal.util"
			);
		} catch (JAXBException e) {
			throw new AssertionError(e);
		}
	}

	private static final XmlAdapter<Object, Object> IdentityAdapter =
	new XmlAdapter<Object, Object>() {
		@Override public Object unmarshal(final Object value) {
			return value;
		}
		@Override public Object marshal(final Object value) {
			return value;
		}
	};

	private static final Map<Class<?>, XmlAdapter<? extends Object, ? extends Object>>
		ADAPTER_CACHE = new HashMap<>();
	static {
		ADAPTER_CACHE.put(Boolean.class, BooleanModel.Adapter);
		ADAPTER_CACHE.put(BooleanModel.class, BooleanModel.Adapter);

		ADAPTER_CACHE.put(Byte.class, ByteModel.Adapter);
		ADAPTER_CACHE.put(ByteModel.class, ByteModel.Adapter);

		ADAPTER_CACHE.put(Character.class, CharacterModel.Adapter);
		ADAPTER_CACHE.put(CharacterModel.class, CharacterModel.Adapter);

		ADAPTER_CACHE.put(Short.class, ShortModel.Adapter);
		ADAPTER_CACHE.put(ShortModel.class, ShortModel.Adapter);

		ADAPTER_CACHE.put(Integer.class, IntegerModel.Adapter);
		ADAPTER_CACHE.put(IntegerModel.class, IntegerModel.Adapter);

		ADAPTER_CACHE.put(Long.class, LongModel.Adapter);
		ADAPTER_CACHE.put(LongModel.class, LongModel.Adapter);

		ADAPTER_CACHE.put(Float.class, FloatModel.Adapter);
		ADAPTER_CACHE.put(FloatModel.class, FloatModel.Adapter);

		ADAPTER_CACHE.put(Double.class, DoubleModel.Adapter);
		ADAPTER_CACHE.put(DoubleModel.class, DoubleModel.Adapter);

		ADAPTER_CACHE.put(String.class, StringModel.Adapter);
		ADAPTER_CACHE.put(StringModel.class, StringModel.Adapter);

		ADAPTER_CACHE.put(Integer64.class, Integer64Model.Adapter);
		ADAPTER_CACHE.put(Integer64Model.class, Integer64Model.Adapter);

		ADAPTER_CACHE.put(Float64.class, Float64Model.Adapter);
		ADAPTER_CACHE.put(Float64Model.class, Float64Model.Adapter);
	}

	/**
	 * Return the an {@code XmlAdapter} for the given {@code vale}. If no
	 * adapter could be found, and identity adapter is returned.
	 *
	 * @param value the object for which to find an {@code XmlAdapter}
	 * @return the {@code XmlAdapter} for the given object, or an identity
	 *         adapter if no one can be found.
	 */
	@SuppressWarnings("unchecked")
	public static XmlAdapter<Object, Object> adapterFor(final Object value) {
		final Class<?> cls = value instanceof Class<?> ?
			(Class<?>)value : value.getClass();

		synchronized (ADAPTER_CACHE) {
			if (!ADAPTER_CACHE.containsKey(cls)) {
				ADAPTER_CACHE.put(cls, newXmlAdapter(cls));
			}

			return (XmlAdapter<Object, Object>)ADAPTER_CACHE.get(cls);
		}
	}

	@SuppressWarnings("unchecked")
	private static XmlAdapter<Object, Object> newXmlAdapter(final Class<?> cls) {
		final List<Class<?>> classes = reflect.allDeclaredClasses(cls);

		XmlAdapter<Object, Object> adapter = IdentityAdapter;
		for (int i = 0; i < classes.size() && adapter == IdentityAdapter; ++i) {
			if (XmlAdapter.class.isAssignableFrom(classes.get(i))) {
				try {
					adapter = (XmlAdapter<Object, Object>)classes.get(i).newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					// ignore exception
				}
			}
		}

		return adapter;
	}

	public static Class<?> modelTypeFor(final Object value) {
		final Object adapter = adapterFor(value);
		final ModelType ma = adapter.getClass().getAnnotation(ModelType.class);
		if (ma != null) {
			return ma.value();
		}
		return value instanceof Class<?> ? (Class<?>)value : value.getClass();
	}

	public static Class<?> valueTypeFor(final Object value) {
		final Object adapter = adapterFor(value);
		final ValueType ma = adapter.getClass().getAnnotation(ValueType.class);
		if (ma != null) {
			return ma.value();
		}
		return value instanceof Class<?> ? (Class<?>)value : value.getClass();
	}

	public static Object marshal(final Object value) throws Exception {
		return adapterFor(value).marshal(value);
	}

	public static Object unmarshal(final Object value) throws Exception {
		return adapterFor(value).unmarshal(value);
	}

	public static <V, B> Function<B, V> marshaller(final XmlAdapter<V, B> adapter) {
		return new Function<B, V>() {
			@Override
			public V apply(final B value) {
				try {
					return adapter.marshal(value);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		};
	}

	public static <V, B> Function<V, B> unmarshaller(final XmlAdapter<V, B> adapter) {
		return new Function<V, B>() {
			@Override
			public B apply(final V value) {
				try {
					return adapter.unmarshal(value);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		};
	}

	public static Function<Object, Object> Marshaller(final Object value) {
		return marshaller(adapterFor(value));
	}

	public static Function<Object, Object> Unmarshaller(final Object c)  {
		return unmarshaller(jaxb.adapterFor(c));
	}

	public static final Function<Object, Object> Unmarshaller =
	new Function<Object, Object>() {
		@Override
		public Object apply(final Object value) {
			Object result = value;
			if (value instanceof Element) {
				final Element element = (Element)value;

				try {
					final Class<?> type = modelTypeFor(
						Class.forName(element.getNodeName())
					);

					final DOMSource source = new DOMSource(element);
					final JAXBElement jaxbElement = CONTEXT.createUnmarshaller()
						.unmarshal(source, type);

					result = jaxb.adapterFor(jaxbElement.getValue())
						.unmarshal(jaxbElement.getValue());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

			return result;
		}
	};

	public static final class JavolutionElementAdapter
		extends XmlAdapter<Object, Object>
	{

		@Override
		public Object unmarshal(final Object v) throws Exception {
			final Element element = (Element)v;
			final Class<?> type = modelTypeFor(
				Class.forName(element.getAttribute("class"))
			);

			final DOMSource source = new DOMSource(element);
			final JAXBElement jaxbElement = CONTEXT.createUnmarshaller()
				.unmarshal(source, type);

			return jaxb.adapterFor(jaxbElement.getValue())
				.unmarshal(jaxbElement.getValue());
		}

		@Override
		public Object marshal(final Object v) throws Exception {
			final DOMResult result = new DOMResult();
			CONTEXT.createMarshaller().marshal(v, result);

			final Element e = ((Document)result.getNode()).getDocumentElement();
			final Class<?> type = valueTypeFor(v);
			e.setAttribute("class", type.getCanonicalName());

			return e;
		}
	}

}
