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
package io.jenetics.incubator.restfulclient;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;

/**
 * Defines the resource parameters: header, path and query.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 8.2
 * @version 8.2
 */
public sealed interface Parameter extends Serializable {

	/**
	 * Factory for a parameter of a given type and key.
	 */
	@FunctionalInterface
	interface Value {

		/**
		 * Return a new parameter value with the given value.
		 *
		 * @param value the parameter value
		 * @return a new parameter with a given type and key
		 */
		Parameter value(String value);
	}

	/**
	 * Header resource parameter.
	 */
	non-sealed interface Header extends Parameter {

		/**
		 * Return a header value factory for the given header name.
		 *
		 * @param key the header name
		 * @return a new header value factory
		 */
		static Value key(String key) {
			return value -> header(key, value);
		}
	}

	/**
	 * Path resource parameter.
	 */
	non-sealed interface Path extends Parameter {

		/**
		 * Return a path parameter value factory for the given path parameter name.
		 *
		 * @param key the path parameter name
		 * @return a new path parameter value factory
		 */
		static Value key(String key) {
			return value -> path(key, value);
		}
	}

	/**
	 * Query resource parameter.
	 */
	non-sealed interface Query extends Parameter {

		/**
		 * Return a query parameter value factory for the given query parameter
		 * name.
		 *
		 * @param key the query parameter name
		 * @return a new query parameter value factory
		 */
		static Value key(String key) {
			return value -> query(key, value);
		}
	}

	/**
	 * Return the parameter name (key).
	 *
	 * @return the parameter name
	 */
	String key();

	/**
	 * Return the parameter value.
	 *
	 * @return the parameter value
	 */
	String value();

	/**
	 * Create a new header parameter.
	 *
	 * @param key the header name
	 * @param value the header value
	 * @return a new header parameter
	 */
	static Header header(final String key, final String value) {
		requireNonNull(key);
		requireNonNull(value);

		record SimpleHeader(String key, String value) implements Header {};
		return new SimpleHeader(key, value);
	}

	/**
	 * Create a new path parameter.
	 *
	 * @param key the path parameter name
	 * @param value the path parameter value
	 * @return a new header parameter
	 */
	static Path path(final String key, final String value) {
		requireNonNull(key);
		requireNonNull(value);

		record SimplePath(String key, String value) implements Path {};
		return new SimplePath(key, value);
	}

	/**
	 * Create a new query parameter.
	 *
	 * @param key the query parameter name
	 * @param value the query parameter value
	 * @return a new header parameter
	 */
	static Query query(final String key, final String value) {
		requireNonNull(key);
		requireNonNull(value);

		record SimpleQuery(String key, String value) implements Query {};
		return new SimpleQuery(key, value);
	}

}
