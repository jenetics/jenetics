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

import static io.jenetics.incubator.beans.internal.Reflect.toRawType;

import java.lang.reflect.Type;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.jenetics.incubator.beans.Filters;
import io.jenetics.incubator.beans.Path;
import io.jenetics.incubator.beans.PathValue;
import io.jenetics.incubator.beans.description.Access;
import io.jenetics.incubator.beans.description.Description;
import io.jenetics.incubator.beans.description.Descriptions;
import io.jenetics.incubator.beans.description.IndexedAccess;
import io.jenetics.incubator.beans.description.IndexedDescription;
import io.jenetics.incubator.beans.description.SimpleDescription;
import io.jenetics.incubator.beans.internal.Dtor;
import io.jenetics.incubator.beans.internal.PreOrderIterator;
import io.jenetics.incubator.beans.reflect.ArrayType;
import io.jenetics.incubator.beans.reflect.BeanType;
import io.jenetics.incubator.beans.reflect.ElementType;
import io.jenetics.incubator.beans.reflect.ListType;
import io.jenetics.incubator.beans.reflect.OptionalType;
import io.jenetics.incubator.beans.reflect.PropertyType;
import io.jenetics.incubator.beans.reflect.RecordType;

/**
 * This class contains helper methods for extracting the properties from a given
 * root object. It is the main entry point for the extracting properties from
 * an object graph.
 *
 * {@snippet class="PropertySnippets" region="walk(Object)"}
 *
 * The code snippet above will create the following output
 * <pre>{@code
 * SimpleProperty[path=title, value=Crossroads, mutable=false, type=java.lang.String, enclosure=Book]
 * SimpleProperty[path=pages, value=832, mutable=false, type=int, enclosure=Book]
 * ListProperty[path=authors, value=[Author[Jonathan Franzen]], mutable=false, type=java.util.List, enclosure=Book]
 * IndexProperty[path=authors[0], value=Author[Jonathan Franzen], mutable=true, type=Author, enclosure=java.util.ImmutableCollections$List12]
 * SimpleProperty[path=authors[0].forename, value=Jonathan, mutable=false, type=java.lang.String, enclosure=Author]
 * SimpleProperty[path=authors[0].surname, value=Franzen, mutable=false, type=java.lang.String, enclosure=Author]
 * SimpleProperty[path=authors[0].birthDate, value=1959-08-17, mutable=false, type=java.time.LocalDate, enclosure=Author]
 * ListProperty[path=authors[0].books, value=[], mutable=false, type=java.util.List, enclosure=Author]
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.2
 * @since 7.2
 */
public final class Properties {

	private Properties() {
	}

	/**
	 * This method extracts the direct properties of the given {@code root}
	 * object.
	 *
	 * {@snippet class="PropertySnippets" region="list(PathValue)"}
	 *
	 * The code snippet above will create the following output
	 * <pre>{@code
	 * SimpleProperty[path=author.forename, value=Jonathan, mutable=false, type=java.lang.String, enclosure=Author]
	 * SimpleProperty[path=author.surname, value=Franzen, mutable=false, type=java.lang.String, enclosure=Author]
	 * SimpleProperty[path=author.birthDate, value=1959-08-17, mutable=false, type=java.time.LocalDate, enclosure=Author]
	 * ListProperty[path=author.books, value=[], mutable=false, type=java.util.List, enclosure=Author]
	 * }</pre>
	 *
	 * @param root the root object from which the properties are extracted
	 * @return all direct properties of the given {@code root} object
	 */
	public static Stream<Property> list(final PathValue<?> root) {
		if (root == null || root.value() == null) {
			return Stream.empty();
		}

		final var type = PathValue.<Type>of(root.value().getClass());
		final var descriptions = Descriptions.list(type);

		return descriptions
			.flatMap(description -> list(root, description));
	}

	private static Stream<Property> list(
		final PathValue<?> root,
		final Description description
	) {
		final var enclosure = root.value();

		return switch (description) {
			case SimpleDescription desc -> {
				final var param = new PropParam(
					root.path().append(desc.path().element()),
					enclosure,
					desc.access().getter().get(root.value()),
					toRawType(description.type()),
					desc.access().getter(),
					switch (desc.access()) {
						case Access.Readonly access -> null;
						case Access.Writable access -> access.setter();
					}
				);

				final Property prop = switch (PropertyType.of(desc.type())) {
					case ElementType t -> new SimpleProperty(param);
					case RecordType t -> new RecordProperty(param);
					case BeanType t -> new BeanProperty(param);
					case OptionalType t -> new OptionalProperty(param);
					case ArrayType t -> new ArrayProperty(param);
					case ListType t -> new ListProperty(param);
				};

				yield Stream.of(prop);
			}
			case IndexedDescription desc -> {
				final var path = desc.path().element() instanceof Path.Index
					? root.path()
					: root.path().append(desc.path().element());

				final int size = desc.size().get(enclosure);

				yield IntStream.range(0, size).mapToObj(i -> {
					final Object value = desc.access().getter().get(enclosure, i);
					final Class<?> type = value != null
						? value.getClass()
						: toRawType(desc.type());

					final var param = new PropParam(
						path.append(new Path.Index(i)),
						enclosure,
						desc.access().getter().get(enclosure, i),
						type,
						object -> desc.access().getter().get(object, i),
						switch (desc.access()) {
							case IndexedAccess.Readonly access -> null;
							case IndexedAccess.Writable access -> (object, val) ->
								access.setter().set(object, i, val);
						}
					);

					return new IndexProperty(param, i);
				});
			}
		};
	}

	/**
	 * This method extracts the direct properties of the given {@code root}
	 * object.
	 *
	 * {@snippet class="PropertySnippets" region="list(Object)"}
	 *
	 * The code snippet above will create the following output
	 * <pre>{@code
	 * SimpleProperty[path=forename, value=Jonathan, mutable=false, type=java.lang.String, enclosure=Author]
	 * SimpleProperty[path=surname, value=Franzen, mutable=false, type=java.lang.String, enclosure=Author]
	 * SimpleProperty[path=birthDate, value=1959-08-17, mutable=false, type=java.time.LocalDate, enclosure=Author]
	 * ListProperty[path=books, value=[], mutable=false, type=java.util.List, enclosure=Author]
	 * }</pre>
	 *
	 * @param root the root object from which the properties are extracted
	 * @return all direct properties of the given {@code root} object
	 */
	public static Stream<Property> list(final Object root) {
		return root instanceof PathValue<?> pv
			? list(pv)
			: list(PathValue.of(root));
	}

	private static Stream<Property> walk(
		final PathValue<?> root,
		final Dtor<? super PathValue<?>, ? extends Property> dtor
	) {
		final Dtor<? super PathValue<?>, Property> recursiveDtor =
			PreOrderIterator.dtor(
				dtor,
				property -> PathValue.of(property.path(), property.value()),
				PathValue::value
			);

		return recursiveDtor.unapply(root);
	}

	/**
	 * Return a {@code Stream} that is lazily populated with {@code Property}
	 * by walking the object tree rooted at a given starting object.
	 *
	 * @see #walk(Object, String...)
	 *
	 * @param root the root of the object tree
	 * @param includes the included object name (glob) patterns
	 * @return a property stream
	 */
	public static Stream<Property>
	walk(final PathValue<?> root, final String... includes) {
		final Dtor<PathValue<?>, Property> dtor = Properties::list;

		return walk(
			root,
			dtor.sourceFilter(includesFilter(includes))
		);
	}

	private static Predicate<? super PathValue<?>>
	includesFilter(final String... includes) {
		final Predicate<? super Path> filter = Stream.of(includes)
			.map(include -> Filters
				.filtering(
					Path::toString,
					Filters.ofGlob(include)
				)
			)
			.reduce((a, b) -> path -> a.test(path) || b.test(path))
			.orElse(a -> true);

		return entry -> filter.test(entry.path());
	}

	/**
	 * Return a {@code Stream} that is lazily populated with {@code Property}
	 * by walking the object tree rooted at a given starting object.
	 *
	 * {@snippet class="PropertySnippets" region="walk(Object)"}
	 *
	 * The code snippet above will create the following output
	 * <pre>{@code
	 * SimpleProperty[path=title, value=Crossroads, mutable=false, type=java.lang.String, enclosure=Book]
	 * SimpleProperty[path=pages, value=832, mutable=false, type=int, enclosure=Book]
	 * ListProperty[path=authors, value=[Author[Jonathan Franzen]], mutable=false, type=java.util.List, enclosure=Book]
	 * IndexProperty[path=authors[0], value=Author[Jonathan Franzen], mutable=true, type=Author, enclosure=java.util.ImmutableCollections$List12]
	 * SimpleProperty[path=authors[0].forename, value=Jonathan, mutable=false, type=java.lang.String, enclosure=Author]
	 * SimpleProperty[path=authors[0].surname, value=Franzen, mutable=false, type=java.lang.String, enclosure=Author]
	 * SimpleProperty[path=authors[0].birthDate, value=1959-08-17, mutable=false, type=java.time.LocalDate, enclosure=Author]
	 * ListProperty[path=authors[0].books, value=[], mutable=false, type=java.util.List, enclosure=Author]
	 * }</pre>
	 *
	 * @see #walk(PathValue, String...)
	 *
	 * @param root the root of the object tree
	 * @param includes the included object name (glob) patterns
	 * @return a property stream
	 */
	public static Stream<Property>
	walk(final Object root, final String... includes) {
		return walk(
			root instanceof PathValue<?> pe ? pe : PathValue.of(root),
			includes
		);
	}

	static String toString(final String name, final Property property) {
		return "%s[path=%s, value=%s, mutable=%s, type=%s, enclosure=%s]".formatted(
			name,
			property.path(),
			property.value(),
			property.writer().isPresent(),
			property.type().getName(),
			property.enclosure().getClass().getName()
		);
	}

}
