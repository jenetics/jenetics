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
package io.jenetics.incubator.beans.internal;

import java.lang.constant.Constable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.temporal.TemporalAccessor;
import java.util.List;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Types {
	private Types() {
	}

	public static boolean isIdentityType(final Object object) {
		return
			object != null &&
				!(object instanceof Constable) &&
				!(object instanceof TemporalAccessor) &&
				!(object instanceof Number);
	}

	public static Class<?> toClass(final Type type) {
		if (type instanceof ParameterizedType pt) {
			return (Class<?>)pt.getRawType();
		} else {
			return (Class<?>)type;
		}
	}

	public static boolean isArrayType(final Type type) {
		return toArrayType(type) != null;
	}

	public static Class<?> toArrayType(final Type type) {
		if (type instanceof Class<?> arrayType &&
			arrayType.isArray() &&
			!arrayType.getComponentType().isPrimitive())
		{
			return arrayType;
		} else {
			return null;
		}
	}

	public static boolean isListType(final Type type) {
		return toListType(type) != null;
	}

	public static Class<?> toListType(final Type type) {
		if (type instanceof Class<?> listType &&
			List.class.isAssignableFrom(listType))
		{
			return listType;
		}
		if (type instanceof ParameterizedType pt &&
			pt.getRawType() instanceof Class<?> listType &&
			List.class.isAssignableFrom(listType))
		{
			return listType;
		}

		return null;
	}

}
