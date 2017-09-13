package io.jenetics.scala

import io.jenetics.Gene
import io.jenetics.Genotype
import io.jenetics.engine.Codec
import io.jenetics.util.Factory

/**
  * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
  * @since !__version__!
  * @version !__version__!
  */
object Codecs {

	def apply[G <: Gene[_, G], T](
		encoding: Factory[Genotype[G]],
		decoder: Genotype[G] => T): Codec[T, G] =
	{
		Codec.of(encoding, gt => decoder(gt))
	}

}
