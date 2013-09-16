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
package org.jenetics.util;

import java.util.IntSummaryStatistics;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since @__new_version__@
 * @version @__new_version__@ &mdash; <em>$Date: 2013-09-16 $</em>
 */
abstract class IntStreamAdapter implements IntStream {

	private IntStream _adoptee;

	IntStreamAdapter() {
	}

	void setAdoptee(final IntStream adoptee) {
		_adoptee = Objects.requireNonNull(adoptee, "IntStream");
	}

	@Override
	public IntStream filter(final IntPredicate predicate) {
		return _adoptee.filter(predicate);
	}

	@Override
	public IntStream map(final IntUnaryOperator mapper) {
		return _adoptee.map(mapper);
	}

	@Override
	public <U> Stream<U> mapToObj(final IntFunction<? extends U> mapper) {
		return _adoptee.mapToObj(mapper);
	}

	@Override
	public LongStream mapToLong(final IntToLongFunction mapper) {
		return _adoptee.mapToLong(mapper);
	}

	@Override
	public DoubleStream mapToDouble(final IntToDoubleFunction mapper) {
		return _adoptee.mapToDouble(mapper);
	}

	@Override
	public IntStream flatMap(final IntFunction<? extends IntStream> mapper) {
		return _adoptee.flatMap(mapper);
	}

	@Override
	public IntStream distinct() {
		return _adoptee.distinct();
	}

	@Override
	public IntStream sorted() {
		return _adoptee.sorted();
	}

	@Override
	public IntStream peek(final IntConsumer consumer) {
		return _adoptee.peek(consumer);
	}

	@Override
	public IntStream limit(final long maxSize) {
		return _adoptee.limit(maxSize);
	}

	@Override
	public IntStream substream(final long startInclusive) {
		return _adoptee.substream(startInclusive);
	}

	@Override
	public IntStream substream(final long startInclusive, final long endExclusive) {
		return _adoptee.substream(startInclusive, endExclusive);
	}

	@Override
	public void forEach(final IntConsumer action) {
		_adoptee.forEach(action);
	}

	@Override
	public void forEachOrdered(final IntConsumer action) {
		_adoptee.forEachOrdered(action);
	}

	@Override
	public int[] toArray() {
		return _adoptee.toArray();
	}

	@Override
	public int reduce(final int identity, final IntBinaryOperator op) {
		return _adoptee.reduce(identity, op);
	}

	@Override
	public OptionalInt reduce(final IntBinaryOperator op) {
		return _adoptee.reduce(op);
	}

	@Override
	public <R> R collect(
		final Supplier<R> resultFactory,
		final ObjIntConsumer<R> accumulator,
		final BiConsumer<R, R> combiner
	) {
		return _adoptee.collect(resultFactory, accumulator, combiner);
	}

	@Override
	public int sum() {
		return _adoptee.sum();
	}

	@Override
	public OptionalInt min() {
		return _adoptee.min();
	}

	@Override
	public OptionalInt max() {
		return _adoptee.max();
	}

	@Override
	public long count() {
		return _adoptee.count();
	}

	@Override
	public OptionalDouble average() {
		return _adoptee.average();
	}

	@Override
	public IntSummaryStatistics summaryStatistics() {
		return _adoptee.summaryStatistics();
	}

	@Override
	public boolean anyMatch(final IntPredicate predicate) {
		return _adoptee.anyMatch(predicate);
	}

	@Override
	public boolean allMatch(final IntPredicate predicate) {
		return _adoptee.allMatch(predicate);
	}

	@Override
	public boolean noneMatch(final IntPredicate predicate) {
		return _adoptee.noneMatch(predicate);
	}

	@Override
	public OptionalInt findFirst() {
		return _adoptee.findFirst();
	}

	@Override
	public OptionalInt findAny() {
		return _adoptee.findAny();
	}

	@Override
	public LongStream asLongStream() {
		return _adoptee.asLongStream();
	}

	@Override
	public DoubleStream asDoubleStream() {
		return _adoptee.asDoubleStream();
	}

	@Override
	public Stream<Integer> boxed() {
		return _adoptee.boxed();
	}

	@Override
	public IntStream sequential() {
		return _adoptee.sequential();
	}

	@Override
	public IntStream parallel() {
		return _adoptee.parallel();
	}

	@Override
	public PrimitiveIterator.OfInt iterator() {
		return _adoptee.iterator();
	}

	@Override
	public Spliterator.OfInt spliterator() {
		return _adoptee.spliterator();
	}

	@Override
	public boolean isParallel() {
		return _adoptee.isParallel();
	}

	@Override
	public IntStream unordered() {
		return _adoptee.unordered();
	}

}
