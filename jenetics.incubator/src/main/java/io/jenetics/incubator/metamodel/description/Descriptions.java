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
package io.jenetics.incubator.metamodel.description;

import java.lang.reflect.Type;
import java.util.stream.Stream;

import io.jenetics.incubator.metamodel.PathValue;
import io.jenetics.incubator.metamodel.internal.Dtor;
import io.jenetics.incubator.metamodel.internal.PreOrderIterator;
import io.jenetics.incubator.metamodel.reflect.CollectionType;
import io.jenetics.incubator.metamodel.reflect.ElementType;
import io.jenetics.incubator.metamodel.reflect.IndexedType;
import io.jenetics.incubator.metamodel.reflect.MetaModelType;
import io.jenetics.incubator.metamodel.reflect.StructType;

/**
 * This class contains methods for extracting the <em>static</em> bean property
 * information from a given object. It is the main entry point for the extracting
 * properties from an object graph.
 * {@snippet class="DescriptionSnippets" region="walk(Type)"}
 *
 * The code snippet above will create the following output
 * <pre>{@code
 * Description[path=title, type=java.lang.String, enclosure=Book]
 * Description[path=pages, type=int, enclosure=Book]
 * Description[path=authors, type=java.util.List<Author>, enclosure=Book]
 * Description[path=authors[0], type=Author, enclosure=java.util.List]
 * Description[path=authors[0].forename, type=java.lang.String, enclosure=Author]
 * Description[path=authors[0].surname, type=java.lang.String, enclosure=Author]
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.2
 * @since 7.2
 */
public final class Descriptions {

	private Descriptions() {
	}

	/**
	 * Lists the <em>directly</em> available property descriptions for the
	 * given {@code type} and start path, {@link PathValue#path()}.
	 * {@snippet class="DescriptionSnippets" region="list(PathValue)"}
	 *
	 * The code snippet above will create the following output
	 * <pre>{@code
	 * Description[path=book.title, type=java.lang.String, enclosure=Book]
	 * Description[path=book.pages, type=int, enclosure=Book]
	 * Description[path=book.authors, type=java.util.List<Author>, enclosure=Book]
	 * }</pre>
	 *
	 * @param type the enclosing type of the listed property descriptions
	 * @return the <em>directly</em> available property descriptions
	 */
	public static Stream<Description> list(final PathValue<? extends Type> type) {
		if (type == null || type.value() == null) {
			return Stream.empty();
		}

		return switch (MetaModelType.of(type.value())) {
			case ElementType t -> Stream.empty();
			case StructType t ->  t.components().map(c -> ElementDescription.of(type.path(), c));
			case IndexedType t -> Stream.of(IndexedDescription.of(type.path(), t));
			case CollectionType t -> Stream.of(CollectionDescription.of(type.path(), t));
		};
	}

	/**
	 * Extracts the <em>directly</em> available property descriptions for the
	 * given {@code type}.
	 * {@snippet class="DescriptionSnippets" region="list(Type)"}
	 *
	 * The code snippet above will create the following output
	 * <pre>{@code
	 * Description[path=title, type=java.lang.String, enclosure=Book]
	 * Description[path=pages, type=int, enclosure=Book]
	 * Description[path=authors, type=java.util.List<Author>, enclosure=Book]
	 * }</pre>
	 *
	 * @param type the enclosing type of the listed property descriptions
	 * @return the <em>directly</em> available property descriptions
	 */
	public static Stream<Description> list(final Type type) {
		return list(PathValue.of(type));
	}

	private static Stream<Description> walk(
		final PathValue<? extends Type> root,
		final Dtor<? super PathValue<? extends Type>, ? extends Description> dtor
	) {
		final Dtor<? super PathValue<? extends Type>, Description>
			recursiveDtor = PreOrderIterator.dtor(
				dtor,
				desc -> PathValue.of(desc.path(), desc.type()),
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
	 * {@snippet class="DescriptionSnippets" region="walk(PathValue)"}
	 *
	 * The code snippet above will create the following output
	 * <pre>{@code
	 * Description[path=library.title, type=java.lang.String, enclosure=Book]
	 * Description[path=library.pages, type=int, enclosure=Book]
	 * Description[path=library.authors, type=java.util.List<Author>, enclosure=Book]
	 * Description[path=library.authors[0], type=Author, enclosure=java.util.List]
	 * Description[path=library.authors[0].forename, type=java.lang.String, enclosure=Author]
	 * Description[path=library.authors[0].surname, type=java.lang.String, enclosure=Author]
	 * }</pre>
	 *
	 * @see #walk(Type)
	 *
	 * @param root the root class of the object graph
	 * @return all <em>statically</em> fetch-able property descriptions
	 */
	public static Stream<Description> walk(final PathValue<? extends Type> root) {
		return walk(root, Descriptions::list);
	}

	/**
	 * Return a Stream that is lazily populated with {@code Description} by
	 * searching for all property descriptions in an object tree rooted at a
	 * given starting {@code root} object. Only the <em>statically</em>
	 * available property descriptions are returned, and the property
	 * descriptions from Java classes are not part of the result.
	 * {@snippet class="DescriptionSnippets" region="walk(Type)"}
	 *
	 * The code snippet above will create the following output
	 * <pre>{@code
	 * Description[path=title, type=java.lang.String, enclosure=Book]
	 * Description[path=pages, type=int, enclosure=Book]
	 * Description[path=authors, type=java.util.List<Author>, enclosure=Book]
	 * Description[path=authors[0], type=Author, enclosure=java.util.List]
	 * Description[path=authors[0].forename, type=java.lang.String, enclosure=Author]
	 * Description[path=authors[0].surname, type=java.lang.String, enclosure=Author]
	 * }</pre>
	 *
	 * @see #walk(PathValue)
	 *
	 * @param root the root class of the object graph
	 * @return all <em>statically</em> fetch-able property descriptions
	 */
	public static Stream<Description> walk(final Type root) {
		return walk(PathValue.of(root));
	}

}
