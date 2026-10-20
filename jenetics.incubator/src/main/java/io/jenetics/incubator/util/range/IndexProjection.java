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
package io.jenetics.incubator.util.range;

import java.util.Objects;
import java.util.stream.IntStream;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 9.2
 * @since 9.2
 */
public interface IndexProjection {

	/**
	 * The projections co-domain range.
	 *
	 * @param min the minimum value of the integer range, inclusively
	 * @param max the maximal value of the integer range, exclusively
	 */
	record Codomain(int min, int max) {
		public Codomain {
			Objects.checkFromToIndex(min, max - 1, max);
		}
	}

	/**
	 * Return the size of the projection, which is the number of projected
	 * indexes. The projection size may be smaller or greater than
	 * {@code max() - min() + 1}, since there might be gaps or overlapping
	 * regions in the projection.
	 *
	 * @return the size of the projection
	 */
	int size();

	/**
	 * Returns {@code true} if {@code this} is an empty projection.
	 *
	 * @return {@code true} if {@code this} is an empty projection, {@code false}
	 *         otherwise
	 */
	default boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * Return the minimal and maximal value of the projection's co-domain.
	 *
	 * @return the codomain borders of the projection
	 */
	Codomain codomain();

	/**
	 * Return the stream of the projected indexes.
	 *
	 * @return the stream of the projected indexes
	 */
	default IntStream stream() {
		return IntStream.range(0, size()).map(this::apply);
	}

	/**
	 * Return the projected index. The returned value is within the range
	 * {@code [min(), max())}.
	 *
	 * @param index index to be project
	 * @return the projected index
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	int apply(int index);

	static IndexProjection of(final Range<Integer> range) {
		return IndexRangeProjection.of(range);
	}

	static IndexProjection identity(final int size) {
		return IndexRangeProjection.identity(size);
	}
}
