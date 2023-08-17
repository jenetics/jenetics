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
package io.jenetics.incubator.beans;

import static java.util.Objects.requireNonNull;

/**
 * A path value associates a value with a path.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.2
 * @since 7.2
 */
public interface PathValue<V> {

	/**
	 * The full path, separated with dots '.', of {@code this} property from
	 * the <em>root</em> object.
	 *
	 * @return the full property path
	 */
	Path path();

	/**
	 * The name of {@code this} property, which is the name of the head path
	 * element. If the path is empty, an empty string is returned.
	 *
	 * @return the node name
	 */
	default String name() {
		final var element = path().element();
		return element != null ? element.toString() :  "";
	}

	/**
	 * Return the value of the entry.
	 *
	 * @return the value of the entry
	 */
	V value();

	/**
	 * Create a new path value from the given {@code path} and {@code value}.
	 * The path {@code value} may be {@code null}.
	 *
	 * @param path the path
	 * @param value the path {@code value}, may be {@code null}
	 * @return a new path value object
	 * @param <V> the path value type
	 * @throws NullPointerException if the given {@code path} is {@code null}
	 */
	static <V> PathValue<V> of(final Path path, final V value) {
		record SimplePathValue<V>(Path path, V value) implements PathValue<V> {
			SimplePathValue {
				requireNonNull(path);
			}
		}

		return new SimplePathValue<>(path, value);
	}

	/**
	 * Create a new path value from the given {@code value} and an <em>empty</em>
	 * path.
	 *
	 * @param value the path {@code value}, may be {@code null}
	 * @return a new path value object
	 * @param <V> the path value type
	 */
	static <V> PathValue<V> of(final V value) {
		return of(Path.of(), value);
	}

}
