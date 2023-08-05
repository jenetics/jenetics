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
package io.jenetics.prog.regression;

import static java.util.Objects.requireNonNull;

import java.util.function.DoubleBinaryOperator;

import io.jenetics.ext.util.Tree;

import io.jenetics.prog.op.Op;

/**
 * This function calculates the <em>overall</em> error of a given program tree.
 * The error is calculated from the {@link LossFunction} and, if desired, the
 * program {@link Complexity}.
 *
 * <pre>{@code
 * final Error<Double> error = Error.of(LossFunction::mse, Complexity.ofNodeCount(50));
 * }</pre>
 *
 * @see LossFunction
 * @see Complexity
 *
 * @param <T> the sample type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.0
 * @since 5.0
 */
@FunctionalInterface
public interface Error<T> {

	/**
	 * Calculates the <em>overall</em> error of a given program tree. The error
	 * is calculated from the {@link LossFunction} and, if desired, the program
	 * {@link Complexity}.
	 *
	 * @param program the program tree which calculated the {@code calculated}
	 *        values
	 * @param calculated the calculated function values
	 * @param expected the expected function values
	 * @return the overall program error
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	double apply(
		final Tree<? extends Op<T>, ?> program,
		final T[] calculated,
		final T[] expected
	);


	/**
	 * Creates an error function which only uses the given {@code loss} function
	 * for calculating the program error
	 *
	 * @param <T> the sample type
	 * @param loss the loss function to use for calculating the program error
	 * @return an error function which uses the loss function for error
	 *         calculation
	 * @throws NullPointerException if the given {@code loss} function is
	 *         {@code null}
	 */
	static <T> Error<T> of(final LossFunction<T> loss) {
		requireNonNull(loss);
		return (p, c, e) -> loss.apply(c, e);
	}

	/**
	 * Creates an error function by combining the given {@code loss} function
	 * and program {@code complexity}. The loss function and program complexity
	 * is combined in the following way: {@code error = loss + loss*complexity}.
	 * The complexity function penalizes programs which grow to big.
	 *
	 * @param <T> the sample type
	 * @param loss the loss function
 	 * @param complexity the program complexity measure
	 * @return a new error function by combining the given loss and complexity
	 *         function
	 * @throws NullPointerException if one of the functions is {@code null}
	 */
	static <T> Error<T>
	of(final LossFunction<T> loss, final Complexity<T> complexity) {
		return of(loss, complexity, (lss, cpx) -> lss + lss*cpx);
	}

	/**
	 * Creates an error function by combining the given {@code loss} function
	 * and program {@code complexity}. The loss function and program complexity
	 * is combined in the following way: {@code error = loss + loss*complexity}.
	 * The complexity function penalizes programs which grow to big.
	 *
	 * @param <T> the sample type
	 * @param loss the loss function
	 * @param complexity the program complexity measure
	 * @param compose the function which composes the {@code loss} and
	 *        {@code complexity} function
	 * @return a new error function by combining the given loss and complexity
	 *         function
	 * @throws NullPointerException if one of the functions is {@code null}
	 */
	static <T> Error<T> of(
		final LossFunction<T> loss,
		final Complexity<T> complexity,
		final DoubleBinaryOperator compose
	) {
		requireNonNull(loss);
		requireNonNull(complexity);
		requireNonNull(compose);

		return (p, c, e) ->
			compose.applyAsDouble(loss.apply(c, e), complexity.apply(p));
	}

}
