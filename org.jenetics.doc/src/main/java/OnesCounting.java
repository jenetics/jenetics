import static org.jenetics.engine.EvolutionResult.toBestPhenotype;
import static org.jenetics.engine.limit.bySteadyFitness;

import org.jenetics.BitChromosome;
import org.jenetics.BitGene;
import org.jenetics.Genotype;
import org.jenetics.Mutator;
import org.jenetics.Phenotype;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.SinglePointCrossover;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionStatistics;

public class OnesCounting {

	// This method calculates the fitness for a given genotype.
	private static Integer count(final Genotype<BitGene> gt) {
		return gt.getChromosome()
			.as(BitChromosome.class)
			.bitCount();
	}

	public static void main(String[] args) {
		// Configure and build the evolution engine.
		final Engine<BitGene, Integer> engine = Engine
			.builder(
				OnesCounting::count,
				BitChromosome.of(20, 0.15))
			.populationSize(500)
			.selector(new RouletteWheelSelector<>())
			.alterers(
				new Mutator<>(0.55),
				new SinglePointCrossover<>(0.06))
			.build();

		// Create evolution statistics consumer.
		final EvolutionStatistics<Integer, ?>
			statistics = EvolutionStatistics.ofNumber();

		final Phenotype<BitGene, Integer> best = engine.stream()
			// Truncate the evolution stream after 7 "steady"
			// generations.
			.limit(bySteadyFitness(7))
			// The evolution will stop after maximal 100
			// generations.
			.limit(100)
			// Update the evaluation statistics after
			// each generation
			.peek(statistics)
			// Collect (reduce) the evolution stream to
			// its best phenotype.
			.collect(toBestPhenotype());

		System.out.println(statistics);
		System.out.println(best);
	}
}
