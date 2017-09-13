package io.jenetics.scala

import java.util.function.{Function => JFunction}
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

//	implicit def toJFF1[T, C <: Comparable[C]](f: T => C): JFunction[T, C] = {
//		v => f(v)
//	}

	implicit def toJFF2[T, C1, C2 <: Comparable[C2]](
		f: T => C1)(
		implicit c: ToComparable[C1, C2]): JFunction[T, C2] =
	{
		v => c.convert(f(v))
	}

//	def apply[G <: Gene[_, G], C <: Comparable[C]](
//		fitness: Genotype[G] => C,
//		genotypeFactory: Factory[Genotype[G]]): Engine.Builder[G, C] =
//	{
//		implicitly[ToComparable[C, C]]
//		EngineBuilder[G, C, C](fitness, genotypeFactory)(ToComparable.identity[C])
///*
//		Engine.builder(
//			Problem.of(
//				fitness,
//				Codec.of(
//					genotypeFactory,
//					(gt: Genotype[G]) => gt
//				)
//			)
//		)
//		*/
//	}

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
		fitness: Genotype[G] => C,
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
