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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.incubator.metamodel.internal;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.constant.Constable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Reflection utility methods and types which are used as building blocks for
 * the bean description and property extraction.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.2
 * @since 7.2
 */
public final class Reflect {

	private Reflect() {
	}

	public static <T> T raise(final RuntimeException exception) {
		throw exception;
	}

	/**
	 * Checks if the given {@code type} is an element type. An element type has
	 * no further properties.
	 *
	 * @param type the type to check
	 * @return {@code true} if the given {@code type} is an element type,
	 *         {@code false} otherwise
	 */
	public static boolean isElementType(final Class<?> type) {
		return type.isPrimitive() ||
			Constable.class.isAssignableFrom(type) ||
			TemporalAccessor.class.isAssignableFrom(type);
	}

	/**
	 * Return the raw type of the given Java type, given its context. If the
	 * type is a {@link ParameterizedType}, its raw type is returned.
	 *
	 * @param type the type to resolve
	 * @return the resolved {@link Class} object or {@code null} if
	 *         the type could not be resolved
	 */
	public static Class<?> toRawType(final Type type) {
		if (type instanceof Class<?> cls) {
			return cls;
		} else if (type instanceof ParameterizedType pt &&
			pt.getRawType() instanceof Class<?> cls)
		{
			return cls;
		} else {
			return null;
		}
	}

	/**
	 * Return all annotations of the given method, inclusively the inherited one.
	 *
	 * @param method the method by which to fetch the annotations
	 * @return all annotations of the given method
	 */
	public static Stream<Annotation> getAnnotations(final Method method) {
		return getClasses(method.getDeclaringClass())
			.flatMap(cls ->
				Stream.concat(
					Stream.of(cls),
					Stream.of(cls.getInterfaces())
				)
			)
			.flatMap(cls -> Stream.of(cls.getMethods()))
			.filter(m -> equals(m, method))
			.flatMap(m -> Stream.of(m.getAnnotations()));
	}

	public static Stream<Annotation> getAnnotations(final PropertyDescriptor desc) {
		return Stream.concat(
			Reflect.getAnnotations(desc.getReadMethod()),
			desc.getWriteMethod() != null
				? Reflect.getAnnotations(desc.getWriteMethod())
				: Stream.empty()
		);
	}

	private static boolean equals(final Method a, final Method b) {
		return a.getName().equals(b.getName()) &&
			Arrays.equals(a.getParameterTypes(), b.getParameterTypes());
	}

	private static Stream<Class<?>> getClasses(final Class<?> parent) {
		final var type = new AtomicReference<Class<?>>(parent);

		final Supplier<Class<?>> supplier = () -> {
			final var next = type.get();
			if (next != null) {
				type.set(next.getSuperclass());
			}
			return next;
		};

		return Stream.generate(supplier).takeWhile(Objects::nonNull);
	}

}
