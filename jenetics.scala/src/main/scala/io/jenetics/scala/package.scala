package io.jenetics

import java.util.function.{Function => JFunction}

import _root_.scala.language.implicitConversions
import _root_.scala.reflect.ClassTag
import _root_.scala.reflect.classTag

/**
  * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
  * @since !__version__!
  * @version !__version__!
  */
package object scala {

	/**
	  * Scalafication of the `Genotype` class.
	  */
	final implicit class RichGenotype[G <: Gene[_, G]](val gt: Genotype[G])
		extends AnyVal
	{

		def apply(index: Int): Chromosome[G] = gt.getChromosome(index)

		def apply(ci: Int, gi: Int): G = gt.get(ci, gi)

		def chromosome: Chromosome[G] =  gt.getChromosome

		def gene: G = gt.getGene

	}


	/**
	  * Scalafication of the `Chromosome` interface.
	  */
	final implicit class RichChromosome[G <: Gene[_, G]](val ch: Chromosome[G])
		extends AnyVal
	{

		def apply(index: Int): G = ch.getGene(index)

		def gene: G = ch.getGene

		def to[C <: Chromosome[G]: ClassTag]: C =  {
			ch.as(classTag[C].runtimeClass.asInstanceOf[Class[C]])
		}

	}

	/**
	  * Scalafication of the `Gene` interface.
	  */
	final implicit class RichGene[A, G <: Gene[A, G]](val gene: G)
		extends AnyVal
	{
		def allele: A = gene.getAllele
	}

	implicit def toFitnessFunction[T, R, C <: Comparable[C]](
		f: T => R)(
		implicit toc: ToComparable[R, C]): JFunction[T, C] =
	{
		v => toc.convert(f(v))
	}

	//implicit def toJavaFunction[T, R](f: T => R): JFunction[T, R] = v => f(v)

}
