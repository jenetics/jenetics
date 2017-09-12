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

	/*
	ff: Function[_ >: Genotype[G], _ <: C],
		genotypeFactory: Factory[Genotype[G]]
	 */

	/*
	def apply[G <: Gene[_, G], C <: Comparable[C]](
		fitness: Genotype[G] => C,
		genotypeFactory: Genotype[G]): Engine.Builder[G, C] =
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
	*/

	/**
	  * Create a new evolution `Engine.Builder` with the given fitness function
	  * and chromosome templates.
	  *
	  * @param fitness the fitness function
	  * @param chromosome  the first chromosome
	  * @param chromosomes the chromosome templates
	  * @return a new engine builder
	  */
	def apply[G <: Gene[_, G], C <: Comparable[C]](
		fitness: Genotype[G] => C,
		chromosome: Chromosome[G],
		chromosomes: Chromosome[G]*): Engine.Builder[G, C] =
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
