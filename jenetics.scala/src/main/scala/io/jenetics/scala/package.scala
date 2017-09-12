package io.jenetics

import java.util.function.{Function => JFunction}

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

	/*
	implicit def toFF[T, C1, C2 <: Comparable[C2]](
		f: T => C1)(
		implicit c: ToComparable[C1, C2]): (T => C2) =
	{
		v => c.convert(f(v))
	}
	*/

	/*
	implicit def toFitnessFunction[T, C1, C2 <: Comparable[C2]](
		f: T => C1)(
		implicit c: ToComparable[C1, C2]): JFunction[T, C2] =
	{
		v => c.convert(f(v))
	}
	*/

	implicit def toFitnessFunction[T, C <: Comparable[C]](f: T => C): JFunction[T, C] = {
		v => f(v)
	}

	/*
	implicit def p[G <: Gene[_, G], C <: Comparable[C]](
		p: (Genotype[G] => C, Chromosome[G])): Problem[Genotype[G], G, C] =
	{
		Problem.of(
			p._1,
			Codec.of(
				Genotype.of(p._2),
				(gt: Genotype[G]) => gt
			)
		)
	}
	*/

}
