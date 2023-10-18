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

import java.lang.reflect.Type;
import java.util.function.Predicate;
import java.util.stream.Stream;

import io.jenetics.incubator.beans.Dtor;
import io.jenetics.incubator.beans.PathValue;
import io.jenetics.incubator.beans.PreOrderIterator;
import io.jenetics.incubator.beans.reflect.Reflect;
import io.jenetics.incubator.beans.reflect.IndexedType;
import io.jenetics.incubator.beans.reflect.SimpleType;
import io.jenetics.incubator.beans.reflect.StructType;

/**
 * This class contains methods for extracting the <em>static</em> bean property
 * information from a given object. It is the main entry point for the extracting
 * properties from an object graph.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.2
 * @since 7.2
 */
public final class Descriptions {

	/**
	 * Standard filter for the source types. It excludes all JDK types from
	 * being used, except array- and list types.
	 */
	public static final Predicate<? super PathValue<? extends Type>>
		STANDARD_SOURCE_FILTER =
		type -> Reflect.isNonJdkType(type.value()) ||
				Reflect.trait(type.value()) instanceof IndexedType;

	/**
	 * Standard filter for the target types. It excludes all JDK types from being
	 * part except they are part of the description
	 * ({@code Description.Value.Indexed}) or the enclosure type is an array- or
	 * list type.
	 */
	public static final Predicate<? super Description>
		STANDARD_TARGET_FILTER =
		prop -> Reflect.isNonJdkType(prop.value().enclosure()) ||
				Reflect.trait(prop.value().enclosure()) instanceof IndexedType ||
				prop.value() instanceof Value.Indexed;


	private Descriptions() {
	}

	/**
	 * Extracts the <em>directly</em> available property descriptions for the
	 * given {@code type} and start path, {@link PathValue#path()}.
	 *
	 * @param type the type to unapply
	 * @return the <em>directly</em> available property descriptions
	 */
	public static Stream<Description> unapply(final PathValue<? extends Type> type) {
		if (type == null || type.value() == null) {
			return Stream.empty();
		}

		var foo = switch (Reflect.trait(type.value())) {
			case SimpleType t -> "asd";
			case StructType t ->  t.components().map(c -> Description.of(type.path(), c));
			case IndexedType t -> Stream.of(Description.of(type.path(), t));
		};

		/*
		return switch (Reflect.trait(type.value())) {
			case IndexedType t when !t.componentType().isPrimitive() ->  Stream.of(Description.of(type.path(), t));
			case StructType t -> t.components().map(c -> Description.of(type.path(), c));
			default -> Stream.empty();
		};
		 */


		if (Reflect.trait(type.value()) instanceof IndexedType trait &&
			!trait.componentType().isPrimitive())
		{
			return Stream.of(Description.of(type.path(), trait));
		} else if (StructType.of(type.value()) instanceof StructType st) {
			return st.components().map(c -> Description.of(type.path(), c));
		} else {
			return Stream.of();
		}
	}

	/**
	 * Extracts the <em>directly</em> available property descriptions for the
	 * given {@code type}.
	 *
	 * @param type the type to unapply
	 * @return the <em>directly</em> available property descriptions
	 */
	public static Stream<Description> unapply(final Type type) {
		return unapply(PathValue.of(type));
	}

	/**
	 * Return a Stream that is lazily populated with {@code Description} by
	 * searching for all property descriptions in an object tree rooted at a
	 * given starting {@code root} object. Only the <em>statically</em>
	 * available property descriptions are returned. If used with the
	 * {@link #unapply(PathValue)} method, all found descriptions are returned,
	 * including the descriptions from the Java classes.
	 * {@snippet lang="java":
	 * Descriptions
	 *     .walk(PathEntry.of(String.class), Descriptions::extract)
	 *     .forEach(System.out::println);
	 * }
	 *
	 * The code snippet above will create the following output:
	 *
	 * <pre>
	 * Description[path=blank, value=Single[value=boolean, enclosure=java.lang.String]]
	 * Description[path=bytes, value=Single[value=class [B, enclosure=java.lang.String]]
	 * Description[path=empty, value=Single[value=boolean, enclosure=java.lang.String]]
	 * </pre>
	 *
	 * If you are not interested in the property descriptions of the Java
	 * classes, you should the {@link #walk(PathValue)} instead.
	 *
	 * @see #walk(PathValue)
	 *
	 * @param root the root class of the object graph
	 * @param dtor the extractor used for fetching the directly available
	 *        descriptions. See {@link #unapply(PathValue)}.
	 * @return all <em>statically</em> fetch-able property descriptions
	 */
	public static Stream<Description> walk(
		final PathValue<? extends Type> root,
		final Dtor<? super PathValue<? extends Type>, ? extends Description> dtor
	) {
		final Dtor<? super PathValue<? extends Type>, Description>
			recursiveDtor = PreOrderIterator.dtor(
				dtor,
				desc -> PathValue.of(desc.path(), desc.value().value()),
				PathValue::value
			);

		return recursiveDtor.unapply(root);
	}

	/**
	 * Return a Stream that is lazily populated with {@code Description} by
	 * searching for all property descriptions in an object tree rooted at a
	 * given starting {@code root} object. Only the <em>statically</em>
	 * available property descriptions are returned, and the property
	 * descriptions from Java classes are not part of the result.
	 *
	 * {@snippet lang="java":
	 * record Author(String forename, String surname) { }
	 * record Book(String title, int pages, List<Author> authors) { }
	 *
	 * Descriptions.walk(PathEntry.of(Book.class))
	 *     .forEach(System.out::println);
	 * }
	 *
	 * The code snippet above will create the following output:
	 *
	 * {@snippet lang="java":
	 * Description[path=authors, value=Single[value=java.util.List<Author>, enclosure=Book]]
	 * Description[path=authors[0], value=Indexed[value=Author, enclosure=java.util.List]]
	 * Description[path=authors[0].forename, value=Single[value=java.lang.String, enclosure=Author]]
	 * Description[path=authors[0].surname, value=Single[value=java.lang.String, enclosure=Author]]
	 * Description[path=pages, value=Single[value=int, enclosure=Book]]
	 * Description[path=title, value=Single[value=java.lang.String, enclosure=Book]]
	 * }
	 *
	 * @see #walk(PathValue, Dtor)
	 * @see #walk(Type)
	 *
	 * @param root the root class of the object graph
	 * @return all <em>statically</em> fetch-able property descriptions
	 */
	public static Stream<Description> walk(final PathValue<? extends Type> root) {
		final Dtor<PathValue<? extends Type>, Description> dtor = Descriptions::unapply;

		return walk(
			root,
			dtor.sourceFilter(STANDARD_SOURCE_FILTER)
				.targetFilter(STANDARD_TARGET_FILTER)
		);
	}

	/**
	 * Return a Stream that is lazily populated with {@code Description} by
	 * searching for all property descriptions in an object tree rooted at a
	 * given starting {@code root} object. Only the <em>statically</em>
	 * available property descriptions are returned, and the property
	 * descriptions from Java classes are not part of the result.
	 *
	 * @see #walk(PathValue, Dtor)
	 * @see #walk(PathValue)
	 *
	 * @param root the root class of the object graph
	 * @return all <em>statically</em> fetch-able property descriptions
	 */
	public static Stream<Description> walk(final Type root) {
		return walk(PathValue.of(root));
	}

}
