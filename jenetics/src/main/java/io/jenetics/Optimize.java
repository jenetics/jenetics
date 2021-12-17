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
package io.jenetics;

import java.util.Comparator;
import java.util.function.BinaryOperator;

/**
 * This {@code enum} determines whether the GA should maximize or minimize the
 * fitness function.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 6.2
 */
public enum Optimize {

	/**
	 * GA minimization
	 */
	MINIMUM {
		@Override
		public <T extends Comparable<? super T>>
		int compare(final T a, final T b) {
			return b.compareTo(a);
		}
	},

	/**
	 * GA maximization
	 */
	MAXIMUM {
		@Override
		public <T extends Comparable<? super T>>
		int compare(final T a, final T b) {
			return a.compareTo(b);
		}
	};

	/**
	 * Compares two comparable objects. Returns a negative integer, zero, or a
	 * positive integer as the first argument is better than, equal to, or worse
	 * than the second. This compare method is {@code null}-hostile. If you need
	 * to make it {@code null}-friendly, you can wrap it with the
	 * {@link Comparator#nullsFirst(Comparator)} method.
	 *
	 * <pre>{@code
	 * final Comparator<Integer> comparator = nullsFirst(Optimize.MAXIMUM::compare);
	 * assertEquals(comparator.compare(null, null), 0);
	 * assertEquals(comparator.compare(null, 4), -1);
	 * assertEquals(comparator.compare(4, null), 1);
	 * }</pre>
	 * or
	 * <pre>{@code
	 * final Comparator<Integer> comparator = nullsFirst(Optimize.MINIMUM::compare);
	 * assertEquals(comparator.compare(null, null), 0);
	 * assertEquals(comparator.compare(null, 4), -1);
	 * assertEquals(comparator.compare(4, null), 1);
	 * }</pre>
	 *
	 * @param <T> the comparable type
	 * @param a the first object to be compared.
	 * @param b the second object to be compared.
	 * @return a negative integer, zero, or a positive integer as the first
	 *          argument is better than, equal to, or worse than the second.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public abstract <T extends Comparable<? super T>>
	int compare(final T a, final T b);

	/**
	 * Create an appropriate comparator of the given optimization strategy. A
	 * collection of comparable objects with the returned comparator will be
	 * sorted in <b>descending</b> order, according to the given definition
	 * of <i>better</i> and <i>worse</i>.
	 *
	 * <pre>{@code
	 * final Population<DoubleGene, Double> population = ...
	 * population.sort(Optimize.MINIMUM.<Double>descending());
	 * }</pre>
	 *
	 * The code example above will populationSort the population according it's fitness
	 * values in ascending order, since lower values are <i>better</i> in this
	 * case.
	 *
	 * @param <T> the type of the objects to compare.
	 * @return a new {@link Comparator} for the type {@code T}.
	 */
	public <T extends Comparable<? super T>> Comparator<T> descending() {
		return (a, b) -> compare(b, a);
	}

	/**
	 * Create an appropriate comparator of the given optimization strategy. A
	 * collection of comparable objects with the returned comparator will be
	 * sorted in <b>ascending</b> order, according to the given definition
	 * of <i>better</i> and <i>worse</i>.
	 *
	 * <pre>{@code
	 * final Population<DoubleGene, Double> population = ...
	 * population.sort(Optimize.MINIMUM.<Double>ascending());
	 * }</pre>
	 *
	 * The code example above will populationSort the population according it's fitness
	 * values in descending order, since lower values are <i>better</i> in this
	 * case.
	 *
	 * @param <T> the type of the objects to compare.
	 * @return a new {@link Comparator} for the type {@code T}.
	 */
	public <T extends Comparable<? super T>> Comparator<T> ascending() {
		return this::compare;
	}

	/**
	 * Return the best value, according to this optimization direction.
	 *
	 * @see #best()
	 *
	 * @param <C> the fitness value type.
	 * @param a the first value.
	 * @param b the second value.
	 * @return the best value. If both values are equal the first one is returned.
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 */
	public <C extends Comparable<? super C>> C best(final C a, final C b) {
		return compare(b, a) > 0 ? b : a;
	}

	/**
	 * Return a {@code null}-friendly function which returns the best element of
	 * two values. E.g.
	 *
	 * <pre>{@code
	 * assertNull(Optimize.MAXIMUM.<Integer>best().apply(null, null));
	 * assertEquals(Optimize.MAXIMUM.<Integer>best().apply(null, 4), (Integer)4);
	 * assertEquals(Optimize.MAXIMUM.<Integer>best().apply(6, null), (Integer)6);
	 * }</pre>
	 *
	 * @see #best(Comparable, Comparable)
	 *
	 * @since 6.2
	 *
	 * @param <C> the comparable argument type
	 * @return a {@code null}-friendly method which returns the best element of
	 * 	       two values
	 */
	public <C extends Comparable<? super C>> BinaryOperator<C> best() {
		return (a, b) -> switch (cmp(a, b)) {
			case 2 -> best(a, b);
			case -1 -> b;
			default -> a;
		};
	}

	/**
	 * Return the worst value, according to this optimization direction.
	 *
	 * @see #worst()
	 *
	 * @param <C> the fitness value type.
	 * @param a the first value.
	 * @param b the second value.
	 * @return the worst value. If both values are equal the first one is returned.
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 */
	public <C extends Comparable<? super C>> C worst(final C a, final C b) {
		return compare(b, a) < 0 ? b : a;
	}

	/**
	 * Return a {@code null}-friendly function which returns the worst element
	 * of two values. E.g.
	 *
	 * <pre>{@code
	 * assertNull(Optimize.MAXIMUM.<Integer>worst().apply(null, null));
	 * assertEquals(Optimize.MAXIMUM.<Integer>worst().apply(null, 4), (Integer)4);
	 * assertEquals(Optimize.MAXIMUM.<Integer>worst().apply(6, null), (Integer)6);
	 * }</pre>
	 *
	 * @see #worst(Comparable, Comparable)
	 *
	 * @since 6.2
	 *
	 * @param <C> the comparable argument type
	 * @return a {@code null}-friendly method which returns the worst element of
	 * 	       two values
	 */
	public <C extends Comparable<? super C>> BinaryOperator<C> worst() {
		return (a, b) -> switch (cmp(a, b)) {
			case 2 -> worst(a, b);
			case -1 -> b;
			default -> a;
		};
	}

	private static <T extends Comparable<? super T>>
	int cmp(final T a, final T b) {
		if (a != null) {
			if (b != null) {
				return 2;
			} else {
				return 1;
			}
		} else {
			if (b != null) {
				return -1;
			} else {
				return 0;
			}
		}
	}

}
