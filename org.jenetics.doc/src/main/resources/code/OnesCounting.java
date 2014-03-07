import org.jenetics.BitChromosome;
import org.jenetics.BitGene;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.Mutator;
import org.jenetics.NumberStatistics;
import org.jenetics.Optimize;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.SinglePointCrossover;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;

final class OneCounter
	implements Function<Genotype<BitGene>, Integer>
{
	@Override
	public Integer apply(final Genotype<BitGene> gt) {
		return ((BitChromosome)gt.getChromosome()).bitCount();
	}
}

public class OnesCounting {
	public static void main(String[] args) {
		Factory<Genotype<BitGene>> gtf = Genotype.of(
			BitChromosome.of(20, 0.15)
		);
		Function<Genotype<BitGene>, Integer> ff = new OneCounter();
		GeneticAlgorithm<BitGene, Integer> ga =
		new GeneticAlgorithm<>(
			gtf, ff, Optimize.MAXIMUM
		);

		ga.setStatisticsCalculator(
			new NumberStatistics.Calculator<BitGene, Integer>()
		);
		ga.setPopulationSize(500);
		ga.setSelectors(
			new RouletteWheelSelector<BitGene, Integer>()
		);
		ga.setAlterers(
			new Mutator<BitGene>(0.55),
			new SinglePointCrossover<BitGene>(0.06)
		);

		ga.setup();
		ga.evolve(100);
		System.out.println(ga.getBestStatistics());
		System.out.println(ga.getBestPhenotype());
	}
}