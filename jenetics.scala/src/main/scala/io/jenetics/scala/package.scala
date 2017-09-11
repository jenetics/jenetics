package io.jenetics

import io.jenetics.engine.Problem

/**
  * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
  * @since !__version__!
  * @version !__version__!
  */
package object scala {

	implicit def toJavaFunction[T, Int](f: (T => Int)): java.util.function.Function[T, Integer] = {
		null
	}

	implicit def toGT[A, G <: Gene[A, G]](ch: Chromosome[G]): Genotype[G] = {
		null
	}

	implicit def problem[T, G <: Gene[_, G], C <: Comparable[C]](p: (Genotype[G] => C, Chromosome[G])): Problem[T, G, C] = ???


}
