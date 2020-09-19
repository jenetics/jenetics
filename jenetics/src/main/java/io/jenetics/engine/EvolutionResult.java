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
package io.jenetics.engine;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;
import static io.jenetics.engine.EvolutionInterceptor.ofAfter;
import static io.jenetics.internal.util.Hashes.hash;
import static io.jenetics.internal.util.SerialIO.readInt;
import static io.jenetics.internal.util.SerialIO.readLong;
import static io.jenetics.internal.util.SerialIO.writeInt;
import static io.jenetics.internal.util.SerialIO.writeLong;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.Optimize;
import io.jenetics.Phenotype;
import io.jenetics.internal.util.Lazy;
import io.jenetics.stat.MinMax;
import io.jenetics.util.Factory;
import io.jenetics.util.ISeq;
import io.jenetics.util.Seq;

/**
 * Represents a state of the GA after an evolution step. It also represents the
 * final state of an evolution process and can be created with an appropriate
 * collector:
 * <pre>{@code
 * final Problem<ISeq<Point>, EnumGene<Point>, Double> tsm = ...;
 * final EvolutionResult<EnumGene<Point>, Double> result = Engine.builder(tsm)
 *     .optimize(Optimize.MINIMUM).build()
 *     .stream()
 *     .limit(100)
 *     .collect(EvolutionResult.toBestEvolutionResult());
 * }</pre>
 *
 * @see EvolutionStart
 * @see Engine
 *
 * @param <G> the gene type
 * @param <C> the fitness type
 *
 * @implNote
 * This class is immutable and thread-safe.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 6.0
 */
public final class EvolutionResult<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Comparable<EvolutionResult<G, C>>, Serializable
{
	private static final long serialVersionUID = 2L;

	private final Optimize _optimize;
	private final ISeq<Phenotype<G, C>> _population;
	private final long _generation;
	private final long _totalGenerations;

	private final EvolutionDurations _durations;
	private final int _killCount;
	private final int _invalidCount;
	private final int _alterCount;

	private final boolean _dirty;

	private final Lazy<Phenotype<G, C>> _best;
	private final Lazy<Phenotype<G, C>> _worst;

	private EvolutionResult(
		final Optimize optimize,
		final ISeq<Phenotype<G, C>> population,
		final long generation,
		final long totalGenerations,
		final EvolutionDurations durations,
		final int killCount,
		final int invalidCount,
		final int alterCount,
		final boolean dirty
	) {
		_optimize = requireNonNull(optimize);
		_population = requireNonNull(population);
		_generation = generation;
		_totalGenerations = totalGenerations;
		_durations = requireNonNull(durations);
		_killCount = killCount;
		_invalidCount = invalidCount;
		_alterCount = alterCount;
		_dirty = dirty;

		_best = Lazy.of(() -> _population.stream()
			.max(_optimize.ascending())
			.orElse(null)
		);

		_worst = Lazy.of(() -> _population.stream()
			.min(_optimize.ascending())
			.orElse(null)
		);
	}

	/**
	 * Return the optimization strategy used.
	 *
	 * @return the optimization strategy used
	 */
	public Optimize optimize() {
		return _optimize;
	}

	/**
	 * Return the population after the evolution step.
	 *
	 * @return the population after the evolution step
	 */
	public ISeq<Phenotype<G, C>> population() {
		return _population;
	}

	/**
	 * Return the current list of genotypes of this evolution result.
	 *
	 * @since 5.2
	 *
	 * @return the list of genotypes of this evolution result.
	 */
	public ISeq<Genotype<G>> genotypes() {
		return _population.map(Phenotype::genotype);
	}

	/**
	 * The current generation.
	 *
	 * @return the current generation
	 */
	public long generation() {
		return _generation;
	}

	/**
	 * Return the generation count evaluated so far.
	 *
	 * @return the total number of generations evaluated so far
	 */
	public long totalGenerations() {
		return _totalGenerations;
	}

	/**
	 * Return the timing (meta) information of the evolution step.
	 *
	 * @return the timing (meta) information of the evolution step
	 */
	public EvolutionDurations durations() {
		return _durations;
	}

	/**
	 * Return the number of killed individuals.
	 *
	 * @return the number of killed individuals
	 */
	public int killCount() {
		return _killCount;
	}

	/**
	 * Return the number of invalid individuals.
	 *
	 * @return the number of invalid individuals
	 */
	public int invalidCount() {
		return _invalidCount;
	}

	/**
	 * The number of altered individuals.
	 *
	 * @return the number of altered individuals
	 */
	public int alterCount() {
		return _alterCount;
	}

	/**
	 * Return the best {@code Phenotype} of the result population.
	 *
	 * @return the best {@code Phenotype} of the result population
	 */
	public Phenotype<G, C> bestPhenotype() {
		return _best.get();
	}

	/**
	 * Return the worst {@code Phenotype} of the result population.
	 *
	 * @return the worst {@code Phenotype} of the result population
	 */
	public Phenotype<G, C> worstPhenotype() {
		return _worst.get();
	}

	/**
	 * Return the best population fitness.
	 *
	 * @return The best population fitness.
	 */
	public C bestFitness() {
		return _best.get() != null
			? _best.get().fitness()
			: null;
	}

	/**
	 * Return the worst population fitness.
	 *
	 * @return The worst population fitness.
	 */
	public C worstFitness() {
		return _worst.get() != null ? _worst.get().fitness() : null;
	}

	/**
	 * Return the next evolution start object with the current population and
	 * the incremented generation.
	 *
	 * @since 4.1
	 *
	 * @return the next evolution start object
	 */
	public EvolutionStart<G, C> next() {
		return new EvolutionStart<>(_population, _totalGenerations + 1, _dirty);
	}

	/**
	 * Return the current evolution result object as an {@code EvolutionStart}
	 * object with the current population and current total generation.
	 *
	 * @since 4.1
	 *
	 * @return the current result as evolution start
	 */
	public EvolutionStart<G, C> toEvolutionStart() {
		return new EvolutionStart<>(_population, _totalGenerations, _dirty);
	}

	/**
	 * Compare {@code this} evolution result with another one, according the
	 * populations best individual.
	 *
	 * @param other the other evolution result to compare
	 * @return  a negative integer, zero, or a positive integer as this result
	 *          is less than, equal to, or greater than the specified result.
	 */
	@Override
	public int compareTo(final EvolutionResult<G, C> other) {
		return _optimize.compare(_best.get(), other._best.get());
	}

	private EvolutionResult<G, C> withTotalGenerations(final long total) {
		return EvolutionResult.of(
			_optimize,
			_population,
			_generation,
			total,
			_durations,
			_killCount,
			_invalidCount,
			_alterCount
		);
	}

	EvolutionResult<G, C> withPopulation(final ISeq<Phenotype<G, C>> population) {
		return EvolutionResult.of(
			optimize(),
			population,
			generation(),
			totalGenerations(),
			durations(),
			killCount(),
			invalidCount(),
			alterCount()
		);
	}

	EvolutionResult<G, C> withDurations(final EvolutionDurations durations) {
		return EvolutionResult.of(
			optimize(),
			population(),
			generation(),
			totalGenerations(),
			durations,
			killCount(),
			invalidCount(),
			alterCount()
		);
	}

	EvolutionResult<G, C> clean() {
		return new EvolutionResult<>(
			optimize(),
			population(),
			generation(),
			totalGenerations(),
			durations(),
			killCount(),
			invalidCount(),
			alterCount(),
			false
		);
	}

	@Override
	public int hashCode() {
		return
			hash(_optimize,
			hash(_population,
			hash(_generation,
			hash(_totalGenerations,
			hash(_durations,
			hash(_killCount,
			hash(_invalidCount,
			hash(_alterCount))))))));
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof EvolutionResult &&
			Objects.equals(_optimize,
				((EvolutionResult)obj)._optimize) &&
			Objects.equals(_population,
				((EvolutionResult)obj)._population) &&
			Objects.equals(_generation,
				((EvolutionResult)obj)._generation) &&
			Objects.equals(_totalGenerations,
				((EvolutionResult)obj)._totalGenerations) &&
			Objects.equals(_durations,
				((EvolutionResult)obj)._durations) &&
			Objects.equals(_killCount,
				((EvolutionResult)obj)._killCount) &&
			Objects.equals(_invalidCount,
				((EvolutionResult)obj)._invalidCount) &&
			Objects.equals(_alterCount,
				((EvolutionResult)obj)._alterCount);
	}


	/* *************************************************************************
	 *  Some static collector/factory methods.
	 * ************************************************************************/


	/**
	 * Return a collector which collects the best result of an evolution stream.
	 *
	 * <pre>{@code
	 * final Problem<ISeq<Point>, EnumGene<Point>, Double> tsm = ...;
	 * final EvolutionResult<EnumGene<Point>, Double> result = Engine.builder(tsm)
	 *     .optimize(Optimize.MINIMUM).build()
	 *     .stream()
	 *     .limit(100)
	 *     .collect(EvolutionResult.toBestEvolutionResult());
	 * }</pre>
	 *
	 * If the collected {@link EvolutionStream} is empty, the collector returns
	 * <b>{@code null}</b>.
	 *
	 * @param <G> the gene type
	 * @param <C> the fitness type
	 * @return a collector which collects the best result of an evolution stream
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Collector<EvolutionResult<G, C>, ?, EvolutionResult<G, C>>
	toBestEvolutionResult() {
		return Collector.of(
			MinMax::of,
			MinMax::accept,
			MinMax::combine,
			(MinMax<EvolutionResult<G, C>> mm) -> mm.max() != null
				? mm.max().withTotalGenerations(mm.count())
				: null
		);
	}

	/**
	 * Return a collector which collects the best phenotype of an evolution
	 * stream.
	 *
	 * <pre>{@code
	 * final Problem<ISeq<Point>, EnumGene<Point>, Double> tsm = ...;
	 * final Phenotype<EnumGene<Point>, Double> result = Engine.builder(tsm)
	 *     .optimize(Optimize.MINIMUM).build()
	 *     .stream()
	 *     .limit(100)
	 *     .collect(EvolutionResult.toBestPhenotype());
	 * }</pre>
	 *
	 * If the collected {@link EvolutionStream} is empty, the collector returns
	 * <b>{@code null}</b>.
	 *
	 * @param <G> the gene type
	 * @param <C> the fitness type
	 * @return a collector which collects the best phenotype of an evolution
	 *         stream
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Collector<EvolutionResult<G, C>, ?, Phenotype<G, C>>
	toBestPhenotype() {
		return Collector.of(
			MinMax::of,
			MinMax::accept,
			MinMax::combine,
			(MinMax<EvolutionResult<G, C>> mm) -> mm.max() != null
				? mm.max().bestPhenotype()
				: null
		);
	}

	/**
	 * Return a collector which collects the best genotype of an evolution
	 * stream.
	 *
	 * <pre>{@code
	 * final Problem<ISeq<Point>, EnumGene<Point>, Double> tsm = ...;
	 * final Genotype<EnumGene<Point>> result = Engine.builder(tsm)
	 *     .optimize(Optimize.MINIMUM).build()
	 *     .stream()
	 *     .limit(100)
	 *     .collect(EvolutionResult.toBestGenotype());
	 * }</pre>
	 *
	 * If the collected {@link EvolutionStream} is empty, the collector returns
	 * <b>{@code null}</b>.
	 *
	 * @param <G> the gene type
	 * @param <C> the fitness type
	 * @return a collector which collects the best genotype of an evolution
	 *         stream
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Collector<EvolutionResult<G, C>, ?, Genotype<G>>
	toBestGenotype() {
		return Collector.of(
			MinMax::of,
			MinMax::accept,
			MinMax::combine,
			(MinMax<EvolutionResult<G, C>> mm) -> mm.max() != null
				? mm.max().bestPhenotype() != null
					? mm.max().bestPhenotype().genotype()
					: null
				: null
		);
	}

	/**
	 * Return a collector which collects the best <em>result</em> (in the native
	 * problem space).
	 *
	 * <pre>{@code
	 * final Problem<ISeq<Point>, EnumGene<Point>, Double> tsm = ...;
	 * final ISeq<Point> route = Engine.builder(tsm)
	 *     .optimize(Optimize.MINIMUM).build()
	 *     .stream()
	 *     .limit(100)
	 *     .collect(EvolutionResult.toBestResult(tsm.codec().decoder()));
	 * }</pre>
	 *
	 * If the collected {@link EvolutionStream} is empty, the collector returns
	 * <b>{@code null}</b>.
	 *
	 * @since 3.6
	 *
	 * @param decoder the decoder which converts the {@code Genotype} into the
	 *        result of the problem space.
	 * @param <T> the <em>native</em> problem result type
	 * @param <G> the gene type
	 * @param <C> the fitness result type
	 * @return a collector which collects the best result of an evolution stream
	 * @throws NullPointerException if the given {@code decoder} is {@code null}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>, T>
	Collector<EvolutionResult<G, C>, ?, T>
	toBestResult(final Function<Genotype<G>, T> decoder) {
		requireNonNull(decoder);

		return Collector.of(
			MinMax::of,
			MinMax::accept,
			MinMax::combine,
			(MinMax<EvolutionResult<G, C>> mm) -> mm.max() != null
				? mm.max().bestPhenotype() != null
					? decoder.apply(mm.max().bestPhenotype().genotype())
					: null
				: null
		);
	}

	/**
	 * Return a collector which collects the best <em>result</em> (in the native
	 * problem space).
	 *
	 * <pre>{@code
	 * final Problem<ISeq<Point>, EnumGene<Point>, Double> tsm = ...;
	 * final ISeq<Point> route = Engine.builder(tsm)
	 *     .optimize(Optimize.MINIMUM).build()
	 *     .stream()
	 *     .limit(100)
	 *     .collect(EvolutionResult.toBestResult(tsm.codec()));
	 * }</pre>
	 *
	 * If the collected {@link EvolutionStream} is empty, the collector returns
	 * <b>{@code null}</b>.
	 *
	 * @since 3.6
	 *
	 * @param codec the problem decoder
	 * @param <T> the <em>native</em> problem result type
	 * @param <G> the gene type
	 * @param <C> the fitness result type
	 * @return a collector which collects the best result of an evolution stream
	 * @throws NullPointerException if the given {@code codec} is {@code null}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>, T>
	Collector<EvolutionResult<G, C>, ?, T>
	toBestResult(final Codec<T, G> codec) {
		return toBestResult(codec.decoder());
	}

	/**
	 * Return a mapping function, which removes duplicate individuals from the
	 * population and replaces it with newly created one by the given genotype
	 * {@code factory}.
	 *
	 * <pre>{@code
	 * final Problem<Double, DoubleGene, Integer> problem = ...;
	 * final Engine<DoubleGene, Integer> engine = Engine.builder(problem)
	 *     .interceptor(toUniquePopulation(problem.codec().encoding(), 100))
	 *     .build();
	 * final Genotype<DoubleGene> best = engine.stream()
	 *     .limit(100);
	 *     .collect(EvolutionResult.toBestGenotype());
	 * }</pre>
	 *
	 * @since 6.0
	 * @see Engine.Builder#interceptor(EvolutionInterceptor)
	 *
	 * @param factory the genotype factory which create new individuals
	 * @param maxRetries the maximal number of genotype creation tries
	 * @param <G> the gene type
	 * @param <C> the fitness function result type
	 * @return  a mapping function, which removes duplicate individuals from the
	 *          population
	 * @throws NullPointerException if the given genotype {@code factory} is
	 *         {@code null}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionInterceptor<G, C>
	toUniquePopulation(final Factory<Genotype<G>> factory, final int maxRetries) {
		requireNonNull(factory);
		return ofAfter(result -> uniquePopulation(factory, maxRetries, result));
	}

	private static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionResult<G, C> uniquePopulation(
		final Factory<Genotype<G>> factory,
		final int maxRetries,
		final EvolutionResult<G, C> result
	) {
		final Seq<Phenotype<G, C>> population = result.population();
		final Map<Genotype<G>, Phenotype<G, C>> elements =
			population.stream()
				.collect(toMap(
					Phenotype::genotype,
					Function.identity(),
					(a, b) -> a));

		EvolutionResult<G, C> uniques = result;
		if (elements.size() < population.size()) {
			int retries = 0;
			while (elements.size() < population.size() && retries < maxRetries) {
				final Genotype<G> gt = factory.newInstance();
				final Phenotype<G, C> pt = elements
					.put(gt, Phenotype.of(gt, result.generation()));
				if (pt != null) {
					++retries;
				}
			}
			uniques = result.withPopulation(
				Stream.concat(elements.values().stream(), population.stream())
					.limit(population.size())
					.collect(ISeq.toISeq())
			);
		}

		return uniques;
	}


	/* *************************************************************************
	 * Some collectors and mapping functions.
	 * ************************************************************************/


	/**
	 * Return a mapping function, which removes duplicate individuals from the
	 * population and replaces it with newly created one by the given genotype
	 * {@code factory}.
	 *
	 * <pre>{@code
	 * final Problem<Double, DoubleGene, Integer> problem = ...;
	 * final Engine<DoubleGene, Integer> engine = Engine.builder(problem)
	 *     .interceptor(toUniquePopulation(problem.codec().encoding()))
	 *     .build();
	 * final Genotype<DoubleGene> best = engine.stream()
	 *     .limit(100);
	 *     .collect(EvolutionResult.toBestGenotype());
	 * }</pre>
	 *
	 * @since 6.0
	 * @see Engine.Builder#interceptor(EvolutionInterceptor)
	 *
	 * @param factory the genotype factory which create new individuals
	 * @param <G> the gene type
	 * @param <C> the fitness function result type
	 * @return  a mapping function, which removes duplicate individuals from the
	 *          population
	 * @throws NullPointerException if the given genotype {@code factory} is
	 *         {@code null}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionInterceptor<G, C>
	toUniquePopulation(final Factory<Genotype<G>> factory) {
		return toUniquePopulation(factory, 100);
	}

	/**
	 * Return a mapping function, which removes duplicate individuals from the
	 * population and replaces it with newly created one by the existing
	 * genotype factory.
	 *
	 * <pre>{@code
	 * final Problem<Double, DoubleGene, Integer> problem = ...;
	 * final Engine<DoubleGene, Integer> engine = Engine.builder(problem)
	 *     .interceptor(toUniquePopulation(10))
	 *     .build();
	 * final Genotype<DoubleGene> best = engine.stream()
	 *     .limit(100);
	 *     .collect(EvolutionResult.toBestGenotype(5));
	 * }</pre>
	 *
	 * @since 6.0
	 * @see Engine.Builder#interceptor(EvolutionInterceptor)
	 *
	 * @param maxRetries the maximal number of genotype creation tries
	 * @param <G> the gene type
	 * @param <C> the fitness function result type
	 * @return  a mapping function, which removes duplicate individuals from the
	 *          population
	 * @throws NullPointerException if the given genotype {@code factory} is
	 *         {@code null}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionInterceptor<G, C> toUniquePopulation(final int maxRetries) {
		return ofAfter(result -> uniquePopulation(
			result.population().get(0).genotype(),
			maxRetries,
			result
		));
	}

	/**
	 * Return a mapping function, which removes duplicate individuals from the
	 * population and replaces it with newly created one by the existing
	 * genotype factory.
	 *
	 * <pre>{@code
	 * final Problem<Double, DoubleGene, Integer> problem = ...;
	 * final Engine<DoubleGene, Integer> engine = Engine.builder(problem)
	 *     .interceptor(EvolutionResult.toUniquePopulation())
	 *     .build();
	 * final Genotype<DoubleGene> best = engine.stream()
	 *     .limit(100);
	 *     .collect(EvolutionResult.toBestGenotype());
	 * }</pre>
	 *
	 * @since 6.0
	 * @see Engine.Builder#interceptor(EvolutionInterceptor)
	 *
	 * @param <G> the gene type
	 * @param <C> the fitness function result type
	 * @return  a mapping function, which removes duplicate individuals from the
	 *          population
	 * @throws NullPointerException if the given genotype {@code factory} is
	 *         {@code null}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionInterceptor<G, C> toUniquePopulation() {
		return ofAfter(result -> uniquePopulation(
			result.population().get(0).genotype(),
			100,
			result
		));
	}

	/**
	 * Return an new {@code EvolutionResult} object with the given values.
	 *
	 * @param optimize the optimization strategy used
	 * @param population the population after the evolution step
	 * @param generation the current generation
	 * @param totalGenerations the overall number of generations
	 * @param durations the timing (meta) information
	 * @param killCount the number of individuals which has been killed
	 * @param invalidCount the number of individuals which has been removed as
	 *        invalid
	 * @param alterCount the number of individuals which has been altered
	 * @param <G> the gene type
	 * @param <C> the fitness type
	 * @return an new evolution result object
	 * @throws java.lang.NullPointerException if one of the parameters is
	 *         {@code null}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionResult<G, C> of(
		final Optimize optimize,
		final ISeq<Phenotype<G, C>> population,
		final long generation,
		final long totalGenerations,
		final EvolutionDurations durations,
		final int killCount,
		final int invalidCount,
		final int alterCount
	) {
		return new EvolutionResult<>(
			optimize,
			population,
			generation,
			totalGenerations,
			durations,
			killCount,
			invalidCount,
			alterCount,
			true
		);
	}

	/**
	 * Return an new {@code EvolutionResult} object with the given values.
	 *
	 * @param optimize the optimization strategy used
	 * @param population the population after the evolution step
	 * @param generation the current generation
	 * @param durations the timing (meta) information
	 * @param killCount the number of individuals which has been killed
	 * @param invalidCount the number of individuals which has been removed as
	 *        invalid
	 * @param alterCount the number of individuals which has been altered
	 * @param <G> the gene type
	 * @param <C> the fitness type
	 * @return an new evolution result object
	 * @throws java.lang.NullPointerException if one of the parameters is
	 *         {@code null}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionResult<G, C> of(
		final Optimize optimize,
		final ISeq<Phenotype<G, C>> population,
		final long generation,
		final EvolutionDurations durations,
		final int killCount,
		final int invalidCount,
		final int alterCount
	) {
		return new EvolutionResult<>(
			optimize,
			population,
			generation,
			generation,
			durations,
			killCount,
			invalidCount,
			alterCount,
			true
		);
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private Object writeReplace() {
		return new Serial(Serial.EVOLUTION_RESULT, this);
	}

	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final ObjectOutput out) throws IOException {
		out.writeObject(_optimize);
		out.writeObject(_population);
		writeLong(_generation, out);
		writeLong(_totalGenerations, out);
		out.writeObject(_durations);
		writeInt(_killCount, out);
		writeInt(_invalidCount, out);
		writeInt(_alterCount, out);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	static Object read(final ObjectInput in)
		throws IOException, ClassNotFoundException
	{
		return new EvolutionResult<>(
			(Optimize)in.readObject(),
			(ISeq)in.readObject(),
			readLong(in),
			readLong(in),
			(EvolutionDurations)in.readObject(),
			readInt(in),
			readInt(in),
			readInt(in),
			true
		);
	}

}
