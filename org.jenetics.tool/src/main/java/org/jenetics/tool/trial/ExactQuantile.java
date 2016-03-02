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
package org.jenetics.tool.trial;

import java.util.function.DoubleConsumer;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import org.jenetics.internal.util.Lazy;

/**
 * A state object for collecting quantiles.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.4
 * @since 3.4
 */
final class ExactQuantile implements DoubleConsumer {

	private final Stream.Builder<ExactQuantile> _collectors = Stream.builder();
	private final DoubleStream.Builder _data = DoubleStream.builder();

	private final Lazy<double[]> _array = Lazy.of(() ->
		_collectors.build()
			.flatMapToDouble(c -> c._data.build())
			.sorted()
			.toArray());

	ExactQuantile() {
		_collectors.accept(this);
	}

	@Override
	public void accept(final double value) {
		if (_array.isEvaluated()) {
			throw new IllegalStateException(
				"Quantile has already been evaluated."
			);
		}

		_data.accept(value);
	}

	public ExactQuantile combine(final ExactQuantile other) {
		if (_array.isEvaluated()) {
			throw new IllegalStateException(
				"Quantile has already been evaluated."
			);
		}

		_collectors.accept(other);
		return this;
	}

	public double quantile(final double p) {
		final int index = (int)(_array.get().length*p);
		return _array.get()[index];
	}

}
