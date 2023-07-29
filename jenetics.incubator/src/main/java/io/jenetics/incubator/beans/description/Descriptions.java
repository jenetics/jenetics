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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import io.jenetics.incubator.beans.PathValue;
import io.jenetics.incubator.beans.property.Property;
import io.jenetics.incubator.beans.property.SimpleProperty;
import io.jenetics.incubator.beans.util.Extractor;
import io.jenetics.incubator.beans.util.PreOrderIterator;

/**
 * This class contains helper methods for extracting the properties from a given
 * root object. It is the main entry point for the extracting properties from
 * an object graph.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Descriptions {

	public static final Predicate<PathValue<Type>>
		STANDARD_SOURCE_FILTER =
		type -> {
			final var cls = type.value() instanceof ParameterizedType pt
				? (Class<?>)pt.getRawType()
				: (Class<?>)type.value();

			final var name = cls.getName();

			return
				// Allow native Java arrays, except byte[] arrays.
				(name.startsWith("[") && !name.endsWith("[B")) ||
				// Allow Java collection classes.
				Collection.class.isAssignableFrom(cls) ||
				(
					!name.startsWith("java") &&
					!name.startsWith("com.sun") &&
					!name.startsWith("sun") &&
					!name.startsWith("jdk")
				);
		};

	public static final Predicate<Description> STANDARD_TARGET_FILTER = prop ->
		!(prop.value() instanceof  SingleValue &&
			prop.value().enclosure().getName().startsWith("java"));

	private Descriptions() {
	}

	public static Stream<Description> walk(
		final PathValue<Type> root,
		final Extractor<PathValue<Type>, Description> extractor
	) {
		final var ext = PreOrderIterator.extractor(
			extractor,
			desc -> new PathValue<>(desc.path(), desc.value().value()),
			PathValue::value
		);
		return ext.extract(root);
	}

	public static Stream<Description>
	walk(final PathValue<Type> root) {
		return walk(
			root,
			DescriptionExtractors.DIRECT
				.sourceFilter(STANDARD_SOURCE_FILTER)
				.targetFilter(STANDARD_TARGET_FILTER)
		);
	}

}
