/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.util;

import java.util.IntSummaryStatistics;
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
 * @since xxx
 * @version xxx &mdash; <em>$Date: 2013-05-03 $</em>
 */
public class RandomIndexStream implements IntStream {

	@Override
	public IntStream filter(IntPredicate predicate) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public IntStream map(IntUnaryOperator mapper) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public <U> Stream<U> mapToObj(IntFunction<? extends U> mapper) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public LongStream mapToLong(IntToLongFunction mapper) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public DoubleStream mapToDouble(IntToDoubleFunction mapper) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public IntStream flatMap(IntFunction<? extends IntStream> mapper) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public IntStream distinct() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public IntStream sorted() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public IntStream peek(IntConsumer consumer) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public IntStream limit(long maxSize) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public IntStream substream(long startInclusive) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public IntStream substream(long startInclusive, long endExclusive) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void forEach(IntConsumer action) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void forEachOrdered(IntConsumer action) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public int[] toArray() {
		return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public int reduce(int identity, IntBinaryOperator op) {
		return 0;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public OptionalInt reduce(IntBinaryOperator op) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public <R> R collect(Supplier<R> resultFactory, ObjIntConsumer<R> accumulator, BiConsumer<R, R> combiner) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public int sum() {
		return 0;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public OptionalInt min() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public OptionalInt max() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public long count() {
		return 0;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public OptionalDouble average() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public IntSummaryStatistics summaryStatistics() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean anyMatch(IntPredicate predicate) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean allMatch(IntPredicate predicate) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean noneMatch(IntPredicate predicate) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public OptionalInt findFirst() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public OptionalInt findAny() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public LongStream longs() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public DoubleStream doubles() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Stream<Integer> boxed() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public IntStream sequential() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public IntStream parallel() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public PrimitiveIterator.OfInt iterator() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Spliterator.OfInt spliterator() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean isParallel() {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public IntStream unordered() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

}
