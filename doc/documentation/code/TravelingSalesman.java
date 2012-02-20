import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.sin;

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

class FF
	implements Function<Genotype<EnumGene<Integer>>, Float64>
{
	private final double[][] _adjacence;
	public FF(final double[][] adjacence) {
		_adjacence = adjacence;
	}
	@Override
	public Float64 apply(Genotype<EnumGene<Integer>> genotype) {
		Chromosome<EnumGene<Integer>> path = 
			genotype.getChromosome();

		double length = 0.0;
		for (int i = 0, n = path.length(); i < n; ++i) {
			final int from = path.getGene(i).getAllele();
			final int to = path.getGene((i + 1)%n).getAllele();
			length += _adjacence[from][to];
		}
		return Float64.valueOf(length);
	}
}

public class TravelingSalesman {

	public static void main(String[] args) {
		final int stops = 20;

		Function<Genotype<EnumGene<Integer>>, Float64> ff = 
			new FF(adjacencyMatrix(stops));
		Factory<Genotype<EnumGene<Integer>>> gt = Genotype.valueOf(
			PermutationChromosome.ofInteger(stops)
		);
		final GeneticAlgorithm<EnumGene<Integer>, Float64>
			ga = new GeneticAlgorithm<>(gt, ff, Optimize.MINIMUM);
		ga.setStatisticsCalculator(
			new Calculator<EnumGene<Integer>, Float64>()
		);
		ga.setPopulationSize(300);
		ga.setAlterers(
			new SwapMutator<EnumGene<Integer>>(0.2),
			new PartiallyMatchedCrossover<Integer>(0.3)
		);

		ga.setup();
		ga.evolve(700);
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
