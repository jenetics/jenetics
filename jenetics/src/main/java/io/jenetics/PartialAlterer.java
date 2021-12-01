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
package io.jenetics;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.stream.IntStream;

import io.jenetics.internal.util.Requires;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;
import io.jenetics.util.MSeq;
import io.jenetics.util.Seq;

/**
 * This alterer wraps a given alterer which works on a given section of the
 * genotype's chromosomes.
 * <pre>{@code
 * // The genotype prototype, consisting of 4 chromosomes
 * final Genotype<DoubleGene> gtf = Genotype.of(
 *     DoubleChromosome.of(0, 1),
 *     DoubleChromosome.of(1, 2),
 *     DoubleChromosome.of(2, 3),
 *     DoubleChromosome.of(3, 4)
 * );
 *
 * // Define the GA engine.
 * final Engine<DoubleGene, Double> engine = Engine
 *     .builder(gt -> gt.getGene().doubleValue(), gtf)
 *     .selector(new RouletteWheelSelector<>())
 *     .alterers(
 *         // The `Mutator` is used on chromosome with index 0 and 2.
 *         PartialAlterer.of(new Mutator<DoubleGene, Double>(), 0, 2),
 *         // The `MeanAlterer` is used on chromosome 3.
 *         PartialAlterer.of(new MeanAlterer<DoubleGene, Double>(), 3),
 *         // The `GaussianMutator` is used on all chromosomes.
 *         new GaussianMutator<>()
 *     )
 *     .build();
 * }</pre>
 *
 * If you are using chromosome indices which are greater or equal than the
 * number of chromosomes defined in the genotype, a
 * {@link java.util.concurrent.CompletionException} is thrown when the evolution
 * stream is evaluated.
 *
 * @implNote
 * This alterer is slower than the performance of the wrapped alterer, because
 * of the needed <em>sectioning</em> of the genotype.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.0
 * @since 5.0
 */
public final class PartialAlterer<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Alterer<G, C>
{

	private final Alterer<G, C> _alterer;
	private final Projection _projection;

	private PartialAlterer(final Alterer<G, C> alterer, final Projection projection) {
		_alterer = requireNonNull(alterer);
		_projection = requireNonNull(projection);
	}

	@Override
	public AltererResult<G, C>
	alter(final Seq<Phenotype<G, C>> population, final long generation) {
		if (!population.isEmpty()) {
			_projection.checkIndices(population.get(0).genotype().length());

			final var projectedPopulation  = _projection.project(population);
			final var result = _alterer.alter(projectedPopulation, generation);

			return new AltererResult<>(
				_projection.merge(result.population(), population),
				result.alterations()
			);
		} else {
			return new AltererResult<>(population.asISeq(), 0);
		}
	}

	/**
	 * Wraps the given {@code alterer}, so that it will only work on chromosomes
	 * with the given chromosome indices.
	 *
	 * @see #of(Alterer, IntRange)
	 *
	 * @param alterer the alterer to user for altering the chromosomes with the
	 *        given {@code indices}
	 * @param indices the chromosomes indices (section)
	 * @param <G> the gene type
	 * @param <C> the fitness value type
	 * @return a wrapped alterer which only works for the given chromosome
	 *         section
	 * @throws NullPointerException if the given {@code indices} array is
	 *         {@code null}
	 * @throws IllegalArgumentException if the given {@code indices} array is
	 *         empty
	 * @throws NegativeArraySizeException if one of the given {@code indices} is
	 *         negative
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Alterer<G, C> of(final Alterer<G, C> alterer, final int... indices) {
		return new PartialAlterer<>(alterer, new Projection(indices));
	}

	/**
	 * Wraps the given {@code alterer}, so that it will only work on chromosomes
	 * with the given chromosome indices.
	 *
	 * @see #of(Alterer, int...)
	 *
	 * @param alterer the alterer to user for altering the chromosomes with the
	 *        given {@code indices}
	 * @param section the half-open chromosome index range {@code [min, max)}
	 * @param <G> the gene type
	 * @param <C> the fitness value type
	 * @return a wrapped alterer which only works for the given chromosome
	 *         section
	 * @throws NullPointerException if the given {@code indices} array is
	 *         {@code null}
	 * @throws IllegalArgumentException if the given {@code indices} array is
	 *         empty
	 * @throws NegativeArraySizeException if one of the given {@code indices} is
	 *         negative
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Alterer<G, C> of(final Alterer<G, C> alterer, final IntRange section) {
		return new PartialAlterer<>(
			alterer,
			new Projection(section.stream().toArray())
		);
	}


	/**
	 * The section class, which defines the chromosomes used by the alterer.
	 */
	record Projection(int[] indices) {
		Projection {
			if (indices.length == 0) {
				throw new IllegalArgumentException(
					"Chromosome indices must not be empty."
				);
			}
			for (int index : indices) {
				Requires.nonNegative(index);
			}
		}

		void checkIndices(final int length) {
			for (int index : indices) {
				if (index >= length) {
					throw new IndexOutOfBoundsException(format(
						"Genotype contains %d Chromosome, but found " +
							"SectionAlterer for Chromosome index %d.",
						length, index
					));
				}
			}
		}

		<G extends Gene<?, G>, C extends Comparable<? super C>>
		Seq<Phenotype<G, C>> project(final Seq<Phenotype<G, C>> population) {
			return population.map(this::project);
		}

		<G extends Gene<?, G>, C extends Comparable<? super C>>
		Phenotype<G, C> project(final Phenotype<G, C> pt) {
			final MSeq<Chromosome<G>> chromosomes = MSeq.ofLength(indices.length);
			for (int i = 0; i < indices.length; ++i) {
				chromosomes.set(i, pt.genotype().get(indices[i]));
			}
			final var gt = Genotype.of(chromosomes);

			return pt.isEvaluated()
				? Phenotype.of(gt, pt.generation(), pt.fitness())
				: Phenotype.of(gt, pt.generation());
		}

		<G extends Gene<?, G>, C extends Comparable<? super C>>
		ISeq<Phenotype<G, C>> merge(
			final Seq<Phenotype<G, C>> projection,
			final Seq<Phenotype<G, C>> population
		) {
			assert projection.length() == population.length();

			return IntStream.range(0, projection.length())
				.mapToObj(i -> merge(projection.get(i), population.get(i)))
				.collect(ISeq.toISeq());
		}

		<G extends Gene<?, G>, C extends Comparable<? super C>>
		Phenotype<G, C> merge(
			final Phenotype<G, C> projection,
			final Phenotype<G, C> pt
		) {
			final var ch = MSeq.of(pt.genotype());
			for (int i = 0; i < indices.length; ++i) {
				ch.set(indices[i], projection.genotype().get(i));
			}
			final var gt = Genotype.of(ch);

			return gt.equals(pt.genotype())
				? pt
				: Phenotype.of(gt, pt.generation());
		}

	}


}
