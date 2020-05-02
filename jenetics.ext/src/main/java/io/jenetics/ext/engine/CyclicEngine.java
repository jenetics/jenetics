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
package io.jenetics.ext.engine;

import java.util.List;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.jenetics.Gene;
import io.jenetics.engine.EvolutionInit;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStart;
import io.jenetics.engine.EvolutionStream;
import io.jenetics.engine.EvolutionStreamable;
import io.jenetics.internal.engine.EvolutionStreamImpl;

import io.jenetics.ext.internal.CyclicSpliterator;

/**
 * The {@code CyclicEngine} lets you concatenate two (or more) evolution
 * {@link io.jenetics.engine.Engine}, with different configurations, and let it
 * use as <em>one</em> engine {@link EvolutionStreamable}. If the last evolution
 * stream terminates, it's <em>final</em> result is fed back to first engine.
 *
 * <pre> {@code
 *                  +----------+               +----------+
 *                  |       ES |               |       ES |
 *          +------------+     |       +------------+     |
 *  (Start) |            |-----+ Start |            |-----+
 * ---+---->|  Engine 1  |------------>|  Engine 2  | --------+
 *    ^     |            | Result      |            |         |
 *    |     +------------+             +------------+         |
 *    |                                                       |
 *    +------------------------------<------------------------+
 *                              Result
 * }</pre>
 *
 * The {@code CyclicEngine} allows to do an broad search-fine search-cycle
 * as long as you want.
 *
 * <pre>{@code
 *  final Problem<double[], DoubleGene, Double> problem = Problem.of(
 *      v -> Math.sin(v[0])*Math.cos(v[1]),
 *      Codecs.ofVector(DoubleRange.of(0, 2*Math.PI), 2)
 *  );
 *
 *  final Engine<DoubleGene, Double> engine1 = Engine.builder(problem)
 *      .minimizing()
 *      .alterers(new Mutator<>(0.2))
 *      .selector(new MonteCarloSelector<>())
 *      .build();
 *
 *  final Engine<DoubleGene, Double> engine2 = Engine.builder(problem)
 *      .minimizing()
 *      .alterers(
 *          new Mutator<>(0.1),
 *          new MeanAlterer<>())
 *      .selector(new RouletteWheelSelector<>())
 *      .build();
 *
 *  final Genotype<DoubleGene> result =
 *      CyclicEngine.of(
 *          engine1.limit(50),
 *          engine2.limit(() -> Limits.bySteadyFitness(30)))
 *      .stream()
 *      .limit(Limits.bySteadyFitness(1000))
 *      .collect(EvolutionResult.toBestGenotype());
 *
 *  System.out.println(result + ": " +
 *      problem.fitness().apply(problem.codec().decode(result)));
 * }</pre>
 *
 * When using a {@code CyclicEnginePool}, you have to limit the final evolution
 * stream, additionally to the defined limits on the used partial engines.
 *
 * @see ConcatEngine
 *
 * @param <G> the gene type
 * @param <C> the fitness type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.1
 * @since 4.1
 */
public final class CyclicEngine<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends EnginePool<G, C>
{

	/**
	 * Create a new cycling evolution engine with the given list of
	 * {@code engines}.
	 *
	 * @param engines the evolution engines which are part of the cycling engine
	 * @throws NullPointerException if the {@code engines} or one of it's
	 *         elements is {@code null}
	 */
	public CyclicEngine(
		final List<? extends EvolutionStreamable<G, C>> engines
	) {
		super(engines);
	}

	@Override
	public EvolutionStream<G, C>
	stream(final Supplier<EvolutionStart<G, C>> start) {
		final AtomicReference<EvolutionStart<G, C>> other =
			new AtomicReference<>(null);

		return new EvolutionStreamImpl<>(
			new CyclicSpliterator<>(
				_engines.stream()
					.map(engine -> toSpliterator(engine, start, other))
					.collect(Collectors.toList())
			),
			false
		);
	}

	private Supplier<Spliterator<EvolutionResult<G, C>>> toSpliterator(
		final EvolutionStreamable<G, C> engine,
		final Supplier<EvolutionStart<G, C>> start,
		final AtomicReference<EvolutionStart<G, C>> other
	) {
		return () -> engine.stream(() -> start(start, other))
			.peek(result -> other.set(result.toEvolutionStart()))
			.spliterator();
	}

	private EvolutionStart<G, C> start(
		final Supplier<EvolutionStart<G, C>> first,
		final AtomicReference<EvolutionStart<G, C>> other
	) {
		return other.get() != null ? other.get() : first.get();
	}

	@Override
	public EvolutionStream<G, C> stream(final EvolutionInit<G> init) {
		final AtomicBoolean first = new AtomicBoolean(true);
		final AtomicReference<EvolutionStart<G, C>> other =
			new AtomicReference<>(null);

		return new EvolutionStreamImpl<>(
			new CyclicSpliterator<>(
				_engines.stream()
					.map(engine -> toSpliterator(engine, init, other, first))
					.collect(Collectors.toList())
			),
			false
		);
	}

	private Supplier<Spliterator<EvolutionResult<G, C>>> toSpliterator(
		final EvolutionStreamable<G, C> engine,
		final EvolutionInit<G> init,
		final AtomicReference<EvolutionStart<G, C>> other,
		final AtomicBoolean first
	) {
		return () -> {
			if (first.get()) {
				first.set(false);
				return engine.stream(init)
					.peek(result -> other.set(result.toEvolutionStart()))
					.spliterator();
			} else {
				return engine.stream(other::get)
					.peek(result -> other.set(result.toEvolutionStart()))
					.spliterator();
			}
		};

	}

	/**
	 * Create a new cycling evolution engine with the given array of
	 * {@code engines}.
	 *
	 * @param engines the evolution engines which are part of the cycling engine
	 * @param <G> the gene type
	 * @param <C> the fitness type
	 * @return a new concatenating evolution engine
	 * @throws NullPointerException if the {@code engines} or one of it's
	 *         elements is {@code null}
	 */
	@SafeVarargs
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	CyclicEngine<G, C> of(final EvolutionStreamable<G, C>... engines) {
		return new CyclicEngine<>(List.of(engines));
	}

}
