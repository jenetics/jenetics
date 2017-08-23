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

import static java.lang.String.format;

import java.util.function.Function;

import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 3.4
 */
public class ArrayISeq<T> extends ArraySeq<T> implements ISeq<T> {
	private static final long serialVersionUID = 1L;

	public ArrayISeq(final Array<T> array) {
		super(array);
		assert array.isSealed();
	}

	@Override
	public <B> ISeq<B> map(final Function<? super T, ? extends B> mapper) {
		final Array<B> mapped = Array.ofLength(length());
		for (int i = 0; i < length(); ++i) {
			mapped.set(i, mapper.apply(array.get(i)));
		}
		return new ArrayISeq<>(mapped.seal());
	}

	@Override
	public ISeq<T> append(final Iterable<? extends T> values) {
		return new ArrayISeq<>(__append(values).seal());
	}

	@Override
	public ISeq<T> prepend(final Iterable<? extends T> values) {
		return new ArrayISeq<>(__prepend(values).seal());
	}

	@Override
	public ISeq<T> subSeq(final int start) {
		if (start < 0 || start > length()) {
			throw new ArrayIndexOutOfBoundsException(format(
				"Index %d range: [%d..%d)", start, 0, length()
			));
		}

		return start == length()
			? Empty.iseq()
			: new ArrayISeq<>(array.slice(start, length()));
	}

	@Override
	public ISeq<T> subSeq(int start, int end) {
		if (start > end) {
			throw new ArrayIndexOutOfBoundsException(format(
				"start[%d] > end[%d]", start, end
			));
		}
		if (start < 0 || end > length()) {
			throw new ArrayIndexOutOfBoundsException(format(
				"Indexes (%d, %d) range: [%d..%d)", start, end, 0, length()
			));
		}

		return start == end
			? Empty.iseq()
			: new ArrayISeq<>(array.slice(start, end));
	}

	@Override
	public MSeq<T> copy() {
		return isEmpty()
			? Empty.mseq()
			: new ArrayMSeq<>(array.copy());
	}

}
