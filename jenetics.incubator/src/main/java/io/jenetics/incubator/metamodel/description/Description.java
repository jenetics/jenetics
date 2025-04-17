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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.stream.Stream;

import io.jenetics.incubator.metamodel.Path;

/**
 * This interface describes the <em>static</em> type information for a property,
 * together with its path in the object graph.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.2
 * @since 7.2
 */
public sealed interface Description
	permits SizedDescription, SimpleDescription
{

	/**
	 * Return the path of the description.
	 *
	 * @return the path of the description
	 */
	Path path();

	/**
	 * Returns the enclosure type.
	 *
	 * @return the enclosure type
	 */
	Class<?> enclosure();

	/**
	 * Return the <em>static</em> type of the property description.
	 *
	 * @return the <em>static</em> type of the property description
	 */
	Type type();

	/**
	 * Return a list of all annotations, available for property description.
	 *
	 * @return a list of all property annotations
	 */
	default Stream<Annotation> annotations() {
		return Stream.empty();
	}

}
