package io.jenetics.scalawrapper

import io.jenetics.BitChromosome
import io.jenetics.BitGene
import io.jenetics.Chromosome
import io.jenetics.Genotype
import io.jenetics.Mutator
import io.jenetics.SwapMutator
import io.jenetics.TournamentSelector
import io.jenetics.engine.Codec
import io.jenetics.engine.Engine
import io.jenetics.engine.Problem
import io.jenetics.scala.EngineBuilder
import io.jenetics.scala._

/**
  * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
  * @since !__version__!
  * @version !__version__!
  */
object Engines {

	def count1(gt: Genotype[BitGene]): Int = {
		gt.chromosome.to[BitChromosome].bitCount()
	}

	def count2(gt: Genotype[BitGene]): Integer = {
		gt.chromosome.to[BitChromosome].bitCount()
	}

	def count3(gt: Genotype[BitGene]): Integer = {
		gt.chromosome.stream()
			.filter(g => g.booleanValue())
			.count()
			.asInstanceOf[Integer]


		gt.chromosome.as(classOf[BitChromosome]).bitCount()
	}

	val e1 = EngineBuilder(count1 _, BitChromosome.of(20, 0.15))
		.alterers(
			new Mutator(),
			new SwapMutator())
		.selector(new TournamentSelector())
		.build()

	val e2 = EngineBuilder(count2 _, BitChromosome.of(20, 0.15))
		.alterers(
			new Mutator(),
			new SwapMutator())
		.selector(new TournamentSelector())
		.build()


	val e3 = EngineBuilder(
			(gt: Genotype[BitGene]) =>
				gt.chromosome.to[BitChromosome].bitCount(),
			BitChromosome.of(20, 0.15))
		.alterers(
			new Mutator(),
			new SwapMutator())
		.selector(new TournamentSelector())
		.build()

	val e4 = EngineBuilder[BitGene, Int, java.lang.Integer](
		(gt: Genotype[BitGene]) =>
			gt.chromosome.to[BitChromosome].bitCount(),
		BitChromosome.of(20, 0.15))
		.alterers(
			new Mutator(),
			new SwapMutator())
		.selector(new TournamentSelector())
		.build()


	val c1 = Codecs(
		Genotype.of(BitChromosome.of(20, 0.15)),
		(gt: Genotype[BitGene]) => gt.chromosome.to[BitChromosome].bitCount()
	)

//	val e4 = Engine.builder(
//		(c: Int) => c,
//		Codecs(
//			Genotype.of(BitChromosome.of(20, 0.15)),
//			(gt: Genotype[BitGene]) => gt.getChromosome.to[BitChromosome].bitCount()
//		)
//	).build()
}
