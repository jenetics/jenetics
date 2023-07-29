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
package io.jenetics.incubator.beans.description;

import static io.jenetics.incubator.beans.internal.Types.toArrayType;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import io.jenetics.incubator.beans.Extractor;
import io.jenetics.incubator.beans.Path;
import io.jenetics.incubator.beans.PathEntry;
import io.jenetics.incubator.beans.internal.PreOrderIterator;

/**
 * This class contains methods for extracting the <em>static</em> bean property
 * information from a given object. It is the main entry point for the extracting
 * properties from an object graph.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Descriptions {

	public static final Predicate<PathEntry<Type>>
		STANDARD_SOURCE_FILTER =
		type -> {
			final var cls = type.value() instanceof ParameterizedType pt
				? (Class<?>)pt.getRawType()
				: (Class<?>)type.value();

			final var name = cls.getName();

			return
				// Allow native Java arrays, except byte[] arrays.
				(name.startsWith("[") && !name.endsWith("[B")) ||
				// Allow Java collection classes.
				Collection.class.isAssignableFrom(cls) ||
				(
					!name.startsWith("java") &&
					!name.startsWith("com.sun") &&
					!name.startsWith("sun") &&
					!name.startsWith("jdk")
				);
		};

	public static final Predicate<Description> STANDARD_TARGET_FILTER = prop ->
		!(prop.value() instanceof Description.Value.Single &&
			prop.value().enclosure().getName().startsWith("java"));

	private Descriptions() {
	}

	public static Stream<Description> extract(final PathEntry<? extends Type> type) {
		if (type == null || type.value() == null) {
			return Stream.empty();
		}

		final var array = toArrayDescription(type);
		if (array != null) {
			return Stream.of(array);
		}

		final var list = toListDescription(type);
		if (list != null) {
			return Stream.of(list);
		}

		return toDescriptions(type)
			.sorted(Comparator.comparing(PathEntry::name));
	}

	private static Description
	toArrayDescription(final PathEntry<? extends Type> type) {
		final var arrayType = toArrayType(type.value());

		if (arrayType != null) {
			return new Description(
				type.path().append(new Path.Index(0)),
				new Description.Value.Indexed(
					arrayType.getComponentType(),
					arrayType,
					Array::getLength, Array::get, Array::set
				)
			);
		} else {
			return null;
		}
	}

	private static Description
	toListDescription(final PathEntry<? extends Type> type) {
		if (type.value() instanceof ParameterizedType parameterizedType &&
			parameterizedType.getRawType() instanceof Class<?> listType &&
			List.class.isAssignableFrom(listType))
		{
			final var typeArguments = parameterizedType.getActualTypeArguments();
			if (typeArguments.length == 1 &&
				typeArguments[0] instanceof Class<?> componentType)
			{
				return new Description(
					type.path().append(new Path.Index(0)),
					new Description.Value.Indexed(
						listType,
						componentType,
						Lists::size, Lists::get, Lists::set
					)
				);
			}
		}

		if (type.value() instanceof Class<?> listType &&
			List.class.isAssignableFrom(listType))
		{
			return new Description(
				type.path().append(new Path.Index(0)),
				new Description.Value.Indexed(
					listType,
					Object.class,
					Lists::size, Lists::get, Lists::set
				)
			);
		}

		return null;
	}

	private static Stream<Description>
	toDescriptions(final PathEntry<? extends Type> type) {
		if (type.value() instanceof Class<?> cls && cls.isRecord()) {
			return Stream.of(cls.getRecordComponents())
				.filter(d -> d.getAccessor().getReturnType() != Class.class)
				.map(c -> toDescription(type.path(), c));
		} else if (type.value() instanceof Class<?> cls) {
			try {
				final PropertyDescriptor[] descriptors = Introspector
					.getBeanInfo(cls)
					.getPropertyDescriptors();

				return Stream.of(descriptors)
					.filter(d -> d.getReadMethod() != null)
					.filter(d -> d.getReadMethod().getReturnType() != Class.class)
					.map(d -> toDescription(type.path(), d));
			} catch (IntrospectionException e) {
				throw new IllegalArgumentException(
					"Can't introspect class '%s'.".formatted(type.value()),
					e
				);
			}
		} else {
			return Stream.of();
		}
	}

	private static Description
	toDescription(final Path path, final PropertyDescriptor descriptor) {
		return new Description(
			path.append(descriptor.getName()),
			new Description.Value.Single(
				descriptor.getReadMethod().getDeclaringClass(),
				descriptor.getReadMethod().getGenericReturnType(),
				Methods.toGetter(descriptor.getReadMethod()),
				Methods.toSetter(descriptor.getWriteMethod())
			)
		);
	}

	private static Description
	toDescription(final Path path, final RecordComponent component) {
		return new Description(
			path.append(component.getName()),
			new Description.Value.Single(
				component.getDeclaringRecord(),
				component.getAccessor().getGenericReturnType(),
				Methods.toGetter(component.getAccessor()),
				null
			)
		);
	}

	/**
	 * asdf
	 * @param root adf
	 * @param extractor af
	 * @return asdf
	 */
	public static Stream<Description> walk(
		final PathEntry<Type> root,
		final Extractor<PathEntry<Type>, Description> extractor
	) {
		final var ext = PreOrderIterator.extractor(
			extractor,
			desc -> PathEntry.of(desc.path(), desc.value().value()),
			PathEntry::value
		);
		return ext.extract(root);
	}

	/**
	 * asdf
	 * @param root adf
	 * @return asdf
	 */
	public static Stream<Description>
	walk(final PathEntry<Type> root) {
		return walk(
			root,
			((Extractor<PathEntry<Type>, Description>)Descriptions::extract)
				.sourceFilter(STANDARD_SOURCE_FILTER)
				.targetFilter(STANDARD_TARGET_FILTER)
		);
	}

}
