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

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import io.jenetics.internal.collection.Empty;
import io.jenetics.internal.collection.Empty.EmptyISeq;
import io.jenetics.internal.util.Requires;

/**
 * Immutable, ordered, fixed sized sequence.
 *
 * @see MSeq
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 5.2
 */
public interface ISeq<T>
	extends
		Seq<T>,
		Copyable<MSeq<T>>
{

	@Override
	ISeq<T> subSeq(final int start, final int end);

	@Override
	ISeq<T> subSeq(final int start);

	@Override
	<B> ISeq<B> map(final Function<? super T, ? extends B> mapper);

	@SuppressWarnings("unchecked")
	@Override
	default ISeq<T> append(final T... values) {
		return append(ISeq.of(values));
	}

	@Override
	ISeq<T> append(final Iterable<? extends T> values);

	@SuppressWarnings("unchecked")
	@Override
	default ISeq<T> prepend(final T... values) {
		return prepend(ISeq.of(values));
	}

	@Override
	ISeq<T> prepend(final Iterable<? extends T> values);

	/**
	 * Return a shallow copy of this sequence. The sequence elements are not
	 * cloned.
	 *
	 * @return a shallow copy of this sequence.
	 */
	@Override
	MSeq<T> copy();


	/* *************************************************************************
	 *  Some static helper methods.
	 * ************************************************************************/

	/**
	 * Return a sequence whose elements are all the elements of the first
	 * element followed by all the elements of the sequence.
	 *
	 * @since 5.0
	 *
	 * @param a the first element
	 * @param b the appending sequence
	 * @param <T> the type of the sequence elements
	 * @return the concatenation of the two inputs
	 * @throws NullPointerException if one of the second arguments is
	 *         {@code null}
	 */
	@SuppressWarnings("unchecked")
	static <T> ISeq<T> concat(
		final T a,
		final ISeq<? extends T> b
	) {
		return ((ISeq<T>)b).prepend(a);
	}

	/**
	 * Return a sequence whose elements are all the elements of the first
	 * sequence followed by all the elements of the vararg array.
	 *
	 * @since 5.0
	 *
	 * @param a the first sequence
	 * @param b the vararg elements
	 * @param <T> the type of the sequence elements
	 * @return the concatenation of the two inputs
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	@SuppressWarnings("unchecked")
	static <T> ISeq<T> concat(
		final ISeq<? extends T> a,
		final T... b
	) {
		return ((ISeq<T>)a).append(b);
	}

	/**
	 * Return a sequence whose elements are all the elements of the first
	 * sequence followed by all the elements of the second sequence.
	 *
	 * @since 5.0
	 *
	 * @param a the first sequence
	 * @param b the second sequence
	 * @param <T> the type of the sequence elements
	 * @return the concatenation of the two input sequences
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	@SuppressWarnings("unchecked")
	static <T> ISeq<T> concat(
		final ISeq<? extends T> a,
		final ISeq<? extends T> b
	) {
		return ((ISeq<T>)a).append(b);
	}

	/* *************************************************************************
	 *  Some static factory methods.
	 * ************************************************************************/

	/**
	 * Single instance of an empty {@code ISeq}.
	 *
	 * @since 3.3
	 */
	ISeq<?> EMPTY = EmptyISeq.INSTANCE;

	/**
	 * Return an empty {@code ISeq}.
	 *
	 * @since 3.3
	 *
	 * @param <T> the element type of the returned {@code ISeq}.
	 * @return an empty {@code ISeq}.
	 */
	static <T> ISeq<T> empty() {
		return Empty.iseq();
	}

	/**
	 * Returns a {@code Collector} that accumulates the input elements into a
	 * new {@code ISeq}.
	 *
	 * @param <T> the type of the input elements
	 * @return a {@code Collector} which collects all the input elements into an
	 *         {@code ISeq}, in encounter order
	 */
	static <T> Collector<T, ?, ISeq<T>> toISeq() {
		return Collector.of(
			(Supplier<List<T>>)ArrayList::new,
			List::add,
			(left, right) -> { left.addAll(right); return left; },
			ISeq::of
		);
	}

	/**
	 * Returns a {@code Collector} that accumulates the last {@code n} input
	 * elements into a new {@code ISeq}.
	 *
	 * @since 5.0
	 *
	 * @param maxSize the maximal size of the collected sequence
	 * @param <T> the type of the input elements
	 * @return a {@code Collector} which collects maximal {@code maxSize} of the
	 *         input elements into an {@code ISeq}, in encounter order
	 * @throws IllegalArgumentException if the {@code maxSize} is negative
	 */
	static <T> Collector<T, ?, ISeq<T>> toISeq(final int maxSize) {
		return Seqs.toSeq(maxSize, Buffer::toSeq);
	}

	/**
	 * Create a new {@code ISeq} from the given values.
	 *
	 * @param <T> the element type
	 * @param values the array values.
	 * @return a new {@code ISeq} with the given values.
	 * @throws NullPointerException if the {@code values} array is {@code null}.
	 */
	@SafeVarargs
	static <T> ISeq<T> of(final T... values) {
		return values.length == 0
			? empty()
			: MSeq.of(values).toISeq();
	}

	/**
	 * Create a new {@code ISeq} from the given values.
	 *
	 * @param <T> the element type
	 * @param values the array values.
	 * @return a new {@code ISeq} with the given values.
	 * @throws NullPointerException if the {@code values} array is {@code null}.
	 */
	static <T> ISeq<T> of(final Iterable<? extends T> values) {
		requireNonNull(values);

		@SuppressWarnings("unchecked")
		final Iterable<T> vals = (Iterable<T>)values;

		return vals instanceof ISeq<T> seq
			? seq
			: vals instanceof MSeq<T> mseq
				? mseq.toISeq()
				: MSeq.<T>of(values).toISeq();
	}

	/**
	 * Creates a new sequence, which is filled with objects created be the given
	 * {@code supplier}.
	 *
	 * @since 3.2
	 *
	 * @param <T> the element type of the sequence
	 * @param supplier the {@code Supplier} which creates the elements, the
	 *        returned sequence is filled with
	 * @param length the length of the returned sequence
	 * @return a new sequence filled with elements given by the {@code supplier}
	 * @throws NegativeArraySizeException if the given {@code length} is
	 *         negative
	 * @throws NullPointerException if the given {@code supplier} is
	 *         {@code null}
	 */
	static <T> ISeq<T> of(
		final Supplier<? extends T> supplier,
		final int length
	) {
		requireNonNull(supplier);
		Requires.nonNegative(length);

		return length == 0
			? empty()
			: MSeq.<T>ofLength(length).fill(supplier).toISeq();
	}

	/**
	 * Allows a safe (without compile warning) upcast from {@code B} to
	 * {@code A}. Since {@code ISeq} instances are immutable, an <i>upcast</i>
	 * will be always safe.
	 *
	 * <pre>{@code
	 * // The sequence which we want to case.
	 * final ISeq<? extends Number> ints = ISeq.of(1, 2, 3, 4, 5);
	 *
	 * // This casts are possible without warning.
	 * final ISeq<Object> objects = ISeq.upcast(ints);
	 * final ISeq<Number> numbers = ISeq.upcast(ints);
	 *
	 * // This cast will, of course, still fail.
	 * final ISeq<String> strings = ISeq.upcast(ints);
	 * final ISeq<Integer> integers = ISeq.upcast(ints);
	 * }</pre>
	 *
	 * @since 3.6
	 *
	 * @param seq the sequence to cast safely
	 * @param <A> the <i>super</i>-object type
	 * @param <B> the <i>sub</i>-object type
	 * @return the cast instance of the given {@code seq}
	 */
	@SuppressWarnings("unchecked")
	static <A, B extends A> ISeq<A> upcast(final ISeq<B> seq) {
		return (ISeq<A>)seq;
	}

}
