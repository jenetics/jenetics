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

import java.util.Iterator;
import java.util.function.Supplier;

import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.internal.util.require;
import io.jenetics.util.ISeq;

/**
 * This interface defines the capability of creating <b>infinite</b> evolution
 * iterators.
 *
 * @see EvolutionStreamable
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface EvolutionIterable<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends Iterable<EvolutionResult<G, C>>
{

	/**
	 * Create a new <b>infinite</b> evolution iterator with the given evolution
	 * start. If an empty {@code Population} is given, the engines genotype
	 * factory is used for creating the population. The given population might
	 * be the result of an other engine and this method allows to start the
	 * evolution with the outcome of an different engine. The fitness function
	 * and the fitness scaler are replaced by the one defined for this engine.
	 *
	 * @param start the data the evolution stream starts with
	 * @return a new <b>infinite</b> evolution iterator
	 * @throws java.lang.NullPointerException if the given evolution
	 *         {@code start} is {@code null}.
	 */
	public Iterator<EvolutionResult<G, C>>
	iterator(final Supplier<EvolutionStart<G, C>> start);

	/**
	 * Create a new <b>infinite</b> evolution iterator with the given initial
	 * value. If an empty {@code Population} is given, the engines genotype
	 * factory is used for creating the population. The given population might
	 * be the result of an other engine and this method allows to start the
	 * evolution with the outcome of an different engine. The fitness function
	 * and the fitness scaler are replaced by the one defined for this engine.
	 *
	 * @param init the data the evolution iterator is initialized with
	 * @return a new <b>infinite</b> evolution stream
	 * @throws java.lang.NullPointerException if the given evolution
	 *         {@code start} is {@code null}.
	 */
	public Iterator<EvolutionResult<G, C>> iterator(final EvolutionInit<G> init);

	/* *************************************************************************
	 * Default interface methods.
	 * ************************************************************************/

	/**
	 * Create a new <b>infinite</b> evolution iterator with a newly created
	 * population. This is an alternative way for evolution. It lets the user
	 * start, stop and resume the evolution process whenever desired.
	 *
	 * @return a new <b>infinite</b> evolution iterator
	 */
	@Override
	public default Iterator<EvolutionResult<G, C>> iterator() {
		return iterator(EvolutionStart.of(ISeq.empty(), 1));
	}

	/**
	 * Create a new <b>infinite</b> evolution iterator with the given evolution
	 * start. If an empty {@code Population} is given, the engines genotype
	 * factory is used for creating the population. The given population might
	 * be the result of an other engine and this method allows to start the
	 * evolution with the outcome of an different engine. The fitness function
	 * and the fitness scaler are replaced by the one defined for this engine.
	 *
	 * @param start the data the evolution stream starts with
	 * @return a new <b>infinite</b> evolution iterator
	 * @throws java.lang.NullPointerException if the given evolution
	 *         {@code start} is {@code null}.
	 */
	public default Iterator<EvolutionResult<G, C>>
	iterator(final EvolutionStart<G, C> start) {
		return iterator(() -> start);
	}

	/**
	 * Create a new <b>infinite</b> evolution iterator starting with a
	 * previously evolved {@link EvolutionResult}. The iterator is initialized
	 * with the population of the given {@code result} and its total generation
	 * {@link EvolutionResult#getTotalGenerations()}.
	 *
	 * @param result the previously evolved {@code EvolutionResult}
	 * @return a new evolution stream, which continues a previous one
	 * @throws NullPointerException if the given evolution {@code result} is
	 *         {@code null}
	 */
	public default Iterator<EvolutionResult<G, C>>
	iterator(final EvolutionResult<G, C> result) {
		return iterator(EvolutionStart.of(
			result.getPopulation(),
			result.getGeneration()
		));
	}

	/**
	 * Create a new <b>infinite</b> evolution iterator with the given initial
	 * population. If an empty {@code Population} is given, the engines genotype
	 * factory is used for creating the population. The given population might
	 * be the result of an other engine and this method allows to start the
	 * evolution with the outcome of an different engine. The fitness function
	 * and the fitness scaler are replaced by the one defined for this engine.
	 *
	 * @param population the initial individuals used for the evolution iterator.
	 *        Missing individuals are created and individuals not needed are
	 *        skipped.
	 * @param generation the generation the iterator starts from; must be greater
	 *        than zero.
	 * @return a new <b>infinite</b> evolution iterator
	 * @throws java.lang.NullPointerException if the given {@code population} is
	 *         {@code null}.
	 * @throws IllegalArgumentException if the given {@code generation} is smaller
	 *        then one
	 */
	public default Iterator<EvolutionResult<G, C>> iterator(
		final ISeq<Phenotype<G, C>> population,
		final long generation
	) {
		return iterator(EvolutionStart.of(population, generation));
	}

	/**
	 * Create a new <b>infinite</b> evolution iterator with the given initial
	 * population. If an empty {@code Population} is given, the engines genotype
	 * factory is used for creating the population. The given population might
	 * be the result of an other engine and this method allows to start the
	 * evolution with the outcome of an different engine. The fitness function
	 * and the fitness scaler are replaced by the one defined for this engine.
	 *
	 * @param population the initial individuals used for the evolution iterator.
	 *        Missing individuals are created and individuals not needed are
	 *        skipped.
	 * @return a new <b>infinite</b> evolution iterator
	 * @throws java.lang.NullPointerException if the given {@code population} is
	 *         {@code null}.
	 */
	public default Iterator<EvolutionResult<G, C>>
	iterator(final ISeq<Phenotype<G, C>> population) {
		return iterator(EvolutionStart.of(population, 1));
	}

	/**
	 * Create a new <b>infinite</b> evolution iterator with the given initial
	 * individuals. If an empty {@code Iterable} is given, the engines genotype
	 * factory is used for creating the population.
	 *
	 * @param genotypes the initial individuals used for the evolution iterator.
	 *        Missing individuals are created and individuals not needed are
	 *        skipped.
	 * @param generation the generation the stream starts from; must be greater
	 *        than zero.
	 * @return a new <b>infinite</b> evolution iterator
	 * @throws java.lang.NullPointerException if the given {@code genotypes} is
	 *         {@code null}.
	 * @throws IllegalArgumentException if the given {@code generation} is
	 *         smaller then one
	 */
	public default Iterator<EvolutionResult<G, C>> iterator(
		final Iterable<Genotype<G>> genotypes,
		final long generation
	) {
		return iterator(EvolutionInit.of(
			ISeq.of(genotypes),
			generation
		));
	}

	/**
	 * Create a new <b>infinite</b> evolution iterator with the given initial
	 * individuals. If an empty {@code Iterable} is given, the engines genotype
	 * factory is used for creating the population.
	 *
	 * @param genotypes the initial individuals used for the evolution iterator.
	 *        Missing individuals are created and individuals not needed are
	 *        skipped.
	 * @return a new <b>infinite</b> evolution iterator
	 * @throws java.lang.NullPointerException if the given {@code genotypes} is
	 *         {@code null}.
	 */
	public default Iterator<EvolutionResult<G, C>>
	iterator(final Iterable<Genotype<G>> genotypes) {
		return iterator(genotypes, 1);
	}

}
