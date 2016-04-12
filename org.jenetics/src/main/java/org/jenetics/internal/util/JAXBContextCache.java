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
 * Caches the JAXB classes and lets you add additional one. You can either add
 * a <em>JAXB</em> class directly, or the package where you have put in a
 * {@code JAXBRegistry} class:
 *
 * <pre>{@code
 * // Class may be package private
 * final class JAXBRegistry {
 *     private JAXBRegistry() {require.noInstance();}
 *
 *     // Must contain static final field 'CLASSES'.
 *     public static final ISeq<Class<?>> CLASSES = ISeq.of(
 *         BitGene.Model.class,
 *         EnumGene.Model.class,
 *         CharacterGene.Model.class,
 *         IntegerGene.Model.class,
 *         LongGene.Model.class,
 *         DoubleGene.Model.class
 *     )
 * }
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.5
 * @since 3.5
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

	/**
	 * Return a {@code JAXBContext} with the currently registered classes. This
	 * method is <em>synchronized</em>.
	 *
	 * @return the {@code JAXBContext} with the currently registered classes
	 */
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

	/**
	 * Return a {@code JAXBContext} with the currently registered classes plus
	 * the registered classes in the given packages. This method is
	 * <em>synchronized</em>.
	 *
	 * @param packages the additional packages of the return {@code JAXBContext}
	 * @return the {@code JAXBContext}
	 */
	public static JAXBContext context(final String... packages) {
		Stream.of(packages).forEach(JAXBContextCache::addPackage);
		return context();
	}

	/**
	 * Return a {@code JAXBContext} with the currently registered classes plus
	 * the given classes. This method is <em>synchronized</em>.
	 *
	 * @param classes the additional classes of the return {@code JAXBContext}
	 * @return the {@code JAXBContext}
	 */
	public static JAXBContext context(final Class<?>... classes) {
		Stream.of(classes).forEach(JAXBContextCache::add);
		return context();
	}

	/**
	 * Register the given source package.
	 *
	 * @param pkg the package to register
	 */
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

	/**
	 * Register the given class.
	 *
	 * @param cls the class to register
	 */
	public static synchronized void add(final Class<?> cls) {
		requireNonNull(cls);

		if (!CLASSES.contains(cls)) {
			_context = null;
			CLASSES.add(cls);
		}
	}

	/**
	 * De-register the given class.
	 *
	 * @param cls the class to de-register
	 */
	public static synchronized void remove(final Class<?> cls) {
		requireNonNull(cls);

		if (CLASSES.contains(cls)) {
			_context = null;
			CLASSES.remove(cls);
		}
	}

	/**
	 * Check is the given class is already registered.
	 *
	 * @param cls the class to check
	 * @return {@code true} if the given class is already registered,
	 *         {@code false} otherwise.
	 */
	public static synchronized boolean contains(final Class<?> cls) {
		requireNonNull(cls);
		return CLASSES.contains(cls);
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
