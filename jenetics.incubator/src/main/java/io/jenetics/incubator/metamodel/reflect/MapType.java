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

import java.util.Map;
import java.util.Objects;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.3
 * @since 8.3
 */
public final class MapType implements SizedType {
	private final Class<?> type;
	private final Class<?> keyType;
	private final Class<?> valueType;

	MapType(Class<?> type, Class<?> keyType, Class<?> valueType) {
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
	public int size(final Object object) {
		return object instanceof Map<?, ?> map
			? map.size()
			: raise(new IllegalArgumentException("Not a map: " + object));
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, keyType, valueType);
	}

}
