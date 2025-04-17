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
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import io.jenetics.incubator.metamodel.Path;
import io.jenetics.incubator.metamodel.reflect.SizedType;

/**
 * his class represents sized objects like arrays, sets, maps and lists.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.3
 * @since 8.3
 */
public sealed class SizedDescription
	implements Description, SizedIterable
	permits IndexedDescription
{
	final Path path;
	final Class<?> enclosure;
	final Type type;
	final Size size;

	SizedDescription(
		final Path path,
		final Class<?> enclosure,
		final Type type,
		final Size size
	) {
		this.path = requireNonNull(path);
		this.enclosure = requireNonNull(enclosure);
		this.type = requireNonNull(type);
		this.size = requireNonNull(size);
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

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<Object> iterator(final Object object) {
		requireNonNull(object);

		return (Iterator<Object>)switch (object) {
			case Collection<?> collection -> collection.iterator();
			case Map<?, ?> map -> map.entrySet().iterator();
			case Optional<?> optional -> optional.stream().iterator();
			default -> throw new IllegalArgumentException(
				"Unsupported type: " + object.getClass().getName()
			);
		};
	}

	@SuppressWarnings("unchecked")
	public Stream<Object> stream(final Object object) {
		requireNonNull(object);

		return (Stream<Object>)switch (object) {
			case Collection<?> collection -> collection.stream();
			case Map<?, ?> map -> map.entrySet().stream();
			case Optional<?> optional -> optional.stream();
			default -> throw new IllegalArgumentException(
				"Unsupported type: " + object.getClass().getName()
			);
		};
	}

	@Override
	public String toString() {
		return "Description[path=%s, type=%s, enclosure=%s]".formatted(
			path,
			type instanceof Class<?> cls ? cls.getName() : type,
			enclosure.getName()
		);
	}

	static SizedDescription of(final Path path, final SizedType type) {
		return new SizedDescription(
			path.append(new Path.Index(0)),
			type.type(),
			type.componentType(),
			type::size
		);
	}

}
