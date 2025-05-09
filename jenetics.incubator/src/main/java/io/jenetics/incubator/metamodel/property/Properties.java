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
package io.jenetics.incubator.metamodel.property;

import static java.util.Objects.requireNonNull;
import static io.jenetics.incubator.metamodel.internal.Reflect.toRawType;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.jenetics.incubator.metamodel.Filters;
import io.jenetics.incubator.metamodel.Path;
import io.jenetics.incubator.metamodel.PathValue;
import io.jenetics.incubator.metamodel.access.Accessor;
import io.jenetics.incubator.metamodel.internal.Dtor;
import io.jenetics.incubator.metamodel.internal.PreOrderIterator;
import io.jenetics.incubator.metamodel.type.ArrayType;
import io.jenetics.incubator.metamodel.type.BeanType;
import io.jenetics.incubator.metamodel.type.CollectionType;
import io.jenetics.incubator.metamodel.type.Description;
import io.jenetics.incubator.metamodel.type.ElementType;
import io.jenetics.incubator.metamodel.type.IndexType;
import io.jenetics.incubator.metamodel.type.IndexedType;
import io.jenetics.incubator.metamodel.type.ListType;
import io.jenetics.incubator.metamodel.type.MapType;
import io.jenetics.incubator.metamodel.type.ModelType;
import io.jenetics.incubator.metamodel.type.OptionalType;
import io.jenetics.incubator.metamodel.type.ComponentType;
import io.jenetics.incubator.metamodel.type.RecordType;
import io.jenetics.incubator.metamodel.type.SetType;

/**
 * This class contains helper methods for extracting the properties from a given
 * root object. It is the main entry point for the extracting properties from
 * an object graph.
 * {@snippet class="PropertySnippets" region="walk(Object)"}
 *
 * The code snippet above will create the following output
 * <pre>{@code
 * SimpleProperty[path=title, value=Crossroads, mutable=false, type=java.lang.String, enclosure=Book]
 * SimpleProperty[path=pages, value=832, mutable=false, type=int, enclosure=Book]
 * ListProperty[path=authors, value=[Author[Jonathan Franzen]], mutable=false, type=java.util.List, enclosure=Book]
 * IndexProperty[path=authors[0], value=Author[Jonathan Franzen], mutable=false, type=Author, enclosure=java.util.ImmutableCollections$List12]
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
		final var descriptions = Description.list(type);

		return descriptions
			.flatMap(description -> list(root, description));
	}

	private static Stream<Property> list(
		final PathValue<?> root,
		final Description description
	) {
		final var enclosure = root.value();

		return switch (description.model()) {
			case ComponentType pt -> {
				final var param = new PropParam(
					root.path().append(description.path().element()),
					enclosure,
					pt.accessor().of(enclosure).getter().get(),
					toRawType(description.type()),
					pt.annotations().toList(),
					pt.accessor().of(enclosure)
				);

				final Property property = switch (ModelType.of(pt.type())) {
					case ElementType t -> new ElementProperty(param);
					case RecordType t -> new RecordProperty(param);
					case BeanType t -> new BeanProperty(param);
					case OptionalType t -> new OptionalProperty(param);
					case ArrayType t -> new ArrayProperty(param);
					case ListType t -> new ListProperty(param);
					case SetType t -> new SetProperty(param);
					case MapType t -> new MapProperty(param);
					case IndexType t -> new IndexProperty(param, t.index());
					case ComponentType t -> new ElementProperty(param);
				};

				yield Stream.of(property);
			}
			case IndexedType it -> {
				final var path = description.path().element() instanceof Path.Index
					? root.path()
					: root.path().append(description.path().element());

				final var i = new AtomicInteger(0);

				final Stream<Object> values = StreamSupport.stream(
					it.iterable().of(enclosure).spliterator(),
					false
				);

				yield values.map(value -> {
					final Class<?> type = value != null
						? value.getClass()
						: toRawType(it.type());

					final var param = new PropParam(
						path.append(new Path.Index(i.get())),
						enclosure,
						value,
						type,
						it.annotations().toList(),
						it.accessor().of(value).at(i.get())
					);

					return new IndexProperty(param, i.getAndIncrement());
				});
			}
			case CollectionType ct -> {
				final var path = description.path().element() instanceof Path.Index
					? root.path()
					: root.path().append(description.path().element());

				final var i = new AtomicInteger(0);

				final Stream<Object> values = StreamSupport.stream(
					ct.iterable().of(enclosure).spliterator(),
					false
				);

				yield values.map(value -> {
					final Class<?> type = value != null
						? value.getClass()
						: toRawType(ct.type());

					final var param = new PropParam(
						path.append(new Path.Index(i.getAndIncrement())),
						enclosure,
						value,
						type,
						ct.annotations().toList(),
						new Accessor.Readonly(() -> value)
					);

					return new ElementProperty(param);
				});
			}
			case OptionalType ot -> {
				final var path = description.path().element() instanceof Path.Index
					? root.path()
					: root.path().append(description.path().element());

				var value = ot.access().of(enclosure).getter().get();
				if (value != null) {
					final var param = new PropParam(
						path.append(new Path.Index(0)),
						enclosure,
						value,
						value.getClass(),
						ot.annotations().toList(),
						ot.access().of(enclosure)
					);

					yield Stream.of(new IndexProperty(param, 0));
				} else {
					yield Stream.empty();
				}
			}
			default -> Stream.empty();
		};
	}

	/**
	 * This method extracts the direct properties of the given {@code root}
	 * object.
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
	 * {@snippet class="PropertySnippets" region="walk(Object)"}
	 *
	 * The code snippet above will create the following output
	 * <pre>{@code
	 * SimpleProperty[path=title, value=Crossroads, mutable=false, type=java.lang.String, enclosure=Book]
	 * SimpleProperty[path=pages, value=832, mutable=false, type=int, enclosure=Book]
	 * ListProperty[path=authors, value=[Author[Jonathan Franzen]], mutable=false, type=java.util.List, enclosure=Book]
	 * IndexProperty[path=authors[0], value=Author[Jonathan Franzen], mutable=false, type=Author, enclosure=java.util.ImmutableCollections$List12]
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

	/**
	 * Lists all properties of the given {@code root} element with the given
	 * {@code type}.
	 *
	 * @param type the property value type
	 * @param root the root object
	 * @return a property value stream of the given {@code type}
	 * @param <T> the property type
	 */
	public static <T> Stream<PathValue<T>>
	listValuesOfType(final Class<? extends T> type, final PathValue<?> root) {
		requireNonNull(type);
		return list(root)
			.filter(Filters.byType(type))
			.map(p -> PathValue.of(p.path(), type.cast(p.value())));
	}

	/**
	 * Lists all properties of the given {@code root} element with the given
	 * {@code type}.
	 *
	 * @param type the property value type
	 * @param root the root object
	 * @return a property value stream of the given {@code type}
	 * @param <T> the property type
	 */
	public static <T> Stream<PathValue<T>>
	listValuesOfType(final Class<? extends T> type, final Object root) {
		requireNonNull(type);
		return list(root)
			.filter(Filters.byType(type))
			.map(p -> PathValue.of(p.path(), type.cast(p.value())));
	}

}
