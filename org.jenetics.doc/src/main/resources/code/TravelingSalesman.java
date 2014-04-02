import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.sin;

import java.io.Serializable;

import org.jenetics.Chromosome;
import org.jenetics.EnumGene;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.NumberStatistics.Calculator;
import org.jenetics.Optimize;
import org.jenetics.PartiallyMatchedCrossover;
import org.jenetics.PermutationChromosome;
import org.jenetics.SwapMutator;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;


public class TravelingSalesman {

	private static class FF
		implements Function<Genotype<EnumGene<Integer>>, Double>,
					Serializable
	{
		private static final long serialVersionUID = 1L;

		private final double[][] adjacence;

		public FF(final double[][] adjacence) {
			this.adjacence = adjacence;
		}

		@Override
		public Double apply(Genotype<EnumGene<Integer>> gt) {
			final Chromosome<EnumGene<Integer>>
				path = gt.getChromosome();

			double length = 0.0;
			for (int i = 0, n = path.length(); i < n; ++i) {
				final int from = path.getGene(i).getAllele();
				final int to = path.getGene((i + 1)%n).getAllele();
				length += adjacence[from][to];
			}
			return length;
		}

		@Override
		public String toString() {
			return "Point distance";
		}
	}

	public static void main(String[] args) {
		final int stops = 20;

		final Function<Genotype<EnumGene<Integer>>, Double> ff =
			new FF(adjacencyMatrix(stops));
		final Factory<Genotype<EnumGene<Integer>>> gtf = Genotype.of(
			PermutationChromosome.ofInteger(stops)
		);
		final GeneticAlgorithm<EnumGene<Integer>, Double>
			ga = new GeneticAlgorithm<>(gtf, ff, Optimize.MINIMUM);
		ga.setStatisticsCalculator(
				new Calculator<EnumGene<Integer>, Double>()
			);
		ga.setPopulationSize(500);
		ga.setAlterers(
			new SwapMutator<EnumGene<Integer>>(0.2),
			new PartiallyMatchedCrossover<Integer>(0.3)
		);

		ga.setup();
		ga.evolve(100);
		System.out.println(ga.getBestStatistics());
		System.out.println(ga.getBestPhenotype());
	}

	private static double[][] adjacencyMatrix(int stops) {
		double[][] matrix = new double[stops][stops];
		for (int i = 0; i < stops; ++i) {
			for (int j = 0; j < stops; ++j) {
				matrix[i][j] = chord(stops, abs(i - j), RADIUS);
			}
		}
		return matrix;
	}
	private static double chord(int stops, int i, double r) {
		return 2.0*r*abs(sin((PI*i)/stops));
	}
	private static double RADIUS = 10.0;
}
