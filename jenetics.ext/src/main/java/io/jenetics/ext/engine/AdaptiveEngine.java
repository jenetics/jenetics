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

import static java.util.Objects.requireNonNull;

import java.util.Spliterator;
import java.util.function.Function;
import java.util.function.Supplier;

import io.jenetics.Alterer;
import io.jenetics.Gene;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionInit;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStart;
import io.jenetics.engine.EvolutionStream;
import io.jenetics.engine.EvolutionStreamable;
import io.jenetics.internal.engine.EvolutionStreamImpl;
import io.jenetics.util.DoubleRange;

import io.jenetics.ext.internal.GeneratorSpliterator;

/**
 * The {@code AdaptiveEngine} allows you to dynamically create engines with
 * different configurations, depending on the last {@link EvolutionResult} of
 * the previous evolution stream. It is therefore possible to increase the
 * mutation probability if the population is converging to fast. The sketch
 * below shows how the {@code AdaptiveEngine} is working.
 *
 * <pre> {@code
 *                                           +----------+
 *                                           |   ES[i]  |
 *           +-------------------------------+------+   |
 *           |                                      +---+
 *   (Start) |  EvolutionResult[i-1] -> Engine[i]   |-----------+-->
 *  -----+-->|           ^                          |  Result   |
 *       ^   +-----------|--------------------------+           |
 *       |               |                                      |
 *       +---------------+--------------<-----------------------+
 * }</pre>
 *
 *
 * <pre>{@code
 *  public static void main(final String[] args) {
 *      final Problem<double[], DoubleGene, Double> problem = Problem.of(
 *          v -> Math.sin(v[0])*Math.cos(v[1]),
 *          Codecs.ofVector(DoubleRange.of(0, 2*Math.PI), 2)
 *      );
 *
 *      final Engine.Builder<DoubleGene, Double> builder = Engine
 *          .builder(problem)
 *          .minimizing();
 *
 *      final Genotype<DoubleGene> result =
 *          AdaptiveEngine.<DoubleGene, Double>of(er -> engine(er, builder))
 *              .stream()
 *              .limit(Limits.bySteadyFitness(50))
 *              .collect(EvolutionResult.toBestGenotype());
 *
 *      System.out.println(result + ": " +
 *          problem.fitness().apply(problem.codec().decode(result)));
 *  }
 *
 *  private static EvolutionStreamable<DoubleGene, Double> engine(
 *      final EvolutionResult<DoubleGene, Double> result,
 *      final Engine.Builder<DoubleGene, Double> builder
 *  ) {
 *      return var(result) < 0.2
 *          ? builder
 *              .alterers(new Mutator<>(0.5))
 *              .build()
 *              .limit(5)
 *          : builder
 *              .alterers(
 *                  new Mutator<>(0.05),
 *                  new MeanAlterer<>())
 *              .selector(new RouletteWheelSelector<>())
 *              .build()
 *              .limit(15);
 *  }
 *
 *  private static double var(final EvolutionResult<DoubleGene, Double> result) {
 *      return result != null
 *          ? result.getPopulation().stream()
 *              .map(Phenotype::getFitness)
 *              .collect(DoubleMoments.toDoubleMoments(Double::doubleValue))
 *              .getVariance()
 *          : 0.0;
 *  }
 * }</pre>
 *
 * @see ConcatEngine
 * @see CyclicEngine
 *
 * @param <G> the gene type
 * @param <C> the fitness type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.1
 * @since 4.1
 *
 * @deprecated Use {@link io.jenetics.engine.EvolutionStream#ofAdjustableEvolution(Supplier, Function)}
 *             instead.
 */
@Deprecated
public final class AdaptiveEngine<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements EvolutionStreamable<G, C>
{

	private final Function<
		? super EvolutionResult<G, C>,
		? extends EvolutionStreamable<G, C>> _engine;

	/**
	 * Return a new adaptive evolution engine, with the given engine generation
	 * function.
	 *
	 * @param engine the engine generating function used for adapting the engines.
	 * @throws NullPointerException if the given {@code engine} is {@code null}
	 */
	public AdaptiveEngine(
		final Function<
			? super EvolutionResult<G, C>,
			? extends EvolutionStreamable<G, C>> engine
	) {
		_engine = requireNonNull(engine);
	}

	@Override
	public EvolutionStream<G, C>
	stream(final Supplier<EvolutionStart<G, C>> start) {
		return new EvolutionStreamImpl<G, C>(
			new GeneratorSpliterator<>(result -> generate(start, result)),
			false
		);
	}

	private Spliterator<EvolutionResult<G, C>>
	generate(
		final Supplier<EvolutionStart<G, C>> start,
		final EvolutionResult<G, C> result
	) {
		final EvolutionStart<G, C> es = result == null
			? start.get()
			: result.toEvolutionStart();

		return _engine.apply(result)
			.stream(es)
			.spliterator();
	}

	@Override
	public EvolutionStream<G, C> stream(final EvolutionInit<G> init) {
		return new EvolutionStreamImpl<G, C>(
			new GeneratorSpliterator<>(result -> generate(init, result)),
			false
		);
	}

	private Spliterator<EvolutionResult<G, C>>
	generate(
		final EvolutionInit<G> init,
		final EvolutionResult<G, C> result
	) {
		return result == null
			? _engine.apply(null)
				.stream(init)
				.spliterator()
			: _engine.apply(result)
				.stream(result.toEvolutionStart())
				.spliterator();
	}

	/**
	 * Return a new adaptive evolution engine, which tries to keep the
	 * population's fitness variance within the given {@code variance} range. If
	 * the population fitness variance is below the desired value, the
	 * {@code enlarge} alterer is used for introduce more diversity in the
	 * population fitness. And if the fitness variance is above the desired
	 * variance, the {@code narrow} alterer is used for reducing the population
	 * diversity.
	 *
	 * @since 5.0
	 *
	 * @param variance the desired fitness variance range for the population's
	 *        fitness
	 * @param builder the engine builder template used for creating new new
	 *        evolution engines with the different alterers
	 * @param narrow the narrowing alterer
	 * @param enlarge the alterer used for introducing a more divers population
	 * @param <G> the gene type
	 * @param <N> the fitness value type
	 * @return a new adaptive evolution engine which maintains the given fitness
	 *         variance
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static
	<G extends Gene<?, G>, N extends Number & Comparable<? super N>>
	AdaptiveEngine<G, N> byFitnessVariance(
		final DoubleRange variance,
		final Engine.Builder<G, N> builder,
		final Alterer<G, N> narrow,
		final Alterer<G, N> enlarge
	) {
		return new AdaptiveEngine<>(new FitnessVarianceAdaptiveEngine<>(
			variance, builder, narrow, enlarge
		));
	}

}
