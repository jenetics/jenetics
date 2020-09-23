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

import static java.util.Objects.requireNonNull;

import io.jenetics.ext.util.Tree;

import io.jenetics.prog.op.Op;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 *
 * @param <T>
 */
public final class RegressionResult<T> {
	private final Tree<Op<T>, ?> _program;
	private final double _error;
	private final long _generation;

	RegressionResult(
		final Tree<Op<T>, ?> program,
		final double error,
		final long generation
	) {
		_program = requireNonNull(program);
		_error = error;
		_generation = generation;
	}

	public Tree<Op<T>, ?> program() {
		return _program;
	}

	public double error() {
		return _error;
	}

	public long generation() {
		return _generation;
	}
}
