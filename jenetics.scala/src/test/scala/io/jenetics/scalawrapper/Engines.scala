package io.jenetics.scalawrapper

import io.jenetics.BitChromosome
import io.jenetics.BitGene
import io.jenetics.Genotype
import io.jenetics.Mutator
import io.jenetics.SwapMutator
import io.jenetics.TournamentSelector
import io.jenetics.scala.EngineBuilder

/**
  * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
  * @since !__version__!
  * @version !__version__!
  */
object Engines {

	def count1(gt: Genotype[BitGene]): Int = {
		gt.getChromosome.as(classOf[BitChromosome]).bitCount()
	}

	def count2(gt: Genotype[BitGene]): Integer = {
		gt.getChromosome.as(classOf[BitChromosome]).bitCount()
	}

	val e1 = EngineBuilder[BitGene, Integer](count1, BitChromosome.of(20, 0.15))
		.alterers(
			new Mutator(),
			new SwapMutator())
		.selector(new TournamentSelector())
		.build()

	val e2 = EngineBuilder[BitGene, Integer](count2, BitChromosome.of(20, 0.15))
		.alterers(
			new Mutator(),
			new SwapMutator())
		.selector(new TournamentSelector())
		.build()

	val e3 = EngineBuilder[BitGene, Integer](
			_.getChromosome.as(classOf[BitChromosome]).bitCount(),
			BitChromosome.of(20, 0.15))
		.alterers(
			new Mutator(),
			new SwapMutator())
		.selector(new TournamentSelector())
		.build()

}
