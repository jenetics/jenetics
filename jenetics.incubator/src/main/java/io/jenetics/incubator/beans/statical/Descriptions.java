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
package io.jenetics.incubator.beans.statical;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Stream;

import io.jenetics.incubator.beans.util.Extractor;
import io.jenetics.incubator.beans.util.RecursiveExtractor;

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

	public static final Predicate<Class<?>> NON_JAVA_CLASSES = type -> {
		final var name = type.getName();

		return
			// Allow native Java arrays, except byte[] arrays.
			(name.startsWith("[") && !name.endsWith("[B")) ||
				// Allow Java collection classes.
				Collection.class.isAssignableFrom(type) ||
				(
					!name.startsWith("java") &&
						!name.startsWith("com.sun") &&
						!name.startsWith("sun") &&
						!name.startsWith("jdk")
				);
	};

	private Descriptions() {
	}

	public static Stream<Description> walk(
		final Class<?> root,
		final Extractor<Class<?>, Description> extractor
	) {
		final var ext = new RecursiveExtractor<Class<?>, Description>(
			extractor,
			Description::type
		);
		return ext.extract(root);
	}

	public static Stream<Description>
	walk(final Class<?> root) {
		return walk(
			root,
			DescriptionExtractors.DIRECT.sourceFilter(NON_JAVA_CLASSES)
		);
	}

}
