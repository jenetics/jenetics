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
 * @version 3.0 &mdash; <em>$Date$</em>
 */
public abstract class StreamProxy<T> implements Stream<T> {

	protected final Stream<T> _proxy;

	protected StreamProxy(final Stream<T> proxy) {
		_proxy = requireNonNull(proxy);
	}

	@Override
	public Stream<T> limit(final long maxSize) {
		return _proxy.limit(maxSize);
	}

	@Override
	public Stream<T>
	filter(final Predicate<? super T> predicate) {
		return _proxy.filter(predicate);
	}

	public boolean
	allMatch(final Predicate<? super T> predicate) {
		return _proxy.allMatch(predicate);
	}

	@Override
	public boolean
	anyMatch(final Predicate<? super T> predicate) {
		return _proxy.anyMatch(predicate);
	}

	@Override
	public <R, A> R
	collect(final Collector<? super T, A, R> collector) {
		return _proxy.collect(collector);
	}

	@Override
	public <R> R collect(
		final Supplier<R> supplier,
		final BiConsumer<R, ? super T> accumulator,
		final BiConsumer<R, R> combiner
	) {
		return _proxy.collect(supplier, accumulator, combiner);
	}

	@Override
	public long count() {
		return _proxy.count();
	}

	@Override
	public Stream<T> distinct() {
		return _proxy.distinct();
	}

	@Override
	public Optional<T> findAny() {
		return _proxy.findAny();
	}

	@Override
	public Optional<T> findFirst() {
		return _proxy.findFirst();
	}

	@Override
	public <R> Stream<R> flatMap(
		final Function<? super T, ? extends Stream<? extends R>> mapper
	) {
		return _proxy.flatMap(mapper);
	}

	@Override
	public DoubleStream flatMapToDouble(
		final Function<? super T, ? extends DoubleStream> mapper
	) {
		return _proxy.flatMapToDouble(mapper);
	}

	@Override
	public IntStream flatMapToInt(
		final Function<? super T, ? extends IntStream> mapper
	) {
		return _proxy.flatMapToInt(mapper);
	}

	@Override
	public LongStream flatMapToLong(
		final Function<? super T, ? extends LongStream> mapper
	) {
		return _proxy.flatMapToLong(mapper);
	}

	@Override
	public void forEach(final Consumer<? super T> action) {
		_proxy.forEach(action);
	}

	@Override
	public void forEachOrdered(final Consumer<? super T> action) {
		_proxy.forEachOrdered(action);
	}

	@Override
	public <R> Stream<R> map(
		final Function<? super T, ? extends R> mapper
	) {
		return _proxy.map(mapper);
	}

	@Override
	public DoubleStream mapToDouble(
		final ToDoubleFunction<? super T> mapper
	) {
		return _proxy.mapToDouble(mapper);
	}

	@Override
	public IntStream mapToInt(
		final ToIntFunction<? super T> mapper
	) {
		return _proxy.mapToInt(mapper);
	}

	@Override
	public LongStream mapToLong(
		final ToLongFunction<? super T> mapper
	) {
		return _proxy.mapToLong(mapper);
	}

	@Override
	public Optional<T> max(
		final Comparator<? super T> comparator
	) {
		return _proxy.max(comparator);
	}

	@Override
	public Optional<T> min(
		final Comparator<? super T> comparator
	) {
		return _proxy.min(comparator);
	}

	@Override
	public boolean noneMatch(
		final Predicate<? super T> predicate
	) {
		return _proxy.noneMatch(predicate);
	}

	@Override
	public Stream<T> peek(
		final Consumer<? super T> action
	) {
		return _proxy.peek(action);
	}

	@Override
	public Optional<T> reduce(
		final BinaryOperator<T> accumulator
	) {
		return _proxy.reduce(accumulator);
	}

	@Override
	public T reduce(
		final T identity,
		final BinaryOperator<T> accumulator
	) {
		return _proxy.reduce(identity, accumulator);
	}

	@Override
	public <U> U reduce(
		final U identity,
		final BiFunction<U, ? super T, U> accumulator,
		final BinaryOperator<U> combiner
	) {
		return _proxy.reduce(identity, accumulator, combiner);
	}

	@Override
	public Stream<T> skip(final long n) {
		return _proxy.skip(n);
	}

	@Override
	public Stream<T> sorted() {
		return _proxy.sorted();
	}

	@Override
	public Stream<T> sorted(
		final Comparator<? super T> comparator
	) {
		return _proxy.sorted(comparator);
	}

	@Override
	public Object[] toArray() {
		return _proxy.toArray();
	}

	@Override
	public <A> A[] toArray(IntFunction<A[]> generator) {
		return _proxy.toArray(generator);
	}

	@Override
	public void close() {
		_proxy.close();
	}

	@Override
	public boolean isParallel() {
		return _proxy.isParallel();
	}

	@Override
	public Iterator<T> iterator() {
		return _proxy.iterator();
	}

	@Override
	public Stream<T> onClose(final Runnable closeHandler) {
		return _proxy.onClose(closeHandler);
	}

	@Override
	public Stream<T> parallel() {
		return _proxy.parallel();
	}

	@Override
	public Stream<T> sequential() {
		return _proxy.sequential();
	}

	@Override
	public Spliterator<T> spliterator() {
		return _proxy.spliterator();
	}

	@Override
	public Stream<T> unordered() {
		return _proxy.unordered();
	}

}
