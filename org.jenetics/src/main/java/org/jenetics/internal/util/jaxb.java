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

import static org.jenetics.internal.util.reflect.classOf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.jenetics.internal.util.model.CharacterModel;

import org.jenetics.util.Function;
import org.jenetics.util.StaticObject;

/**
 * JAXB helper methods.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 1.6 &mdash; <em>$Date: 2014-04-12 $</em>
 * @since 2.0
 */
public class jaxb extends StaticObject {
	private jaxb() {}

	private static final class JAXBContextHolder {
		private static final JAXBContext CONTEXT; static {
			try {
				CONTEXT = JAXBContext.newInstance(
					"org.jenetics:org.jenetics.internal.util"
				);
			} catch (JAXBException e) {
				throw new DataBindingException(
					"Something went wrong while creating JAXBContext.", e
				);
			}
		}
	}

	public static JAXBContext context() {
		return JAXBContextHolder.CONTEXT;
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

	private static final Map<Class<?>, XmlAdapter<?, ?>> ADAPTERS = new HashMap<>();

	static {
		ADAPTERS.put(Character.class, CharacterModel.ADAPTER);
		ADAPTERS.put(CharacterModel.class, CharacterModel.ADAPTER);
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
		final Class<?> cls = classOf(value);

		synchronized (ADAPTERS) {
			if (!ADAPTERS.containsKey(cls)) {
				ADAPTERS.put(cls, newXmlAdapter(cls));
			}

			return (XmlAdapter<Object, Object>) ADAPTERS.get(cls);
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

	/**
	 * Shorthand for {@code adapterFor(value).marshal(value)}
	 */
	public static Object marshal(final Object value) throws Exception {
		return adapterFor(value).marshal(value);
	}

	/**
	 * Shorthand for {@code adapterFor(value).unmarshal(value)}
	 */
	public static Object unmarshal(final Object value) throws Exception {
		return adapterFor(value).unmarshal(value);
	}

	/**
	 * Return a marshaller function from the given
	 * {@link javax.xml.bind.annotation.adapters.XmlAdapter}.
	 *
	 * @param a the adapter used by the marshaller function.
	 * @return the marshaller function
	 */
	public static <V, B> Function<B, V> Marshaller(final XmlAdapter<V, B> a) {
		return new Function<B, V>() {
			@Override
			public V apply(final B value) {
				try {
					return a.marshal(value);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		};
	}

	/**
	 * Return a unmarshaller function from the given
	 * {@link javax.xml.bind.annotation.adapters.XmlAdapter}.
	 *
	 * @param a the adapter used by the unmarshaller function.
	 * @return the unmarshaller function
	 */
	public static <V, B> Function<V, B> Unmarshaller(final XmlAdapter<V, B> a) {
		return new Function<V, B>() {
			@Override
			public B apply(final V value) {
				try {
					return a.unmarshal(value);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		};
	}

	/**
	 * Return a marshaller function for the given object.
	 *
	 * @param value the value to marshal
	 * @return the marshaller function
	 */
	public static Function<Object, Object> Marshaller(final Object value) {
		return Marshaller(adapterFor(value));
	}

	/**
	 * Return a unmarshaller function for the given object.
	 *
	 * @param value the value to unmarshal
	 * @return the unmarshaller function
	 */
	public static Function<Object, Object> Unmarshaller(final Object value)  {
		return Unmarshaller(jaxb.adapterFor(value));
	}

}
