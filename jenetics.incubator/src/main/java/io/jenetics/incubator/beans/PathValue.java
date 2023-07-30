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

/**
 * A {@code PathEntry} associates a value with a path.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
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
	 * The name of {@code this} property; always non-{@code null}.
	 *
	 * @return the node name
	 */
	default String name() {
		return path().isEmpty() ? "" : path().element().toString();
	}

	/**
	 * Return the value of the entry.
	 *
	 * @return the value of the entry
	 */
	V value();

	static <V> PathValue<V> of(final Path path, final V value) {
		record SimplePathValue<V>(Path path, V value) implements PathValue<V> {
		}

		return new SimplePathValue<>(path, value);
	}

	static <V> PathValue<V> of(final V value) {
		return of(Path.of(), value);
	}

}
