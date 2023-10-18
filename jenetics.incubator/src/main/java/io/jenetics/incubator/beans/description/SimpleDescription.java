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

import java.lang.reflect.Type;

import io.jenetics.incubator.beans.Path;
import io.jenetics.incubator.beans.reflect.StructType;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.0
 * @since 8.0
 */
public record SimpleDescription(
	Path path,
	Class<?> enclosure,
	Type type,
	Accessor accessor
)
	implements Description
{

	static SimpleDescription of(final Path path, final StructType.Component component) {
		final var getter = Methods.toGetter(component.getter());
		final var setter = Methods.toSetter(component.setter());

		return new SimpleDescription(
			path.append(component.name()),
			component.enclosure(),
			component.value(),
			setter != null
				? new Accessor.Writable(getter, setter)
				: new Accessor.Readonly(getter)
		);
	}

}
