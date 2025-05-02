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

import io.jenetics.DoubleGene;
import io.jenetics.MeanAlterer;
import io.jenetics.Mutator;
import io.jenetics.Phenotype;
import io.jenetics.TournamentSelector;
import io.jenetics.engine.Codecs;
import io.jenetics.engine.Engine;
import io.jenetics.engine.Limits;
import io.jenetics.engine.Problem;
import io.jenetics.tool.trial.Gnuplot;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;

import io.jenetics.ext.moea.MOEA;
import io.jenetics.ext.moea.UFTournamentSelector;
import io.jenetics.ext.moea.Vec;
import io.jenetics.ext.moea.VecFactory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.1
 * @since 4.1
 */
public class CircleMaxFront {

	static final VecFactory<double[]> FACTORY = VecFactory.ofDoubleVec(
		//Optimize.MINIMUM, Optimize.MAXIMUM
	);

	static final Problem<double[], DoubleGene, Vec<double[]>> PROBLEM = Problem.of(
		v -> FACTORY.newVec(new double[]{v[0]*cos(v[1]) + 1, v[0]*sin(v[1]) + 1}),
		Codecs.ofVector(
			new DoubleRange(0, 1),
			new DoubleRange(0, 2*PI)
		)
	);

	public static void main(final String[] args) throws IOException {
		final String base = "/home/fwilhelm/Workspace/Development/Projects/" +
			"Jenetics/jenetics.tool/src/main/resources/io/jenetics/tool/moea";

		final Path data = Paths.get(base, "circle_max_front.dat");
		final Path output = Paths.get(base, "circle_max_front.svg");

		final Engine<DoubleGene, Vec<double[]>> engine = Engine.builder(PROBLEM)
			.alterers(
				new Mutator<>(0.1),
				new MeanAlterer<>())
			.offspringSelector(new TournamentSelector<>(3))
			.survivorsSelector(UFTournamentSelector.ofVec())
			.build();

		final ISeq<Phenotype<DoubleGene, Vec<double[]>>> front = engine.stream()
			.limit(Limits.byFixedGeneration(100))
			.collect(MOEA.toParetoSet(new IntRange(100, 150)));

		final StringBuilder out = new StringBuilder();
		out.append("#x y\n");
		front.forEach(p -> {
			out.append(p.fitness().data()[0]);
			out.append(" ");
			out.append(p.fitness().data()[1]);
			out.append("\n");
		});

		Files.write(data, out.toString().getBytes());
		final Gnuplot gnuplot = new Gnuplot(Paths.get(base, "circle_points.gp"));
		gnuplot.create(data, output);
	}

}
