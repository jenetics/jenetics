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
package io.jenetics.incubator.metamodel.reflect;

import static java.util.Objects.requireNonNull;
import static io.jenetics.incubator.metamodel.internal.Reflect.raise;

import java.util.List;

/**
 * Type which represents a {@code List} class.
 *
 * @param type the list type
 * @param componentType the list component type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.0
 * @since 8.0
 */
public record ListType(
	Class<?> type,
	Class<?> componentType
)
	implements IndexedType
{

	public ListType {
		if (!List.class.isAssignableFrom(type)) {
			throw new IllegalArgumentException("Not a list type: " + type);
		}
		requireNonNull(componentType);
	}

	@Override
	public int size(final Object object) {
		return object instanceof List<?> list
			? list.size()
			: raise(new IllegalArgumentException("Not a list: " + object));
	}

	@Override
	public Object get(final Object object, final int index) {
		return object instanceof List<?> list
			? list.get(index)
			: raise(new IllegalArgumentException("Not a list: " + object));
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void set(Object object, int index, Object value) {
		if (object instanceof List list) {
			list.set(index, value);
		} else {
			throw new IllegalArgumentException("Not a list: " + object);
		}
	}

}
