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
import java.util.Objects;
import java.util.Optional;

import io.jenetics.incubator.beans.Path;
import io.jenetics.incubator.beans.PathValue;
import io.jenetics.incubator.beans.internal.Types;

/**
 * A {@code PropertyDesc} describes one property that a Java Bean exports or a
 * {@link java.lang.reflect.RecordComponent} in the case of a record class.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public record Description(Path path, Value value)
	implements PathValue<Description.Value>
{

	public Description {
		requireNonNull(path);
		requireNonNull(value);
	}

	/**
	 * The value type for the property description. It contains the information
	 * about the <em>static</em> type of the property and the <em>static</em>
	 * type of the enclosure type.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
	 * @version !__version__!
	 * @since !__version__!
	 */
	public sealed interface Value {

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
		Type value();

		/**
		 * Implementation of a single valued property description.
		 *
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

			/**
			 * Return the getter function of the property.
			 *
			 * @return the getter function of the property
			 */
			public Getter getter() {
				return getter;
			}

			/**
			 * Return the setter function of the property if the property is
			 * writable.
			 *
			 * @return the setter function of the property if the property
			 */
			public Optional<Setter> setter() {
				return Optional.ofNullable(setter);
			}

			@Override
			public int hashCode() {
				return Objects.hash(enclosure, value);
			}

			@Override
			public boolean equals(final Object obj) {
				return obj == this ||
					obj instanceof Single s &&
					enclosure.equals(s.enclosure) &&
					value.equals(s.value);
			}

			@Override
			public String toString() {
				return "Single[value=%s, enclosure=%s]".formatted(
					Types.toClass(value()) != null
						? Types.toClass(value()).getName()
						: value(),
					enclosure().getName()
				);
			}

		}

		/**
		 * Implements an indexed description property.
		 *
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

			/**
			 * Return the size function of the <em>indexed</em> property.
			 *
			 * @return the size function of the <em>indexed</em> property
			 */
			public Size size() {
				return size;
			}

			/**
			 * Return the getter function of the <em>indexed</em> property.
			 *
			 * @return the getter function of the <em>indexed</em> property
			 */
			public IndexedGetter getter() {
				return getter;
			}

			/**
			 * Return the setter function of the <em>indexed</em> property, if
			 * the property is writable.
			 *
			 * @return the setter function of the <em>indexed</em> property
			 */
			public Optional<IndexedSetter> setter() {
				return Optional.ofNullable(setter);
			}

			@Override
			public int hashCode() {
				return Objects.hash(enclosure, value);
			}

			@Override
			public boolean equals(final Object obj) {
				return obj == this ||
					obj instanceof Indexed i &&
					enclosure.equals(i.enclosure) &&
					value.equals(i.value);
			}

			@Override
			public String toString() {
				return "Indexed[value=%s, enclosure=%s]".formatted(
					Types.toClass(value()) != null
						? Types.toClass(value()).getName()
						: value(),
					enclosure().getName()
				);
			}

		}

	}
}
