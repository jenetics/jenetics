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
package io.jenetics.incubator.parser;

import static java.util.Objects.requireNonNull;

/**
 * This interface represents a parsed token. A token is a <em>pair</em> of a
 * token type and a token value.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public interface Token {

	/**
	 * Represents the type of the token, with a given type code and type name.
	 */
	interface Type {
		Type EOF = Type.of(-1, "EOF");

		/**
		 * Return the type code, which uniquely identifies the token type.
		 *
		 * @return the code of the token type
		 */
		int code();

		/**
		 * Return the name of the token.
		 *
		 * @return the name of the token
		 */
		String name();

		/**
		 * Create a new token type with the given {@code code} and {@code name}.
		 *
		 * @param code the code of the created token type
		 * @param name the name of the created token type
		 * @return a new token type
		 * @throws NullPointerException if the given {@code name} is {@code null}
		 */
		static Type of(final int code, final String name) {
			record SimpleType(int code, String name) implements Type {
				SimpleType {
					requireNonNull(name);
				}
			}

			return new SimpleType(code, name);
		}
	}

	/**
	 * Return the type of {@code this} token.
	 *
	 * @return the type of {@code this} token
	 */
	Type type();

	/**
	 * Return the actual token value.
	 *
	 * @return the actual token value
	 */
	String value();

	/**
	 * Create a new token with the given {@code type} and {@code value}.
	 *
	 * @param type the token type
	 * @param value the token value
	 * @return a new token with the given {@code type} and {@code value}
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	static Token of(final Type type, final String value) {
		record SimpleToken(Type type, String value) implements Token {
			public SimpleToken {
				requireNonNull(type);
				requireNonNull(value);
			}
		}

		return new SimpleToken(type, value);
	}

}
