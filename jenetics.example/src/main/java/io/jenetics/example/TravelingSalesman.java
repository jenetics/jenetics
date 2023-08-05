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
package io.jenetics.example;

import static java.lang.String.format;
import static java.lang.System.getProperty;
import static java.util.Objects.requireNonNull;
import static io.jenetics.engine.EvolutionResult.toBestPhenotype;
import static io.jenetics.jpx.Length.Unit.METER;

import java.io.IOException;
import java.util.function.Function;

import io.jenetics.EnumGene;
import io.jenetics.Optimize;
import io.jenetics.PartiallyMatchedCrossover;
import io.jenetics.Phenotype;
import io.jenetics.SwapMutator;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Codecs;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.engine.Problem;
import io.jenetics.util.ISeq;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.GPX.Reader;
import io.jenetics.jpx.GPX.Writer.Indent;
import io.jenetics.jpx.WayPoint;
import io.jenetics.jpx.geom.Geoid;

/**
 * Implementation of the Traveling Salesman Problem. This example tries to find
 * the shortest path, which visits all Austrian district capitals.
 *
 * @see DoubleGeneTravelingSalesman
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.1
 * @since 3.6
 */
public final class TravelingSalesman
	implements Problem<ISeq<WayPoint>, EnumGene<WayPoint>, Double>
{

	private final ISeq<WayPoint> _points;

	/**
	 * Create a new TSP instance with the way-points we want to visit.
	 *
	 * @param points the way-points we want to visit
	 * @throws NullPointerException if the given {@code points} seq is {@code null}
	 */
	public TravelingSalesman(final ISeq<WayPoint> points) {
		_points = requireNonNull(points);
	}

	@Override
	public Codec<ISeq<WayPoint>, EnumGene<WayPoint>> codec() {
		return Codecs.ofPermutation(_points);
	}

	@Override
	public Function<ISeq<WayPoint>, Double> fitness() {
		return way -> way.stream()
			.collect(Geoid.DEFAULT.toTourLength())
			.to(METER);
	}

	public static void main(String[] args) throws IOException {
		final var tsm = new TravelingSalesman(districtCapitals().subSeq(0, 10));

		final Engine<EnumGene<WayPoint>, Double> engine = Engine.builder(tsm)
			.optimize(Optimize.MINIMUM)
			.alterers(
				new SwapMutator<>(0.15),
				new PartiallyMatchedCrossover<>(0.15))
			.build();

		// Create evolution statistics consumer.
		final EvolutionStatistics<Double, ?>
			statistics = EvolutionStatistics.ofNumber();

		final Phenotype<EnumGene<WayPoint>, Double> best = engine.stream()
			.limit(1_000)
			.peek(statistics)
			.collect(toBestPhenotype());

		final ISeq<WayPoint> path = tsm.decode(best.genotype());

		final GPX gpx = GPX.builder()
			.addTrack(track -> track
				.name("Best Track")
				.addSegment(s -> s.points(path.asList())))
			.build();

		final double km = tsm.fitness(best.genotype())/1_000.0;
		GPX.Writer.of(new Indent("    "))
			.write(gpx, format("%s/out_%d.gpx", getProperty("user.home"), (int)km));

		System.out.println(statistics);
		System.out.println("Length: " + km);
	}

	// Return the district capitals, we want to visit.
	static ISeq<WayPoint> districtCapitals() throws IOException {
		final String capitals = "/io/jenetics/example/DistrictCapitals.gpx";
		try (var in = TravelingSalesman.class.getResourceAsStream(capitals)) {
			return ISeq.of(Reader.DEFAULT.read(in).getWayPoints());
		}
	}

}
