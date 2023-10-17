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
package io.jenetics.incubator.beans.reflect;

import java.lang.reflect.Type;
import java.util.stream.Stream;

/**
 * Trait which represents a {@code Record} type.
 *
 * @param type the type object
 */
public record RecordType(Class<?> type) implements StructType {

	public RecordType {
		if (!type.isRecord()) {
			throw new IllegalArgumentException("Not a record type: " + type);
		}
	}

	/**
	 * Return the record components of {@code this} record type.
	 *
	 * @return the record components of {@code this} record type
	 */
	@Override
	public Stream<Component> components() {
		return Stream.of(type.getRecordComponents())
			.filter(comp -> comp.getAccessor().getReturnType() != Class.class)
			.map(rc -> new Component(
				rc.getDeclaringRecord(),
				rc.getName(),
				rc.getAccessor().getGenericReturnType(),
				rc.getAccessor(),
				null
			));
	}

	/**
	 * Return a {@code RecordType} instance if the given {@code type} is a
	 * {@code Record} class.
	 * {@snippet lang = "java":
	 * final Type type = null; // @replace substring='null' replacement="..."
	 * if (RecordType.of(type) instanceof RecordType rt) {
	 *     System.out.println(rt);
	 * }
	 * }
     *
     * @param type the type object
     * @return an {@code RecordType} if the given {@code type} is a record type,
     * or {@code null}
     */
    public static Trait of(final Type type) {
        if (type instanceof Class<?> cls && cls.isRecord()) {
            return new RecordType(cls);
        } else {
            return null;
        }
    }
}
