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
package io.jenetics.incubator.metamodel.type;

import static java.util.Objects.requireNonNull;
import static io.jenetics.incubator.metamodel.internal.Methods.toGetter;
import static io.jenetics.incubator.metamodel.internal.Methods.toSetter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import io.jenetics.incubator.metamodel.access.Accessor;
import io.jenetics.incubator.metamodel.access.Curryer;

/**
 * Represents a <em>property</em>.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.3
 * @since 8.3
 */
public final class PropertyType implements EnclosedType, ConcreteType {
	private final String name;
	private final StructType enclosure;
	private final Type type;
	private final Method getter;
	private final Method setter;
	private final List<Annotation> annotations;

	PropertyType(
		final String name,
		final StructType enclosure,
		final Type type,
		final Method getter,
		final Method setter,
		final List<Annotation> annotations
	) {
		this.name = requireNonNull(name);
		this.enclosure = requireNonNull(enclosure);
		this.type = requireNonNull(type);
		this.getter = requireNonNull(getter);
		this.setter = setter;
		this.annotations = List.copyOf(annotations);
	}

	/**
	 * Return the name of the property.
	 *
	 * @return the name of the property
	 */
	public String name() {
		return name;
	}

	@Override
	public StructType enclosure() {
		return enclosure;
	}

	@Override
	public Type type() {
		return type;
	}

	@Override
	public Curryer<Accessor> access() {
		return setter != null
			? object -> new Accessor.Writable(
					toGetter(getter).curry(object),
					toSetter(setter).curry(object)
				)
			: object -> new Accessor.Readonly(
					toGetter(getter).curry(object)
				);
	}

	@Override
	public Stream<Annotation> annotations() {
		return annotations.stream();
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, enclosure, type);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof PropertyType pt &&
			pt.name.equals(name) &&
			pt.enclosure.equals(enclosure) &&
			pt.type.equals(type);
	}

	@Override
	public String toString() {
		return "PropertyType[name=%s, enclosure=%s, type=%s]".formatted(
			name,
			enclosure,
			type
		);
	}

}
