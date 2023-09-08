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
package io.jenetics.ext.moea;

import io.jenetics.util.ISeq;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public record Solutions<T>(ISeq<Vec<T>> values, int objectives)
	implements Iterable<Vec<T>>
{

	public Solutions {
		if (values.isEmpty()) {
			throw new IllegalArgumentException("Empty solutions.");
		}

		final int objs = values.stream()
			.map(Vec::length)
			.reduce(objectives, Math::max);

		if (objs != objectives) {
			throw new IllegalArgumentException("""
				Expected all solutions to have %d objectives, but maximal \
				objectives was %d.
				""".formatted(objectives, objs)
			);
		}
	}

	public Solutions(ISeq<Vec<T>> values) {
		this(values, values.nonEmpty() ? values.get(0).length() : 0);
	}

	@Override
	public Iterator<Vec<T>> iterator() {
		return values.iterator();
	}

	public Stream<Vec<T>> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

}
