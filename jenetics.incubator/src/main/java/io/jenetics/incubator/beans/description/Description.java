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

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Type;
import java.util.Optional;

import io.jenetics.incubator.beans.Path;
import io.jenetics.incubator.beans.PathEntry;

/**
 * A {@code PropertyDesc} describes one property that a Java Bean exports or a
 * {@link java.lang.reflect.RecordComponent} in the case of a record class.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public record Description(
	Path path,
	Value value
)
	implements PathEntry<Description.Value>
{

	public Description {
		requireNonNull(path);
		requireNonNull(value);
	}

	/**
	 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
	 * @version !__version__!
	 * @since !__version__!
	 */
	public sealed interface Value {

		/**
		 * Returns the object which contains {@code this} node.
		 *
		 * @return the object which contains {@code this} node
		 */
		Class<?> enclosure();

		/**
		 * The value of the metaobject, may be {@code null}. This method always
		 * returns the initial property value.
		 *
		 * @return the <em>original</em> value of the metaobject
		 */
		Type value();

		/**
		 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
		 * @version !__version__!
		 * @since !__version__!
		 */
		final class Indexed implements Value {

			private final Class<?> enclosure;
			private final Type value;
			private final Size size;
			private final IndexedGetter getter;
			private final IndexedSetter setter;

			Indexed(
				final Class<?> enclosure,
				final Type value,
				final Size size,
				final IndexedGetter getter,
				final IndexedSetter setter
			) {
				this.enclosure = requireNonNull(enclosure);
				this.value = requireNonNull(value);
				this.size = requireNonNull(size);
				this.getter = requireNonNull(getter);
				this.setter = setter;
			}

			@Override
			public Class<?> enclosure() {
				return enclosure;
			}

			@Override
			public Type value() {
				return value;
			}

			public Size size() {
				return size;
			}

			public IndexedGetter getter() {
				return getter;
			}

			public Optional<IndexedSetter> setter() {
				return Optional.ofNullable(setter);
			}

			@Override
			public String toString() {
				return "Indexed[value=%s, enclosure=%s]".formatted(
					value(), enclosure().getName()
				);
			}

		}

		/**
		 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
		 * @version !__version__!
		 * @since !__version__!
		 */
		final class Single implements Value {

			private final Class<?> enclosure;
			private final Type value;
			private final Getter getter;
			private final Setter setter;

			Single(
				final Class<?> enclosure,
				final Type value,
				final Getter getter,
				final Setter setter
			) {
				this.enclosure = requireNonNull(enclosure);
				this.value = requireNonNull(value);
				this.getter = requireNonNull(getter);
				this.setter = setter;
			}

			@Override
			public Class<?> enclosure() {
				return enclosure;
			}

			@Override
			public Type value() {
				return value;
			}

			public Getter getter() {
				return getter;
			}

			public Optional<Setter> setter() {
				return Optional.ofNullable(setter);
			}

			@Override
			public String toString() {
				return "Single[value=%s, enclosure=%s]".formatted(
					value(), enclosure().getName()
				);
			}

		}
	}
}
