class Item implements Serializable {
    private static final long serialVersionUID = 1L;
    public double size;
    public double value;
}

class KnapsackFunction implements FitnessFunction<BitGene, Float64> {
	private static final long serialVersionUID = -924756568100918419L;

	private final Item[] _items;
	private final double _knapsackSize;
	
	public KnappsackFunction(final Item[] items, double knapsackSize) {
		_items = items;
		_knapsackSize = knapsackSize;
	}
	
	public Item[] getItems() {
		return _items;
	}
	
	@Override
	public Float64 evaluate(final Genotype&lt;BitGene&gt; genotype) {
		final Chromosome&lt;BitGene&gt; ch = genotype.getChromosome();
		
		double size = 0;
		double value = 0;
		for (int i = 0, n = ch.length(); i &lt; n; ++i) {
			if (ch.getGene(i).getBit()) {
				 size += _items[i].size;
				 value += _items[i].value;
			}
		}
		
		if (size &gt; _knapsackSize) {
			return Float64.ZERO;
		} else {
			return Float64.valueOf(value);
		}
	}
}

public class Knapsack {
	
	private static KnappsackFunction newFitnessFuntion(int n, double size) {
		Item[] items = new Item[n];
		for (int i = 0; i &lt; items.length; ++i) {
			items[i] = new Item();
			items[i].size = (Math.random() + 1)*10;
			items[i].value = (Math.random() + 1)*15;
		}
		
		return new KnappsackFunction(items, size);
	}
	
	 public static void main(String[] argv) throws Exception {
		//Defining the fitness function and the genotype.
		final KnappsackFunction ff = newFitnessFuntion(15, 100);
		final Factory&lt;Genotype&lt;BitGene&gt;&gt; genotype = Genotype.valueOf(
			BitChromosome.valueOf(15, 0.5)
		);
		 
		final GeneticAlgorithm&lt;BitGene, Float64&gt; ga = 
			GeneticAlgorithm.valueOf(genotype, ff);
		ga.setMaximalPhenotypeAge(10);
		ga.setPopulationSize(1000);
		ga.setSelectors(new RouletteWheelSelector&lt;BitGene, Float64&gt;());
		ga.setAlterer(new Mutator&lt;BitGene&gt;(0.115));
		ga.addAlterer(new SinglePointCrossover&lt;BitGene&gt;(0.06));
		
		ga.setup();
		ga.evolve(100);
	}
}
