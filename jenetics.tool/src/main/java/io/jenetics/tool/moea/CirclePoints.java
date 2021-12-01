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
package io.jenetics.tool.moea;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.random.RandomGenerator;
import java.util.stream.Stream;

import io.jenetics.DoubleGene;
import io.jenetics.engine.Codecs;
import io.jenetics.engine.Problem;
import io.jenetics.prngine.LCG64ShiftRandom;
import io.jenetics.tool.trial.Gnuplot;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.ISeq;

import io.jenetics.ext.moea.Vec;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.1
 * @since 4.1
 */
public class CirclePoints {

	static final Problem<double[], DoubleGene, Vec<double[]>> PROBLEM = Problem.of(
		v -> Vec.of(v[0]*cos(v[1]), v[0]*sin(v[1])),
		Codecs.ofVector(
			DoubleRange.of(0, 1),
			DoubleRange.of(0, 2*PI)
		)
	);

	public static void main(final String[] args) throws IOException {
		final String base = "/home/fwilhelm/Workspace/Development/Projects/" +
			"Jenetics/jenetics.tool/src/main/resources/io/jenetics/tool/moea";

		final Path data = Paths.get(base, "circle_points.dat");
		final Path output = Paths.get(base, "circle_points.svg");

		final var random = new LCG64ShiftRandom();

		final ISeq<Vec<double[]>> points = Stream.generate(() -> point(random))
			.limit(1000)
			.collect(ISeq.toISeq());

		final StringBuilder out = new StringBuilder();
		out.append("#x y\n");
		points.forEach(p -> {
			out.append(p.data()[0]);
			out.append(" ");
			out.append(p.data()[1]);
			out.append("\n");
		});

		Files.write(data, out.toString().getBytes());
		final Gnuplot gnuplot = new Gnuplot(Paths.get(base, "circle_points.gp"));
		gnuplot.create(data, output);
	}

	private static Vec<double[]> point(final RandomGenerator random) {
		final double r = random.nextDouble();
		final double a = random.nextDouble()*2*PI;

		return Vec.of(r*cos(a) + 1, r*sin(a) + 1);
	}

}
