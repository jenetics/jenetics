package org.jenetix;

import static org.jenetics.engine.EvolutionResult.toBestPhenotype;
import static org.jenetics.engine.limit.byFitnessThreshold;

import java.util.stream.IntStream;

import org.jenetics.CharacterChromosome;
import org.jenetics.CharacterGene;
import org.jenetics.Genotype;
import org.jenetics.Phenotype;
import org.jenetics.engine.Engine;
import org.jenetics.util.CharSeq;
import org.jenetics.util.Factory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
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
			new CharacterChromosome(chars, TARGET.length())
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
