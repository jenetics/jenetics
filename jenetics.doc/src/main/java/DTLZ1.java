import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.pow;

import io.jenetics.DoubleGene;
import io.jenetics.Mutator;
import io.jenetics.Phenotype;
import io.jenetics.TournamentSelector;
import io.jenetics.engine.Codecs;
import io.jenetics.engine.Engine;
import io.jenetics.engine.Problem;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;

import io.jenetics.ext.SimulatedBinaryCrossover;
import io.jenetics.ext.moea.MOEA;
import io.jenetics.ext.moea.NSGA2Selector;
import io.jenetics.ext.moea.Vec;

public class DTLZ1 {
	static final int VARIABLES = 4;
	static final int OBJECTIVES = 3;
	static final int K = VARIABLES - OBJECTIVES + 1;

	static final Problem<double[], DoubleGene, Vec<double[]>>
	PROBLEM = Problem.of(
		DTLZ1::f,
		Codecs.ofVector(DoubleRange.of(0, 1.0), VARIABLES)
	);

	static Vec<double[]> f(final double[] x) {
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

	static final Engine<DoubleGene, Vec<double[]>> ENGINE =
		Engine.builder(PROBLEM)
			.populationSize(100)
			.alterers(
				new SimulatedBinaryCrossover<>(1),
				new Mutator<>(1.0/VARIABLES))
			.offspringSelector(new TournamentSelector<>(5))
			.survivorsSelector(NSGA2Selector.ofVec(OBJECTIVES))
			.minimizing()
			.build();

	public static void main(final String[] args) {
		final ISeq<Vec<double[]>> front = ENGINE.stream()
			.limit(2500)
			.collect(MOEA.toParetoSet(IntRange.of(1000, 1100), OBJECTIVES))
			.map(Phenotype::fitness);
	}

}
