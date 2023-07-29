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
package io.jenetics.incubator.beans.property;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.jenetics.incubator.beans.Path;

/**
 * Base class for properties which consists of 0 to n objects.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public abstract sealed class IndexedProperty
	implements Iterable<Object>, Property
	permits ListProperty, ArrayProperty
{

	private final Path path;
	private final Value value;

	IndexedProperty(
		final Path path,
		final Value value
	) {
		this.path = requireNonNull(path);
		this.value = requireNonNull(value);
	}

	@Override
	public Path path() {
		return path;
	}

	@Override
	public Value value() {
		return value;
	}

	/**
	 * Return the size of the <em>indexed</em> property.
	 *
	 * @return the size of the <em>indexed</em> property
	 */
	public abstract int size();

	/**
	 * Return the property value at the given {@code index}.
	 *
	 * @param index the property index
	 * @return the property value at the given index
	 */
	public abstract Object get(final int index);

	/**
	 * Return the property values.
	 *
	 * @return the property values
	 */
	public Stream<Object> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	@Override
	public int hashCode() {
		return Objects.hash(path, value);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof IndexedProperty ip &&
			path.equals(ip.path) &&
			value.equals(ip.value);
	}

	@Override
	public String toString() {
		return Properties.toString(getClass().getSimpleName(), this);
	}

}
