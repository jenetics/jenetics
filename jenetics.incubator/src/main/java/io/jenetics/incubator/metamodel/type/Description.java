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
package io.jenetics.incubator.metamodel.type;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Type;
import java.util.stream.Stream;

import io.jenetics.incubator.metamodel.Path;
import io.jenetics.incubator.metamodel.PathValue;
import io.jenetics.incubator.metamodel.internal.Dtor;
import io.jenetics.incubator.metamodel.internal.PreOrderIterator;

/**
 * Adds path information to a {@link ModelType}.
 * This class contains methods for extracting the <em>static</em> bean property
 * information from a given object. It is the main entry point for the extracting
 * properties from an object graph.
 * {@snippet class="ReflectSnippets" region="walk(Type)"}
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
 * @param path the path for a given model type
 * @param type the actual property type
 * @param enclosure the enclosing type for the model property
 * @param model the model type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.2
 * @since 7.2
 */
public record Description(
	Path path,
	Type type,
	Type enclosure,
	ModelType model
) {

	public Description {
		requireNonNull(path);
		requireNonNull(type);
		requireNonNull(enclosure);
		requireNonNull(model);
	}

	@Override
	public String toString() {
		return "Description[path=%s, type=%s, enclosure=%s]".formatted(
			path,
			type.getTypeName(),
			enclosure.getTypeName()
		);
	}


	/**
	 * Lists the <em>directly</em> available property descriptions for the
	 * given {@code type} and start path, {@link PathValue#path()}.
	 * {@snippet class="ReflectSnippets" region="list(PathValue)"}
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
		requireNonNull(type);
		if (type.value() == null) {
			return Stream.empty();
		}

		return switch (ModelType.of(type.value())) {
			case ElementType t -> Stream.empty();
			case StructType t -> t.components().map(p -> new Description(
				type.path().append(p.name()),
				p.type(), p.enclosure().type(), p
			));
			case EnclosingType t -> Stream.of(new Description(
				type.path().append(new Path.Index(0)),
				t.componentType(), t.type(), t
			));
			case EnclosedType t -> Stream.empty();
		};
	}

	/**
	 * Extracts the <em>directly</em> available property descriptions for the
	 * given {@code type}.
	 * {@snippet class="ReflectSnippets" region="list(Type)"}
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
		final Dtor<? super PathValue<? extends Type>, ? extends Description>
			recursiveDtor = PreOrderIterator.dtor(
			dtor,
			tp -> PathValue.of(tp.path(), tp.type()),
			PathValue::value
		);

		@SuppressWarnings("unchecked")
		var result =  (Stream<Description>)recursiveDtor.unapply(root);
		return result;
	}

	/**
	 * Return a Stream that is lazily populated with {@code Description} by
	 * searching for all property descriptions in an object tree rooted at a
	 * given starting {@code root} object. Only the <em>statically</em>
	 * available property descriptions are returned, and the property
	 * descriptions from Java classes are not part of the result.
	 * {@snippet class="ReflectSnippets" region="walk(PathValue)"}
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
		return walk(root, Description::list);
	}

	/**
	 * Return a Stream that is lazily populated with {@code Description} by
	 * searching for all property descriptions in an object tree rooted at a
	 * given starting {@code root} object. Only the <em>statically</em>
	 * available property descriptions are returned, and the property
	 * descriptions from Java classes are not part of the result.
	 * {@snippet class="ReflectSnippets" region="walk(Type)"}
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
