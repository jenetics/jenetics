package io.jenetics

import java.util.function.{Function => JFunction}

import _root_.scala.language.implicitConversions
import _root_.scala.reflect.ClassTag

/**
  * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
  * @since !__version__!
  * @version !__version__!
  */
package object scala {

	final implicit  class RichChromosome[G <: Gene[_, G]](val ch: Chromosome[G])
		extends AnyVal
	{

		def to[C <: Chromosome[G]: ClassTag]: C = {
			???
		}

	}

	implicit def toFitnessFunction[T, R, C <: Comparable[C]](
		f: T => R)(
		implicit toc: ToComparable[R, C]): JFunction[T, C] =
	{
		v => toc.convert(f(v))
	}

	implicit def toJavaFunction[T, R](f: T => R): JFunction[T, R] = v => f(v)

}
