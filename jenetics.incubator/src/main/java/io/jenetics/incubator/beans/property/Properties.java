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

import static io.jenetics.incubator.beans.internal.Filters.STANDARD_SOURCE_FILTER;
import static io.jenetics.incubator.beans.internal.Filters.STANDARD_TARGET_FILTER;
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
import io.jenetics.incubator.beans.internal.Filters;
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

	private Properties() {
	}

	/**
	 * This method extracts the direct properties of the given {@code root}
	 * object.
	 *
	 * @param root the root object from which the properties are extracted
	 * @return all direct properties of the given {@code root} object
	 */
	public static Stream<Property> extract(final PathEntry<?> root) {
		if (root == null || root.value() == null) {
			return Stream.empty();
		}

		final var type = PathEntry.<Type>of(root.value().getClass());
		final var descriptions = Descriptions.extract(type);

		return descriptions
			.flatMap(description -> extract(root, description));
	}

	private static Stream<Property> extract(
		final PathEntry<?> root,
		final Description description
	) {
		final var enclosing = root.value();

		if (description.value() instanceof Description.Value.Single desc) {
			final var path = root.path().append(description.name());
			final var value = desc.getter().get(root.value());

			final Property prop;
			if (isArrayType(desc.value())) {
				prop = new ArrayProperty(path, toValue(enclosing, value, desc));
			} else if (isListType(desc.value())) {
				prop = new ListProperty(path, toValue(enclosing, value, desc));
			} else {
				prop = new SimpleProperty(path, toValue(enclosing, value, desc));
			}

			return Stream.of(prop);
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
		} else {
			return Stream.empty();
		}
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
	 * Return a Stream that is lazily populated with {@code Property} by
	 * searching for all properties in an object tree rooted at a given
	 * starting {@code root} object. If used with the {@link #extract(PathEntry)}
	 * method, all found descriptions are returned, including the descriptions
	 * from the Java classes.
	 * <pre>{@code
	 * final var object = "Some Value";
	 * Properties.walk(PathEntry.of(object), Properties::extract)
	 *     .forEach(System.out::println);
	 * }</pre>
	 * The code snippet above will create the following output:
	 * <pre>
	 * SimpleProperty[path=blank, value=Mutable[value=false, type=boolean, enclosureType=java.lang.String]]
	 * SimpleProperty[path=bytes, value=Mutable[value=[B@41e1455d, type=[B, enclosureType=java.lang.String]]
	 * SimpleProperty[path=empty, value=Mutable[value=false, type=boolean, enclosureType=java.lang.String]]
	 * </pre>
	 *
	 * If you are not interested in the property descriptions of the Java
	 * classes, you should the {@link #walk(PathEntry, String...)} )} instead.
	 *
	 * @see #walk(PathEntry, String...)
	 * @see #walk(Object, String...)
	 *
	 * @param root the root of the object tree
	 * @param extractor the first level property extractor used for extracting
	 *        the object properties
	 * @return a property stream
	 */
	public static Stream<Property> walk(
		final PathEntry<?> root,
		final Extractor<? super PathEntry<?>, ? extends Property> extractor
	) {
		final Extractor<? super PathEntry<?>, Property>
			recursiveExtractor = PreOrderIterator.extractor(
				extractor,
				property -> PathEntry.of(property.path(), property.value().value()),
				PathEntry::value
			);

		return recursiveExtractor.extract(root);
	}

	/**
	 * Return a {@code Stream} that is lazily populated with {@code Property}
	 * by walking the object tree rooted at a given starting object.
	 *
	 * <pre>{@code
	 * record Author(String forename, String surname) { }
	 * record Book(String title, int pages, List<Author> authors) { }
	 *
	 * final var object = new Book(
	 *     "Oliver Twist",
	 *     366,
	 *     List.of(new Author("Charles", "Dickens"))
	 * );
	 *
	 * Properties.walk(PathEntry.of(object))
	 *     .forEach(System.out::println);
	 * }</pre>
	 *
	 * The code snippet above will create the following output:
	 *
	 * <pre>{@code
	 * ListProperty[path=authors, value=Immutable[value=[Author[forename=Charles, surname=Dickens]], type=java.util.List, enclosureType=Book]]
	 * IndexProperty[path=authors[0], value=Mutable[value=Author[forename=Charles, surname=Dickens], type=Author, enclosureType=java.util.ImmutableCollections$List12]]
	 * SimpleProperty[path=authors[0].forename, value=Immutable[value=Charles, type=java.lang.String, enclosureType=Author]]
	 * SimpleProperty[path=authors[0].surname, value=Immutable[value=Dickens, type=java.lang.String, enclosureType=Author]]
	 * SimpleProperty[path=pages, value=Immutable[value=366, type=int, enclosureType=Book]]
	 * SimpleProperty[path=title, value=Immutable[value=Oliver Twist, type=java.lang.String, enclosureType=Book]]
	 * }</pre>
	 *
	 * @see #walk(Object, String...)
	 *
	 * @param root the root of the object tree
	 * @param includes the included object name (glob) patterns
	 * @return a property stream
	 */
	public static Stream<Property>
	walk(final PathEntry<?> root, final String... includes) {
		final Extractor<PathEntry<?>, Property>
			extractor = Properties::extract;

		return walk(
			root,
			extractor
				.sourceFilter(STANDARD_SOURCE_FILTER)
				.sourceFilter(includesFilter(includes))
				.targetFilter(STANDARD_TARGET_FILTER)
		);
	}

	private static Predicate<? super PathEntry<?>>
	includesFilter(final String... includes) {
		return Stream.of(includes)
			.map(Filters::toPattern)
			.map(Filters::toFilter)
			.reduce((a, b) -> p -> a.test(p) || b.test(p))
			.orElse(a -> true);
	}

	/**
	 * Return a {@code Stream} that is lazily populated with {@code Property}
	 * by walking the object tree rooted at a given starting object.
	 *
	 * @see #walk(PathEntry, String...)
	 *
	 * @param root the root of the object tree
	 * @param includes the included object name (glob) patterns
	 * @return a property stream
	 */
	public static Stream<Property> walk(
		final Object root,
		final String... includes
	) {
		return walk(
			root instanceof PathEntry<?> po
				? po : PathEntry.of(root),
			includes
		);
	}

	static String toString(final String name, final Property property) {
		return "%s[path=%s, value=%s]".formatted(
			name,
			property.path(),
			property.value()
		);
	}




}
