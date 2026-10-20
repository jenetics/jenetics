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

import java.util.List;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;

sealed interface IndexRangeProjection extends IndexProjection {

	record Dense(int min, int max) implements IndexRangeProjection, RandomAccess {
		@Override
		public int size() {
			return max - min;
		}
		@Override
		public Codomain codomain() {
			return new Codomain(min, max);
		}
		@Override
		public int apply(int index) {
			Objects.checkIndex(index, size());
			return min + index;
		}
		@Override
		public IntStream stream() {
			return IntStream.range(min, max);
		}
	}

	final class Sparse implements IndexRangeProjection {

		private final List<Dense> ranges;

		private final IntUnaryOperator apply;
		private final int[] sums;

		Sparse(final List<Dense> ranges) {
			this.ranges = List.copyOf(ranges);


			if (ranges.size() < 32) {
				this.sums = null;
				apply = this::applyLinearSearch;
			} else {
				this.sums = new int[ranges.size() + 1];
				sums[ranges.size()] = Integer.MAX_VALUE;
				int sum = 0;
				for (int i = 0; i < ranges.size(); ++i) {
					sums[i] = sum;
					sum += ranges.get(i).size();
				}

				apply = this::applyBinarySearch;
			}
		}

		@Override
		public int size() {
			return ranges.stream().mapToInt(Dense::size).sum();
		}
		@Override
		public Codomain codomain() {
			return !isEmpty()
				? new Codomain(ranges.getFirst().min(), ranges.getLast().max())
				: new Codomain(0, 0);
		}
		@Override
		public int apply(int index) {
			Objects.checkIndex(index, size());
			return apply.applyAsInt(index);
		}

		private int applyLinearSearch(int index) {
			int sum = 0;
			int pos = index;
			for (final var range : ranges) {
				sum += range.size();

				if (index < sum) {
					return range.apply(pos);
				}
				pos -= range.size();
			}

			throw new AssertionError("Unreachable!");
		}

		private int applyBinarySearch(int index) {
			int low = 0;
			int high = ranges.size() - 1;

			while (low <= high) {
				final int mid = (low + high) >>> 1;
				final var range = ranges.get(mid);

				if (index >= sums[mid] && index < sums[mid + 1]) {
					return range.apply(index - sums[mid]);
				} else {
					if (index < sums[mid]) {
						high = mid - 1;
					} else {
						low = mid + 1;
					}
				}
			}

			throw new AssertionError("Unreachable!");
		}

		@Override
		public IntStream stream() {
			return ranges.stream().flatMapToInt(Dense::stream);
		}
	}

	default Range<Integer> toRange() {
		return switch (this) {
			case Dense d -> Range.INTEGER.dense(d.min, d.max);
			case Sparse s -> Range.INTEGER.of(
				s.ranges.stream()
					.map(r -> Range.INTEGER.dense(r.min, r.max))
					.toList()
			);
		};
	}

	static IndexRangeProjection of(final Range<Integer> range) {
		return switch (range) {
			case DenseRange<Integer> dr -> new Dense(dr.start(), dr.end());
			case SparseRange<Integer> sr -> new Sparse(
				sr.ranges().stream()
					.map(r -> new Dense(r.start(), r.end()))
					.toList()
			);
		};
	}

	static IndexRangeProjection identity(final int size) {
		return new Dense(0, size - 1);
	}

}
