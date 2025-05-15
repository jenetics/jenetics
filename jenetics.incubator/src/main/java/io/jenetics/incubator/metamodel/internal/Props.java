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

import static java.util.Objects.requireNonNull;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

import io.jenetics.incubator.metamodel.Path;
import io.jenetics.incubator.metamodel.PathValue;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.3
 * @since 8.3
 */
public final class Props {
	private Props() {
	}

	public static Stream<PathValue<?>> list(final PathValue<?> root) {
		requireNonNull(root);

		final PreOrderIterator<PathValue<?>, PathValue<?>> iterator =
			new PreOrderIterator<>(
				root,
				Props::deconstruct,
				Function.identity(),
				PathValue::value
			);

		return iterator.stream();
	}

	public static Stream<PathValue<?>> deconstruct(final PathValue<?> root) {
		requireNonNull(root);

		return switch (root.value()) {
			case null -> Stream.empty();
			case Object element when isElement(element) -> Stream.empty();
			case Record record -> deconstruct(root.path(), record);
			case Collection<?> coll -> deconstruct(root.path(), coll);
			case Object[] array -> deconstruct(root.path(), Arrays.asList(array));
			case Object value -> deconstruct(root.path(), value);
		};
	}

	private static boolean isElement(final Object value) {
		return Reflect.isElementType(value.getClass());
	}

	private static Stream<PathValue<?>> deconstruct(Path path, Collection<?> col) {
		final int[] i = new int[] {0};
		return  col.stream()
			.map(value -> PathValue.of(path.append(new Path.Index(i[0]++)), value));
	}

	private static Stream<PathValue<?>> deconstruct(Path path, Record record) {
		return Stream.of(record.getClass().getRecordComponents())
			.map(comp -> {
				final var value = get(comp.getAccessor(), record);
				return PathValue.of(path.append(comp.getName()), value);
			});
	}

	private static Object get(Method method, Object object) {
		try {
			return method.invoke(object);
		} catch (IllegalAccessException | InvocationTargetException e) {
			return null;
		}
	}

	private static Stream<PathValue<?>> deconstruct(Path path, Object bean) {
		try {
			final var descriptors = Introspector
				.getBeanInfo(bean.getClass())
				.getPropertyDescriptors();

			return Stream.of(descriptors)
				.map(desc -> {
					final var value = get(desc.getReadMethod(), bean);
					return PathValue.of(path.append(desc.getName()), value);
				});
		} catch (IntrospectionException e) {
			return Stream.empty();
		}
	}

}
