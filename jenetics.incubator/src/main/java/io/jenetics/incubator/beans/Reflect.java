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
package io.jenetics.incubator.beans;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.constant.Constable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Reflection utility methods and types which supports the bean description and
 * property extraction.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Reflect {

	private static final Set<String> JDK_PACKAGE_PREFIXES = Set.of(
		"com.sun",
		"java",
		"javax",
		"jdk",
		"sun"
	);

	private Reflect() {
	}

	/**
	 * Base interface used for matching {@link Type} objects.
	 *
	 * <pre>{@code
	 * final Type type = ...;
	 * if (ArrayType.of(type) instanceof ArrayType at) {
	 *     System.out.println(at);
	 * }
	 * }</pre>
	 */
	public sealed interface Trait {
	}

	/**
	 * Trait which represents an array type.
	 *
	 * @param arrayType the array type
	 * @param componentType the array component type
	 */
	public record ArrayType(Class<?> arrayType, Class<?> componentType)
		implements Trait
	{

		/**
		 * Determines if the specified {@code Trait} object represents a
		 * primitive type array.
		 *
		 * @return {@code true} if and only if this class represents a primitive
		 *         type array
		 */
		public boolean isPrimitive() {
			return componentType.isPrimitive();
		}

		/**
		 * Return an {@code ArrayType} instance if the given {@code type} is an
		 * array class.
		 * <pre>{@code
		 * final Type type = ...;
		 * if (ArrayType.of(type) instanceof ArrayType at) {
		 *     System.out.println(at);
		 * }
		 * }</pre>
		 *
		 * @param type the type object
		 * @return an {@code ArrayType} if the given {@code type} is an array
		 *         type, or null
		 */
		public static Trait of(final Type type) {
			if (type instanceof Class<?> arrayType && arrayType.isArray()) {
				return new ArrayType(arrayType, arrayType.getComponentType());
			}  {
				return null;
			}
		}
	}

	/**
	 * Trait which represents a {@code List} type.
	 *
	 * @param listType the list type
	 * @param componentType the list component type
	 */
	public record ListType(Class<?> listType, Class<?> componentType) implements Trait {

		/**
		 * Return a {@code ListType} instance if the given {@code type} is a
		 * {@code List} class.
		 * <pre>{@code
		 * final Type type = ...;
		 * if (ListType.of(type) instanceof ListType lt) {
		 *     System.out.println(lt);
		 * }
		 * }</pre>
		 *
		 * @param type the type object
		 * @return an {@code ListType} if the given {@code type} is a list type,
		 *         or null
		 */
		public static Trait of(final Type type) {
			if (type instanceof ParameterizedType parameterizedType &&
				parameterizedType.getRawType() instanceof Class<?> listType &&
				List.class.isAssignableFrom(listType))
			{
				final var typeArguments = parameterizedType.getActualTypeArguments();
				if (typeArguments.length == 1 &&
					typeArguments[0] instanceof Class<?> componentType)
				{
					return new ListType(listType, componentType);
				}
			}

			if (type instanceof Class<?> listType &&
				List.class.isAssignableFrom(listType))
			{
				return new ListType(listType, Object.class);
			}

			return null;
		}
	}

	/**
	 * Trait which represents a {@code Record} type.
	 *
	 * @param type the type object
	 */
	public record RecordType(Class<?> type) implements Trait {

		/**
		 * Return the record components of {@code this} record type.
		 *
		 * @return the record components of {@code this} record type
		 */
		public Stream<RecordComponent> components() {
			return Stream.of(type.getRecordComponents())
				.filter(comp -> comp.getAccessor().getReturnType() != Class.class);
		}

		/**
		 * Return a {@code RecordType} instance if the given {@code type} is a
		 * {@code Record} class.
		 * <pre>{@code
		 * final Type type = ...;
		 * if (RecordType.of(type) instanceof RecordType rt) {
		 *     System.out.println(rt);
		 * }
		 * }</pre>
		 *
		 * @param type the type object
		 * @return an {@code RecordType} if the given {@code type} is a record
		 *         type, or null
		 */
		public static Trait of(final Type type) {
			if (type instanceof Class<?> cls && cls.isRecord()) {
				return new RecordType(cls);
			} else {
				return null;
			}
		}
	}

	/**
	 * Trait which represents a bean type.
	 *
	 * @param type the type object
	 */
	public record BeanType(Class<?> type) implements Trait {

		/**
		 * Return the property descriptors of {@code this} bean.
		 *
		 * @return the property descriptors of {@code this} bean
		 */
		public Stream<PropertyDescriptor> descriptors() {
			final PropertyDescriptor[] descriptors;
			try {
				descriptors = Introspector
					.getBeanInfo(type)
					.getPropertyDescriptors();
			} catch (IntrospectionException e) {
				throw new IllegalArgumentException(
					"Can't introspect class '%s'.".formatted(type),
					e
				);
			}

			return Stream.of(descriptors)
				.filter(d -> d.getReadMethod() != null)
				.filter(d -> d.getReadMethod().getReturnType() != Class.class);
		}

		/**
		 * Return a {@code BeanType} instance if the given {@code type} is a
		 * bean class.
		 * <pre>{@code
		 * final Type type = ...;
		 * if (BeanType.of(type) instanceof BeanType bt) {
		 *     System.out.println(bt);
		 * }
		 * }</pre>
		 *
		 * @param type the type object
		 * @return an {@code ListType} if the given {@code type} is a bean type,
		 *         or null
		 */
		public static Trait of(final Type type) {
			if (type instanceof ParameterizedType pt &&
				pt.getRawType() instanceof Class<?> rt
			) {
				return new BeanType(rt);
			} else if (type instanceof Class<?> cls) {
				return new BeanType(cls);
			} else {
				return null;
			}
		}
	}

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

	/**
	 * Checks if the given {@code type} is part of the JDK.
	 *
	 * @param type the type to check
	 * @return {@code true} if the given {@code type} is part of the JDK,
	 *         {@code false} otherwise
	 */
	public static boolean isJdkType(final Type type) {
		if (type == null) {
			return false;
		}

		final var cls = toRawType(type);
		final var name = cls != null ? cls.getName() : "-";

		return JDK_PACKAGE_PREFIXES.stream()
			.anyMatch(name::startsWith);

	}

	/**
	 * Return the raw type of the given Java type, given its context. If the
	 * type is a {@link ParameterizedType}, its raw type is returned.
	 *
	 * @param type the type to resolve
	 * @return the resolved {@link Class} object or {@code null} if
	 *      * the type could not be resolved
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
