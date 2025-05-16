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

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static io.jenetics.incubator.metamodel.internal.Methods.invoke;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.util.Collection;
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
		return PreOrderIterator
			.<PathValue<?>>of(root, Props::destruct, PathValue::value)
			.asStream();
	}

	private static Stream<PathValue<?>> destruct(final PathValue<?> root) {
		requireNonNull(root);

		return switch (root.value()) {
			case null -> Stream.empty();
			case Object element when isElement(element) -> Stream.empty();
			case Collection<?> coll -> destruct(root.path(), coll);
			case Object[] array -> destruct(root.path(), asList(array));
			case Record record -> destruct(root.path(), record);
			case Object bean -> destruct(root.path(), bean);
		};
	}

	private static boolean isElement(final Object value) {
		return Reflect.isElementType(value.getClass());
	}

	private static Stream<PathValue<?>> destruct(Path path, Collection<?> col) {
		final int[] i = new int[] {0};
		return  col.stream()
			.map(value -> PathValue.of(path.append(new Path.Index(i[0]++)), value));
	}

	private static Stream<PathValue<?>> destruct(Path path, Record record) {
		return Stream.of(record.getClass().getRecordComponents())
			.map(comp -> {
				final var value = invoke(comp.getAccessor(), record);
				return PathValue.of(path.append(comp.getName()), value);
			});
	}

	private static Stream<PathValue<?>> destruct(Path path, Object bean) {
		try {
			final var descriptors = Introspector
				.getBeanInfo(bean.getClass())
				.getPropertyDescriptors();

			return Stream.of(descriptors)
				.map(desc -> {
					final var value = invoke(desc.getReadMethod(), bean);
					return PathValue.of(path.append(desc.getName()), value);
				});
		} catch (IntrospectionException e) {
			return Stream.empty();
		}
	}

}
