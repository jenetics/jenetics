final class Item implements Serializable {
	private static final long serialVersionUID = 1L;
	public double size;
	public double value;
}

final class KnapsackFunction 
	implements Function<Genotype<BitGene>, Double>,
				Serializable
{
	private static final long serialVersionUID = 1L;
	
	private final Item[] _items;
	private final double _knapsackSize;

	public KnapsackFunction(final Item[] items, double size) {
		_items = items;
		_knapsackSize = size;
	}

	public Item[] getItems() {
		return _items;
	}

	@Override
	public Double apply(final Genotype<BitGene> genotype) {
		final Chromosome<BitGene> ch = genotype.getChromosome();

		double size = 0;
		double value = 0;
		for (int i = 0, n = ch.length(); i < n; ++i) {
			if (ch.getGene(i).getBit()) {
				size += _items[i].size;
				value += _items[i].value;
			}
		}

		if (size > _knapsackSize) {
			return 0;
		} else {
			return value;
		}
	}
}

public class Knapsack {
	
	private static KnappsackFunction newFitnessFuntion(
		final int n, final double knapsackSize
	) {
		Item[] items = new Item[n];
		for (int i = 0; i < items.length; ++i) {
			items[i] = new Item();
			items[i].size = (Math.random() + 1)*10;
			items[i].value = (Math.random() + 1)*15;
		}
		return new KnapsackFunction(items, knapsackSize);
	}
	
	public static void main(String[] argv) throws Exception {
		final KnappsackFunction ff = newFitnessFuntion(15, 100);
		final Factory<Genotype<BitGene>> genotype = Genotype.valueOf(
			new BitChromosome(15, 0.5)
		);
		 
		final GeneticAlgorithm<BitGene, Double> ga = 
			new GeneticAlgorithm<>(genotype, ff);
		ga.setMaximalPhenotypeAge(30);
		ga.setPopulationSize(100);
		ga.setStatisticsCalculator(
			new NumberStatistics.Calculator<BitGene, Double>()
		);
		ga.setSelectors(
			new RouletteWheelSelector<BitGene, Float64>()
		);
		ga.setAlterer(new CompositeAlterer<>(
			 new Mutator<BitGene>(0.115),
			 new SinglePointCrossover<BitGene>(0.16)
	 	));
		
		final int generations = 100;
		
		GAUtils.printConfig(
			"Knapsack", ga, generations, 
			((CompositeAlterer<?>)ga.getAlterer())
			.getAlterers().toArray()
		);
		GAUtils.execute(ga, generations, 10);
	}
}

