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

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.pow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.jenetics.DoubleGene;
import io.jenetics.MeanAlterer;
import io.jenetics.Mutator;
import io.jenetics.Phenotype;
import io.jenetics.SwapMutator;
import io.jenetics.TournamentSelector;
import io.jenetics.engine.Codecs;
import io.jenetics.engine.Engine;
import io.jenetics.engine.Problem;
import io.jenetics.tool.trial.Gnuplot;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;

import io.jenetics.ext.moea.MOEA;
import io.jenetics.ext.moea.NSGA2Selector;
import io.jenetics.ext.moea.UFTournamentSelector;
import io.jenetics.ext.moea.Vec;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class DTLZ1Diagram {

	private static final int VARIABLES = 7;
	private static final int OBJECTIVES = 3;
	private static final int K = VARIABLES - OBJECTIVES + 1;

	private static final
	Problem<double[], DoubleGene, Vec<double[]>>
		PROBLEM = Problem.of(
		DTLZ1Diagram::f,
		Codecs.ofVector(DoubleRange.of(0, 1.0), VARIABLES)
	);

	private static Vec<double[]> f(final double[] x) {
		double g = 0.0;
		for (int i = VARIABLES - K; i < VARIABLES; i++) {
			g += pow(x[i] - 0.5, 2.0) - cos(20.0*PI*(x[i] - 0.5));
		}
		g = 100.0*(K + g);

		final double[] f = new double[OBJECTIVES];
		for (int i = 0; i < OBJECTIVES; ++i) {
			f[i] = 0.5 * (1.0 + g);
			for (int j = 0; j < OBJECTIVES - i - 1; ++j) {
				f[i] *= x[j];
			}
			if (i != 0) {
				f[i] *= 1 - x[OBJECTIVES - i - 1];
			}
		}

		return Vec.of(f);
	}

	public static void main(final String[] args) throws IOException {
		final String base = "/home/fwilhelm/Workspace/Development/Projects/" +
			"Jenetics/jenetics.doc/src/main/resources/diagram";

		final Path data = Paths.get(base, "dtlz1.dat");
		final Path output = Paths.get(base, "dtlz1.svg");

		final Engine<DoubleGene, Vec<double[]>> engine =
			Engine.builder(PROBLEM)
				.alterers(
					new Mutator<>(0.1),
					new MeanAlterer<>())
				//.offspringSelector(NSGA2Selector.vec())
				.selector(new TournamentSelector<>(5))
				.minimizing()
				.build();

		final ISeq<Vec<double[]>> front = engine.stream()
			.limit(1_000_000)
			.collect(MOEA.toParetoSet(IntRange.of(1000, 1100)))
			.map(Phenotype::getFitness);

		System.out.println(front.size());

		final StringBuilder out = new StringBuilder();
		front.forEach(p -> {
			out.append(p.data()[0]);
			out.append(" ");
			out.append(p.data()[1]);
			out.append(" ");
			out.append(p.data()[2]);
			out.append("\n");
		});

		Files.write(data, out.toString().getBytes());
		final Gnuplot gnuplot = new Gnuplot(Paths.get(base, "dtlz1.gp"));
		gnuplot.create(data, output);
	}
}
