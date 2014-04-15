import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.MeanAlterer;
import org.jenetics.Mutator;
import org.jenetics.NumberStatistics;
import org.jenetics.Optimize;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;

final class Real
	implements Function<Genotype<DoubleGene>, Double>
{
	@Override
	public Double apply(Genotype<DoubleGene> genotype) {
		final double x = genotype.getGene().doubleValue();
		return cos(0.5 + sin(x)) * cos(x);
	}
}

public class RealFunction {
	public static void main(String[] args) {
		Factory<Genotype<DoubleGene>> gtf = Genotype.of(
			new DoubleChromosome(0.0, 2.0 * PI)
		);
		Function<Genotype<DoubleGene>, Double> ff = new Real();
		GeneticAlgorithm<DoubleGene, Double> ga =
			new GeneticAlgorithm<>(
				gtf, ff, Optimize.MINIMUM
			);

		ga.setStatisticsCalculator(
			new NumberStatistics.Calculator<DoubleGene, Double>()
		);
		ga.setPopulationSize(500);
		ga.setAlterers(
			new Mutator<DoubleGene>(0.03),
			new MeanAlterer<DoubleGene>(0.6)
		);

		ga.setup();
		ga.evolve(100);
		System.out.println(ga.getBestStatistics());
		System.out.println(ga.getBestPhenotype());
	}
}
