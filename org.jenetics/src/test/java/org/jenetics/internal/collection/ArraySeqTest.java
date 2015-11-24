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
package org.jenetics.internal.collection;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;
import java.util.stream.Stream;

import org.testng.annotations.Test;

import org.jenetics.util.ISeq;
import org.jenetics.util.Seq;
import org.jenetics.util.SeqTestBase;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
@Test
public class ArraySeqTest extends SeqTestBase {

	private static final class ArraySeqImpl<T>
		extends ArraySeq<T>
	{
		private static final long serialVersionUID = 1L;

		ArraySeqImpl(final Array<T> array) {
			super(array.seal());
		}

		@Override
		public <B> ISeq<B> map(final Function<? super T, ? extends B> mapper) {
			final Array<B> mapped = Array.ofLength(length());
			for (int i = 0; i < length(); ++i) {
				mapped.set(i, mapper.apply(array.get(i)));
			}
			return new ArrayISeq<>(mapped.seal());
		}

		@SuppressWarnings("unchecked")
		@Override
		public ISeq<T> append(final Iterable<? extends T> values) {
			requireNonNull(values);

			final Stream.Builder<T> builder = Stream.builder();
			values.forEach(builder::add);
			final Object[] objects = builder.build().toArray();

			final Array<T> appended = Array.ofLength(length() + objects.length);
			for (int i = 0; i < length(); ++i) {
				appended.set(i, get(i));
			}
			for (int i = 0; i < objects.length; ++i) {
				appended.set(i + length(), (T)objects[i]);
			}

			return new ArrayISeq<>(appended.seal());
		}

		@SuppressWarnings("unchecked")
		@Override
		public ISeq<T> prepend(final Iterable<? extends T> values) {
			requireNonNull(values);

			final Stream.Builder<T> builder = Stream.builder();
			values.forEach(builder::add);
			final Object[] objects = builder.build().toArray();

			final Array<T> appended = Array.ofLength(length() + objects.length);
			for (int i = 0; i < objects.length; ++i) {
				appended.set(i, (T)objects[i]);
			}
			for (int i = 0; i < length(); ++i) {
				appended.set(i + objects.length, get(i));
			}

			return new ArrayISeq<>(appended.seal());
		}

		@Override
		public ISeq<T> subSeq(final int start) {
			return new ArrayISeq<>(array.slice(start, length()));
		}

		@Override
		public ISeq<T> subSeq(int start, int end) {
			return new ArrayISeq<>(array.slice(start, end));
		}

	}

	@Override
	protected Seq<Integer> newSeq(final int length) {
		final Array<Integer> impl = Array.ofLength(length);
		for (int i = 0; i < length; ++i) {
			impl.set(i, i);
		}
		return new ArraySeqImpl<>(impl);
	}


}
