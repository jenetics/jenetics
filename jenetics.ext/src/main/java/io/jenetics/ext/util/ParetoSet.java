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
package io.jenetics.ext.util;

import static java.util.Objects.requireNonNull;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;

import io.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class ParetoSet<T> extends AbstractSet<T> {

	private final Comparator<? super T> _dominance;

	private final List<T> _population = new ArrayList<>();

	public ParetoSet(final Comparator<? super T> dominance) {
		_dominance = requireNonNull(dominance);
	}

	@Override
	public boolean add(final T element) {
		final Iterator<T> iterator = _population.iterator();

		while (iterator.hasNext()) {
			final T existing = iterator.next();

			int cmp = _dominance.compare(element, existing);
			if (cmp < 0) {
				iterator.remove();
			} else if (cmp > 0 || element.equals(existing)) {
				return true;
			}
		}

		_population.add(element);
		return true;
	}

	@Override
	public boolean addAll(final Collection<? extends T> elements) {
		elements.forEach(this::add);
		return true;
	}

	public ParetoSet<T> merge(final ParetoSet<T> elements) {
		addAll(elements);
		return this;
	}

	@Override
	public Iterator<T> iterator() {
		return _population.iterator();
	}

	@Override
	public int size() {
		return _population.size();
	}

	@Override
	public boolean isEmpty() {
		return _population.isEmpty();
	}

	public ISeq<T> toISeq() {
		return ISeq.of(_population);
	}


	public static <T>
	Collector<T, ?, ParetoSet<T>>
	toParetoSet(final Comparator<? super T> dominance) {
		return Collector.of(
			() -> new ParetoSet<>(dominance),
			ParetoSet::add,
			ParetoSet::merge
		);
	}

	public static <A, B>
	Collector<A, ?, ParetoSet<B>>
	toParetoSet(
		final Function<? super A, ? extends B> mapper,
		final Comparator<? super B> dominance
	) {
		return Collector.of(
			() -> new ParetoSet<>(dominance),
			(set, result) -> set.add(mapper.apply(result)),
			ParetoSet::merge
		);
	}

	public static <A, B>
	Collector<A, ?, ParetoSet<B>>
	toFlattenedParetoSet(
		final Function<? super A, ? extends Collection<B>> mapper,
		final Comparator<? super B> dominance
	) {
		return Collector.of(
			() -> new ParetoSet<>(dominance),
			(set, result) -> set.addAll(mapper.apply(result)),
			ParetoSet::merge
		);
	}

}
