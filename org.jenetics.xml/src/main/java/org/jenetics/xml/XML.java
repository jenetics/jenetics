/*
 * Java GPX Library (@__identifier__@).
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
package org.jenetics.xml;

import javax.xml.stream.XMLStreamException;

/**
 * Contains specializations of functional interfaces, which throw
 * {@link XMLStreamException}.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail">Franz Wilhelmstötter</a>
 * @version 1.0.1
 * @since 1.0.1
 */
final class XML {

	private XML() {
	}

	/**
	 * Represents a function that accepts one argument and produces a result.
	 *
	 * @param <T> the input type
	 * @param <R> the result type
	 */
	@FunctionalInterface
	interface Function<T, R> {

		/**
		 * Applies this function to the given argument.
		 *
		 * @param value the function argument
		 * @return the function result
		 * @throws XMLStreamException if an error occurs
		 */
		R apply(final T value) throws XMLStreamException;

	}

}
