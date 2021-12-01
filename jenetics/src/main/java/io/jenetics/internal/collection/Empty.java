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
package io.jenetics.internal.collection;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Stream;

import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;

/**
 * Contains static {@code Seq} definitions.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.1
 * @since 3.4
 */
public final class Empty {
	private Empty() {}

	public enum EmptyMSeq implements MSeq<Object> {

		INSTANCE;

		@Override
		public void set(final int index, final Object value) {
			throw new ArrayIndexOutOfBoundsException("MSeq is empty.");
		}

		@Override
		public MSeq<Object> sort(
			final int start,
			final int end,
			final Comparator<? super Object> comparator
		) {
			Array.checkIndex(start, end, length());
			return this;
		}

		@Override
		public ListIterator<Object> listIterator() {
			return Collections.emptyListIterator();
		}

		@Override
		public MSeq<Object> subSeq(final int start, final int end) {
			Array.checkIndex(start, end, length());
			return this;
		}

		@Override
		public MSeq<Object> subSeq(final int start) {
			Array.checkIndex(start, 0, length());
			return this;
		}

		@Override
		public <B> MSeq<B> map(final Function<? super Object, ? extends B> mapper) {
			requireNonNull(mapper);
			return mseq();
		}

		@Override
		public MSeq<Object> append(final Object... values) {
			return MSeq.of(values);
		}

		@Override
		public MSeq<Object> append(final Iterable<?> values) {
			return MSeq.of(values);
		}

		@Override
		public MSeq<Object> prepend(final Object... values) {
			return MSeq.of(values);
		}

		@Override
		public MSeq<Object> prepend(final Iterable<?> values) {
			return MSeq.of(values);
		}

		@Override
		public Stream<Object> stream() {
			return Stream.empty();
		}

		@Override
		public Stream<Object> parallelStream() {
			return Stream.empty();
		}

		@Override
		public Spliterator<Object> spliterator() {
			return Spliterators.emptySpliterator();
		}

		@Override
		public ISeq<Object> toISeq() {
			return EmptyISeq.INSTANCE;
		}

		@Override
		public MSeq<Object> copy() {
			return this;
		}

		@Override
		public Object get(final int index) {
			throw new ArrayIndexOutOfBoundsException("MSeq is empty: " + index);
		}

		@Override
		public int length() {
			return 0;
		}

		@Override
		public List<Object> asList() {
			return Collections.emptyList();
		}

		@Override
		public Iterator<Object> iterator() {
			return Collections.emptyIterator();
		}

		@Override
		public String toString() {
			return "[]";
		}

	}

	public enum EmptyISeq implements ISeq<Object> {

		INSTANCE;

		@Override
		public Iterator<Object> iterator() {
			return Collections.emptyIterator();
		}

		@Override
		public ISeq<Object> subSeq(final int start, final int end) {
			Array.checkIndex(start, end, length());
			return this;
		}

		@Override
		public ISeq<Object> subSeq(final int start) {
			Array.checkIndex(start, 0, length());
			return this;
		}

		@Override
		public Object get(final int index) {
			throw new ArrayIndexOutOfBoundsException("ISeq is empty: " + index);
		}

		@Override
		public int length() {
			return 0;
		}

		@Override
		public List<Object> asList() {
			return Collections.emptyList();
		}

		@Override
		public <B> ISeq<B> map(final Function<? super Object, ? extends B> mapper) {
			requireNonNull(mapper);
			return iseq();
		}

		@Override
		public ISeq<Object> append(final Object... values) {
			return ISeq.of(values);
		}

		@Override
		public ISeq<Object> append(final Iterable<?> values) {
			return ISeq.of(values);
		}

		@Override
		public ISeq<Object> prepend(final Object... values) {
			return ISeq.of(values);
		}

		@Override
		public ISeq<Object> prepend(final Iterable<?> values) {
			return ISeq.of(values);
		}

		@Override
		public Stream<Object> stream() {
			return Stream.empty();
		}

		@Override
		public Stream<Object> parallelStream() {
			return Stream.empty();
		}

		@Override
		public Spliterator<Object> spliterator() {
			return Spliterators.emptySpliterator();
		}

		@Override
		public MSeq<Object> copy() {
			return EmptyMSeq.INSTANCE;
		}

		@Override
		public String toString() {
			return "[]";
		}

	}

	@SuppressWarnings("unchecked")
	public static <T> MSeq<T> mseq() {
		return (MSeq<T>)EmptyMSeq.INSTANCE;
	}

	@SuppressWarnings("unchecked")
	public static <T> ISeq<T> iseq() {
		return (ISeq<T>)EmptyISeq.INSTANCE;
	}

}
