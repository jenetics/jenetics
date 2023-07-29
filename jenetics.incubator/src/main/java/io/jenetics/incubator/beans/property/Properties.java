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
package io.jenetics.incubator.beans.property;

import static java.lang.String.format;

import java.util.function.Predicate;
import java.util.stream.Stream;

import io.jenetics.incubator.beans.PathValue;
import io.jenetics.incubator.beans.description.Descriptions;
import io.jenetics.incubator.beans.Extractor;
import io.jenetics.incubator.beans.internal.PreOrderIterator;

/**
 * This class contains helper methods for extracting the properties from a given
 * root object. It is the main entry point for the extracting properties from
 * an object graph.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Properties {

	public static final Predicate<PathValue<Object>>
		STANDARD_SOURCE_FILTER =
		object -> {
			final var type = object.value() != null
				? object.value().getClass()
				: Object.class;

			return Descriptions.STANDARD_SOURCE_FILTER
				.test(new PathValue<>(object.path(), type));
		};

	public static final Predicate<Property> STANDARD_TARGET_FILTER = prop ->
		!(prop instanceof SimpleProperty &&
		prop.value().enclosure().getClass().getName().startsWith("java"));

	private Properties() {
	}

	/**
	 * Return a {@code Stream} that is lazily populated with {@code Property}
	 * by walking the object tree rooted at a given starting object. The object
	 * tree is traversed in pre-order.
	 *
	 * @param root the root of the object tree
	 * @param extractor the first level property extractor used for extracting
	 *        the object properties
	 * @return a property stream
	 */
	public static Stream<Property> walk(
		final PathValue<Object> root,
		final Extractor<PathValue<Object>, Property> extractor
	) {
		final var ext = PreOrderIterator.extractor(
			extractor,
			property -> new PathValue<>(property.path(), property.value().value()),
			PathValue::value
		);
		return ext.extract(root);
	}

	/**
	 * Return a {@code Stream} that is lazily populated with {@code Property}
	 * by walking the object tree rooted at a given starting object. The object
	 * tree is traversed in pre-order.
	 *
	 * <pre>{@code
	 * Property.walk(new DataObject(root), "my.object.packages.*")
	 *    .forEach(System.out::println);
	 * }</pre>
	 *
	 * @param root the root of the object tree
	 * @param includes the included object name (glob) patterns
	 * @return a property stream
	 */
	public static Stream<Property>
	walk(final PathValue<Object> root, final String... includes) {
		return walk(
			root,
			PropertyExtractors.DIRECT
				.sourceFilter(includesFilter(includes))
				.sourceFilter(STANDARD_SOURCE_FILTER)
				.targetFilter(STANDARD_TARGET_FILTER)
		);
	}

	private static Predicate<PathValue<Object>>
	includesFilter(final String... includes) {
		return Stream.of(includes)
			.map(Filters::toPattern)
			.map(Filters::toFilter)
			.reduce(Predicate::or)
			.orElse(a -> true);
	}

	public static Stream<Property> walk(
		final Object root,
		final String... includes
	) {
		@SuppressWarnings("unchecked")
		final var object = root instanceof PathValue<?> po
			? (PathValue<Object>)po
			: new PathValue<>(root);

		return walk(object, includes);
	}

	static String toString(final String name, final Property property) {
		return "%s[path=%s, value=%s]".formatted(
			name,
			property.path(),
			property.value()
		);
	}

}
