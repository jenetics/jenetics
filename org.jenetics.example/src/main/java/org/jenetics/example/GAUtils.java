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
package org.jenetics.example;

import static java.lang.String.format;

import org.jenetics.GeneticAlgorithm;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0 &mdash; <em>$Date$</em>
 */
final class GAUtils {

	private GAUtils() {
	}

	public static void execute(
		final GeneticAlgorithm<?, ?> ga,
		final int generations,
		final int printEveryNthElement
	) {
		ga.setup();
		System.out.println(ga);
		for (int i = 1; i < generations; ++i) {
			ga.evolve();
			if (i%printEveryNthElement == (printEveryNthElement - 1)) {
				System.out.println(ga);
				System.out.flush();
			}
		}

		System.out.println(ga.getTimeStatistics());
		System.out.println(ga.getBestStatistics());
		System.out.flush();
	}

	public static void printConfig(
		final String name,
		final GeneticAlgorithm<?, ?> ga,
		final int generations,
		final Object... alterers
	) {
		final StringBuilder out = new StringBuilder();
		final String spattern = "| %18s: %-36s|\n";
		final String sspattern = "| %12s %-43s|\n";
		final String ipattern = "| %18s: %-36d|\n";

		out.append("+---------------------------------------------------------+\n");
		out.append(format(spattern, "GA", name));
		out.append("+---------------------------------------------------------+\n");
		out.append(format(spattern, "FitnessFunction", ga.getFitnessFunction()));
		out.append(format(ipattern, "Population", ga.getPopulationSize()));
		out.append(format(ipattern, "Generations", generations));
		out.append(format(spattern, "Alterers", ""));
		for (Object alterer : alterers) {
			out.append(format(sspattern, "*", alterer));
		}
		out.append("+---------------------------------------------------------+\n");


		System.out.print(out);
	}

}
