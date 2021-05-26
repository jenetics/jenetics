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
package io.jenetics.incubator.timeseries;

import io.jenetics.ext.util.Tree;
import io.jenetics.prog.op.Op;
import io.jenetics.prog.regression.Sample;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * This class contains an actual regression result + some <em>meta</em>
 * information about the regression process.
 *
 * @param <T>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class RegressionResult<T> {
	private final Tree<Op<T>, ?> _program;
	private final List<Sample<T>> _samples;
	private final double _error;
	private final long _generation;

	RegressionResult(
		final Tree<Op<T>, ?> program,
		final List<Sample<T>> samples,
		final double error,
		final long generation
	) {
		_program = requireNonNull(program);
		_error = error;
		_samples = List.copyOf(samples);
		_generation = generation;
	}

	/**
	 * Return the best fitting program.
	 *
	 * @return the best fitting program
	 */
	public Tree<Op<T>, ?> program() {
		return _program;
	}

	/**
	 * Return the sample points which are currently processed.
	 *
	 * @return the sample points which are currently processed
	 */
	public List<Sample<T>> samples() {
		return _samples;
	}

	/**
	 * The error value of {@code this} regression result.
	 *
	 * @return error value of {@code this} regression result
	 */
	public double error() {
		return _error;
	}

	/**
	 * The evolution generation {@code this} regression result belongs to.
	 *
	 * @return evolution generation {@code this} regression result belongs to
	 */
	public long generation() {
		return _generation;
	}
}
