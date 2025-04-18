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

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Type;
import java.util.Objects;

import io.jenetics.incubator.metamodel.Path;
import io.jenetics.incubator.metamodel.access.Access;
import io.jenetics.incubator.metamodel.type.PropertyType;
import io.jenetics.incubator.metamodel.type.StructType;

/**
 * This class represents <em>non</em>-indexed property descriptions.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.0
 * @since 8.0
 */
public final class PropertyDescription implements Description {
    private final Path path;
    private final PropertyType type;

	PropertyDescription(
		final Path path,
		final PropertyType type
    ) {
        this.path = requireNonNull(path);
        this.type = requireNonNull(type);
    }

    @Override
    public Path path() {
        return path;
    }

    @Override
    public StructType enclosure() {
        return type.enclosure();
    }

    @Override
    public Type type() {
        return type.type();
    }

	/**
	 * Return the access object for the description.
	 *
	 * @return the access object for the description
	 */
    public Access access() {
        return type.access();
    }

	@Override
	public int hashCode() {
		return Objects.hash(path, type);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof PropertyDescription pd &&
			pd.path.equals(path) &&
			pd.type.equals(type);
	}

	@Override
	public String toString() {
		return "Description[path=%s, type=%s, enclosure=%s]".formatted(
			path,
			type().getTypeName(),
			enclosure().type().getTypeName()
		);
	}

	/**
	 * Create a new description object with the given {@code path} and struct
	 * {@code component}.
	 *
	 * @param path the description path
	 * @param type the struct type
	 * @return a new simple description object
	 */
	static PropertyDescription of(
		final Path path,
		final PropertyType type
	) {
		return new PropertyDescription(
			path.append(type.name()),
			type
		);
	}

}
