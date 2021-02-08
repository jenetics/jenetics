import static io.jenetics.engine.EvolutionResult.toBestPhenotype;
import static io.jenetics.engine.Limits.bySteadyFitness;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

import io.jenetics.BitGene;
import io.jenetics.Mutator;
import io.jenetics.Phenotype;
import io.jenetics.RouletteWheelSelector;
import io.jenetics.SinglePointCrossover;
import io.jenetics.TournamentSelector;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Codecs;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;

// The main class.
public class Knapsack {

	// This class represents a knapsack item, with a specific
	// "size" and "value".
	final static class Item {
		public final double size;
		public final double value;

		Item(final double size, final double value) {
			this.size = size;
			this.value = value;
		}

		// Create a new random knapsack item.
		static Item random() {
			final Random r = RandomRegistry.random();
			return new Item(
				r.nextDouble()*100,
				r.nextDouble()*100
			);
		}

		// Collector for summing up the knapsack items.
		static Collector<Item, ?, Item> toSum() {
			return Collector.of(
				() -> new double[2],
				(a, b) -> {a[0] += b.size; a[1] += b.value;},
				(a, b) -> {a[0] += b[0]; a[1] += b[1]; return a;},
				r -> new Item(r[0], r[1])
			);
		}
	}

	// Creating the fitness function.
	static Function<ISeq<Item>, Double>
	fitness(final double size) {
		return items -> {
			final Item sum = items.stream().collect(Item.toSum());
			return sum.size <= size ? sum.value : 0;
		};
	}

	public static void main(final String[] args) {
		final int nitems = 15;
		final double kssize = nitems*100.0/3.0;

		final ISeq<Item> items =
			Stream.generate(Item::random)
				.limit(nitems)
				.collect(ISeq.toISeq());

		// Defining the codec.
		final Codec<ISeq<Item>, BitGene> codec =
			Codecs.ofSubSet(items);

		// Configure and build the evolution engine.
		final Engine<BitGene, Double> engine = Engine
			.builder(fitness(kssize), codec)
			.populationSize(500)
			.survivorsSelector(new TournamentSelector<>(5))
			.offspringSelector(new RouletteWheelSelector<>())
			.alterers(
				new Mutator<>(0.115),
				new SinglePointCrossover<>(0.16))
			.build();

		// Create evolution statistics consumer.
		final EvolutionStatistics<Double, ?>
			statistics = EvolutionStatistics.ofNumber();

		final Phenotype<BitGene, Double> best = engine.stream()
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

		final ISeq<Item> knapsack = codec.decode(best.genotype());

		System.out.println(statistics);
		System.out.println(best);
		System.out.println("\n\n");
		System.out.printf(
			"Genotype of best item: %s%n",
			best.genotype()
		);

		final double fillSize = knapsack.stream()
			.mapToDouble(it -> it.size)
			.sum();

		System.out.printf("%.2f%% filled.%n", 100*fillSize/kssize);
	}
}
