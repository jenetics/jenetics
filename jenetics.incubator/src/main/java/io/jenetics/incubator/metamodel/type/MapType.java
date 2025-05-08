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

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import io.jenetics.incubator.metamodel.access.Carrier;
import io.jenetics.incubator.metamodel.access.Size;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.3
 * @since 8.3
 */
public final class MapType implements CollectionType, ConcreteType {
	private final Class<?> type;
	private final Class<?> keyType;
	private final Class<?> valueType;

	MapType(final Class<?> type, final Class<?> keyType, final Class<?> valueType) {
		this.type = requireNonNull(type);
		this.keyType = requireNonNull(keyType);
		this.valueType = requireNonNull(valueType);
	}

	@Override
	public Class<?> type() {
		return type;
	}

	@Override
	public Class<?> componentType() {
		return Map.Entry.class;
	}

	public Class<?> keyType() {
		return keyType;
	}

	public Class<?> valueType() {
		return valueType;
	}

	@Override
	public Carrier<Size> size() {
		return object -> () -> size(object);
	}

	private int size(final Object object) {
		return object instanceof Map<?, ?> map
			? map.size()
			: raise(new IllegalArgumentException("Not a map: " + object));
	}

	@Override
	public Carrier<Iterable<Object>> iterable() {
		return this::iterable;
	}

	@SuppressWarnings("unchecked")
	private Iterable<Object> iterable(final Object object) {
		if (object instanceof Map<?, ?> map) {
			return () -> (Iterator<Object>)(Object)map.entrySet().iterator();
		} else {
			throw new IllegalArgumentException("Not a map: " + object);
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, keyType, valueType);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof MapType st &&
			type.equals(st.type) &&
			keyType().equals(st.keyType()) &&
			valueType().equals(st.valueType);
	}

	@Override
	public String toString() {
		return "MapType[type=%s, keyType=%s, valueType=%s]"
			.formatted(type.getName(), keyType.getName(), valueType.getName());
	}

}
