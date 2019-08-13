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
package io.jenetics.prog.regression;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.0
 * @since 5.0
 */
final class ObjectSample<T> implements Sample<T>, Serializable {

	private static final long serialVersionUID = 1L;

	private final T[] _sample;

	/**
	 * Create a new sample point with the given argument array and result value.
	 *
	 * @param sample the arguments of the sample point
	 * @throws IllegalArgumentException if the argument array is empty
	 * @throws NullPointerException if the argument array is {@code null}
	 */
	@SafeVarargs
	ObjectSample(final T... sample) {
		if (sample.length < 2) {
			throw new IllegalArgumentException(format(
				"Argument sample must contain at least two values: %s",
				sample.length
			));
		}

		_sample = requireNonNull(sample);
	}

	@Override
	public int arity() {
		return _sample.length - 1;
	}

	@Override
	public T argAt(final int index) {
		if (index < 0 || index >= arity()) {
			throw new ArrayIndexOutOfBoundsException(format(
				"Argument index out or range [0, %s): %s", arity(), index
			));
		}

		return _sample[index];
	}

	public T[] args() {
		return Arrays.copyOfRange(_sample, 0, _sample.length - 1);
	}

	@Override
	public T result() {
		return _sample[_sample.length - 1];
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(_sample);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof ObjectSample &&
			Arrays.equals(_sample, ((ObjectSample)obj)._sample);
	}

	@Override
	public String toString() {
		return format("%s -> %s", Arrays.toString(args()), result());
	}

}
