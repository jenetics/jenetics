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

import static io.jenetics.incubator.beans.reflect.Reflect.toRawType;

import java.lang.reflect.Type;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.jenetics.incubator.beans.Path;
import io.jenetics.incubator.beans.reflect.IndexedType;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.0
 * @since 8.0
 */
public record IndexedDescription(
	Path path,
	Class<?> enclosure,
	Type type,
	Size size,
	IndexedAccess access
)
	implements Description
{

	public Stream<IndexDescription> descriptions(final Object enclosure) {
		final int size = size().get(enclosure);

		return IntStream.range(0, size).mapToObj(index -> {
			final Object value = access.getter().get(enclosure, index);
			final Class<?> type = value != null
				? value.getClass()
				: toRawType(type());

			return new IndexDescription(
				path.append(new Path.Index(index)),
				enclosure.getClass(),
				type,
				switch (access) {
					case IndexedAccess.Readonly acc -> new Access.Readonly(
						object -> acc.getter().get(object, index)
					);
					case IndexedAccess.Writable acc -> new Access.Writable(
						object -> acc.getter().get(object, index),
						(object, val) -> acc.setter().set(object, index, val)
					);
				},
				index
			);
		});
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
