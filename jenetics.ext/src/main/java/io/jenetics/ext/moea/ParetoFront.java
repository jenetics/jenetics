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

import static java.util.Objects.requireNonNull;
import static io.jenetics.internal.util.Arrays.revert;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.jenetics.util.ISeq;
import io.jenetics.util.ProxySorter;
import io.jenetics.util.Seq;

/**
 * This class only contains non-dominate (Pareto-optimal) elements according to
 * a given <em>dominance</em> measure. Like a {@link Set}, it only contains no
 * duplicate entries. Unlike the usual set implementation, the iteration order
 * is deterministic.
 * <p>
 * You can create a new {@code ParetoFront} for {@link Vec} objects
 * <pre>{@code
 * final ParetoFront<Vec<double[]>> front = new ParetoFront<>(Vec::dominance);
 * front.add(Vec.of(1.0, 2.0));
 * front.add(Vec.of(1.1, 2.5));
 * front.add(Vec.of(0.9, 2.1));
 * front.add(Vec.of(0.0, 2.9));
 * }</pre>
 *
 * or directly for {@code double[]} array objects
 * <pre>{@code
 * final ParetoFront<double[]> front = new ParetoFront<>(Pareto::dominance);
 * front.add(new double[]{1.0, 2.0});
 * front.add(new double[]{1.1, 2.5});
 * front.add(new double[]{0.9, 2.1});
 * front.add(new double[]{0.0, 2.9});
 * }</pre>
 *
 * You only have to specify the <a href="https://en.wikipedia.org/wiki/Pareto_efficiency">
 *     Pareto dominance/efficiency</a> measure.
 *
 * @see Pareto
 *
 * @apiNote
 * Inserting a new element has a time complexity of {@code O(n)}.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.1
 * @since 4.1
 */
public final class ParetoFront<T> extends AbstractSet<T> {

	private final List<T> _population = new ArrayList<>();

	private final Comparator<? super T> _dominance;
	private final BiPredicate<? super T, ? super T> _equals;

	/**
	 * Create a new {@code ParetoSet} with the given {@code dominance} measure.
	 *
	 * @since 5.1
	 *
	 * @param dominance the <em>Pareto</em> dominance measure
	 * @param equals the equals predicate used for keeping the set distinct
	 * @throws NullPointerException if the given {@code dominance} measure is
	 *         {@code null}
	 */
	public ParetoFront(
		final Comparator<? super T> dominance,
		final BiPredicate<? super T, ? super T> equals
	) {
		_dominance = requireNonNull(dominance);
		_equals = requireNonNull(equals);
	}

	/**
	 * Create a new {@code ParetoSet} with the given {@code dominance} measure.
	 *
	 * @param dominance the <em>Pareto</em> dominance measure
	 * @throws NullPointerException if the given {@code dominance} measure is
	 *         {@code null}
	 */
	public ParetoFront(final Comparator<? super T> dominance) {
		this(dominance, Objects::equals);
	}

	/**
	 * Inserts an {@code element} to this pareto front.
	 *
	 * @implNote
	 * Inserting a new element has a time complexity of {@code O(this.size())},
	 * where <em>n</em> is the number of elements of {@code this} pareto-front.
	 *
	 * @param element the element to add
	 * @return {@code true} if this set did not already contain the specified
	 *         element
	 */
	@Override
	public boolean add(final T element) {
		requireNonNull(element);

		boolean updated = false;
		final Iterator<T> iterator = _population.iterator();
		while (iterator.hasNext()) {
			final T existing = iterator.next();

			int cmp = _dominance.compare(element, existing);
			if (cmp > 0) {
				iterator.remove();
				updated = true;
			} else if (cmp < 0 || _equals.test(element, existing)) {
				return updated;
			}
		}

		_population.add(element);
		return true;
	}

	/**
	 * Adds all elements of the given collection to {@code this} pareto front.
	 *
	 * @implNote
	 * The runtime complexity of this operation is
	 * {@code O(elements.size()*this.size())}.
	 *
	 * @param elements the elements to add to {@code this} pareto front
	 * @return {@code true} if {@code this} pareto front has been changed,
	 *         {@code false} otherwise
	 */
	@Override
	public boolean addAll(final Collection<? extends T> elements) {
		final int sum = elements.stream()
			.mapToInt(e -> add(e) ? 1 : 0)
			.sum();
		return sum > 0;
	}

	/**
	 * Add the all {@code elements} to {@code this} pareto-set.
	 *
	 * @implNote
	 * Merging two pareto fronts has a time complexity of
	 * {@code O(elements.size()*this.size())}.
	 *
	 * @param elements the elements to add
	 * @return {@code this} pareto-set
	 * @throws NullPointerException if the given parameter is {@code null}
	 */
	public ParetoFront<T> merge(final ParetoFront<? extends T> elements) {
		addAll(elements);
		return this;
	}

	/**
	 * Trims {@code this} pareto front to the given size. The front elements are
	 * sorted according its crowding distance and the elements which have smaller
	 * distance to its neighbors are removed first.
	 *
	 * <pre>{@code
	 * final ParetoFront<Vec<double[]>> front = new ParetoFront<>(Vec::dominance);
	 * front.trim(10, Vec::compare, Vec::distance, Vec::length);
	 * }</pre>
	 * The example above reduces the given front to 10 elements.
	 *
	 * @param size the number of front elements after the trim. If
	 *        {@code size() <= size}, nothing is trimmed.
	 * @param comparator the element comparator used for calculating the
	 *        crowded distance
	 * @param distance the element distance measure
	 * @param dimension the number of vector elements of {@code T}
	 * @return {@code this} trimmed pareto front
	 * @throws NullPointerException if one of the objects is {@code null}
	 */
	public ParetoFront<T> trim(
		final int size,
		final ElementComparator<? super T> comparator,
		final ElementDistance<? super T> distance,
		final ToIntFunction<? super T> dimension
	) {
		requireNonNull(comparator);
		requireNonNull(distance);
		requireNonNull(dimension);

		if (size() > size) {
			final double[] distances = Pareto.crowdingDistance(
				Seq.viewOf(_population),
				comparator,
				distance,
				dimension
			);
			final int[] indexes = ProxySorter.sort(distances);
			revert(indexes);

			final List<T> list = IntStream.of(indexes)
				.limit(size)
				.mapToObj(_population::get)
				.collect(Collectors.toList());

			_population.clear();
			_population.addAll(list);
		}

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

	/**
	 * Return the elements of {@code this} pareto-front as {@link ISeq}.
	 *
	 * @return the elements of {@code this} pareto-front as {@link ISeq}
	 */
	public ISeq<T> toISeq() {
		return ISeq.of(_population);
	}

	/**
	 * Return a pareto-front collector. The natural order of the elements is
	 * used as pareto-dominance order.
	 *
	 * @param <C> the element type
	 * @return a new pareto-front collector
	 */
	public static <C extends Comparable<? super C>>
	Collector<C, ?, ParetoFront<C>> toParetoFront() {
		return toParetoFront(Comparator.naturalOrder());
	}

	/**
	 * Return a pareto-front collector with the given pareto {@code dominance}
	 * measure.
	 *
	 * @param dominance the pareto dominance comparator
	 * @param <T> the element type
	 * @return a new pareto-front collector
	 * @throws NullPointerException if the given {@code dominance} collector is
	 *         {@code null}
	 */
	public static <T> Collector<T, ?, ParetoFront<T>>
	toParetoFront(final Comparator<? super T> dominance) {
		requireNonNull(dominance);

		return Collector.of(
			() -> new ParetoFront<>(dominance),
			ParetoFront::add,
			ParetoFront::merge
		);
	}

}
