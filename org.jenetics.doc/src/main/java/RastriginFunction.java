import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static org.jenetics.engine.EvolutionResult.toBestPhenotype;
import static org.jenetics.engine.limit.bySteadyFitness;

import org.jenetics.DoubleGene;
import org.jenetics.MeanAlterer;
import org.jenetics.Mutator;
import org.jenetics.Optimize;
import org.jenetics.Phenotype;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionStatistics;
import org.jenetics.engine.codecs;
import org.jenetics.util.DoubleRange;

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
				codecs.ofVector(DoubleRange.of(-R, R), N))
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
