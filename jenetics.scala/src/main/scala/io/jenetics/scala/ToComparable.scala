package io.jenetics.scala

/**
  * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
  * @since !__version__!
  * @version !__version__!
  */
trait ToComparable[T, C <: Comparable[C]] {
	def convert(v: T): C
}

object ToComparable {

	implicit def identity[C <: Comparable[C]](): ToComparable[C, C] = c => c

	implicit object Byte2Byte extends ToComparable[Byte, java.lang.Byte] {
		override def convert(v: Byte): java.lang.Byte = v.asInstanceOf[java.lang.Byte]
	}

	implicit object Short2Short extends ToComparable[Short, java.lang.Short] {
		override def convert(v: Short): java.lang.Short = v.asInstanceOf[java.lang.Short]
	}

	implicit object Char2Character extends ToComparable[Char, Character] {
		override def convert(v: Char): Character = v.asInstanceOf[Character]
	}

	implicit object Int2Integer extends ToComparable[Int, Integer] {
		override def convert(v: Int): Integer = v.asInstanceOf[Integer]
	}

	implicit object Long2Long extends ToComparable[Long, java.lang.Long] {
		override def convert(v: Long): java.lang.Long = v.asInstanceOf[java.lang.Long]
	}

	implicit object Float2Float extends ToComparable[Float, java.lang.Float] {
		override def convert(v: Float): java.lang.Float = v.asInstanceOf[java.lang.Float]
	}

	implicit object Double2Double extends ToComparable[Double, java.lang.Double] {
		override def convert(v: Double): java.lang.Double = v.asInstanceOf[java.lang.Double]
	}

}
