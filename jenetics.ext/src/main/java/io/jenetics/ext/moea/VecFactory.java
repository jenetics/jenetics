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
package io.jenetics.ext.moea;

import java.util.Comparator;
import java.util.List;

import io.jenetics.Optimize;

/**
 * This interface allows to create vector object from a given array type
 * {@code T}. It is useful if you need some additional parametrization of the
 * created vectors.
 * <p>
 * As the following example shows, only one {@code VecFactory} instance should
 * be used for creating the vectors for a given multi-objective <em>problem</em>.
 * <pre>{@code
 * private static final VecFactory<double[]> FACTORY = VecFactory.ofDoubleVec(
 *     Optimize.MAXIMUM,
 *     Optimize.MINIMUM,
 *     Optimize.MINIMUM,
 *     Optimize.MAXIMUM
 * );
 *
 * // The fitness function.
 * static Vec<double[]> fitness(final double[] x) {
 *     final double[] result = new double[4];
 *     // ...
 *     return FACTORY.newVec(result);
 * }
 * }</pre>
 * In the example above, the first dimension of the created vector is maximized,
 * the following two are minimized and the last vector component is again
 * maximized.
 *
 * @see Vec
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 6.0
 * @since 5.2
 */
@FunctionalInterface
public interface VecFactory<T> {

	/**
	 * Create a new {@link Vec} object from the given {@code array}.
	 *
	 * @param array the array used in the created vector
	 * @return a new {@link Vec} object from the given {@code array}
	 * @throws NullPointerException if the given {@code array} is {@code null}
	 * @throws IllegalArgumentException if the {@code array} length is zero or
	 *         doesn't match the required length of the actual factory
	 */
	Vec<T> newVec(final T array);

	/**
	 * Create a new factory for {@code int[]} vectors. Additionally you can
	 * specify the optimization direction (maximization or minimization) for
	 * each dimension. The dimensionality of the created vectors must be exactly
	 * the same as the given length of the given {@code optimizes}. If the
	 * lengths doesn't match, an {@link IllegalArgumentException} is thrown.
	 *
	 * @see #ofIntVec(Optimize...)
	 *
	 * @apiNote
	 * Only one factory instance should be used for a given multi-objective
	 * <em>problem</em>.
	 *
	 * @param optimizes the optimization <em>direction</em> for each dimension
	 * @return a new factory for {@code int[]} vectors
	 * @throws NullPointerException if the given {@code optimizes} is
	 *         {@code null}
	 * @throws IllegalArgumentException if the {@code optimizes} length is zero
	 */
	static VecFactory<int[]> ofIntVec(final List<Optimize> optimizes) {
		return new GeneralIntVecFactory(optimizes);
	}

	/**
	 * Create a new factory for {@code int[]} vectors. Additionally you can
	 * specify the optimization direction (maximization or minimization) for
	 * each dimension. The dimensionality of the created vectors must be exactly
	 * the same as the given length of the given {@code optimizes}. If the
	 * lengths doesn't match, an {@link IllegalArgumentException} is thrown.
	 *
	 * @see #ofIntVec(List)
	 *
	 * @apiNote
	 * Only one factory instance should be used for a given multi-objective
	 * <em>problem</em>.
	 *
	 * @param optimizes the optimization <em>direction</em> for each dimension
	 * @return a new factory for {@code int[]} vectors
	 * @throws NullPointerException if the given {@code optimizes} is
	 *         {@code null}
	 * @throws IllegalArgumentException if the {@code optimizes} length is zero
	 */
	static VecFactory<int[]> ofIntVec(final Optimize... optimizes) {
		return ofIntVec(List.of(optimizes));
	}

	/**
	 * Create a new factory for {@code int[]} vectors, where all dimensions are
	 * maximized.
	 *
	 * @see Vec#of(int...)
	 *
	 * @return a new factory for {@code int[]} vectors, where all dimensions are
	 * 	       maximized
	 */
	static VecFactory<int[]> ofIntVec() {
		return Vec::of;
	}

	/**
	 * Create a new factory for {@code long[]} vectors. Additionally you can
	 * specify the optimization direction (maximization or minimization) for
	 * each dimension. The dimensionality of the created vectors must be exactly
	 * the same as the given length of the given {@code optimizes}. If the
	 * lengths doesn't match, an {@link IllegalArgumentException} is thrown.
	 *
	 * @see #ofLongVec(Optimize...)
	 *
	 * @apiNote
	 * Only one factory instance should be used for a given multi-objective
	 * <em>problem</em>.
	 *
	 * @param optimizes the optimization <em>direction</em> for each dimension
	 * @return a new factory for {@code long[]} vectors
	 * @throws NullPointerException if the given {@code optimizes} is
	 *         {@code null}
	 * @throws IllegalArgumentException if the {@code optimizes} length is zero
	 */
	static VecFactory<long[]> ofLongVec(final List<Optimize> optimizes) {
		return new GeneralLongVecFactory(optimizes);
	}

	/**
	 * Create a new factory for {@code long[]} vectors. Additionally you can
	 * specify the optimization direction (maximization or minimization) for
	 * each dimension. The dimensionality of the created vectors must be exactly
	 * the same as the given length of the given {@code optimizes}. If the
	 * lengths doesn't match, an {@link IllegalArgumentException} is thrown.
	 *
	 * @see #ofLongVec(List)
	 *
	 * @apiNote
	 * Only one factory instance should be used for a given multi-objective
	 * <em>problem</em>.
	 *
	 * @param optimizes the optimization <em>direction</em> for each dimension
	 * @return a new factory for {@code long[]} vectors
	 * @throws NullPointerException if the given {@code optimizes} is
	 *         {@code null}
	 * @throws IllegalArgumentException if the {@code optimizes} length is zero
	 */
	static VecFactory<long[]> ofLongVec(final Optimize... optimizes) {
		return ofLongVec(List.of(optimizes));
	}

	/**
	 * Create a new factory for {@code long[]} vectors, where all dimensions are
	 * maximized.
	 *
	 * @see Vec#of(long...)
	 *
	 * @return a new factory for {@code long[]} vectors, where all dimensions are
	 * 	       maximized
	 */
	static VecFactory<long[]> ofLongVec() {
		return Vec::of;
	}

	/**
	 * Create a new factory for {@code double[]} vectors. Additionally you can
	 * specify the optimization direction (maximization or minimization) for
	 * each dimension. The dimensionality of the created vectors must be exactly
	 * the same as the given length of the given {@code optimizes}. If the
	 * lengths doesn't match, an {@link IllegalArgumentException} is thrown.
	 *
	 * @see #ofDoubleVec(Optimize...)
	 *
	 * @apiNote
	 * Only one factory instance should be used for a given multi-objective
	 * <em>problem</em>.
	 *
	 * @param optimizes the optimization <em>direction</em> for each dimension
	 * @return a new factory for {@code double[]} vectors
	 * @throws NullPointerException if the given {@code optimizes} is
	 *         {@code null}
	 * @throws IllegalArgumentException if the {@code optimizes} length is zero
	 */
	static VecFactory<double[]> ofDoubleVec(final List<Optimize> optimizes) {
		return new GeneralDoubleVecFactory(optimizes);
	}

	/**
	 * Create a new factory for {@code double[]} vectors. Additionally you can
	 * specify the optimization direction (maximization or minimization) for
	 * each dimension. The dimensionality of the created vectors must be exactly
	 * the same as the given length of the given {@code optimizes}. If the
	 * lengths doesn't match, an {@link IllegalArgumentException} is thrown.
	 *
	 * @see #ofDoubleVec(List)
	 *
	 * @apiNote
	 * Only one factory instance should be used for a given multi-objective
	 * <em>problem</em>.
	 *
	 * @param optimizes the optimization <em>direction</em> for each dimension
	 * @return a new factory for {@code double[]} vectors
	 * @throws NullPointerException if the given {@code optimizes} is
	 *         {@code null}
	 * @throws IllegalArgumentException if the {@code optimizes} length is zero
	 */
	static VecFactory<double[]> ofDoubleVec(final Optimize... optimizes) {
		return ofDoubleVec(List.of(optimizes));
	}

	/**
	 * Create a new factory for {@code double[]} vectors, where all dimensions
	 * are maximized.
	 *
	 * @see Vec#of(double...)
	 *
	 * @return a new factory for {@code double[]} vectors, where all dimensions
	 *         are maximized
	 */
	static VecFactory<double[]> ofDoubleVec() {
		return Vec::of;
	}

	/**
	 * Create a new factory for {@code T[]} vectors. Additionally you can
	 * specify the optimization direction (maximization or minimization) for
	 * each dimension. The dimensionality of the created vectors must be exactly
	 * the same as the given length of the given {@code optimizes}. If the
	 * lengths doesn't match, an {@link IllegalArgumentException} is thrown.
	 *
	 * @see #ofObjectVec(Comparator, ElementDistance, Optimize...)
	 *
	 * @param comparator the array element comparator
	 * @param distance the element distance function
	 * @param optimizes the optimization <em>direction</em> for each dimension
	 * @param <T> the array element type
	 * @return  a new factory for {@code T[]} vectors
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if the {@code optimizes} length is zero
	 */
	static <T> VecFactory<T[]> ofObjectVec(
		final Comparator<? super T> comparator,
		final ElementDistance<T[]> distance,
		final List<Optimize> optimizes
	) {
		return new GeneralObjectVecFactory<>(comparator, distance, optimizes);
	}

	/**
	 * Create a new factory for {@code T[]} vectors. Additionally you can
	 * specify the optimization direction (maximization or minimization) for
	 * each dimension. The dimensionality of the created vectors must be exactly
	 * the same as the given length of the given {@code optimizes}. If the
	 * lengths doesn't match, an {@link IllegalArgumentException} is thrown.
	 *
	 * @see #ofObjectVec(Comparator, ElementDistance, List)
	 *
	 * @param comparator the array element comparator
	 * @param distance the element distance function
	 * @param optimizes the optimization <em>direction</em> for each dimension
	 * @param <T> the array element type
	 * @return  a new factory for {@code T[]} vectors
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if the {@code optimizes} length is zero
	 */
	static <T> VecFactory<T[]> ofObjectVec(
		final Comparator<? super T> comparator,
		final ElementDistance<T[]> distance,
		final Optimize... optimizes
	) {
		return ofObjectVec(comparator, distance, List.of(optimizes));
	}

	/**
	 * Create a new factory for {@code T[]} vectors with comparable element
	 * types. Additionally you can specify the optimization direction
	 * (maximization or minimization) for each dimension. The dimensionality of
	 * the created vectors must be exactly the same as the given length of the
	 * given {@code optimizes}. If the lengths doesn't match, an
	 * {@link IllegalArgumentException} is thrown.
	 *
	 * @since 6.0
	 *
	 * @see #ofObjectVec(Comparator, ElementDistance, List)
	 *
	 * @param distance the element distance function
	 * @param optimizes the optimization <em>direction</em> for each dimension
	 * @param <T> the array element type
	 * @return a new factory for {@code T[]} vectors
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if the {@code optimizes} length is zero
	 */
	static <T extends Comparable<? super T>> VecFactory<T[]> ofObjectVec(
		final ElementDistance<T[]> distance,
		final List<Optimize> optimizes
	) {
		return ofObjectVec(
			Comparator.naturalOrder(),
			distance,
			optimizes
		);
	}

	/**
	 * Create a new factory for {@code T[]} vectors with comparable element
	 * types. Additionally you can specify the optimization direction
	 * (maximization or minimization) for each dimension. The dimensionality of
	 * the created vectors must be exactly the same as the given length of the
	 * given {@code optimizes}. If the lengths doesn't match, an
	 * {@link IllegalArgumentException} is thrown.
	 *
	 * @since 6.0
	 *
	 * @see #ofObjectVec(Comparator, ElementDistance, List)
	 *
	 * @param distance the element distance function
	 * @param optimizes the optimization <em>direction</em> for each dimension
	 * @param <T> the array element type
	 * @return a new factory for {@code T[]} vectors
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if the {@code optimizes} length is zero
	 */
	static <T extends Comparable<? super T>> VecFactory<T[]> ofObjectVec(
		final ElementDistance<T[]> distance,
		final Optimize... optimizes
	) {
		return ofObjectVec(
			Comparator.naturalOrder(),
			distance,
			optimizes
		);
	}

	/**
	 * Create a new factory for {@code T[]} vectors with comparable element
	 * types. Additionally you can specify the optimization direction
	 * (maximization or minimization) for each dimension. The dimensionality of
	 * the created vectors must be exactly the same as the given length of the
	 * given {@code optimizes}. If the lengths doesn't match, an
	 * {@link IllegalArgumentException} is thrown.
	 *
	 * @since 6.0
	 *
	 * @see #ofObjectVec(Comparator, ElementDistance, List)
	 *
	 * @param optimizes the optimization <em>direction</em> for each dimension
	 * @param <T> the array element type
	 * @return a new factory for {@code T[]} vectors
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if the {@code optimizes} length is zero
	 */
	static <T extends Comparable<? super T>> VecFactory<T[]>
	ofObjectVec(final List<Optimize> optimizes) {
		return ofObjectVec(VecFactory::dist, optimizes);
	}

	private static <T extends Comparable<? super T>>
	double dist(final T[] u, final T[] v, final int i) {
		return Integer.compare(u[i].compareTo(v[i]), 0);
	}

	/**
	 * Create a new factory for {@code T[]} vectors with comparable element
	 * types. Additionally you can specify the optimization direction
	 * (maximization or minimization) for each dimension. The dimensionality of
	 * the created vectors must be exactly the same as the given length of the
	 * given {@code optimizes}. If the lengths doesn't match, an
	 * {@link IllegalArgumentException} is thrown.
	 *
	 * @since 6.0
	 *
	 * @see #ofObjectVec(Comparator, ElementDistance, List)
	 *
	 * @param optimizes the optimization <em>direction</em> for each dimension
	 * @param <T> the array element type
	 * @return a new factory for {@code T[]} vectors
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if the {@code optimizes} length is zero
	 */
	static <T extends Comparable<? super T>> VecFactory<T[]>
	ofObjectVec(final Optimize... optimizes) {
		return ofObjectVec(VecFactory::dist, optimizes);
	}

	/**
	 * Create a new factory for {@code T[]} vectors with comparable element
	 * types. Additionally you can specify the optimization direction
	 * (maximization or minimization) for each dimension. The dimensionality of
	 * the created vectors must be exactly the same as the given length of the
	 * given {@code optimizes}. If the lengths doesn't match, an
	 * {@link IllegalArgumentException} is thrown.
	 *
	 * @since 6.0
	 *
	 * @see Vec#of(Comparable[])
	 *
	 * @param <T> the array element type
	 * @return a new factory for {@code T[]} vectors
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if the {@code optimizes} length is zero
	 */
	static <T extends Comparable<? super T>> VecFactory<T[]> ofObjectVec() {
		return Vec::of;
	}

}
