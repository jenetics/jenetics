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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.jenetics.Phenotype;
import io.jenetics.tool.trial.Gnuplot;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;

import io.jenetics.ext.moea.MOEA;
import io.jenetics.ext.moea.Vec;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.1
 * @since 4.1
 */
public class DTLZ1Diagram {

	public static void main(final String[] args) throws IOException {
		final String base = "/home/fwilhelm/Workspace/Development/Projects/" +
			"Jenetics/jenetics.doc/src/main/resources/diagram";

		final Path data = Paths.get(base, "dtlz1.dat");
		final Path output = Paths.get(base, "dtlz1.svg");

		final ISeq<Vec<double[]>> front = DTLZ1.ENGINE.stream()
			.limit(3500)
			.collect(MOEA.toParetoSet(IntRange.of(1000, 1100), DTLZ1.OBJECTIVES))
			.map(Phenotype::fitness);

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
