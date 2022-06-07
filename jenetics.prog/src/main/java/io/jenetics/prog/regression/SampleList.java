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

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import io.jenetics.ext.util.Tree;

import io.jenetics.prog.op.Op;
import io.jenetics.prog.op.Program;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.1
 * @since 5.0
 */
final class SampleList<T>
	extends AbstractList<Sample<T>>
	implements
		Sampling<T>,
		Serializable
{
	@Serial
	private static final long serialVersionUID = 1L;

	private final List<? extends Sample<T>> _samples;

	private final Class<T> _type;
	private final T[][] _arguments;
	private final T[] _results;

	@SuppressWarnings("unchecked")
	SampleList(final List<? extends Sample<T>> samples) {
		if (samples.isEmpty()) {
			throw new IllegalArgumentException("Sample list must not be empty.");
		}

		_type = (Class<T>)samples.get(0).argAt(0).getClass();

		final int arity = samples.get(0).arity();
		if (arity == 0) {
			throw new IllegalArgumentException(
				"The arity of the sample point must not be zero."
			);
		}

		for (int i = 0; i < samples.size(); ++i) {
			final var sample = samples.get(i);
			if (arity != sample.arity()) {
				throw new IllegalArgumentException(format(
					"Expected arity %d, but got %d for sample index %d.",
					arity, sample.arity(), i
				));
			}
		}

		_samples = samples;

		_arguments = samples.stream()
			.map(s -> args(_type, s))
			.toArray(size -> (T[][])Array.newInstance(_type, size, 0));

		_results = _samples.stream()
			.map(Sample::result)
			.toArray(size -> (T[])Array.newInstance(_type, size));
	}

	private static <T> T[] args(final Class<T> type, final Sample<T> sample) {
		@SuppressWarnings("unchecked")
		final T[] args = (T[])Array
			.newInstance(sample.argAt(0).getClass(), sample.arity());
		for (int i = 0; i < args.length; ++i) {
			args[i] = sample.argAt(i);
		}

		return args;
	}

	@Override
	public Result<T> eval(final Tree<? extends Op<T>, ?> program) {
		requireNonNull(program);
		return eval(args -> Program.eval(program, args));
	}

	@Override
	public Result<T> eval(final Function<? super T[], ? extends T> function) {
		requireNonNull(function);
		@SuppressWarnings("unchecked")
		final T[] calculated = Stream.of(_arguments)
			.map(function)
			.toArray(size -> (T[])Array.newInstance(_type, size));

		return new Result<>(calculated, _results);
	}

	@Override
	public Sample<T> get(int index) {
		return _samples.get(index);
	}

	@Override
	public int size() {
		return _samples.size();
	}

}
