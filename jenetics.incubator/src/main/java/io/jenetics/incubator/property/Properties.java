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
package io.jenetics.incubator.property;

import static java.lang.String.format;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import io.jenetics.incubator.property.Property.Path;

/**
 * This class contains helper methods for extracting the properties from a given
 * root object.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Properties {
	private Properties() {
	}

	/**
	 * Return a property extractor object, which extracts the direct (first level)
	 * properties of the input object.
	 *
	 * @return a first level property extractor
	 */
	public static Extractor<PathObject, Property> extractor() {
		return PropertyExtractor.DEFAULT;
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
		final PathObject root,
		final Extractor<PathObject, Property> extractor
	) {
		return new RecursivePropertyExtractor(extractor).extract(root);
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
	public static Stream<Property> walk(final PathObject root, final String... includes) {
		final var filter = Stream.of(includes)
			.map(Filters::toPattern)
			.map(Filters::toFilter)
			.reduce(Predicate::or)
			.orElse(a -> true);

		return walk(root, extractor().sourceFilter(filter));
	}

	public static Optional<Property> get(final Object bean, final Path path) {
		for (var p : path) {

		}
		return Optional.empty();
	}

	static String toString(final String name, final Property property) {
		return format(
			"%s[path=%s, value=%s, type=%s, enclosingType=%s]",
			name,
			property.path(),
			property.value(),
			property.type() != null ? property.type().getName() : null,
			property.enclosingObject().getClass().getName()
		);
	}

}
