/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.ext.util;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.Comparator;

/**
 * Defines the needed methods for a multi-objective fitness value.
 *
 * @param <T> the underlying data type, like {@code int[]} or {@code double[]}
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface Vec<T> extends Comparable<Vec<T>> {

	/**
	 * Return the underlying data structure.
	 *
	 * @return the underlying data structure
	 */
	public T data();

	/**
	 * Return the number of vector elements.
	 *
	 * @return the number of vector elements
	 */
	public int length();

	/**
	 * Return the comparator for comparing the elements of this MO vector.
	 *
	 * @return the comparator for comparing the elements of this MO vector
	 */
	public ElementComparator<T> comparator();

	/**
	 * Return the comparator which defines the (Pareto) dominance measure.
	 *
	 * @return the comparator which defines the (Pareto) dominance measure
	 */
	public Comparator<T> dominance();


	/* *************************************************************************
	 * Default methods derived from the methods above.
	 * ************************************************************************/

	/**
	 * Compares the {@code this} vector with the {@code other} at the given
	 * component {@code index}.
	 *
	 * @param index the component index
	 * @param other the other vector
	 * @return a negative integer, zero, or a positive integer as
	 *        {@code this[index]} is less than, equal to, or greater than
	 *        {@code other[index]}
	 * @throws NullPointerException if the {@code other} object is {@code null}
	 */
	public default int compareTo(final int index, final T other) {
		return comparator().compare(index, data(), other);
	}

	/**
	 * Calculates the <a href="https://en.wikipedia.org/wiki/Pareto_efficiency">
	 * <b>Pareto Dominance</b></a> of vector {@code value()} and {@code other}.
	 *
	 * @param other the other vector
	 * @return {@code 1} if <b>value()</b> ≻ <b>other</b>, {@code -1} if
	 *         <b>other</b> ≻ <b>value()</b> and {@code 0} otherwise
	 * @throws NullPointerException if the {@code other} vector is {@code null}
	 */
	public default int dominance(final T other) {
		return dominance().compare(data(), other);
	}

	/**
	 * The default implementation uses the {@link #dominance(Object)} function
	 * for defining a <b>partial</b> order of two vectors.
	 *
	 * @param other the other vector
	 * @return {@code 1} if <b>value()</b> ≻ <b>other</b>, {@code -1} if
	 *         <b>other</b> ≻ <b>value()</b> and {@code 0} otherwise
	 * @throws NullPointerException if the {@code other} vector is {@code null}
	 */
	@Override
	public default int compareTo(final Vec<T> other) {
		return dominance(other.data());
	}

	/* *************************************************************************
	 * Common 'dominance' methods.
	 * ************************************************************************/

	/**
	 * Calculates the <a href="https://en.wikipedia.org/wiki/Pareto_efficiency">
	 *     <b>Pareto Dominance</b></a> of the two vectors <b>u</b> and <b>v</b>.
	 *
	 * @see Pareto#dominance(Comparable[], Comparable[])
	 *
	 * @param u the first vector
	 * @param v the second vector
	 * @param <C> the element type of vector <b>u</b> and <b>v</b>
	 * @return {@code 1} if <b>u</b> ≻ <b>v</b>, {@code -1} if <b>v</b> ≻
	 *         <b>u</b> and {@code 0} otherwise
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if {@code u.length != v.length}
	 */
	public static <C extends Comparable<? super C>> int
	dominance(final C[] u, final C[] v) {
		return dominance(u, v, Comparator.naturalOrder());
	}

	/**
	 * Calculates the <a href="https://en.wikipedia.org/wiki/Pareto_efficiency">
	 *     <b>Pareto Dominance</b></a> of the two vectors <b>u</b> and <b>v</b>.
	 *
	 * @see Pareto#dominance(Object[], Object[], Comparator)
	 *
	 * @param u the first vector
	 * @param v the second vector
	 * @param comparator the element comparator which is used for calculating
	 *        the dominance
	 * @param <T> the element type of vector <b>u</b> and <b>v</b>
	 * @return {@code 1} if <b>u</b> ≻ <b>v</b>, {@code -1} if <b>v</b> ≻
	 *         <b>u</b> and {@code 0} otherwise
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if {@code u.length != v.length}
	 */
	public static <T> int
	dominance(final T[] u, final T[] v, final Comparator<? super T> comparator) {
		return Pareto.dominance(u, v, comparator);
	}

	/**
	 * Calculates the <a href="https://en.wikipedia.org/wiki/Pareto_efficiency">
	 *     <b>Pareto Dominance</b></a> of the two vectors <b>u</b> and <b>v</b>.
	 *
	 * @see Pareto#dominance(int[], int[])
	 *
	 * @param u the first vector
	 * @param v the second vector
	 * @return {@code 1} if <b>u</b> ≻ <b>v</b>, {@code -1} if <b>v</b> ≻
	 *         <b>u</b> and {@code 0} otherwise
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if {@code u.length != v.length}
	 */
	public static int dominance(final int[] u, final int[] v) {
		return Pareto.dominance(u, v);
	}

	/**
	 * Calculates the <a href="https://en.wikipedia.org/wiki/Pareto_efficiency">
	 *     <b>Pareto Dominance</b></a> of the two vectors <b>u</b> and <b>v</b>.
	 *
	 * @see Pareto#dominance(long[], long[])
	 *
	 * @param u the first vector
	 * @param v the second vector
	 * @return {@code 1} if <b>u</b> ≻ <b>v</b>, {@code -1} if <b>v</b> ≻
	 *         <b>u</b> and {@code 0} otherwise
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if {@code u.length != v.length}
	 */
	public static int dominance(final long[] u, final long[] v) {
		return Pareto.dominance(u, v);
	}

	/**
	 * Calculates the <a href="https://en.wikipedia.org/wiki/Pareto_efficiency">
	 *     <b>Pareto Dominance</b></a> of the two vectors <b>u</b> and <b>v</b>.
	 *
	 * @see Pareto#dominance(double[], double[])
	 *
	 * @param u the first vector
	 * @param v the second vector
	 * @return {@code 1} if <b>u</b> ≻ <b>v</b>, {@code -1} if <b>v</b> ≻
	 *         <b>u</b> and {@code 0} otherwise
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if {@code u.length != v.length}
	 */
	public static int dominance(final double[] u, final double[] v) {
		return Pareto.dominance(u, v);
	}

	/* *************************************************************************
	 * Static factory functions for wrapping ordinary arrays.
	 * ************************************************************************/

	/**
	 * Wraps the given array into a {@code Vec} object.
	 *
	 * @param array the wrapped array
	 * @param <C> the array element type
	 * @return the given array wrapped into a {@code Vec} object.
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <C extends Comparable<? super C>> Vec<C[]> of(final C[] array) {
		return of(array, Comparator.naturalOrder());
	}

	/**
	 * Wraps the given array into a {@code Vec} object.
	 *
	 * @param array the wrapped array
	 * @param comparator the (natural order) comparator of the array elements
	 * @param <T> the array element type
	 * @return the given array wrapped into a {@code Vec} object.
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <T> Vec<T[]> of(
		final T[] array,
		final Comparator<? super T> comparator
	) {
		requireNonNull(array);
		requireNonNull(comparator);

		return new Vec<T[]>() {
			@Override
			public T[] data() {
				return array;
			}

			@Override
			public ElementComparator<T[]> comparator() {
				return (i, u, v) -> comparator.compare(u[i], v[i]);
			}

			@Override
			public Comparator<T[]> dominance() {
				return (u, v) -> Vec.dominance(u, v, comparator);
			}

			@Override
			public int length() {
				return array.length;
			}
		};
	}

	/**
	 * Wraps the given array into a {@code Vec} object.
	 *
	 * @param array the wrapped array
	 * @return the given array wrapped into a {@code Vec} object.
	 * @throws NullPointerException if the given {@code array} is {@code null}
	 */
	public static Vec<int[]> of(final int[] array) {
		requireNonNull(array);

		return new Vec<int[]>() {
			@Override
			public int[] data() {
				return array;
			}

			@Override
			public ElementComparator<int[]> comparator() {
				return (i, u, v) -> {
					if (i < 0 || i > 1) {
						throw new IndexOutOfBoundsException(format(
							"Index out of bounds [0, %d): %d", length(), i
						));
					}

					return Integer.compare(u[i], v[i]);
				};
			}

			@Override
			public Comparator<int[]> dominance() {
				return Vec::dominance;
			}

			@Override
			public int length() {
				return array.length;
			}
		};
	}

	/**
	 * Wraps the given array into a {@code Vec} object.
	 *
	 * @param array the wrapped array
	 * @return the given array wrapped into a {@code Vec} object.
	 * @throws NullPointerException if the given {@code array} is {@code null}
	 */
	public static Vec<long[]> of(final long[] array) {
		requireNonNull(array);

		return new Vec<long[]>() {
			@Override
			public long[] data() {
				return array;
			}

			@Override
			public ElementComparator<long[]> comparator() {
				return (i, u, v) -> {
					if (i < 0 || i > 1) {
						throw new IndexOutOfBoundsException(format(
							"Index out of bounds [0, %d): %d", length(), i
						));
					}

					return Long.compare(u[i], v[i]);
				};
			}

			@Override
			public Comparator<long[]> dominance() {
				return Vec::dominance;
			}

			@Override
			public int length() {
				return array.length;
			}
		};
	}

	/**
	 * Wraps the given array into a {@code Vec} object.
	 *
	 * @param array the wrapped array
	 * @return the given array wrapped into a {@code Vec} object.
	 * @throws NullPointerException if the given {@code array} is {@code null}
	 */
	public static Vec<double[]> of(final double[] array) {
		requireNonNull(array);

		return new Vec<double[]>() {
			@Override
			public double[] data() {
				return array;
			}

			@Override
			public ElementComparator<double[]> comparator() {
				return (i, u, v) -> {
					if (i < 0 || i > 1) {
						throw new IndexOutOfBoundsException(format(
							"Index out of bounds [0, %d): %d", length(), i
						));
					}

					return Double.compare(u[i], v[i]);
				};
			}

			@Override
			public Comparator<double[]> dominance() {
				return Vec::dominance;
			}

			@Override
			public int length() {
				return array.length;
			}
		};
	}

}
