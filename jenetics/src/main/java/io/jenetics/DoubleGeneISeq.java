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
package io.jenetics;

import java.util.Comparator;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.DoubleStream;

import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class DoubleGeneISeq implements ISeq<DoubleGene> {

	private final double[] _values;
	private final double _min;
	private final double _max;

	private final int _start;
	private final int _length;

	DoubleGeneISeq(
		final double[] values,
		final double min,
		final double max,
		final int from,
		final int until
	) {
		_values = values;
		_min = min;
		_max = max;
		_start = from;
		_length = until - from;
	}

	@Override
	public DoubleGene get(final int index) {
		return DoubleGene.of(_values[index], _min, _max);
	}

	@Override
	public int length() {
		return _length;
	}

	@Override
	public ISeq<DoubleGene> subSeq(final int start, final int end) {
		return new DoubleGeneISeq(_values, _min, _max, start + _start, end + _start);
	}

	@Override
	public ISeq<DoubleGene> subSeq(final int start) {
		return subSeq(start, _length);
	}

	@Override
	public <B> ISeq<B> map(final Function<? super DoubleGene, ? extends B> mapper) {
		return copy().<B>map(mapper).toISeq();
	}

	@Override
	public ISeq<DoubleGene> append(final Iterable<? extends DoubleGene> values) {
		final DoubleStream.Builder vals = DoubleStream.builder();
		values.forEach(v -> vals.accept(v.doubleValue()));

		return null;
	}

	@Override
	public ISeq<DoubleGene> prepend(final Iterable<? extends DoubleGene> values) {
		return null;
	}

	@Override
	public MSeq<DoubleGene> copy() {
		final MSeq<DoubleGene> seq = MSeq.ofLength(_length);
		for (int i = 0; i < _length; ++i) {
			seq.set(i, get(i));
		}
		return seq;
	}
}

final class DoubleGeneMSeq implements MSeq<DoubleGene> {

	private final double[] _values;
	private final double _min;
	private final double _max;

	private final int _start;
	private final int _length;

	DoubleGeneMSeq(
		final double[] values,
		final double min,
		final double max,
		final int from,
		final int until
	) {
		_values = values;
		_min = min;
		_max = max;
		_start = from;
		_length = until - from;
	}

	@Override
	public void set(final int index, DoubleGene value) {
	}

	@Override
	public MSeq<DoubleGene> sort(final int start, final int end, final Comparator<? super DoubleGene> comparator) {
		return null;
	}

	@Override
	public MSeq<DoubleGene> subSeq(final int start, final int end) {
		return null;
	}

	@Override
	public MSeq<DoubleGene> subSeq(final int start) {
		return null;
	}

	@Override
	public DoubleGene get(final int index) {
		return null;
	}

	@Override
	public int length() {
		return 0;
	}

	@Override
	public <B> MSeq<B> map(final Function<? super DoubleGene, ? extends B> mapper) {
		return null;
	}

	@Override
	public void swap(final int i, final int j) {

	}

	@Override
	public void swap(
		final int start, final int end,
		final MSeq<DoubleGene> other, final int otherStart
	) {
	}

	@Override
	public void swap(final int index, final MSeq<DoubleGene> other) {
	}

	@Override
	public MSeq<DoubleGene> append(final Iterable<? extends DoubleGene> values) {
		return null;
	}

	@Override
	public MSeq<DoubleGene> prepend(final Iterable<? extends DoubleGene> values) {
		return null;
	}

	@Override
	public ISeq<DoubleGene> toISeq() {
		return null;
	}

	@Override
	public MSeq<DoubleGene> copy() {
		return null;
	}
}
