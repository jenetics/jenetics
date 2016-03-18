package org.jenetix;

import static org.jenetics.engine.EvolutionResult.toBestPhenotype;

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
 * @since !__version__!
 * @version !__version__!
 */
public class WeaselProgram {

	private static final String TARGET_STRING = "methinks it is like a weasel";

	private static Integer evaluate(final Genotype<CharacterGene> gt) {
		final CharSequence source = (CharSequence)gt.getChromosome();

		return IntStream.range(0, TARGET_STRING.length())
			.map(i -> source.charAt(i) == TARGET_STRING.charAt(i) ? 1 : 0)
			.sum();
	}

	public static void main(String[] args) throws Exception {
		final CharSeq chars = CharSeq.of("a-z ");
		final Factory<Genotype<CharacterGene>> gtf = Genotype.of(
			new CharacterChromosome(chars, TARGET_STRING.length())
		);

		final Engine<CharacterGene, Integer> engine = Engine
			.builder(WeaselProgram::evaluate, gtf)
			.populationSize(200)
			.selector(new WeaselSelector<>())
			.offspringFraction(1)
			.alterers(new WeaselMutator<>(0.05))
			.build();

		final Phenotype<CharacterGene, Integer> result = engine.stream()
			.limit(50)
			.peek(r -> System.out.println(r.getBestPhenotype()))
			.collect(toBestPhenotype());

		System.out.println(result);
	}

}
