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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Stream;

import io.jenetics.Phenotype;
import io.jenetics.engine.EvolutionInterceptor;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStart;

import io.jenetics.prog.ProgramGene;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class RegressionRunner<T>
	implements
		EvolutionInterceptor<ProgramGene<T>, Double>,
		Runnable
{

	private Stream<EvolutionResult<ProgramGene<T>, Double>> _stream;
	private Consumer<? super RegressionResult<T>> _sink;

	private final AtomicBoolean _reset = new AtomicBoolean(false);
	private final AtomicBoolean _proceed = new AtomicBoolean(true);

	private EvolutionResult<ProgramGene<T>, Double> _best;

	RegressionRunner() {
	}

	void init(
		final Stream<EvolutionResult<ProgramGene<T>, Double>> stream,
		final Consumer<? super RegressionResult<T>> sink
	) {
		_stream = requireNonNull(stream);
		_sink = requireNonNull(sink);
	}

	@Override
	public void run() {
		if (_stream != null) {
			_stream
				.takeWhile(e -> _proceed.get())
				.forEach(this::submit);
		}
	}

	private void submit(final EvolutionResult<ProgramGene<T>, Double> result) {
		if (_reset.getAndSet(false)) {
			_best = null;
		}

		final var best = min(_best, result);
		if (best == _best) {
			// nicht besser
		} else {
			// besser;
		}

		_best = best;
	}

	private <C extends Comparable<? super C>> C min(final C a, final C b) {
		if (a == null && b == null) return null;
		if (a == null) return b;
		if (b == null) return a;
		return a.compareTo(b) <= 0 ? a : b;
	}

	void onNextGeneration(final Runnable action) {
	}

	void stop() {
		_proceed.set(false);
	}

	@Override
	public EvolutionStart<ProgramGene<T>, Double>
	before(final EvolutionStart<ProgramGene<T>, Double> start) {
		return start;
	}

	private EvolutionStart<ProgramGene<T>, Double>
	invalidate(final EvolutionStart<ProgramGene<T>, Double> start) {
		return EvolutionStart.of(
			start.population().map(Phenotype::nullifyFitness),
			start.generation()
		);
	}

	@Override
	public EvolutionResult<ProgramGene<T>, Double>
	after(final EvolutionResult<ProgramGene<T>, Double> result) {
		return result;
	}

}
