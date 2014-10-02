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

import org.jenetics.Gene;
import org.jenetics.stat.DoubleMomentStatistics;
import org.jenetics.stat.IntMomentStatistics;
import org.jenetics.stat.MinMax;

/**
 * This class can be used to gather additional statistic information of an
 * evolution process.
 *
 * [code]
 * final Engine&lt;DoubleGene, Double&gt; engine = ...
 * final EvolutionStatistics&lt;DoubleGene, Double&gt; statistics =
 *     new EvolutionStatistics&lt;&gt;();
 *
 * final Phenotype&lt;DoubleGene, Double&gt; result = engine.stream()
 *     .limit(100)
 *     .peek(statistics)
 *     .collect(toBestPhenotype());
 *
 * System.println(statistics);
 * [/code]
 *
 * <pre>
 +---------------------------------------------------------------------------+
 |  Time statistics                                                          |
 +---------------------------------------------------------------------------+
 |              Selection: Σ=0.031135653000 s, μ=0.000311356530 s            |
 |               Altering: Σ=0.176647042000 s, μ=0.001766470420 s            |
 |    Fitness calculation: Σ=0.046197010000 s, μ=0.000461970100 s            |
 |      Overall execution: Σ=0.269783151000 s, μ=0.002697831510 s            |
 +---------------------------------------------------------------------------+
 |  Evolution statistics                                                     |
 +---------------------------------------------------------------------------+
 |                Altered: Σ=18,766, μ=187.660000000                         |
 |                 Killed: Σ=0, μ=0.000000000                                |
 |               Invalids: Σ=0, μ=0.000000000                                |
 +---------------------------------------------------------------------------+
 |  Population statistics                                                    |
 +---------------------------------------------------------------------------+
 |                    Age: μ=1.892140, s²=7.063327                           |
 |            Max fitness: 0.9381474996702884                                |
 |            Min fitness: -0.9381718976956661                               |
 +---------------------------------------------------------------------------+
 * </pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-10-03 $</em>
 */
public class EvolutionStatistics<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Consumer<EvolutionResult<G, C>>
{

	private final DoubleMomentStatistics
		_selectionDuration = new DoubleMomentStatistics();
	private final DoubleMomentStatistics
		_alterDuration = new DoubleMomentStatistics();
	private final DoubleMomentStatistics
		_evaluationDuration = new DoubleMomentStatistics();
	private final DoubleMomentStatistics
		_evolveDuration = new DoubleMomentStatistics();

	private final IntMomentStatistics _killed = new IntMomentStatistics();
	private final IntMomentStatistics _invalids = new IntMomentStatistics();
	private final IntMomentStatistics _altered = new IntMomentStatistics();

	private final IntMomentStatistics _phenotypeAge = new IntMomentStatistics();
	private final MinMax<C> _fitness = MinMax.of();

	@Override
	public void accept(final EvolutionResult<G, C> result) {
		_killed.accept(result.getKillCount());
		_invalids.accept(result.getInvalidCount());
		_altered.accept(result.getAlterCount());

		accept(result.getDurations());

		result.getPopulation()
			.forEach(pt -> {
				_phenotypeAge.accept(pt.getAge(result.getGeneration()));
				_fitness.accept(pt.getFitness());
			});
	}

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
		return _phenotypeAge;
	}

	/**
	 * Return the minimal and maximal fitness.
	 *
	 * @return minimal and maximal fitness
	 */
	public MinMax<C> getFitness() {
		return _fitness;
	}

	@Override
	public String toString() {
		final String pattern = "| %22s: %-50s|\n";
		final StringBuilder out = new StringBuilder();
		out.append("+---------------------------------------------------------------------------+\n");
		out.append("|  Time statistics                                                          |\n");
		out.append("+---------------------------------------------------------------------------+\n");
		out.append(format(pattern, "Selection", d(_selectionDuration)));
		out.append(format(pattern, "Altering", d(_alterDuration)));
		out.append(format(pattern, "Fitness calculation", d(_evaluationDuration)));
		out.append(format(pattern, "Overall execution", d(_evolveDuration)));
		out.append("+---------------------------------------------------------------------------+\n");
		out.append("|  Evolution statistics                                                     |\n");
		out.append("+---------------------------------------------------------------------------+\n");
		out.append(format(pattern, "Altered", i(_altered)));
		out.append(format(pattern, "Killed", i(_killed)));
		out.append(format(pattern, "Invalids", i(_invalids)));
		out.append("+---------------------------------------------------------------------------+\n");
		out.append("|  Population statistics                                                    |\n");
		out.append("+---------------------------------------------------------------------------+\n");
		out.append(format(pattern, "Age", p(_phenotypeAge)));
		out.append(format(pattern, "Max fitness", _fitness.getMax()));
		out.append(format(pattern, "Min fitness", _fitness.getMin()));
		out.append("+---------------------------------------------------------------------------+\n");
		return out.toString();
	}

	private static String d(final DoubleMomentStatistics statistics) {
		return format(
			"Σ=%3.12f s, μ=%3.12f s",
			statistics.getSum(), statistics.getMean()
		);
	}

	private static String i(final IntMomentStatistics statistics) {
		final NumberFormat nf = NumberFormat.getIntegerInstance();
		return format(
			"Σ=%s, μ=%6.9f",
			nf.format(statistics.getSum()), statistics.getMean()
		);
	}

	private static String p(final IntMomentStatistics statistics) {
		final NumberFormat nf = NumberFormat.getIntegerInstance();
		return format(
			"μ=%6.6f, s²=%6.6f",
			statistics.getMean(), statistics.getVariance()
		);
	}

}
