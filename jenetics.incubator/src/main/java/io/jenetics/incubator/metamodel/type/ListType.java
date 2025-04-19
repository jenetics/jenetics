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
import static io.jenetics.incubator.metamodel.internal.Reflect.raise;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import io.jenetics.incubator.metamodel.access.IndexedAccess;
import io.jenetics.incubator.metamodel.access.IterableFactory;
import io.jenetics.incubator.metamodel.access.Size;

/**
 * Type which represents a {@code List} class.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.0
 * @since 8.0
 */
public final class ListType implements IndexedType, ConcreteType {
	private final Class<?> type;
	private final Class<?> componentType;

	ListType(Class<?> type, Class<?> componentType) {
		if (!List.class.isAssignableFrom(type)) {
			throw new IllegalArgumentException("Not a list type: " + type);
		}

		this.type = type;
		this.componentType = requireNonNull(componentType);
	}

	@Override
	public Class<?> type() {
		return type;
	}

	@Override
	public Class<?> componentType() {
		return componentType;
	}

	@Override
	public Size size() {
		return this::size;
	}

	@Override
	public IndexedAccess access() {
		if (isMutable()) {
			return new IndexedAccess.Writable(this::get, this::set);
		} else {
			return new IndexedAccess.Readonly(this::get);
		}
	}

	private boolean isMutable() {
		return type == ArrayList.class ||
			type == LinkedList.class;
	}

	private int size(final Object object) {
		return object instanceof List<?> list
			? list.size()
			: raise(new IllegalArgumentException("Not a list: " + object));
	}

	private Object get(final Object object, final int index) {
		return object instanceof List<?> list
			? list.get(index)
			: raise(new IllegalArgumentException("Not a list: " + object));
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private void set(Object object, int index, Object value) {
		if (value != null && componentType.isAssignableFrom(value.getClass())) {
			throw new IllegalArgumentException(
				"Value is not from component type: %s instanceof %s."
					.formatted(value, value.getClass().getName())
			);
		}

		if (object instanceof List list) {
			list.set(index, value);
		} else {
			throw new IllegalArgumentException("Not a list: " + object);
		}
	}

	@Override
	public IterableFactory iterable() {
		return this::iterable;
	}

	@SuppressWarnings("unchecked")
	private Iterable<Object> iterable(final Object object) {
		if (object instanceof List<?> list) {
			return () -> (Iterator<Object>)list.iterator();
		} else {
			throw new IllegalArgumentException("Not a list: " + object);
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, componentType);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ListType lt &&
			type.equals(lt.type) &&
			componentType.equals(lt.componentType);
	}

	@Override
	public String toString() {
		return "ListType[type=%s, componentType=%s]"
			.formatted(type.getName(), componentType.getName());
	}


}
