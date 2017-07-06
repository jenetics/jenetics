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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.engine;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0
 */
abstract class StreamProxy<T> implements Stream<T> {

	private final Stream<T> _self;

	StreamProxy(final Stream<T> self) {
		_self = requireNonNull(self);
	}

	@Override
	public Stream<T> limit(final long maxSize) {
		return _self.limit(maxSize);
	}

	@Override
	public Stream<T>
	filter(final Predicate<? super T> predicate) {
		return _self.filter(predicate);
	}

	public boolean
	allMatch(final Predicate<? super T> predicate) {
		return _self.allMatch(predicate);
	}

	@Override
	public boolean
	anyMatch(final Predicate<? super T> predicate) {
		return _self.anyMatch(predicate);
	}

	@Override
	public <R, A> R
	collect(final Collector<? super T, A, R> collector) {
		return _self.collect(collector);
	}

	@Override
	public <R> R collect(
		final Supplier<R> supplier,
		final BiConsumer<R, ? super T> accumulator,
		final BiConsumer<R, R> combiner
	) {
		return _self.collect(supplier, accumulator, combiner);
	}

	@Override
	public long count() {
		return _self.count();
	}

	@Override
	public Stream<T> distinct() {
		return _self.distinct();
	}

	@Override
	public Optional<T> findAny() {
		return _self.findAny();
	}

	@Override
	public Optional<T> findFirst() {
		return _self.findFirst();
	}

	@Override
	public <R> Stream<R> flatMap(
		final Function<? super T, ? extends Stream<? extends R>> mapper
	) {
		return _self.flatMap(mapper);
	}

	@Override
	public DoubleStream flatMapToDouble(
		final Function<? super T, ? extends DoubleStream> mapper
	) {
		return _self.flatMapToDouble(mapper);
	}

	@Override
	public IntStream flatMapToInt(
		final Function<? super T, ? extends IntStream> mapper
	) {
		return _self.flatMapToInt(mapper);
	}

	@Override
	public LongStream flatMapToLong(
		final Function<? super T, ? extends LongStream> mapper
	) {
		return _self.flatMapToLong(mapper);
	}

	@Override
	public void forEach(final Consumer<? super T> action) {
		_self.forEach(action);
	}

	@Override
	public void forEachOrdered(final Consumer<? super T> action) {
		_self.forEachOrdered(action);
	}

	@Override
	public <R> Stream<R> map(
		final Function<? super T, ? extends R> mapper
	) {
		return _self.map(mapper);
	}

	@Override
	public DoubleStream mapToDouble(
		final ToDoubleFunction<? super T> mapper
	) {
		return _self.mapToDouble(mapper);
	}

	@Override
	public IntStream mapToInt(
		final ToIntFunction<? super T> mapper
	) {
		return _self.mapToInt(mapper);
	}

	@Override
	public LongStream mapToLong(
		final ToLongFunction<? super T> mapper
	) {
		return _self.mapToLong(mapper);
	}

	@Override
	public Optional<T> max(
		final Comparator<? super T> comparator
	) {
		return _self.max(comparator);
	}

	@Override
	public Optional<T> min(
		final Comparator<? super T> comparator
	) {
		return _self.min(comparator);
	}

	@Override
	public boolean noneMatch(
		final Predicate<? super T> predicate
	) {
		return _self.noneMatch(predicate);
	}

	@Override
	public Stream<T> peek(
		final Consumer<? super T> action
	) {
		return _self.peek(action);
	}

	@Override
	public Optional<T> reduce(
		final BinaryOperator<T> accumulator
	) {
		return _self.reduce(accumulator);
	}

	@Override
	public T reduce(
		final T identity,
		final BinaryOperator<T> accumulator
	) {
		return _self.reduce(identity, accumulator);
	}

	@Override
	public <U> U reduce(
		final U identity,
		final BiFunction<U, ? super T, U> accumulator,
		final BinaryOperator<U> combiner
	) {
		return _self.reduce(identity, accumulator, combiner);
	}

	@Override
	public Stream<T> skip(final long n) {
		return _self.skip(n);
	}

	@Override
	public Stream<T> sorted() {
		return _self.sorted();
	}

	@Override
	public Stream<T> sorted(
		final Comparator<? super T> comparator
	) {
		return _self.sorted(comparator);
	}

	@Override
	public Object[] toArray() {
		return _self.toArray();
	}

	@Override
	public <A> A[] toArray(IntFunction<A[]> generator) {
		return _self.toArray(generator);
	}

	@Override
	public void close() {
		_self.close();
	}

	@Override
	public boolean isParallel() {
		return _self.isParallel();
	}

	@Override
	public Iterator<T> iterator() {
		return _self.iterator();
	}

	@Override
	public Stream<T> onClose(final Runnable closeHandler) {
		return _self.onClose(closeHandler);
	}

	@Override
	public Stream<T> parallel() {
		return _self.parallel();
	}

	@Override
	public Stream<T> sequential() {
		return _self.sequential();
	}

	@Override
	public Spliterator<T> spliterator() {
		return _self.spliterator();
	}

	@Override
	public Stream<T> unordered() {
		return _self.unordered();
	}

}
