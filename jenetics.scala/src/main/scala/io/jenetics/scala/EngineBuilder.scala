package io.jenetics.scala

import io.jenetics.Chromosome
import io.jenetics.Gene
import io.jenetics.Genotype
import io.jenetics.engine.Engine

/**
  * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
  * @since !__version__!
  * @version !__version__!
  */
object EngineBuilder {

	def apply[G <: Gene[_, G], C <: Comparable[C]](ff: (Genotype[G] => C), ch: Chromosome[G]): Engine.Builder[G, C] = {
		null
	}

}
