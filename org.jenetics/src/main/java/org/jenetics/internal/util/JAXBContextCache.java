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

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.jenetics.util.ISeq;

/**
 * Caches the JAXB classes and lets you add additional one.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class JAXBContextCache {
	private JAXBContextCache() {require.noInstance();}

	private static final Set<String> PACKAGES = new HashSet<>();

	private static final Set<Class<?>> CLASSES = new HashSet<>();
	static {
		addPackage("org.jenetics");
		addPackage("org.jenetics.engine");
		addPackage("org.jenetics.internal.util");
	}

	private static JAXBContext _context;

	public static synchronized JAXBContext context() {
		if (_context == null) {
			try {
				_context = JAXBContext
					.newInstance(CLASSES.toArray(new Class<?>[CLASSES.size()]));
			} catch (JAXBException e) {
				throw new DataBindingException(
					"Something went wrong while creating JAXBContext.", e
				);
			}
		}

		return _context;
	}

	public static JAXBContext context(final String... packages) {
		Stream.of(packages).forEach(JAXBContextCache::addPackage);
		return context();
	}

	public static JAXBContext context(final Class<?>... classes) {
		Stream.of(classes).forEach(JAXBContextCache::addClass);
		return context();
	}

	public static synchronized void addPackage(final String pkg) {
		if (!PACKAGES.contains(pkg)) {
			PACKAGES.add(pkg);

			final ISeq<Class<?>> classes = jaxbClasses(pkg).stream()
				.filter(cls -> !CLASSES.contains(cls))
				.collect(ISeq.toISeq());

			if (!classes.isEmpty()) {
				_context = null;
				CLASSES.addAll(classes.asList());
			}
		}
	}

	public static synchronized void addClass(final Class<?> cls) {
		requireNonNull(cls);

		if (!CLASSES.contains(cls)) {
			_context = null;
			CLASSES.add(cls);
		}
	}

	@SuppressWarnings("unchecked")
	private static ISeq<Class<?>> jaxbClasses(final String pkg) {
		requireNonNull(pkg);

		try {
			final Field field = Class
				.forName(pkg + ".JAXBRegistry")
				.getField("CLASSES");
			field.setAccessible(true);

			return (ISeq<Class<?>>)field.get(null);
		} catch (ReflectiveOperationException e) {
			return ISeq.empty();
		}
	}
}
