import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.jenetics.DoubleGene;
import io.jenetics.MeanAlterer;
import io.jenetics.Mutator;
import io.jenetics.Phenotype;
import io.jenetics.engine.Codecs;
import io.jenetics.engine.Engine;
import io.jenetics.engine.Problem;
import io.jenetics.tool.trial.Gnuplot;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;

import io.jenetics.ext.moea.MOEA;
import io.jenetics.ext.moea.UFTournamentSelector;
import io.jenetics.ext.moea.Vec;

public class DTLZ1 {

	private static final int VARIABLES = 3;
	private static final int OBJECTIVES = 3;

	private static final Problem<double[], DoubleGene, Vec<double[]>>
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

	private static double g(final double[] x) {
		double sum = 0;
		for (int i = 1; i < x.length; ++i) {
			sum += (x[i] - 0.5)*(x[i] - 0.5);
			sum -= cos(20*PI*(x[i] - 0.5));
		}

		return 100*(sqrt(x[0]*x[0] + x[1]*x[1] + x[2]*x[2]) + sum);
	}

	public static void main(final String[] args) throws IOException {
		final String base = "/home/fwilhelm/Workspace/Development/Projects/" +
			"Jenetics/jenetics.doc/src/main/resources/diagram";

		final Path data = Paths.get(base, "dtlz1.dat");
		final Path output = Paths.get(base, "dtlz1.svg");

		final Engine<DoubleGene, Vec<double[]>> engine = Engine.builder(PROBLEM)
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


		final StringBuilder out = new StringBuilder();
		out.append("#x y\n");
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
