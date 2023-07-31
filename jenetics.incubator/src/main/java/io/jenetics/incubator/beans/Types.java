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
import java.util.stream.Stream;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Types {
	private Types() {
	}

	public sealed interface Trait {
	}

	public record ArrayType(Class<?> arrayType, Class<?> componentType) implements Trait {
		public static Trait of(final Type type) {
			if (type instanceof Class<?> arrayType &&
				arrayType.isArray() &&
				!arrayType.getComponentType().isPrimitive())
			{
				return new ArrayType(arrayType, arrayType.getComponentType());
			} else {
				return null;
			}
		}
	}

	public record ListType(Class<?> listType, Class<?> componentType) implements Trait {
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

	public record RecordType(Class<?> type) implements Trait {

		public Stream<RecordComponent> components() {
			return Stream.of(type.getRecordComponents())
				.filter(comp -> comp.getAccessor().getReturnType() != Class.class);
		}

		public static Trait of(final Type type) {
			if (type instanceof Class<?> cls && cls.isRecord()) {
				return new RecordType(cls);
			} else {
				return null;
			}
		}
	}

	public record BeanType(Class<?> type) implements Trait {

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

	public static Trait trait(final Type type) {
		Trait trait = ArrayType.of(type);
		if (trait != null) {
			return trait;
		}

		trait = ListType.of(type);
		if (trait != null) {
			return trait;
		}

		trait = RecordType.of(type);
		if (trait != null) {
			return trait;
		}

		trait = BeanType.of(type);
		if (trait != null) {
			return trait;
		}

		return trait;
	}

	public static boolean isIdentityType(final Object object) {
		return
			object != null &&
			!(object instanceof Constable) &&
			!(object instanceof TemporalAccessor) &&
			!(object instanceof Number);
	}

}
