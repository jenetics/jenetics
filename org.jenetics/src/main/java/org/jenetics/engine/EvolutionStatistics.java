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
package org.jenetics.engine;

import static java.lang.Math.sqrt;
import static java.lang.String.format;

import java.text.NumberFormat;
import java.time.Duration;
import java.util.function.Consumer;

import org.jenetics.Phenotype;
import org.jenetics.stat.DoubleMomentStatistics;
import org.jenetics.stat.IntMomentStatistics;
import org.jenetics.stat.LongMomentStatistics;
import org.jenetics.stat.MinMax;

/**
 * This class can be used to gather additional statistic information of an
 * evolution process. The additional information can be useful during the
 * development phase of the GA or while testing the GA's performance. The
 * following example shows how to integrate the <i>statistics</i> object into
 * your evolution <i>stream</i>.
 *
 * <pre>{@code
 * final Engine<DoubleGene, Double> engine = ...
 * final EvolutionStatistics<Double, DoubleMomentStatistics> statistics =
 *     EvolutionStatistics.ofNumber();
 *
 * final Phenotype<DoubleGene, Double> result = engine.stream()
 *     .limit(bySteadyFitness(7))
 *     .limit(100)
 *     .peek(statistics)
 *     .collect(toBestPhenotype());
 *
 * System.println(statistics);
 * }</pre>
 *
 * <b>Example output</b>
 *
 * <pre>{@code
 * +---------------------------------------------------------------------------+
 * |  Time statistics                                                          |
 * +---------------------------------------------------------------------------+
 * |             Selection: sum=0.046538278000 s; mean=0.003878189833 s        |
 * |              Altering: sum=0.086155457000 s; mean=0.007179621417 s        |
 * |   Fitness calculation: sum=0.022901606000 s; mean=0.001908467167 s        |
 * |     Overall execution: sum=0.147298067000 s; mean=0.012274838917 s        |
 * +---------------------------------------------------------------------------+
 * |  Evolution statistics                                                     |
 * +---------------------------------------------------------------------------+
 * |           Generations: 12                                                 |
 * |               Altered: sum=7,331; mean=610.916666667                      |
 * |                Killed: sum=0; mean=0.000000000                            |
 * |              Invalids: sum=0; mean=0.000000000                            |
 * +---------------------------------------------------------------------------+
 * |  Population statistics                                                    |
 * +---------------------------------------------------------------------------+
 * |                   Age: max=11; mean=1.951000; var=5.545190                |
 * |               Fitness:                                                    |
 * |                      min  = 0.000000000000                                |
 * |                      max  = 481.748227114537                              |
 * |                      mean = 384.430345078660                              |
 * |                      var  = 13006.132537301528                            |
 * |                      std  = 114.044432                                    |
 * +---------------------------------------------------------------------------+
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0
 */
public abstract class EvolutionStatistics<
	C extends Comparable<? super C>,
	FitnessStatistics
>
	implements Consumer<EvolutionResult<?, C>>
{

	// The duration statistics values.
	private final DoubleMomentStatistics
		_selectionDuration = new DoubleMomentStatistics();
	private final DoubleMomentStatistics
		_alterDuration = new DoubleMomentStatistics();
	private final DoubleMomentStatistics
		_evaluationDuration = new DoubleMomentStatistics();
	private final DoubleMomentStatistics
		_evolveDuration = new DoubleMomentStatistics();

	// The evolution statistics values.
	private final IntMomentStatistics _killed = new IntMomentStatistics();
	private final IntMomentStatistics _invalids = new IntMomentStatistics();
	private final IntMomentStatistics _altered = new IntMomentStatistics();

	// The population statistics values.
	final LongMomentStatistics _age = new LongMomentStatistics();
	FitnessStatistics _fitness = null;

	EvolutionStatistics() {
	}

	@Override
	public void accept(final EvolutionResult<?, C> result) {
		accept(result.getDurations());

		_killed.accept(result.getKillCount());
		_invalids.accept(result.getInvalidCount());
		_altered.accept(result.getAlterCount());

		result.getPopulation()
			.forEach(pt -> accept(pt, result.getGeneration()));
	}

	void accept(final Phenotype<?, C> pt, final long generation) {
		_age.accept(pt.getAge(generation));
	}

	// Calculate duration statistics
	private void accept(final EvolutionDurations durations) {
		final double selection =
			toSeconds(durations.getOffspringSelectionDuration()) +
				toSeconds(durations.getSurvivorsSelectionDuration());
		final double alter =
			toSeconds(durations.getOffspringAlterDuration()) +
				toSeconds(durations.getOffspringFilterDuration());

		_selectionDuration.accept(selection);
		_alterDuration.accept(alter);
		_evaluationDuration
			.accept(toSeconds(durations.getEvaluationDuration()));
		_evolveDuration
			.accept(toSeconds(durations.getEvolveDuration()));
	}

	private static double toSeconds(final Duration duration) {
		return duration.toNanos()/1_000_000_000.0;
	}

	/* *************************************************************************
	 * Evaluation timing statistics
	 * ************************************************************************/

	/**
	 * Return the duration statistics needed for selecting the population, in
	 * seconds.
	 *
	 * @return the duration statistics needed for selecting the population
	 */
	public DoubleMomentStatistics getSelectionDuration() {
		return _selectionDuration;
	}

	/**
	 * Return the duration statistics needed for altering the population, in
	 * seconds.
	 *
	 * @return the duration statistics needed for altering the population
	 */
	public DoubleMomentStatistics getAlterDuration() {
		return _alterDuration;
	}

	/**
	 * Return the duration statistics needed for evaluating the fitness function
	 * of the new individuals, in seconds.
	 *
	 * @return the duration statistics needed for evaluating the fitness
	 *         function of the new individuals
	 */
	public DoubleMomentStatistics getEvaluationDuration() {
		return _evaluationDuration;
	}

	/**
	 * Return the duration statistics needed for the whole evolve step, in
	 * seconds.
	 *
	 * @return the duration statistics needed for the whole evolve step
	 */
	public DoubleMomentStatistics getEvolveDuration() {
		return _evolveDuration;
	}



	/* *************************************************************************
	 * Evolution statistics
	 * ************************************************************************/

	/**
	 * Return the statistics about the killed individuals during the evolution
	 * process.
	 *
	 * @return killed individual statistics
	 */
	public IntMomentStatistics getKilled() {
		return _killed;
	}

	/**
	 * Return the statistics about the invalid individuals during the evolution
	 * process.
	 *
	 * @return invalid individual statistics
	 */
	public IntMomentStatistics getInvalids() {
		return _invalids;
	}

	/**
	 * Return the statistics about the altered individuals during the evolution
	 * process.
	 *
	 * @return altered individual statistics
	 */
	public IntMomentStatistics getAltered() {
		return _altered;
	}

	/**
	 * Return the statistics about the individuals age.
	 *
	 * @return individual age statistics
	 */
	public LongMomentStatistics getPhenotypeAge() {
		return _age;
	}

	/**
	 * Return the minimal and maximal fitness.
	 *
	 * @return minimal and maximal fitness
	 */
	public FitnessStatistics getFitness() {
		return _fitness;
	}

	final String cpattern = "| %22s %-51s|\n";
	final String spattern = "| %27s %-46s|\n";

	@Override
	public String toString() {
		return
			"+---------------------------------------------------------------------------+\n" +
			"|  Time statistics                                                          |\n" +
			"+---------------------------------------------------------------------------+\n" +
			format(cpattern, "Selection:", d(_selectionDuration)) +
			format(cpattern, "Altering:", d(_alterDuration)) +
			format(cpattern, "Fitness calculation:", d(_evaluationDuration)) +
			format(cpattern, "Overall execution:", d(_evolveDuration)) +
			"+---------------------------------------------------------------------------+\n" +
			"|  Evolution statistics                                                     |\n" +
			"+---------------------------------------------------------------------------+\n" +
			format(cpattern, "Generations:", i(_altered.getCount())) +
			format(cpattern, "Altered:", i(_altered)) +
			format(cpattern, "Killed:", i(_killed)) +
			format(cpattern, "Invalids:", i(_invalids));
	}

	private static String d(final DoubleMomentStatistics statistics) {
		return format(
			"sum=%3.12f s; mean=%3.12f s",
			statistics.getSum(), statistics.getMean()
		);
	}

	private static String i(final IntMomentStatistics statistics) {
		final NumberFormat nf = NumberFormat.getIntegerInstance();
		return format(
			"sum=%s; mean=%6.9f",
			nf.format(statistics.getSum()), statistics.getMean()
		);
	}

	private static String i(final long value) {
		final NumberFormat nf = NumberFormat.getIntegerInstance();
		return nf.format(value);
	}

	private static String p(final IntMomentStatistics statistics) {
		final NumberFormat nf = NumberFormat.getIntegerInstance();
		return format(
			"max=%s; mean=%6.6f; var=%6.6f",
			nf.format(statistics.getMax()),
			statistics.getMean(),
			statistics.getVariance()
		);
	}

	private static String p(final LongMomentStatistics statistics) {
		final NumberFormat nf = NumberFormat.getIntegerInstance();
		return format(
			"max=%s; mean=%6.6f; var=%6.6f",
			nf.format(statistics.getMax()),
			statistics.getMean(),
			statistics.getVariance()
		);
	}

	private static final class Comp<
		C extends Comparable<? super C>
		>
		extends EvolutionStatistics<C, MinMax<C>>
	{
		private Comp() {
			_fitness = MinMax.of();
		}

		@Override
		public void accept(final EvolutionResult<?, C> result) {
			if (_fitness.getMax() == null) {
				_fitness = MinMax.of(result.getOptimize().ascending());
			}

			super.accept(result);
		}

		@Override
		void accept(final Phenotype<?, C> pt, final long generation) {
			super.accept(pt, generation);
			_fitness.accept(pt.getFitness());
		}

		@Override
		public String toString() {
			return super.toString() +
				"+---------------------------------------------------------------------------+\n" +
				"|  Population statistics                                                    |\n" +
				"+---------------------------------------------------------------------------+\n" +
				format(cpattern, "Age:", p(_age)) +
				format(cpattern, "Fitness", "") +
				format(spattern, "min =", _fitness.getMin()) +
				format(spattern, "max =", _fitness.getMax()) +
				"+---------------------------------------------------------------------------+";
		}
	}

	private static final class Num<N extends Number & Comparable<? super N>>
		extends EvolutionStatistics<N, DoubleMomentStatistics>
	{
		private Num() {
			_fitness = new DoubleMomentStatistics();
		}

		@Override
		void accept(final Phenotype<?, N> pt, final long generation) {
			super.accept(pt, generation);
			_fitness.accept(pt.getFitness().doubleValue());
		}

		@Override
		public String toString() {
			return super.toString() +
				"+---------------------------------------------------------------------------+\n" +
				"|  Population statistics                                                    |\n" +
				"+---------------------------------------------------------------------------+\n" +
				format(cpattern, "Age:", p(_age)) +
				format(cpattern, "Fitness:", "") +
				format(spattern, "min  =", d(_fitness.getMin())) +
				format(spattern, "max  =", d(_fitness.getMax())) +
				format(spattern, "mean =", d(_fitness.getMean())) +
				format(spattern, "var  =", d(_fitness.getVariance())) +
				format(spattern, "std  =", d(sqrt(_fitness.getVariance()))) +
				"+---------------------------------------------------------------------------+";
		}

		private static String d(final double value) {
			return format("%3.12f", value);
		}
	}

	public static <C extends Comparable<? super C>>
	EvolutionStatistics<C, MinMax<C>> ofComparable() {
		return new Comp<>();
	}

	public static <N extends Number & Comparable<? super N>>
	EvolutionStatistics<N, DoubleMomentStatistics> ofNumber() {
		return new Num<>();
	}

}
