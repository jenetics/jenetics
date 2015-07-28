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
package org.jenetics;

import static java.lang.String.format;
import static org.jenetics.util.RandomRegistry.using;

import java.io.PrintStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.stat.Histogram;
import org.jenetics.util.Factory;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.ObjectTester;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public abstract class SelectorTester<S extends Selector<DoubleGene, Double>>
	extends ObjectTester<S>
{

	public static final int CLASS_COUNT = 25;
	public static final double MIN = 0.0;
	public static final double MAX = 1_000.0;
	public static final double SELECTION_FRACTION = 2.0;
	public static final int POPULATION_COUNT = (int)(CLASS_COUNT*10*SELECTION_FRACTION);

	protected S selector() {
		return factory().newInstance();
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void selectNegativeCountArgument() {
		final Factory<Genotype<DoubleGene>> gtf =
			Genotype.of(new DoubleChromosome(0.0, 1.0));

		final Population<DoubleGene, Double> population =
			new Population<>(2);

		for (int i = 0, n = 2; i < n; ++i) {
			population.add(Phenotype.of(gtf.newInstance(), 12, TestUtils.FF));
		}

		selector().select(population, -1, Optimize.MAXIMUM);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void selectNullPopulationArgument() {
		selector().select(null, 23, Optimize.MAXIMUM);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void selectNullOptimizeArgument() {
		final Factory<Genotype<DoubleGene>> gtf =
			Genotype.of(new DoubleChromosome(0.0, 1.0));

		final Population<DoubleGene, Double> population =
			new Population<>(2);

		for (int i = 0, n = 2; i < n; ++i) {
			population.add(Phenotype.of(gtf.newInstance(), 12, TestUtils.FF));
		}

		selector().select(population, 1, null);
	}

	@Test(dataProvider = "selectionPerformanceParameters")
	public void selectionPerformance(
		final Integer size,
		final Integer count,
		final Optimize opt
	) {
		final Function<Genotype<DoubleGene>, Double> ff =
			g -> g.getGene().getAllele();

		final Factory<Phenotype<DoubleGene, Double>> ptf = () ->
			Phenotype.of(Genotype.of(DoubleChromosome.of(0.0, 100.0)), 1, ff);

		using(new LCG64ShiftRandom(543455), r -> {
			final Population<DoubleGene, Double> population = IntStream.range(0, size)
				.mapToObj(i -> ptf.newInstance())
				.collect(Population.toPopulation());

			final Selector<DoubleGene, Double> selector = selector();

			if (!(selector instanceof MonteCarloSelector<?, ?>)) {
				final double monteCarloSelectionSum =
					new MonteCarloSelector<DoubleGene, Double>()
						.select(population, count, opt).stream()
						.mapToDouble(Phenotype::getFitness)
						.sum();

				final double selectionSum =
					selector
						.select(population, count, opt).stream()
						.mapToDouble(Phenotype::getFitness)
						.sum();

				if (opt == Optimize.MAXIMUM) {
					Assert.assertTrue(
						selectionSum > monteCarloSelectionSum,
						format("%f <= %f", selectionSum, monteCarloSelectionSum)
					);
				} else {
					Assert.assertTrue(
						selectionSum < monteCarloSelectionSum,
						format("%f >= %f", selectionSum, monteCarloSelectionSum)
					);
				}
			}
		});
	}

	@DataProvider(name = "selectionPerformanceParameters")
	public Object[][] selectionPerformanceParameters() {
		return new Object[][] {
			{200, 100, Optimize.MAXIMUM},
			{2000, 1000, Optimize.MAXIMUM},
			{200, 100, Optimize.MINIMUM},
			{2000, 1000, Optimize.MINIMUM}
		};
	}

	@Test(dataProvider = "selectParameters")
	public void select(final Integer size, final Integer count, final Optimize opt) {
		final Function<Genotype<DoubleGene>, Double> ff =
			gt -> gt.getGene().getAllele();

		final Factory<Phenotype<DoubleGene, Double>> ptf = () ->
			Phenotype.of(Genotype.of(DoubleChromosome.of(0.0, 1_000.0)), 1, ff);

		final Population<DoubleGene, Double> population = IntStream.range(0, size)
			.mapToObj(i -> ptf.newInstance())
			.collect(Population.toPopulation());

		final Population<DoubleGene, Double> selection =
			selector().select(population, count, opt);

		if (size == 0) {
			Assert.assertEquals( selection.size(), 0 );
		} else {
			Assert.assertEquals(selection.size(), count.intValue());
		}
		for (Phenotype<DoubleGene, Double> pt : selection) {
			Assert.assertTrue(
				population.contains(pt),
				format("Population doesn't contain %s.", pt)
			);
		}
	}


	@DataProvider(name = "selectParameters")
	public Object[][] selectParameters() {
		final List<Integer> sizes = Arrays.asList(0, 1, 2, 3, 5, 11, 50, 100, 10_000);
		final List<Integer> counts = Arrays.asList(0, 1, 2, 3, 5, 11, 50, 100, 10_000);

		final List<Object[]> result = new ArrayList<>();
		for (Integer size : sizes) {
			for (Integer count : counts) {
				result.add(new Object[]{size, count, Optimize.MINIMUM});
				result.add(new Object[]{size, count, Optimize.MAXIMUM});
			}
		}

		return result.toArray(new Object[0][]);
	}



	/* *************************************************************************
	 * Distribution creation code.
	 ***************************************************************************/

	/**
	 * Print the distributions, each for every parameter.
	 *
	 * @param writer the print writer where the histograms are printed
	 * @param parameters the selector creation parameters
	 * @param selector the selector factory
	 * @param opt the optimization strategy
	 * @param populationCount the number of individuals for the test population
	 * @param loops the number of selections performed for one population
	 * @param <P> the parameter type
	 */
	public static <P> void printDistributions(
		final PrintStream writer,
		final List<P> parameters,
		final Function<P, Selector<DoubleGene, Double>> selector,
		final Optimize opt,
		final int populationCount,
		final int loops
	) {
		final List<Histogram<Double>> histograms = distributions(
			parameters,
			selector,
			opt,
			populationCount,
			loops
		);

		final List<Selector<?, ?>> selectors = parameters.stream()
			.map(selector)
			.collect(Collectors.toList());

		print(writer, opt, selectors, parameters, histograms, populationCount, loops);
	}

	/**
	 * Create a list of distribution, each for every parameter.
	 *
	 * @param parameters the selector creation parameters
	 * @param selector the selector factory
	 * @param opt the optimization strategy
	 * @param populationCount the number of individuals for the test population
	 * @param loops the number of selections performed for one population
	 * @param <P> the parameter type
	 * @return the selector distributions
	 */
	public static <P> List<Histogram<Double>> distributions(
		final List<P> parameters,
		final Function<P, Selector<DoubleGene, Double>> selector,
		final Optimize opt,
		final int populationCount,
		final int loops
	) {

		return parameters.stream()
			.map(p -> distribution(selector.apply(p), opt, populationCount, loops))
			.collect(Collectors.toList());
	}


	/**
	 * Create a selection histogram (distribution) for the given selector and
	 * with the given parameters.
	 *
	 * @param selector the selector for which to determine the distribution
	 * @param opt the selectors optimization strategy
	 * @param populationCount the number of in used for determining the
	 *        selector distribution.
	 * @param loops the number of selections performed for one population
	 * @return the selectors selection distribution
	 */
	public static Histogram<Double> distribution(
		final Selector<DoubleGene, Double> selector,
		final Optimize opt,
		final int populationCount,
		final int loops
	) {
		final Function<Genotype<DoubleGene>, Double> ff =
			gt -> gt.getGene().getAllele();

		final Factory<Phenotype<DoubleGene, Double>> ptf = () ->
			Phenotype.of(Genotype.of(DoubleChromosome.of(MIN, MAX)), 1, ff);

		return IntStream.range(0, loops).parallel().mapToObj(j -> {
			final Histogram<Double> hist = Histogram.ofDouble(MIN, MAX, CLASS_COUNT);

			final Population<DoubleGene, Double> population =
				IntStream.range(0, populationCount)
					.mapToObj(i -> ptf.newInstance())
					.collect(Population.toPopulation());

			final int selectionCount = (int)(populationCount/SELECTION_FRACTION);
			selector.select(population, selectionCount, opt).stream()
				.map(pt -> pt.getGenotype().getGene().getAllele())
				.forEach(hist::accept);

			return hist;
		}).collect(Histogram.toDoubleHistogram(MIN, MAX, CLASS_COUNT));
	}

	/**
	 * Print the list of histogram as CSV to the given writer.
	 *
	 * @param writer the print writer where the histograms are printed
	 * @param parameters the selector creation parameters
	 * @param histograms the histograms to print
	 */
	private static void print(
		final PrintStream writer,
		final Optimize opt,
		final List<Selector<?, ?>> selectors,
		final List<?> parameters,
		final List<Histogram<Double>> histograms,
		final int populationCount,
		final int loops
	) {
		final NumberFormat format = NumberFormat.getNumberInstance(Locale.ENGLISH);
		format.setMinimumFractionDigits(15);
		format.setMaximumFractionDigits(15);

		printt(writer,
			"Creating selector distribution: %s",
			new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())
		);
		printv(writer);

		println(writer, "# %-76s#", format("Selector distributions (opt=%s, npop=%d, loops=%d):", opt, populationCount, loops));
		for (Selector<?, ?> selector : selectors) {
			println(writer, "# %-76s#", format("   - %s", selector));
		}
		println(writer, "#=============================================================================#");

		final String header = parameters.stream()
			.map(Objects::toString)
			.collect(Collectors.joining(",", "", ""));

		writer.println();
		writer.println(header);

		final double[][] array = histograms.stream()
			.map(Histogram::getNormalizedHistogram)
			.toArray(double[][]::new);

		for (int i = 0; i < array[0].length; ++i) {
			for (int j = 0; j < array.length; ++j) {
				writer.print(format.format(array[j][i]));
				if (j < array.length - 1) {
					writer.print(',');
				}
			}
			writer.println();
		}
	}

	private static void printt(final PrintStream writer, final String title, final Object... args) {
		println(writer, "#=============================================================================#");
		println(writer, "# %-76s#", format(title, args));
		println(writer, "#=============================================================================#");
	}

	private static void printv(final PrintStream writer) {
		println(writer, "#=============================================================================#");
		println(writer,
			"# %-76s#",
			format("%s %s (%s) ", p("os.name"), p("os.version"), p("os.arch"))
		);
		println(writer,
			"# %-76s#",
			format("java version \"%s\"", p("java.version"))
		);
		println(writer,
			"# %-76s#",
			format("%s (build %s)", p("java.runtime.name"), p("java.runtime.version"))
		);
		println(writer,
			"# %-76s#",
			format("%s (build %s)", p("java.vm.name"), p("java.vm.version"))
		);
		println(writer,"#=============================================================================#");
	}

	private static String p(final String name) {
		return System.getProperty(name);
	}

	private static void println(final PrintStream writer, final String pattern, final Object... args) {
		writer.println(format(pattern, args));
	}
}
