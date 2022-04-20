package io.jenetics.example;

import static java.lang.String.format;
import static java.lang.System.getProperty;
import static java.util.Objects.requireNonNull;
import static io.jenetics.engine.EvolutionResult.toBestPhenotype;
import static io.jenetics.example.TravelingSalesman.districtCapitals;
import static io.jenetics.jpx.Length.Unit.METER;

import java.io.IOException;
import java.util.function.Function;
import java.util.stream.IntStream;

import io.jenetics.DoubleChromosome;
import io.jenetics.DoubleGene;
import io.jenetics.Genotype;
import io.jenetics.MeanAlterer;
import io.jenetics.Optimize;
import io.jenetics.Phenotype;
import io.jenetics.SwapMutator;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.engine.Problem;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.ISeq;
import io.jenetics.util.ProxySorter;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.GPX.Writer.Indent;
import io.jenetics.jpx.WayPoint;
import io.jenetics.jpx.geom.Geoid;

/**
 * Implementation of the Traveling Salesman Problem. This example tries to find
 * the shortest path, which visits all Austrian district capitals. The difference
 * to the {@link TravelingSalesman} example is, that a {@code Genotype<DoubleGene>}
 * is used for encoding the problem.
 *
 * @see TravelingSalesman
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.1
 * @since 7.1
 */
public class DoubleGeneTravelingSalesman
	implements Problem<ISeq<WayPoint>, DoubleGene, Double>
{
	private final ISeq<WayPoint> _points;

	/**
	 * Create a new TSP instance with the way-points we want to visit.
	 *
	 * @param points the way-points we want to visit
	 * @throws NullPointerException if the given {@code points} seq is {@code null}
	 */
	public DoubleGeneTravelingSalesman(final ISeq<WayPoint> points) {
		_points = requireNonNull(points);
	}

	@Override
	public Codec<ISeq<WayPoint>, DoubleGene> codec() {
		return Codec.of(
			Genotype.of(
				DoubleChromosome.of(DoubleRange.of(0, 1), _points.length())
			),
			gt -> {
				// Use the sorted indexes as path permutations.
				final int[] path = ProxySorter.sort(
					gt.get(0)
						.as(DoubleChromosome.class)
						.toArray()
				);

				return IntStream.of(path)
					.mapToObj(_points)
					.collect(ISeq.toISeq());
			}
		);
	}

	@Override
	public Function<ISeq<WayPoint>, Double> fitness() {
		return way -> way.stream()
			.collect(Geoid.DEFAULT.toTourLength())
			.to(METER);
	}

	public static void main(String[] args) throws IOException {
		final var tsm = new DoubleGeneTravelingSalesman(
			districtCapitals().subSeq(0, 10)
		);

		final Engine<DoubleGene, Double> engine = Engine.builder(tsm)
			.optimize(Optimize.MINIMUM)
			.alterers(
				new SwapMutator<>(0.15),
				new MeanAlterer<>(0.15))
			.build();

		// Create evolution statistics consumer.
		final EvolutionStatistics<Double, ?>
			statistics = EvolutionStatistics.ofNumber();

		final Phenotype<DoubleGene, Double> best = engine.stream()
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

}
