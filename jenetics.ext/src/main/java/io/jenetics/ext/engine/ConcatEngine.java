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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.BaseStream;
import java.util.stream.Collectors;

import io.jenetics.Gene;
import io.jenetics.engine.EvolutionInit;
import io.jenetics.engine.EvolutionStart;
import io.jenetics.engine.EvolutionStream;
import io.jenetics.engine.EvolutionStreamable;
import io.jenetics.internal.engine.EvolutionStreamImpl;

import io.jenetics.ext.internal.ConcatSpliterator;

/**
 * The {@code ConcatEngine} lets you concatenate two (or more) evolution
 * {@link io.jenetics.engine.Engine}, with different configurations, and let it
 * use as <em>one</em> engine {@link EvolutionStreamable}.
 *
 * <pre> {@code
 *                  +----------+               +----------+
 *                  |       ES |               |       ES |
 *          +-------+----+     |       +-------+----+     |
 *  (Start) |            +-----+ Start |            +-----+
 *   ------>|  Engine 1  |------------>|  Engine 2  |----------->
 *          |            | Result      |            |      Result
 *          +------------+             +------------+
 * }</pre>
 *
 * The sketch above shows how the engine concatenation works. In this example,
 * the evolution stream of the first engine is evaluated until it terminates.
 * The result of the first stream is then used as start input of the second
 * evolution stream, which then delivers the final result.
 * <p>
 * Concatenating evolution engines might be useful, if you want to explore your
 * search space with random search first and then start the <em>real</em> GA
 * search.
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
 *      ConcatEngine.of(
 *          engine1.limit(50),
 *          engine2.limit(() -> Limits.bySteadyFitness(30)))
 *      .stream()
 *          .collect(EvolutionResult.toBestGenotype());
 *
 *  System.out.println(result + ": " +
 *          problem.fitness().apply(problem.codec().decode(result)));
 * }</pre>
 *
 * An essential part, when concatenating evolution engines, is to make sure your
 * your engines are creating <em>limited</em> evolution streams. This is what
 * the {@link EvolutionStreamable#limit(Supplier)} and
 * {@link EvolutionStreamable#limit(long)} methods are for. Limiting an engine
 * means, that this engine will surely create only streams, which are limited
 * with the predicate/generation given to the engine. If you have limited your
 * engines, it is no longer necessary to limit your final evolution stream, but
 * your are still able to do so.
 *
 * @see CyclicEngine
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class ConcatEngine<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends EnginePool<G, C>
{

	public ConcatEngine(
		final List<? extends EvolutionStreamable<G, C>> engines
	) {
		super(engines);
	}

	@Override
	public EvolutionStream<G, C>
	stream(final Supplier<EvolutionStart<G, C>> start) {
		final AtomicReference<EvolutionStart<G, C>> other =
			new AtomicReference<>(null);

		return new EvolutionStreamImpl<G, C>(
			new ConcatSpliterator<>(
				_engines.stream()
					.map(engine -> engine
						.stream(() -> start(start, other))
						.peek(result -> other.set(result.toEvolutionStart())))
					.map(BaseStream::spliterator)
					.collect(Collectors.toList())
			),
			false
		);
	}

	private EvolutionStart<G, C> start(
		final Supplier<EvolutionStart<G, C>> first,
		final AtomicReference<EvolutionStart<G, C>> other
	) {
		return other.get() != null ? other.get() : first.get();
	}

	@Override
	public EvolutionStream<G, C> stream(final EvolutionInit<G> init) {
		final AtomicReference<EvolutionStart<G, C>> start =
			new AtomicReference<>(null);

		return new EvolutionStreamImpl<G, C>(
			new ConcatSpliterator<>(
				_engines.stream()
					.map(engine -> engine
						.stream(init)
						.peek(result -> start.set(result.toEvolutionStart())))
					.map(BaseStream::spliterator)
					.collect(Collectors.toList())
			),
			false
		);
	}

	@SafeVarargs
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	ConcatEngine<G, C> of(final EvolutionStreamable<G, C>... engines) {
		return new ConcatEngine<>(Arrays.asList(engines));
	}


}
