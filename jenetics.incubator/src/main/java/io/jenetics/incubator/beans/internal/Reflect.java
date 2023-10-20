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
package io.jenetics.incubator.beans.internal;

import java.lang.constant.Constable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.temporal.TemporalAccessor;
import java.util.Set;

/**
 * Reflection utility methods and types which are used as building blocks for
 * the bean description and property extraction.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.2
 * @since 7.2
 */
public final class Reflect {

	private static final Set<String> JDK_TYPE_PREFIXES = Set.of(
		"[", // Java arrays
		"com.sun.",
		"java.",
		"javax.",
		"jdk.",
		"sun."
	);

	private Reflect() {
	}

	public static <T> T raise(final RuntimeException exception) {
		throw exception;
	}

	/*
	public static <T> T call(final Callable<? extends T> callable) {
		try {
			return callable.call();
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof RuntimeException re) {
				throw re;
			} else {
				throw new IllegalStateException(e.getTargetException());
			}
		} catch (VirtualMachineError|ThreadDeath|LinkageError e) {
			throw e;
		} catch (Throwable e) {
			throw new IllegalArgumentException(e);
		}
	}
	 */

	/**
	 * Return {@code true} if the given {@code object} is considered as an
	 * identity type.
	 *
	 * @param object the object to test
	 * @return {@code true} if the object can be treated as identity class,
	 *         {@code false} otherwise
	 */
	public static boolean isIdentityType(final Object object) {
		return
			object != null &&
			!(object instanceof Constable) &&
			!(object instanceof TemporalAccessor) &&
			!(object instanceof Number);
	}

	public static boolean isElementType(final Class<?> type) {
		return type.isPrimitive() ||
			Constable.class.isAssignableFrom(type) ||
			TemporalAccessor.class.isAssignableFrom(type);
	}

	/**
	 * Checks if the given {@code type} is part of the JDK.
	 *
	 * @param type the type to check
	 * @return {@code true} if the given {@code type} is part of the JDK,
	 *         {@code false} otherwise
	 */
	public static boolean isNonJdkType(final Type type) {
		if (type == null) {
			return true;
		}

		final var cls = toRawType(type);
		final var name = cls != null ? cls.getName() : "-";

		return JDK_TYPE_PREFIXES.stream().noneMatch(name::startsWith);
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

}
