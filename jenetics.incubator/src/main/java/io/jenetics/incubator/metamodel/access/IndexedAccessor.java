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
package io.jenetics.incubator.metamodel.access;

import static java.util.Objects.requireNonNull;

/**
 * This interface holds a property getter and, optionally, property setter.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.0
 * @since 8.0
 */
public sealed interface IndexedAccessor {

	/**
	 * Return the property getter, never {@code null}.
	 *
	 * @return the property getter
	 */
	IndexedGetter getter();

	/**
	 * Currying this indexed access object with the given index.
	 *
	 * @param index the currying index
	 * @return the curried access object
	 */
	default Accessor curry(int index) {
		if (index < 0) {
			throw new IllegalArgumentException(
				"Index must be >= 0: %d".formatted(index)
			);
		}

		return switch (this) {
			case IndexedAccessor.Readonly(var g) -> new Accessor.Readonly(
				g.curry(index)
			);
			case IndexedAccessor.Writable(var g, var s) -> new Accessor.Writable(
				g.curry(index),
				s.curry(index)
			);
		};
	}

	/**
	 * Read-only property access-object.
	 *
	 * @param getter the property getter
	 */
	record Readonly(IndexedGetter getter) implements IndexedAccessor {
		public Readonly {
			requireNonNull(getter);
		}
	}

	/**
	 * Writable property access-object.
	 *
	 * @param getter the property getter
	 * @param setter the property setter
	 */
	record Writable(IndexedGetter getter, IndexedSetter setter)
		implements IndexedAccessor
	{
		public Writable {
			requireNonNull(getter);
			requireNonNull(setter);
		}
	}

}
