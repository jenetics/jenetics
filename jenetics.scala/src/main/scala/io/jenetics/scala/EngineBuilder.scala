package io.jenetics.scala

import io.jenetics.Chromosome
import io.jenetics.Gene
import io.jenetics.Genotype
import io.jenetics.engine.Codec
import io.jenetics.engine.Engine
import io.jenetics.engine.Problem
import io.jenetics.util.Factory

/**
  * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
  * @since !__version__!
  * @version !__version__!
  */
object EngineBuilder {

	def apply[G <: Gene[_, G], R, C <: Comparable[C]](
		fitness: Genotype[G] => R,
		genotypeFactory: Factory[Genotype[G]])(
		implicit c: ToComparable[R, C]): Engine.Builder[G, C] =
	{
		Engine.builder(
			Problem.of(
				fitness,
				Codec.of(
					genotypeFactory,
					(gt: Genotype[G]) => gt
				)
			)
		)
	}

	def apply[G <: Gene[_, G], CH <: Chromosome[G], R, C <: Comparable[C]](
		fitness: Genotype[G] => R,
		chromosome: CH,
		chromosomes: CH*)(
		implicit c: ToComparable[R, C]): Engine.Builder[G, C] =
	{
		Engine.builder(
			Problem.of(
				fitness,
				Codec.of(
					Genotype.of(chromosome, chromosomes: _*),
					(gt: Genotype[G]) => gt
				)
			)
		)
	}

}
