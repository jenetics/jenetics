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
package io.jenetics.incubator.util;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Map;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.0
 * @since 8.0
 */
public class TypeToken<T> {
	private final Type type;
	private final Class<T> raw;

	private TypeToken(final Type type, final Class<T> raw) {
		this.type = requireNonNull(type);
		this.raw = requireNonNull(raw);
	}

	@SuppressWarnings("unchecked")
	private TypeToken(final Type type) {
		this(type, (Class<T>)toRawType(type));
	}

	@SuppressWarnings("unchecked")
	protected TypeToken() {
		if (getClass().getGenericSuperclass() instanceof ParameterizedType typ) {
			this.type = typ.getActualTypeArguments()[0];
			this.raw = (Class<T>)toRawType(this.type);
		} else {
			throw new IllegalStateException("TypeToken must be parameterized.");
		}
	}

	private static Class<?> toRawType(final Type type) {
        return switch (type) {
            case Class<?> typ -> typ;
            case ParameterizedType typ -> (Class<?>)typ.getRawType();
            case GenericArrayType typ -> Array
	            .newInstance(toRawType(typ.getGenericComponentType()), 0)
	            .getClass();
            case TypeVariable<?> typ -> toRawType(typ.getBounds()[0]);
            case WildcardType typ -> toRawType(typ.getUpperBounds()[0]);
            case null, default -> {
                var typName = type == null ? "<null>" : type.getClass().getName();
                throw new IllegalStateException(
					"Could not determine raw type for '%s'.".formatted(typName)
                );
            }
        };
	}

	T cast(final Object value) {
		return raw.cast(value);
	}

	T put(final Map<Object, Object> map, final Object value) {
		return cast(map.put(this, value));
	}

	T get(final Map<Object, Object> map) {
		return cast(map.get(this));
	}

	@Override
	public final int hashCode() {
		return super.hashCode();
	}

	@Override
	public final boolean equals(Object obj) {
		return obj == this;
	}

	@Override
	public final String toString() {
		return type.toString() + "@" + Integer.toHexString(hashCode());
	}

//	public static <T> TypeToken<T> of(Type type) {
//		return new TypeToken<T>(type);
//	}

}
