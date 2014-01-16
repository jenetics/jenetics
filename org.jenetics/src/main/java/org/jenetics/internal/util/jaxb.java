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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

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

	// Identity XmlAdapter.
	private static final
	XmlAdapter<Object, Object> ID_XML_ADAPTER = new XmlAdapter<Object, Object>() {
		@Override public Object unmarshal(final Object value) {
			return value;
		}
		@Override public Object marshal(final Object value) {
			return value;
		}
	};

	private static final Map<Class<?>, XmlAdapter<Object, Object>>
	xmlAdapterCache = new HashMap<>();

	/**
	 * Return the an {@code XmlAdapter} for the given {@code vale}. If no
	 * adapter could be found, and identity adapter is returned.
	 *
	 * @param value the object for which to find an {@code XmlAdapter}
	 * @return the {@code XmlAdapter} for the given object, or an identity
	 *         adapter if no one can be found.
	 */
	public static XmlAdapter<Object, Object> adapterFor(final Object value) {
		final Class<?> cls = value instanceof Class<?> ?
			(Class<?>)value : value.getClass();

		synchronized (xmlAdapterCache) {
			if (!xmlAdapterCache.containsKey(cls)) {
				xmlAdapterCache.put(cls, newXmlAdapter(cls));
			}

			return xmlAdapterCache.get(cls);
		}
	}

	@SuppressWarnings("unchecked")
	private static XmlAdapter<Object, Object> newXmlAdapter(final Class<?> cls) {
		final List<Class<?>> classes = reflect.allDeclaredClasses(cls);

		XmlAdapter<Object, Object> adapter = ID_XML_ADAPTER;
		for (int i = 0; i < classes.size() && adapter == ID_XML_ADAPTER; ++i) {
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

	/**
	 * Adapts the given object for marshalling, with the {@code XmlAdapter}
	 * associated with the object.
	 *
	 * @param value the object to adapt for marshalling.
	 * @return the adapted value
	 * @throws Exception if the adaption fails.
	 */
	public static Object adaptMarshal(final Object value) throws Exception {
		return adapterFor(value).marshal(value);
	}

	/**
	 * Adapts the given object for un-marshalling, with the {@code XmlAdapter}
	 * associated with the object.
	 *
	 * @param value the object to adapt for un-marshalling.
	 * @return the adapted value
	 * @throws Exception if the adaption fails.
	 */
	public static Object adaptUnmarshal(final Object value) throws Exception {
		return adapterFor(value).unmarshal(value);
	}

	/**
	 * Checks, whether the given object is an JAXB model or has an model adapter
	 * defined.
	 *
	 * @param value the object used for the model check.
	 * @return {@code true} if the given object has/is an JAXB model,
	 *         {@code false} otherwise.
	 */
	public static boolean hasModel(final Object value) {
		final Class<?> cls = value instanceof Class<?> ?
			(Class<?>)value : value.getClass();

		return cls.isAnnotationPresent(XmlJavaTypeAdapter.class) ||
			cls.isAnnotationPresent(XmlRootElement.class) ||
			cls.isAnnotationPresent(XmlType.class);
	}

	public static <V, B> List<V> marshalMap(
		final XmlAdapter<V, B> adapter,
		final List<B> values
	)
		throws Exception
	{
		final List<V> result = new ArrayList<>(values.size());
		for (B value : values) {
			result.add(adapter.marshal(value));
		}
		return result;
	}

	public static <V, B> List<B> unmarshalMap(
		final XmlAdapter<V, B> adapter,
		final List<V> values
	)
		throws Exception
	{
		final List<B> result = new ArrayList<>(values.size());
		for (V value : values) {
			result.add(adapter.unmarshal(value));
		}
		return result;
	}

}
