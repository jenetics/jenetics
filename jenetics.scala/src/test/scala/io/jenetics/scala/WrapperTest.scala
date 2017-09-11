package io.jenetics.scala

import io.jenetics.BitChromosome
import io.jenetics.BitGene
import io.jenetics.Genotype
import io.jenetics.Mutator
import io.jenetics.TournamentSelector
import io.jenetics.engine.Engine
import io.jenetics.engine.EvolutionResult
import io.jenetics.engine.limit
import org.scalatest.FunSuite
import io.jenetics.scala._

/**
  * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
  * @since !__version__!
  * @version !__version__!
  */
class WrapperTest extends FunSuite {

	test("Hello World") {
		val engine: Engine[BitGene, Integer] =
			EngineBuilder(WrapperTest.count, BitChromosome.of(20, 0.15))
				.alterers(
					new Mutator[BitGene, Integer](),
					new Mutator[BitGene, Integer]())
				.selector(new TournamentSelector[BitGene, Integer]())
				.build()

		val gt = engine.stream()
			.limit(limit.byFixedGeneration(100))
	    	.collect(EvolutionResult.toBestGenotype[BitGene, Integer])

		println(gt)

		val engine1: Engine[BitGene, Integer] = Engine
			.builder(
				(WrapperTest.count _,
				BitChromosome.of(20, 0.15)))
	    	.build()
		()
	}

}

object WrapperTest {

	val count2: (Genotype[BitGene] => Int) = gt =>
		gt.getChromosome.as(classOf[BitChromosome]).bitCount()

	def count(gt: Genotype[BitGene]): Integer = {
		gt.getChromosome.as(classOf[BitChromosome]).bitCount()
	}
}
