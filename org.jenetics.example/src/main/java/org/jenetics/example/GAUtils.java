/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.example;

import static java.lang.String.format;

import org.jenetics.GeneticAlgorithm;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 1.0 &mdash; <em>$Date: 2012-11-06 $</em>
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
