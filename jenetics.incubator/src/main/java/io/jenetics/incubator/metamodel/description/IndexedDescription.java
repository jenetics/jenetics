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
import io.jenetics.incubator.metamodel.reflect.IndexedType;

/**
 * This class represents indexed objects like arrays and lists.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.0
 * @since 8.0
 */
public final class IndexedDescription implements Description {

    private final Path path;
    private final Class<?> enclosure;
    private final Type type;
    private final Size size;
    private final IndexedAccess access;

    IndexedDescription(
		final Path path,
		final Class<?> enclosure,
		final Type type,
		final Size size,
		final IndexedAccess access
    ) {
        this.path = requireNonNull(path);
        this.enclosure = requireNonNull(enclosure);
        this.type = requireNonNull(type);
        this.size = requireNonNull(size);
        this.access = requireNonNull(access);
    }

    @Override
    public Path path() {
        return path;
    }

    @Override
    public Class<?> enclosure() {
        return enclosure;
    }

    @Override
    public Type type() {
        return type;
    }

	/**
	 * Return the size function of the description.
	 *
	 * @return the size function of the description
	 */
    public Size size() {
        return size;
    }

	/**
	 * Return the access object for the description.
	 *
	 * @return the access object for the description
	 */
    public IndexedAccess access() {
        return access;
    }

	@Override
	public int hashCode() {
		return Objects.hash(path, enclosure, type.getTypeName());
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof IndexedDescription id &&
			id.path.equals(path) &&
			id.type.getTypeName().equals(type.getTypeName()) &&
			id.enclosure.equals(enclosure);
	}

	@Override
	public String toString() {
		return "Description[path=%s, type=%s, enclosure=%s]".formatted(
			path,
			type instanceof Class<?> cls ? cls.getName() : type,
			enclosure.getName()
		);
	}

	static IndexedDescription of(final Path path, final IndexedType type) {
		return new IndexedDescription(
			path.append(new Path.Index(0)),
			type.type(),
			type.componentType(),
			type::size,
			type.isMutable()
				? new IndexedAccess.Writable(type::get, type::set)
				: new IndexedAccess.Readonly(type::get)
		);
	}

}
