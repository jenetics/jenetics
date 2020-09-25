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

import io.jenetics.Phenotype;
import io.jenetics.engine.EvolutionInterceptor;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStart;
import io.jenetics.prog.ProgramGene;
import io.jenetics.prog.regression.Sample;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * Executes the evolution stream into an separate thread, for not blocking the
 * reactive regression processor. It is also responsible for <em>nullifying</em>
 * the already evaluated error values when the sample points changes.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class RegressionRunner<T>
	implements
		EvolutionInterceptor<ProgramGene<T>, Double>,
		Runnable
{

	private final AtomicReference<Stream<EvolutionResult<ProgramGene<T>, Double>>> _stream =
		new AtomicReference<>();

	private final AtomicReference<Consumer<? super RegressionResult<T>>> _sink =
		new AtomicReference<>();

	private final AtomicBoolean _proceed = new AtomicBoolean(true);
	private final AtomicReference<Runnable> _action = new AtomicReference<>();

	// The currently best evolution result object. Can be reset.
	private final AtomicReference<EvolutionResult<ProgramGene<T>, Double>> _best =
		new AtomicReference<>();

	// The currently analysed list of sample points.
	private final AtomicReference<List<Sample<T>>> _samples =
		new AtomicReference<>(List.of());

	/**
	 * Create a new, <em>uninitialized</em> regression runner.
	 */
	RegressionRunner() {
	}

	/**
	 * Initializes the runner with the given evolution {@code stream} and the
	 * regression result {@code sink}.
	 *
	 * @param stream the evolution stream
	 * @param sink the regression result sink (consumer)
	 * @throws NullPointerException if one of the parameters is {@code null}
	 */
	void init(
		final Stream<EvolutionResult<ProgramGene<T>, Double>> stream,
		final Consumer<? super RegressionResult<T>> sink
	) {
		_stream.set(requireNonNull(stream));
		_sink.set(requireNonNull(sink));

		_action.set(null);
		_best.set(null);
		_samples.set(List.of());
	}

	@Override
	public void run() {
		final var stream = _stream.get();
		if (stream != null) {
			stream
				.takeWhile(e -> _proceed.get())
				.forEach(this::submit);
		}
	}

	private void submit(final EvolutionResult<ProgramGene<T>, Double> result) {
		final var previousBest = _best.get();
		final var best = min(previousBest, result);

		// Found a new, better result.
		if (best != previousBest) {
			final var rr = new RegressionResult<T>(
				best.bestPhenotype().genotype().gene(),
				_samples.get(),
				best.bestFitness() != null ? best.bestFitness() : Double.NaN,
				best.generation()
			);

			final var sink = _sink.get();
			if (sink != null) {
				sink.accept(rr);
			}
		}

		_best.set(best);
	}

	private <C extends Comparable<? super C>> C min(final C a, final C b) {
		if (a == null && b == null) return null;
		if (a == null) return b;
		if (b == null) return a;
		return a.compareTo(b) <= 0 ? a : b;
	}

	/**
	 * Set the action which will be executed on the start of the next generation.
	 * The action is executed only once.
	 *
	 * @param action the action to execute on the next generation
	 */
	void onNextGeneration(final Runnable action) {
		_action.set(action);
	}

	/**
	 * Resets the best found program so far. This is done when the sample point
	 * window has changed.
	 */
	void reset() {
		_best.set(null);
	}

	/**
	 * Sets the actually processed sample points.
	 *
	 * @param samples the actually processed sample points
	 * @throws NullPointerException if the {@code sample} points are {@code null}
	 */
	void samples(final List<Sample<T>> samples) {
		_samples.set(requireNonNull(samples));
	}

	/**
	 * Stops the regression evolution process.
	 */
	void stop() {
		_proceed.set(false);
	}

	@Override
	public EvolutionStart<ProgramGene<T>, Double>
	before(final EvolutionStart<ProgramGene<T>, Double> start) {
		final var action = _action.getAndSet(null);
		if (action != null) {
			action.run();
		}

		return _best.get() == null ? invalidate(start) : start;
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
