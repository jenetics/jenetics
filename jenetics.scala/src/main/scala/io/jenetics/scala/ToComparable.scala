package io.jenetics.scala

import java.lang.{Byte => JByte}
import java.lang.{Double => JDouble}
import java.lang.{Float => JFloat}
import java.lang.{Long => JLong}
import java.lang.{Short => JShort}

/**
  * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
  * @since !__version__!
  * @version !__version__!
  */
trait ToComparable[T, C <: Comparable[C]] {
	def convert(v: T): C
}

object ToComparable {

	implicit def identity[C <: Comparable[C]]: ToComparable[C, C] = c => c

	implicit object Byte2Byte extends ToComparable[Byte, JByte] {
		override def convert(v: Byte): JByte = v.asInstanceOf[JByte]
	}

	implicit object Short2Short extends ToComparable[Short, JShort] {
		override def convert(v: Short): JShort = v.asInstanceOf[JShort]
	}

	implicit object Char2Character extends ToComparable[Char, Character] {
		override def convert(v: Char): Character = v.asInstanceOf[Character]
	}

	implicit object Int2Integer extends ToComparable[Int, Integer] {
		override def convert(v: Int): Integer = v.asInstanceOf[Integer]
	}

	implicit object Long2Long extends ToComparable[Long, JLong] {
		override def convert(v: Long): JLong = v.asInstanceOf[JLong]
	}

	implicit object Float2Float extends ToComparable[Float, JFloat] {
		override def convert(v: Float): JFloat = v.asInstanceOf[JFloat]
	}

	implicit object Double2Double extends ToComparable[Double, JDouble] {
		override def convert(v: Double): JDouble = v.asInstanceOf[JDouble]
	}

}
