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

import java.util.function.Predicate;
import java.util.stream.Gatherer;

import static java.util.Objects.requireNonNull;

/**
 * Helper methods for ranges.
 */
public final class Ranges {
	private Ranges() {
	}

	/**
	 * Return a {@link Gatherer} which collects index ranges fulfilling the
	 * given {@code predicate}.
	 * {@snippet lang = java:
	 * // List with null-values
	 * final List<String> list = IntStream.range(0, 100)
	 *     .mapToObj(i -> i%10 == 0 ? "value" : null)
	 *     .toList();
	 *
	 * // The indexes of the null-values in the list.
	 * final Range<Integer> nulls = list.stream()
	 *    .gather(ListProjection.rangeOf(Objects::isNull))
	 *    .collect(Range.INTEGER.toRange());
	 *}
	 *
	 * @param predicate the predicate which defines the element ranges
	 * @return list index ranges of elements which fulfills the given
	 *         {@code predicate}
	 * @param <T> the element type
	 */
	public static <T> Gatherer<T, ?, Range<Integer>>
	rangeOf(Predicate<? super T> predicate) {
		requireNonNull(predicate);

		final class State {
			int count = 0;
			int start = -1;
		}

		return Gatherer.ofSequential(
			State::new,
			(state, element, downstream) -> {
				if (predicate.test(element)) {
					if (state.start == -1) {
						state.start = state.count;
					}
				} else {
					if (state.start != -1) {
						final var range = Range.INTEGER.dense(
							state.start,
							state.count
						);
						downstream.push(range);

						state.start = -1;
					}
				}

				++state.count;
				return true;
			},
			(state, downstream) -> {
				if (state.start != -1) {
					final var range = Range.INTEGER.dense(
						state.start,
						state.count
					);
					downstream.push(range);
				}
			}
		);
	}

}
