package io.jenetics

import io.jenetics.engine.Codec
import io.jenetics.engine.Problem

import _root_.scala.language.implicitConversions

/**
  * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
  * @since !__version__!
  * @version !__version__!
  */
package object scala {

	type FF[G <: Gene[_, G], C1] = Genotype[G] => C1

	implicit def toFitnessFunction[T, C1, C2 <: Comparable[C2]](
		f: T => C1)(
		implicit c: ToComparable[C1, C2]): java.util.function.Function[T, C2] =
	{
		v => c.convert(f(v))
	}

	implicit def p[G <: Gene[_, G], C1, C2 <: Comparable[C2]](
		p: (Genotype[G] => C1, Chromosome[G]))(
		implicit c: ToComparable[C1, C2]): Problem[Genotype[G], G, C2] =
	{
		Problem.of(
			p._1,
			Codec.of(
				Genotype.of(p._2),
				(gt: Genotype[G]) => gt
			)
		)
	}

}
