import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.pow;

import io.jenetics.DoubleGene;
import io.jenetics.MeanAlterer;
import io.jenetics.Mutator;
import io.jenetics.Phenotype;
import io.jenetics.engine.Codecs;
import io.jenetics.engine.Engine;
import io.jenetics.engine.Problem;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;

import io.jenetics.ext.moea.MOEA;
import io.jenetics.ext.moea.UFTournamentSelector;
import io.jenetics.ext.moea.Vec;

public class DTLZ1 {
	private static final int VARIABLES = 7;
	private static final int OBJECTIVES = 3;

	private static final
	Problem<double[], DoubleGene, Vec<double[]>>
		PROBLEM = Problem.of(
			DTLZ1::f,
			Codecs.ofVector(DoubleRange.of(0, 1.0), VARIABLES)
		);

	private static Vec<double[]> f(final double[] x) {
		final int k = VARIABLES - OBJECTIVES + 1;

		double g = 0.0;
		for (int i = VARIABLES - k; i < VARIABLES; i++) {
			g += pow(x[i] - 0.5, 2.0) - cos(20.0*PI*(x[i] - 0.5));
		}
		g = 100.0*(k + g);

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

	public static void main(final String[] args) {
		final Engine<DoubleGene, Vec<double[]>> engine =
			Engine.builder(PROBLEM)
				.alterers(
					new Mutator<>(0.1),
					new MeanAlterer<>())
				.selector(UFTournamentSelector.vec())
				.minimizing()
				.build();

		final ISeq<Vec<double[]>> front = engine.stream()
			.limit(4000)
			.collect(MOEA.toParetoSet(IntRange.of(750, 800)))
			.map(Phenotype::getFitness);
	}

}
