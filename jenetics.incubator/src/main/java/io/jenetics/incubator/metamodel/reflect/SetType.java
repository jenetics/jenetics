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
import java.util.Objects;
import java.util.Set;

/**
 * Type which represents a {@code List} class.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.3
 * @since 8.3
 */
public final class SetType implements CollectionType {
	private final Class<?> type;
	private final Class<?> componentType;

	SetType(Class<?> type, Class<?> componentType) {
		if (!Set.class.isAssignableFrom(type)) {
			throw new IllegalArgumentException("Not a list type: " + type);
		}

		this.type = type;
		this.componentType = requireNonNull(componentType);
	}

	@Override
	public int size(final Object object) {
		return object instanceof Set<?> set
			? set.size()
			: raise(new IllegalArgumentException("Not a set: " + object));
	}

	@Override
	public boolean isMutable() {
		return false;
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
	public int hashCode() {
		return Objects.hash(type, componentType);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof SetType st &&
			type.equals(st.type) &&
			componentType.equals(st.componentType);
	}

	@Override
	public String toString() {
		return "SetType[type=%s, componentType=%s]"
			.formatted(type, componentType);
	}


}
