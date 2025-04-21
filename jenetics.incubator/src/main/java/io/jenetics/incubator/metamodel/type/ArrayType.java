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

import java.lang.reflect.Array;
import java.util.Objects;

import io.jenetics.incubator.metamodel.access.Curryer;
import io.jenetics.incubator.metamodel.access.IndexedAccessor;
import io.jenetics.incubator.metamodel.access.Size;

/**
 * Trait which represents an array type.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.0
 * @since 8.0
 */
public final class ArrayType implements IndexedType, ConcreteType {
	private final Class<?> type;
	private final Class<?> componentType;

	ArrayType(final Class<?> type, final Class<?> componentType) {
		this.type = requireNonNull(type);
		this.componentType = requireNonNull(componentType);
		if (!type.isArray()) {
			throw new IllegalArgumentException("Not an array type: " + type);
		}
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
	public Curryer<Size> size() {
		return object -> () -> Array.getLength(object);
	}

	@Override
	public Curryer<IndexedAccessor.Writable> accessor() {
		return object -> new IndexedAccessor.Writable(
			index -> Array.get(object, index),
			(index, value) -> Array.set(object, index, value)
		);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, componentType);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ArrayType at &&
			type.equals(at.type) &&
			componentType.equals(at.componentType);
	}

	@Override
	public String toString() {
		return "ArrayType[type=%s, componentType=%s]"
			.formatted(type.getName(), componentType.getName());
	}

}
