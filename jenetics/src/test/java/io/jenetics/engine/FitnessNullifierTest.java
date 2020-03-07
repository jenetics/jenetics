package io.jenetics.engine;

import static java.lang.String.format;

import java.util.concurrent.atomic.AtomicInteger;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.DoubleChromosome;
import io.jenetics.DoubleGene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;

public class FitnessNullifierTest {

	@Test
	public void nullifyFitness() {
		final int nullifiedGeneration = 3;
		final int populationSize = 66;

		final var evalCount = new AtomicInteger();
		final var nullifiedPhenotypes = new AtomicInteger();

		final Evaluator<DoubleGene, Double> evaluator = population -> {
			evalCount.incrementAndGet();
			nullifiedPhenotypes.set(
				(int)population.stream()
					.filter(Phenotype::nonEvaluated)
					.count()
			);
			if (nullifiedGeneration + 2 == evalCount.get()) {
				Assert.assertEquals(nullifiedPhenotypes.get(), populationSize);
			} else if (evalCount.get() > 1) {
				Assert.assertTrue(
					nullifiedPhenotypes.get() < populationSize,
					format(
						"nullified phenotypes >= population size: %s, >= %s",
						nullifiedPhenotypes, populationSize
					)
				);
			}
			return population
				.map(pt -> pt.withFitness(5.0))
				.asISeq();
		};
		final var genotype = Genotype.of(DoubleChromosome.of(0, 1));
		final var nullifier = new FitnessNullifier<DoubleGene, Double>();

		final Engine<DoubleGene, Double> engine =
			new Engine.Builder<>(evaluator, genotype)
				.interceptor(nullifier)
				.populationSize(populationSize)
				.build();

		engine.stream()
			.peek(er -> {
				if (er.generation() == nullifiedGeneration) {
					nullifier.nullifyFitness();
				} })
			.limit(6)
			.forEach(er -> {});

	}

}
