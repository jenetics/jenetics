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

import static java.lang.String.format;

import java.text.NumberFormat;
import java.time.Duration;
import java.util.function.Consumer;

import org.jenetics.Phenotype;
import org.jenetics.stat.DoubleMomentStatistics;
import org.jenetics.stat.IntMomentStatistics;
import org.jenetics.stat.MinMax;

/**
 * This class can be used to gather additional statistic information of an
 * evolution process. The additional information can be useful during the
 * development phase of the GA or while testing the GA's performance. The
 * following example shows how to integrate the <i>statistics</i> object into
 * your evolution <i>stream</i>.
 *
 * [code]
 * final Engine&lt;DoubleGene, Double&gt; engine = ...
 * final EvolutionStatistics&lt;Double, DoubleMomentStatistics&gt; statistics =
 *     EvolutionStatistics.ofNumber();
 *
 * final Phenotype&lt;DoubleGene, Double&gt; result = engine.stream()
 *     .limit(bySteadyFitness(7))
 *     .limit(100)
 *     .peek(statistics)
 *     .collect(toBestPhenotype());
 *
 * System.println(statistics);
 * [/code]
 *
 * <b>Example output</b>
 *
 * [code]
 +---------------------------------------------------------------------------+
 |  Time statistics                                                          |
 +---------------------------------------------------------------------------+
 |              Selection: Σ=0.022079809000 s; μ=0.001051419476 s            |
 |               Altering: Σ=0.077028587000 s; μ=0.003668027952 s            |
 |    Fitness calculation: Σ=0.009673436000 s; μ=0.000460639810 s            |
 |      Overall execution: Σ=0.113376443000 s; μ=0.005398878238 s            |
 +---------------------------------------------------------------------------+
 |  Evolution statistics                                                     |
 +---------------------------------------------------------------------------+
 |            Generations: 21                                                |
 |                Altered: Σ=3,937; μ=187.476190476                          |
 |                 Killed: Σ=0; μ=0.000000000                                |
 |               Invalids: Σ=0; μ=0.000000000                                |
 +---------------------------------------------------------------------------+
 |  Population statistics                                                    |
 +---------------------------------------------------------------------------+
 |                    Age: ∨=7; μ=1.084000; s²=1.839595                      |
 |                Fitness:                                                   |
 |                      ∧: -0.938171897696                                   |
 |                      ∨: 0.912073050166                                    |
 |                      μ: -0.904772655473                                   |
 |                     s²: 0.021677650988                                    |
 +---------------------------------------------------------------------------+
 * [/code]
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-10-21 $</em>
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
	final IntMomentStatistics _age = new IntMomentStatistics();
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

	void accept(final Phenotype<?, C> pt, final int generation) {
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
	public IntMomentStatistics getPhenotypeAge() {
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

	final String pattern = "| %22s: %-50s|\n";

	@Override
	public String toString() {
		return
		"+---------------------------------------------------------------------------+\n" +
		"|  Time statistics                                                          |\n" +
		"+---------------------------------------------------------------------------+\n" +
		format(pattern, "Selection", d(_selectionDuration)) +
		format(pattern, "Altering", d(_alterDuration)) +
		format(pattern, "Fitness calculation", d(_evaluationDuration)) +
		format(pattern, "Overall execution", d(_evolveDuration)) +
		"+---------------------------------------------------------------------------+\n" +
		"|  Evolution statistics                                                     |\n" +
		"+---------------------------------------------------------------------------+\n" +
		format(pattern, "Generations", i(_altered.getCount())) +
		format(pattern, "Altered", i(_altered)) +
		format(pattern, "Killed", i(_killed)) +
		format(pattern, "Invalids", i(_invalids));
	}

	private static String d(final DoubleMomentStatistics statistics) {
		return format(
			"Σ=%3.12f s; μ=%3.12f s",
			statistics.getSum(), statistics.getMean()
		);
	}

	private static String i(final IntMomentStatistics statistics) {
		final NumberFormat nf = NumberFormat.getIntegerInstance();
		return format(
			"Σ=%s; μ=%6.9f",
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
			"∨=%s; μ=%6.6f; s²=%6.6f",
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
		void accept(final Phenotype<?, C> pt, final int generation) {
			super.accept(pt, generation);
			_fitness.accept(pt.getFitness());
		}

		@Override
		public String toString() {
			return super.toString() +
			"+---------------------------------------------------------------------------+\n" +
			"|  Population statistics                                                    |\n" +
			"+---------------------------------------------------------------------------+\n" +
			format(pattern, "Age", p(_age)) +
			format(pattern, "Fitness", "") +
			format(pattern, "∧", _fitness.getMin()) +
			format(pattern, "∨", _fitness.getMax()) +
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
		void accept(final Phenotype<?, N> pt, final int generation) {
			super.accept(pt, generation);
			_fitness.accept(pt.getFitness().doubleValue());
		}

		@Override
		public String toString() {
			return super.toString() +
			"+---------------------------------------------------------------------------+\n" +
			"|  Population statistics                                                    |\n" +
			"+---------------------------------------------------------------------------+\n" +
			format(pattern, "Age", p(_age)) +
			format(pattern, "Fitness", "") +
			format(pattern, "∧", d(_fitness.getMin())) +
			format(pattern, "∨", d(_fitness.getMax())) +
			format(pattern, "μ", d(_fitness.getMean())) +
			format(pattern, "s²", d(_fitness.getVariance())) +
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
