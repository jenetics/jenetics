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
package io.jenetics.ext;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import io.jenetics.internal.util.IndexSorter;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class ParetoSet<T> {

	private final IntRange _capacity;
	private final Comparator<? super T> _dominance;
	private final Function<? super List<T>, double[]> _distances;

	private final List<T> _population = new ArrayList<>();

	ParetoSet(
		final IntRange capacity,
		final Comparator<? super T> dominance,
		final Function<? super List<T>, double[]> distances
	) {
		_capacity = capacity;
		_dominance = requireNonNull(dominance);
		_distances = requireNonNull(distances);
	}

	void add(final T element) {
		final Iterator<T> iterator = _population.iterator();

		while (iterator.hasNext()) {
			final T existing = iterator.next();

			int cmp = _dominance.compare(element, existing);
			if (cmp < 0) {
				iterator.remove();
			} else if (cmp > 0 || element.equals(existing)) {
				return;
			}
		}

		if (_population.size() >= _capacity.getMax()) {
			final double[] distances = _distances.apply(_population);
			final int[] indexes = IndexSorter.sort(distances);

			final List<T> sorted = new ArrayList<>(_population.size());
			for (int i = 0; i < indexes.length - 1; ++i) {
				sorted.add(_population.get(indexes[i]));
			}
			_population.clear();
			_population.addAll(sorted);
		}

		_population.add(element);
	}

	void addAll(final Iterable<? extends T> elements) {
		elements.forEach(this::add);
	}

	ParetoSet<T> merge(final ParetoSet<T> elements) {
		elements._population.forEach(this::add);
		return this;
	}

	boolean isEmpty() {
		return _population.isEmpty();
	}

	ISeq<T> toISeq() {
		return ISeq.of(_population);
	}

}
