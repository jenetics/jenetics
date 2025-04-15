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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import io.jenetics.incubator.metamodel.Path;
import io.jenetics.incubator.metamodel.internal.Reflect;
import io.jenetics.incubator.metamodel.reflect.StructType;

/**
 * This class represents <em>non</em>-indexed property descriptions.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.0
 * @since 8.0
 */
public final class SimpleDescription
	implements Description
{
    private final Path path;
    private final Class<?> enclosure;
    private final Type type;
    private final Access access;
	private final Supplier<Stream<Annotation>> annotations;

	SimpleDescription(
		final Path path,
		final Class<?> enclosure,
		final Type type,
		final Supplier<Stream<Annotation>> annotations,
		final Access access
    ) {
        this.path = requireNonNull(path);
        this.enclosure = requireNonNull(enclosure);
        this.type = requireNonNull(type);
        this.access = requireNonNull(access);
		this.annotations = requireNonNull(annotations);
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

	@Override
	public Stream<Annotation> annotations() {
		return annotations.get();
	}

	/**
	 * Return the access object for the description.
	 *
	 * @return the access object for the description
	 */
    public Access access() {
        return access;
    }


	@Override
	public int hashCode() {
		return Objects.hash(path, enclosure, type.getTypeName());
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof SimpleDescription sd &&
			sd.path.equals(path) &&
			sd.type.getTypeName().equals(type.getTypeName()) &&
			sd.enclosure.equals(enclosure);
	}

	@Override
	public String toString() {
		return "Description[path=%s, type=%s, enclosure=%s]".formatted(
			path,
			type instanceof Class<?> cls ? cls.getName() : type,
			enclosure.getName()
		);
	}

	/**
	 * Create a new description object with the given {@code path} and struct
	 * {@code component}.
	 *
	 * @param path the description path
	 * @param component the struct component
	 * @return a new simple description object
	 */
	static SimpleDescription of(
		final Path path,
		final StructType.Component component
	) {
		final var getter = Methods.toGetter(component.getter());
		final var setter = Methods.toSetter(component.setter());

		return new SimpleDescription(
			path.append(component.name()),
			component.enclosure(),
			component.value(),
			() -> Reflect.getAnnotations(component.getter()),
			setter != null
				? new Access.Writable(getter, setter)
				: new Access.Readonly(getter)
		);
	}

}
