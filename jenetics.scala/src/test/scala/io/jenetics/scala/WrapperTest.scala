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

/**
  * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
  * @since !__version__!
  * @version !__version__!
  */
class WrapperTest extends FunSuite {

	test("Hello World") {
		val engine = EngineBuilder[BitGene, Integer](WrapperTest.count, BitChromosome.of(20, 0.15))
			.alterers(
				new Mutator(),
				new Mutator())
			.selector(new TournamentSelector())
			.build()

		val gt = engine.stream()
			.limit(limit.byFixedGeneration(100))
	    	.collect(EvolutionResult.toBestGenotype[BitGene, Integer])

		println(gt)

		/*
		val engine1: Engine[BitGene, Integer] = Engine
			.builder((
				WrapperTest.count3 _,
				BitChromosome.of(20, 0.15)))
	    	.build()

		println(engine1)
		*/
	}

}

object WrapperTest {

	val count2: (Genotype[BitGene] => Int) = gt =>
		gt.getChromosome.as(classOf[BitChromosome]).bitCount()

	def count(gt: Genotype[BitGene]): Int = {
		gt.getChromosome.as(classOf[BitChromosome]).bitCount()
	}

	def count3(gt: Genotype[BitGene]): Int = {
		gt.getChromosome.as(classOf[BitChromosome]).bitCount()
	}

}
