import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static io.jenetics.engine.EvolutionResult.toBestPhenotype;
import static io.jenetics.engine.Limits.bySteadyFitness;

import io.jenetics.DoubleGene;
import io.jenetics.MeanAlterer;
import io.jenetics.Mutator;
import io.jenetics.Optimize;
import io.jenetics.Phenotype;
import io.jenetics.engine.Codecs;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.util.DoubleRange;

public class RastriginFunction {
	private static final double A = 10;
	private static final double R = 5.12;
	private static final int N = 2;

	private static double fitness(final double[] x) {
		double value = A*N;
		for (int i = 0; i < N; ++i) {
			value += x[i]*x[i] - A*cos(2.0*PI*x[i]);
		}

		return value;
	}

	public static void main(final String[] args) {
		final Engine<DoubleGene, Double> engine = Engine
			.builder(
				RastriginFunction::fitness,
				// Codec for 'x' vector.
				Codecs.ofVector(new DoubleRange(-R, R), N))
			.populationSize(500)
			.optimize(Optimize.MINIMUM)
			.alterers(
				new Mutator<>(0.03),
				new MeanAlterer<>(0.6))
			.build();

		final EvolutionStatistics<Double, ?>
			statistics = EvolutionStatistics.ofNumber();

		final Phenotype<DoubleGene, Double> best = engine.stream()
			.limit(bySteadyFitness(7))
			.peek(statistics)
			.collect(toBestPhenotype());

		System.out.println(statistics);
		System.out.println(best);
	}
}
