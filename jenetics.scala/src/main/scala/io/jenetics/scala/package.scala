package io.jenetics

import java.util.function.{Function => JFunction}

import _root_.scala.language.implicitConversions

/**
  * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
  * @since !__version__!
  * @version !__version__!
  */
package object scala {

	implicit def toFitnessFunction[T, R, C <: Comparable[C]](
		f: T => R)(
		implicit toc: ToComparable[R, C]): JFunction[T, C] =
	{
		v => toc.convert(f(v))
	}

}
