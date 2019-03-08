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

import io.jenetics.internal.util.require;
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
	private final Section _section;

	private PartialAlterer(final Alterer<G, C> alterer, final Section section) {
		_alterer = requireNonNull(alterer);
		_section = requireNonNull(section);
	}

	@Override
	public AltererResult<G, C>
	alter(final Seq<Phenotype<G, C>> population, final long generation) {
		if (!population.isEmpty()) {
			_section.checkIndices(population.get(0).getGenotype().length());

			final Seq<Phenotype<G, C>> split  = _section.split(population);
			final AltererResult<G, C> result = _alterer.alter(split, generation);

			return AltererResult.of(
				_section.merge(result.getPopulation(), population),
				result.getAlterations()
			);
		} else {
			return AltererResult.of(population.asISeq(), 0);
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
		return new PartialAlterer<>(alterer, Section.of(indices));
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
			Section.of(section.stream().toArray())
		);
	}


	/**
	 * The section class, which defines the chromosomes used by the alterer.
	 */
	static final class Section {
		final int[] indices;

		private Section(final int[] indices) {
			if (indices.length == 0) {
				throw new IllegalArgumentException(
					"Chromosome indices must not be empty."
				);
			}
			for (int index : indices) {
				require.nonNegative(index);
			}

			this.indices = indices;
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
		Seq<Phenotype<G, C>> split(final Seq<Phenotype<G, C>> population) {
			return population.map(this::split);
		}

		<G extends Gene<?, G>, C extends Comparable<? super C>>
		Phenotype<G, C> split(final Phenotype<G, C> phenotype) {
			final ISeq<Chromosome<G>> chromosomes = IntStream.of(indices)
				.mapToObj(phenotype.getGenotype()::get)
				.collect(ISeq.toISeq());

			final Genotype<G> genotype = Genotype.of(chromosomes);

			return phenotype.isEvaluated()
				? Phenotype.of(
					genotype,
					phenotype.getGeneration(),
					phenotype.getFitness())
				: Phenotype.of(genotype, phenotype.getGeneration());
		}

		<G extends Gene<?, G>, C extends Comparable<? super C>>
		ISeq<Phenotype<G, C>> merge(
			final Seq<Phenotype<G, C>> section,
			final Seq<Phenotype<G, C>> population
		) {
			assert section.length() == population.length();

			return IntStream.range(0, section.length())
				.mapToObj(i -> merge(section.get(i), population.get(i)))
				.collect(ISeq.toISeq());
		}

		<G extends Gene<?, G>, C extends Comparable<? super C>>
		Phenotype<G, C> merge(
			final Phenotype<G, C> section,
			final Phenotype<G, C> phenotype
		) {
			final MSeq<Chromosome<G>> chromosomes = phenotype.getGenotype()
				.toSeq()
				.copy();

			for (int i = 0; i < indices.length; ++i) {
				chromosomes.set(indices[i], section.getGenotype().get(i));
			}

			final Genotype<G> genotype = Genotype.of(chromosomes);

			return phenotype.isEvaluated()
				? Phenotype.of(
					genotype,
					phenotype.getGeneration(),
					phenotype.getFitness())
				: Phenotype.of(genotype, phenotype.getGeneration());
		}

		static Section of(final int... indices) {
			return new Section(indices);
		}

	}


}
