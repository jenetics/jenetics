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

import java.util.Objects;
import java.util.Optional;

/**
 * Trait which represents an {@code Optional} type.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.0
 * @since 8.0
 */
public final class OptionalType implements IndexedType {
	private final Class<?> componentType;


	OptionalType(Class<?> componentType) {
		this.componentType = requireNonNull(componentType);
	}

	@Override
	public Class<?> type() {
		return Optional.class;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public int size(Object object) {
		return object instanceof Optional<?> optional ? optional.isPresent()
			? 1 : 0 : raise(new IllegalArgumentException("Not an Optional: " + object));
	}

	@Override
	public Object get(Object object, int index) {
		return object instanceof Optional<?> optional
			? optional.orElseThrow()
			: raise(new IllegalArgumentException("Not an Optional: " + object));
	}

	@Override
	public void set(Object object, int index, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Class<?> componentType() {
		return componentType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(componentType);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof OptionalType ot &&
			componentType.equals(ot.componentType);
	}

	@Override
	public String toString() {
		return "OptionalType[componentType=%s]".formatted(componentType);
	}


}
