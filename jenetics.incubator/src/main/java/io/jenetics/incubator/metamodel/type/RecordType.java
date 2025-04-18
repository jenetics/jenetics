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

import java.util.Objects;
import java.util.stream.Stream;

/**
 * Trait which represents a {@code Record} type.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.0
 * @since 8.0
 */
public final class RecordType implements StructType {
	private final Class<?> type;

	RecordType(Class<?> type) {
		if (!type.isRecord()) {
			throw new IllegalArgumentException("Not a record type: " + type);
		}
		this.type = type;
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

	@Override
	public Class<?> type() {
		return type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(type);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof RecordType rt &&
			type.equals(rt.type);
	}

	@Override
	public String toString() {
		return "RecordType[type=%s]".formatted(type.getName());
	}


}
