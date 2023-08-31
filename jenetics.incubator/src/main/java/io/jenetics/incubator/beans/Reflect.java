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

import static java.util.Objects.requireNonNull;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.constant.Constable;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

	/**
	 * Base interface used for matching {@link Type} objects.
	 *
	 * {@snippet lang="java":
	 * final Type type = ...;
	 * if (ArrayType.of(type) instanceof ArrayType at) {
	 *     System.out.println(at);
	 * }
	 * }
	 */
	public sealed interface Trait {
	}

	/**
	 * Represents indexed types. An indexed type is a container where
	 * its elements are accessible via index. Such types are arrays and lists.
	 */
	public sealed interface IndexedType extends Trait {

		/**
		 * Return the container type, e.g., Array or List.
		 *
		 * @return the container type
		 */
		Class<?> type();

		/**
		 * Return the container element type.
		 *
		 * @return the container element type
		 */
		Class<?> componentType();

		/**
		 * Return {@code true} if {@code this} type is mutable.
		 *
		 * @return {@code true} if {@code this} type is mutable
		 */
		default boolean isMutable() {
			return true;
		}

		/**
		 * Returns the length of the given indexed object, as an {@code int}.
		 *
		 * @param object the indexed type
		 * @return the length of the array
		 * @throws NullPointerException if the specified object is {@code null}
		 */
		int size(final Object object);

		/**
		 * Returns the value of the indexed object at the given index.
		 *
		 * @param object the indexed type
		 * @param index the index
		 * @return the value of the indexed object at the given index
		 * @throws NullPointerException if the specified object is {@code null}
		 * @throws IndexOutOfBoundsException if the index is out of range
		 *         ({@code index < 0 || index >= size()})
		 */
		Object get(final Object object, final int index);

		/**
		 * Sets the value of the indexed object at the given index.
		 *
		 * @param object the indexed object
		 * @param index the index
		 * @param value the new value of the indexed object
		 * @throws NullPointerException if the specified object argument is
		 *         {@code null}
		 * @throws IndexOutOfBoundsException if the index is out of range
		 *         ({@code index < 0 || index >= size()})
		 */
		void set(final Object object, final int index, final Object value);

		/**
		 * Return an {@code IndexedType} from the given {@code type}. If the
		 * type parameter doesn't represent an indexed type, {@code null} is
		 * returned.
		 *
		 * @param type the input type
		 * @return an indexed type if the input {@code type} is an optional,
		 *         array or list, {@code null} otherwise
		 */
		static Trait of(final Type type) {
			var trait = OptionalType.of(type);
			if (trait == null) {
				trait = ArrayType.of(type);
			}
			if (trait == null) {
				trait = ListType.of(type);
			}

			return trait;
		}
	}

	/**
	 * Trait which represents an {@code Optional} type.
	 *
	 * @param componentType the optional component type
	 */
	public record OptionalType(Class<?> componentType) implements IndexedType {

		@Override
		public Class<?> type() {
			return Optional.class;
		}

		@Override
		public boolean isMutable() {
			return false;
		}

		@Override
		public int size(Object object) {
			return object instanceof Optional<?> optional
				? optional.isPresent() ? 1 : 0
				: raise(new IllegalArgumentException("Not an Optional: " + object));
		}

		@Override
		public Object get(Object object, int index) {
			return object instanceof Optional<?> optional
				? optional.orElseThrow()
				: raise(new IllegalArgumentException("Not an Optional: " + object));
		}

		@Override
		public void set(Object object, int index, Object value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * Return a {@code OptionalType} instance if the given {@code type} is a
		 * {@code Optional} class.
		 * {@snippet lang="java":
		 * final Type type = ...;
		 * if (OptionalType.of(type) instanceof OptionalType ot) {
		 *     System.out.println(ot);
		 * }
		 * }
		 *
		 * @param type the type object
		 * @return an {@code OptionalType} if the given {@code type} is an
		 *         optional type, or {@code null}
		 */
		public static IndexedType of(final Type type) {
			if (type instanceof ParameterizedType parameterizedType &&
				parameterizedType.getRawType() instanceof Class<?> optionalType &&
				Optional.class.isAssignableFrom(optionalType))
			{
				final var typeArguments = parameterizedType.getActualTypeArguments();
				if (typeArguments.length == 1 &&
					toRawType(typeArguments[0]) != null)
				{
					return new OptionalType(toRawType(typeArguments[0]) );
				}
			}

			if (type instanceof Class<?> optionalType &&
				Optional.class.isAssignableFrom(optionalType))
			{
				return new OptionalType(Object.class);
			}

			return null;
		}
	}

	/**
	 * Trait which represents an array type.
	 *
	 * @param type the array type
	 * @param componentType the array component type
	 */
	public record ArrayType(Class<?> type, Class<?> componentType)
		implements IndexedType
	{

		public ArrayType {
			if (!type.isArray()) {
				throw new IllegalArgumentException("Not an array type: " + type);
			}
		}

		@Override
		public int size(Object object) {
			return Array.getLength(object);
		}

		@Override
		public Object get(Object object, int index) {
			return Array.get(object, index);
		}

		@Override
		public void set(Object object, int index, Object value) {
			Array.set(object, index, value);
		}

		/**
		 * Return an {@code ArrayType} instance if the given {@code type} is an
		 * array class.
		 * {@snippet lang="java":
		 * final Type type = ...;
		 * if (ArrayType.of(type) instanceof ArrayType at) {
		 *     System.out.println(at);
		 * }
		 * }
		 *
		 * @param type the type object
		 * @return an {@code ArrayType} if the given {@code type} is an array
		 *         type, or {@code null}
		 */
		public static IndexedType of(final Type type) {
			if (type instanceof Class<?> arrayType && arrayType.isArray()) {
				return new ArrayType(
					arrayType,
					arrayType.getComponentType()
				);
			}  {
				return null;
			}
		}
	}

	/**
	 * Trait which represents a {@code List} type.
	 *
	 * @param type the list type
	 * @param componentType the list component type
	 */
	public record ListType(Class<?> type, Class<?> componentType)
		implements IndexedType
	{

		public ListType {
			if (!List.class.isAssignableFrom(type)) {
				throw new IllegalArgumentException("Not a list type: " + type);
			}
		}

		@Override
		public int size(final Object object) {
			return object instanceof List<?> list
				? list.size()
				: raise(new IllegalArgumentException("Not a list: " + object));
		}

		@Override
		public Object get(final Object object, final int index) {
			return object instanceof List<?> list
				? list.get(index)
				: raise(new IllegalArgumentException("Not a list: " + object));
		}

		@Override
		@SuppressWarnings({"unchecked", "rawtypes"})
		public void set(Object object, int index, Object value) {
			if (object instanceof List list) {
				list.set(index, value);
			} else {
				throw new IllegalArgumentException("Not a list: " + object);
			}
		}

		/**
		 * Return a {@code ListType} instance if the given {@code type} is a
		 * {@code List} class.
		 * {@snippet lang="java":
		 * final Type type = ...;
		 * if (ListType.of(type) instanceof ListType lt) {
		 *     System.out.println(lt);
		 * }
		 * }
		 *
		 * @param type the type object
		 * @return an {@code ListType} if the given {@code type} is a list type,
		 *         or {@code null}
		 */
		public static IndexedType of(final Type type) {
			if (type instanceof ParameterizedType parameterizedType &&
				parameterizedType.getRawType() instanceof Class<?> listType &&
				List.class.isAssignableFrom(listType))
			{
				final var typeArguments = parameterizedType.getActualTypeArguments();
				if (typeArguments.length == 1 &&
					toRawType(typeArguments[0]) != null)
				{
					return new ListType(listType, toRawType(typeArguments[0]) );
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
	 * Represents a <em>structural</em> type like a record or bean class.
	 */
	public sealed interface StructType extends Trait {

		/**
		 * Component information for the <em>structural</em> trait
		 *
		 * @param enclosure the enclosing type
		 * @param name the component name
		 * @param value the component type
		 * @param getter the getter method
		 * @param setter the setter method, may be {@code null}
		 */
		record Component(
			Class<?> enclosure,
			String name,
			Type value,
			Method getter,
			Method setter
		) {
			public Component {
				requireNonNull(enclosure);
				requireNonNull(name);
				requireNonNull(value);
				requireNonNull(getter);
			}
		}

		/**
		 * Return the record components of {@code this} struct trait.
		 *
		 * @return the record components of {@code this} struct trait
		 */
		Stream<Component> components();

		/**
		 * Return an {@code StructType} from the given {@code type}. If the
		 * type parameter doesn't represent a structural type, {@code null} is
		 * returned.
		 *
		 * @param type the input type
		 * @return a structure type if the input {@code type} is a record or
		 *         bean
		 */
		static Trait of(final Type type) {
			var trait = RecordType.of(type);
			if (trait == null) {
				trait = BeanType.of(type);
			}
			return trait;
		}

	}

	/**
	 * Trait which represents a {@code Record} type.
	 *
	 * @param type the type object
	 */
	public record RecordType(Class<?> type) implements StructType {

		public RecordType {
			if (!type.isRecord()) {
				throw new IllegalArgumentException("Not a record type: " + type);
			}
		}

		/**
		 * Return the record components of {@code this} record type.
		 *
		 * @return the record components of {@code this} record type
		 */
		@Override
		public Stream<Component> components() {
			return Stream.of(type.getRecordComponents())
				.filter(comp -> comp.getAccessor().getReturnType() != Class.class)
				.map(rc -> new Component(
					rc.getDeclaringRecord(),
					rc.getName(),
					rc.getAccessor().getGenericReturnType(),
					rc.getAccessor(),
					null
				));
		}

		/**
		 * Return a {@code RecordType} instance if the given {@code type} is a
		 * {@code Record} class.
		 * {@snippet lang="java":
		 * final Type type = ...;
		 * if (RecordType.of(type) instanceof RecordType rt) {
		 *     System.out.println(rt);
		 * }
		 * }
		 *
		 * @param type the type object
		 * @return an {@code RecordType} if the given {@code type} is a record
		 *         type, or {@code null}
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
	public record BeanType(Class<?> type) implements StructType {
		@Override
		public Stream<Component> components() {
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
				.filter(pd -> pd.getReadMethod() != null)
				.filter(pd -> pd.getReadMethod().getReturnType() != Class.class)
				.map(pd -> new Component(
					pd.getReadMethod().getDeclaringClass(),
					pd.getName(),
					pd.getReadMethod().getGenericReturnType(),
					pd.getReadMethod(),
					pd.getWriteMethod()
				));
		}

		/**
		 * Return a {@code BeanType} instance if the given {@code type} is a
		 * bean class.
		 * {@snippet lang="java":
		 * final Type type = ...;
		 * if (BeanType.of(type) instanceof BeanType bt) {
		 *     System.out.println(bt);
		 * }
		 * }
		 *
		 * @param type the type object
		 * @return an {@code ListType} if the given {@code type} is a bean type,
		 *         or {@code null}
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

	private static <T> T raise(final RuntimeException exception) {
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
