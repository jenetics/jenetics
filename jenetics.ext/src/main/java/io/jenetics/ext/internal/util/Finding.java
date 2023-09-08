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
package io.jenetics.ext.internal.util;

import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.StreamSupport;

/**
 * Implements methods for finding the {@code argmin} or {@code argmax} values
 * from a given argument set.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Finding {
	private Finding() {
	}

	private record Result<A, R extends Comparable<? super R>>(A argument, R result) {
		Result<A, R> min(final Result<A, R> other) {
			if (other.result == null) {
				return this;
			}
			if (result == null) {
				return other;
			}

			return result.compareTo(other.result) > 0 ? other : this;
		}
		Result<A, R> max(final Result<A, R> other) {
			if (other.result == null) {
				return this;
			}
			if (result == null) {
				return other;
			}

			return result.compareTo(other.result) < 0 ? other : this;
		}
	}

	private record DoubleResult<T>(T argument, double result) {
		DoubleResult<T> min(final DoubleResult<T> other) {
			return Double.compare(result, other.result) > 0 ? other : this;
		}
		DoubleResult<T> max(final DoubleResult<T> other) {
			return Double.compare(result, other.result) < 0 ? other : this;
		}
	}

	/**
	 * Returns the argument, which minimizes the given function {@code fn}.
	 *
	 * @param arguments the arguments to search
	 * @param fn the function applied to the arguments
	 * @return the argument which minimizes the given function
	 * @param <A> the argument type
	 * @param <R> the result type of the function
	 */
	public static <A, R extends Comparable<? super R>> A argmin(
		final Iterable<? extends A> arguments,
		final Function<? super A, ? extends R> fn
	) {
		return StreamSupport.stream(arguments.spliterator(), false)
			.map(arg -> new Result<>(arg, fn.apply(arg)))
			.reduce(new Result<>(null, null), Result::min)
			.argument();
	}

	/**
	 * Returns the argument, which maximizes the given function {@code fn}.
	 *
	 * @param arguments the arguments to search
	 * @param fn the function applied to the arguments
	 * @return the argument which maximizes the given function
	 * @param <A> the argument type
	 * @param <R> the result type of the function
	 */
	public static <A, R extends Comparable<? super R>> A argmax(
		final Iterable<? extends A> arguments,
		final Function<? super A, ? extends R> fn
	) {
		return StreamSupport.stream(arguments.spliterator(), false)
			.map(arg -> new Result<>(arg, fn.apply(arg)))
			.reduce(new Result<>(null, null), Result::max)
			.argument();
	}

	/**
	 * Returns the argument, which minimizes the given function {@code fn}.
	 *
	 * @param arguments the arguments to search
	 * @param fn the function applied to the arguments
	 * @return the argument which minimizes the given function
	 * @param <A> the argument type
	 */
	public static <A> A argmin(
		final Iterable<? extends A> arguments,
		final ToDoubleFunction<? super A> fn
	) {
		return StreamSupport.stream(arguments.spliterator(), false)
			.map(arg -> new DoubleResult<>(arg, fn.applyAsDouble(arg)))
			.reduce(new DoubleResult<>(null, Double.POSITIVE_INFINITY), DoubleResult::min)
			.argument();
	}

	/**
	 * Returns the argument, which maximizes the given function {@code fn}.
	 *
	 * @param arguments the arguments to search
	 * @param fn the function applied to the arguments
	 * @return the argument which maximizes the given function
	 * @param <A> the argument type
	 */
	public static <A> A argmax(
		final Iterable<? extends A> arguments,
		final ToDoubleFunction<? super A> fn
	) {
		return StreamSupport.stream(arguments.spliterator(), false)
			.map(arg -> new DoubleResult<>(arg, fn.applyAsDouble(arg)))
			.reduce(new DoubleResult<>(null, Double.NEGATIVE_INFINITY), DoubleResult::max)
			.argument();
	}

}
