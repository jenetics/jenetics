package io.jenetics.ext;

import static io.jenetics.engine.EvolutionResult.toBestPhenotype;
import static io.jenetics.engine.Limits.byFitnessThreshold;

import java.util.stream.IntStream;

import io.jenetics.CharacterChromosome;
import io.jenetics.CharacterGene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.engine.Engine;
import io.jenetics.util.CharSeq;
import io.jenetics.util.Factory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.5
 * @version 3.5
 */
public class WeaselProgram {

	private static final String TARGET = "METHINKS IT IS LIKE A WEASEL";

	private static int score(final Genotype<CharacterGene> gt) {
		final CharSequence source = (CharSequence)gt.getChromosome();

		return IntStream.range(0, TARGET.length())
			.map(i -> source.charAt(i) == TARGET.charAt(i) ? 1 : 0)
			.sum();
	}

	public static void main(String[] args) throws Exception {
		final CharSeq chars = CharSeq.of("A-Z ");
		final Factory<Genotype<CharacterGene>> gtf = Genotype.of(
			CharacterChromosome.of(chars, TARGET.length())
		);

		final Engine<CharacterGene, Integer> engine = Engine
			.builder(WeaselProgram::score, gtf)
			.populationSize(150)
			.selector(new WeaselSelector<>())
			.offspringFraction(1)
			.alterers(new WeaselMutator<>(0.05))
			.build();

		final Phenotype<CharacterGene, Integer> result = engine.stream()
			.limit(byFitnessThreshold(TARGET.length() - 1))
			.peek(r -> System.out.println(
				r.getTotalGenerations() + ": " + r.getBestPhenotype()))
			.collect(toBestPhenotype());

		System.out.println(result);
	}

}
