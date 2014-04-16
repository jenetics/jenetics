import static org.jenetics.util.math.random.nextDouble;

import java.util.Random;

import org.jenetics.BitChromosome;
import org.jenetics.BitGene;
import org.jenetics.Chromosome;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.Mutator;
import org.jenetics.NumberStatistics;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.SinglePointCrossover;
import org.jenetics.TournamentSelector;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.Scoped;

final class Item {
	public final double size;
	public final double value;

	Item(final double size, final double value) {
		this.size = size;
		this.value = value;
	}
}

final class KnapsackFunction
	implements Function<Genotype<BitGene>, Double>
{
	private final Item[] items;
	private final double size;

	public KnapsackFunction(final Item[] items, double size) {
		this.items = items;
		this.size = size;
	}

	@Override
	public Double apply(final Genotype<BitGene> genotype) {
		final Chromosome<BitGene> ch = genotype.getChromosome();
		double size = 0;
		double value = 0;
		for (int i = 0, n = ch.length(); i < n; ++i) {
			if (ch.getGene(i).getBit()) {
				size += items[i].size;
				value += items[i].value;
			}
		}

		return size <= this.size ? value : 0;
	}
}

public class Knapsack {

	private static KnapsackFunction FF(final int n, final double size) {
		final Item[] items = new Item[n];
		try (Scoped<? extends Random> random =
			RandomRegistry.scope(new LCG64ShiftRandom(123)))
		{
			for (int i = 0; i < items.length; ++i) {
				items[i] = new Item(
					nextDouble(random.get(), 0, 100),
					nextDouble(random.get(), 0, 100)
				);
			}
		}

		return new KnapsackFunction(items, size);
	}

	public static void main(String[] args) throws Exception {
		final int nitems = 15;
		final double kssize = nitems*100.0/3.0;

		final KnapsackFunction ff = FF(nitems, kssize);
		final Factory<Genotype<BitGene>> genotype = Genotype.of(
			BitChromosome.of(nitems, 0.5)
		);

		final GeneticAlgorithm<BitGene, Double> ga = new GeneticAlgorithm<>(
			genotype, ff
		);
		ga.setPopulationSize(500);
		ga.setStatisticsCalculator(
			new NumberStatistics.Calculator<BitGene, Double>()
		);
		ga.setSurvivorSelector(
			new TournamentSelector<BitGene, Double>(5)
		);
		ga.setOffspringSelector(
			new RouletteWheelSelector<BitGene, Double>()
		);
		ga.setAlterers(
			 new Mutator<BitGene>(0.115),
			 new SinglePointCrossover<BitGene>(0.16)
		);

		ga.setup();
		ga.evolve(100);
		System.out.println(ga.getBestStatistics());
		System.out.println(ga.getBestPhenotype());
	}
}
