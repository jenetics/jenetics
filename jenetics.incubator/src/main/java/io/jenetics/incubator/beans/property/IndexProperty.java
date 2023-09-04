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

import io.jenetics.incubator.beans.Path;

/**
 * This property represents an element of an {@link IndexedProperty}.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.2
 * @since 7.2
 */
public final class IndexProperty implements Property {

    private final Path path;
    private final int index;
	private final Value value;


	IndexProperty(final Path path, final int index, final Value value) {
        this.path = requireNonNull(path);
        this.index = index;
        this.value = requireNonNull(value);
    }

    @Override
    public Path path() {
        return path;
    }

	/**
	 * Return the actual index of the property.
	 *
	 * @return the actual index of the property
	 */
    public int index() {
        return index;
    }

    @Override
    public Value value() {
        return value;
    }

	@Override
	public int hashCode() {
		return Objects.hash(path, index, value);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof IndexProperty ip &&
			path.equals(ip.path) &&
			index == ip.index &&
			value.equals(ip.value);
	}

	@Override
	public String toString() {
		return Properties.toString(IndexProperty.class.getSimpleName(), this);
	}

}
