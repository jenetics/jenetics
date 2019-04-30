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
package io.jenetics.util;

import static java.lang.String.format;

import java.util.function.Function;
import java.util.stream.Collector;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class Seqs {
	private Seqs() {
	}

	/**
	 * Returns a {@code Collector} that accumulates the last {@code n} input
	 * elements into a new {@code ISeq}.
	 *
	 * @since !__version__!
	 *
	 * @param maxSize the maximal size of the collected sequence
	 * @param <T> the type of the input elements
	 * @param <S> the sequence type
	 * @return a {@code Collector} which collects maximal {@code maxSize} of the
	 *         input elements into an {@code ISeq}, in encounter order
	 * @throws IllegalArgumentException if the {@code maxSize} is negative
	 */
	static <T, S extends Seq<T>> Collector<T, Buffer<T>, S>
	toSeq(final int maxSize, final Function<Buffer<T>, S> finisher) {
		if (maxSize < 0) {
			throw new IllegalArgumentException(format(
				"Max size is negative: %d", maxSize
			));
		}
		return maxSize > 0
			? Collector.of(
				() -> new Buffer<T>(maxSize),
				Buffer::add,
				(left, right) -> { left.addAll(right.toSeq()); return left; },
				finisher)
			: Collector.of(
				() -> null,
				(buffer, value) -> {},
				(left, right) -> left,
				buffer -> {
					@SuppressWarnings("unchecked") final S seq = (S) Seq.empty();
					return seq;
				});
	}

}
