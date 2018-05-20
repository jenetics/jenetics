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
import java.util.function.Function;
import java.util.stream.StreamSupport;

import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;
import io.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class DoubleGeneISeq implements ISeq<DoubleGene> {

	final double[] _values;
	final double _min;
	final double _max;

	private final int _start;
	private final int _length;

	private DoubleGeneISeq(
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
		return new DoubleGeneISeq(
			_values,
			_min, _max,
			start + _start, end + _start
		);
	}

	@Override
	public ISeq<DoubleGene> subSeq(final int start) {
		return subSeq(start, _length);
	}

	@Override
	public <B> ISeq<B>
	map(final Function<? super DoubleGene, ? extends B> mapper) {
		return stream()
			.map(mapper)
			.collect(ISeq.toISeq());
	}

	@Override
	public ISeq<DoubleGene> append(final Iterable<? extends DoubleGene> values) {
		final double[] vals = toArray(values);
		final double[] array = new double[_length + vals.length];

		System.arraycopy(_values, _start, array, 0, _length);
		System.arraycopy(vals, 0, array, _length, vals.length);

		return new DoubleGeneISeq(array, _min, _max, 0, array.length);
	}

	@Override
	public ISeq<DoubleGene> prepend(final Iterable<? extends DoubleGene> values) {
		final double[] vals = toArray(values);
		final double[] array = new double[_length + vals.length];

		System.arraycopy(vals, 0, array, 0, vals.length);
		System.arraycopy(_values, _start, array, vals.length, _length);

		return new DoubleGeneISeq(array, _min, _max, 0, array.length);
	}

	@Override
	public MSeq<DoubleGene> copy() {
		final double[] array = new double[_length];
		System.arraycopy(_values, 0, array, 0, _length);

		return DoubleGeneMSeq.of(array, _min, _max);
	}

	@Override
	public String toString() {
		return toString("[", ",", "]");
	}

	@Override
	public int hashCode() {
		return Seq.hashCode(this);
	}

	@Override
	public boolean equals(final Object object) {
		return Seq.equals(this, object);
	}

	static double[] toArray(final Iterable<? extends DoubleGene> values) {
		final double[] array;
		if (values instanceof DoubleGeneISeq) {
			array = ((DoubleGeneISeq)values)._values;
		} else if (values instanceof DoubleGeneMSeq) {
			array = ((DoubleGeneMSeq)values)._values;
		} else {
			array = StreamSupport.stream(values.spliterator(), false)
				.mapToDouble(DoubleGene::doubleValue)
				.toArray();
		}

		return array;
	}

	static DoubleGeneISeq of(
		final double[] values,
		final double min,
		final double max
	) {
		return new DoubleGeneISeq(values, min, max, 0, values.length);
	}
}

final class DoubleGeneMSeq implements MSeq<DoubleGene> {

	final double[] _values;
	final double _min;
	final double _max;

	private final int _start;
	private final int _length;

	private DoubleGeneMSeq(
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
	public void set(final int index, final DoubleGene value) {
		_values[index] = value.doubleValue();
	}

	@Override
	public int length() {
		return _length;
	}

	@Override
	public MSeq<DoubleGene> subSeq(final int start, final int end) {
		return new DoubleGeneMSeq(
			_values,
			_min, _max,
			start + _start, end + _start
		);
	}

	@Override
	public MSeq<DoubleGene> subSeq(final int start) {
		return subSeq(start, _length);
	}

	@Override
	public <B> MSeq<B>
	map(final Function<? super DoubleGene, ? extends B> mapper) {
		return stream()
			.map(mapper)
			.collect(MSeq.toMSeq());
	}

	@Override
	public MSeq<DoubleGene> sort(
		final int start,
		final int end,
		final Comparator<? super DoubleGene> comparator
	) {
		return null;
	}

	@Override
	public void swap(final int i, final int j) {
		final double temp = _values[i + _start];
		_values[i + _start] = _values[j +_start];
		_values[j + _start] = temp;
	}

	@Override
	public void swap(
		final int start, final int end,
		final MSeq<DoubleGene> other, final int otherStart
	) {
		if (other instanceof DoubleGeneMSeq) {
			final DoubleGeneMSeq o = (DoubleGeneMSeq)other;

			if (start < end) {
				for (int i = end - start; --i >= 0;) {
					final double temp = _values[i + _start];
					_values[i + _start] = o._values[i + o._start];
					o._values[i + o._start] = temp;
				}
			}
		} else {
			MSeq.super.swap(start, end, other, otherStart);
		}
	}

	@Override
	public void swap(final int index, final MSeq<DoubleGene> other) {
		if (other instanceof DoubleGeneMSeq) {
			final DoubleGeneMSeq o = (DoubleGeneMSeq)other;
			final double temp = _values[index + _start];
			_values[index + _start] = o._values[index + o._start];
			o._values[index + o._start] = temp;
		} else {
			MSeq.super.swap(index, other);
		}
	}

	@Override
	public MSeq<DoubleGene> append(final Iterable<? extends DoubleGene> values) {
		final double[] vals = DoubleGeneISeq.toArray(values);
		final double[] array = new double[_length + vals.length];

		System.arraycopy(_values, _start, array, 0, _length);
		System.arraycopy(vals, 0, array, _length, vals.length);

		return new DoubleGeneMSeq(array, _min, _max, 0, array.length);
	}

	@Override
	public MSeq<DoubleGene> prepend(final Iterable<? extends DoubleGene> values) {
		final double[] vals = DoubleGeneISeq.toArray(values);
		final double[] array = new double[_length + vals.length];

		System.arraycopy(vals, 0, array, 0, vals.length);
		System.arraycopy(_values, _start, array, vals.length, _length);

		return new DoubleGeneMSeq(array, _min, _max, 0, array.length);
	}

	@Override
	public ISeq<DoubleGene> toISeq() {
		final double[] array = new double[_length];
		System.arraycopy(_values, 0, array, 0, _length);

		return DoubleGeneISeq.of(array, _min, _max);
	}

	@Override
	public MSeq<DoubleGene> copy() {
		final double[] array = new double[_length];
		System.arraycopy(_values, 0, array, 0, _length);

		return DoubleGeneMSeq.of(array, _min, _max);
	}

	@Override
	public String toString() {
		return toString("[", ",", "]");
	}

	@Override
	public int hashCode() {
		return Seq.hashCode(this);
	}

	@Override
	public boolean equals(final Object object) {
		return Seq.equals(this, object);
	}

	static DoubleGeneMSeq of(
		final double[] values,
		final double min,
		final double max
	) {
		return new DoubleGeneMSeq(values, min, max, 0, values.length);
	}
}
