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

import java.lang.reflect.Type;
import java.util.Objects;

import io.jenetics.incubator.metamodel.access.Accessor;
import io.jenetics.incubator.metamodel.access.Carried;

/**
 * Represents a <em>property</em>.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.3
 * @since 8.3
 */
public final class IndexType implements EnclosedType, ConcreteType {
	private final int index;
	private final IndexedType enclosure;
	private final Type type;

	IndexType(int index, IndexedType enclosure, Type type) {
		this.index = index;
		this.enclosure = requireNonNull(enclosure);
		this.type = requireNonNull(type);
	}

	/**
	 * The index of where {@code this} type is embedded in the enclosing type.
	 *
	 * @return the index of the embedding index type
	 */
	public int index() {
		return index;
	}

	@Override
	public IndexedType enclosure() {
		return enclosure;
	}

	@Override
	public Type type() {
		return type;
	}

	@Override
	public Carried<Accessor> accessor() {
		return object -> enclosure.accessor().of(object).at(index);
	}

	@Override
	public int hashCode() {
		return Objects.hash(index, enclosure, type);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof IndexType it &&
			it.index == index &&
			it.enclosure.equals(enclosure) &&
			it.type.equals(type);
	}

	@Override
	public String toString() {
		return "IndexType[index=%d, enclosure=%s, type=%s]".formatted(
			index,
			enclosure,
			type.getTypeName()
		);
	}

}
