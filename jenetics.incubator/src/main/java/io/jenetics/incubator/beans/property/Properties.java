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

import static io.jenetics.incubator.beans.internal.Types.isArrayType;
import static io.jenetics.incubator.beans.internal.Types.isListType;
import static io.jenetics.incubator.beans.internal.Types.toClass;

import java.lang.reflect.Type;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.jenetics.incubator.beans.Extractor;
import io.jenetics.incubator.beans.Path;
import io.jenetics.incubator.beans.PathEntry;
import io.jenetics.incubator.beans.description.Description;
import io.jenetics.incubator.beans.description.Descriptions;
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

	public static final Predicate<PathEntry<Object>>
		STANDARD_SOURCE_FILTER =
		object -> {
			final var type = object.value() != null
				? object.value().getClass()
				: Object.class;

			return Descriptions.STANDARD_SOURCE_FILTER
				.test(PathEntry.of(object.path(), type));
		};

	public static final Predicate<Property> STANDARD_TARGET_FILTER = prop ->
		!(prop instanceof SimpleProperty &&
		prop.value().enclosure().getClass().getName().startsWith("java"));

	private Properties() {
	}


	/**
	 * This method extracts the direct properties of the given {@code root}
	 * object.
	 *
	 * @param root the root object from which the properties are extracted
	 * @return all direct properties of the given {@code root} object
	 */
	public static Stream<Property> extract(final PathEntry<Object> root) {
		if (root == null || root.value() == null) {
			return Stream.empty();
		}

		final var type = PathEntry.<Type>of(root.value().getClass());
		final var descriptions = Descriptions.extract(type);

		return descriptions.flatMap(description -> {
			final var enclosing = root.value();

			if (description.value() instanceof Description.Value.Single desc) {
				final var path = root.path().append(description.name());
				final var value = desc.getter().get(root.value());

				final Property property;
				if (isArrayType(desc.value())) {
					property = new ArrayProperty(path, toValue(enclosing, value, desc));
				} else if (isListType(desc.value())) {
					property = new ListProperty(path, toValue(enclosing, value, desc));
				} else {
					property = new SimpleProperty(path, toValue(enclosing, value, desc));
				}

				return Stream.of(property);
			} else if (description.value() instanceof Description.Value.Indexed desc) {
				final var path = description.path().element() instanceof Path.Index
					? root.path()
					: root.path().append(new Path.Name(description.name()));

				final int size = desc.size().get(enclosing);

				return IntStream.range(0, size).mapToObj(i -> {
					final var value = desc.getter().get(enclosing, i);

					return new IndexProperty(
						path.append(new Path.Index(i)),
						i,
						new Property.Value.Mutable(
							enclosing,
							value,
							value != null ? value.getClass() : toClass(desc.value()),
							o -> desc.getter().get(o, i),
							(o, v) -> desc.setter().orElseThrow().set(o, i, v)
						)
					);
				});
			}

			return Stream.empty();
		});
	}

	private static Property.Value toValue(
		final Object enclosing,
		final Object value,
		final Description.Value.Single description
	) {
		if (description.setter().isPresent()) {
			return new Property.Value.Mutable(
				enclosing,
				value,
				toClass(description.value()),
				description.getter(),
				description.setter().orElseThrow()
			);
		} else {
			return new Property.Value.Immutable(
				enclosing,
				value,
				toClass(description.value())
			);
		}
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
		final PathEntry<Object> root,
		final Extractor<PathEntry<Object>, Property> extractor
	) {
		final var ext = PreOrderIterator.extractor(
			extractor,
			property -> PathEntry.of(property.path(), property.value().value()),
			PathEntry::value
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
	walk(final PathEntry<Object> root, final String... includes) {
		return walk(
			root,
			((Extractor<PathEntry<Object>, Property>)Properties::extract)
				.sourceFilter(includesFilter(includes))
				.sourceFilter(STANDARD_SOURCE_FILTER)
				.targetFilter(STANDARD_TARGET_FILTER)
		);
	}

	private static Predicate<PathEntry<Object>>
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
		final var object = root instanceof PathEntry<?> po
			? (PathEntry<Object>)po
			: PathEntry.of(root);

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
