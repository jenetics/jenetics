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
package io.jenetics.internal.util;

import java.net.URL;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 * This class contains some common predicates
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 4.3
 */
public final class Predicates {
	private Predicates() {}

	public static final Predicate<Object> TRUE = a -> true;

	/**
	 * Return a predicate, which always return {@code true}.
	 *
	 * @param <T> the predicate type
	 * @return a predicate, which always return {@code true}
	 */
	@SuppressWarnings("unchecked")
	public static <T> Predicate<T> True() {
		return (Predicate<T>)TRUE;
	}


	/**
	 * Checks whether the given {@code throwable} is a non-fatal error.
	 *
	 * @param throwable the throwable to check
	 * @return true if the given {@code throwable} is a non-fatal error
	 */
	public static boolean nonFatal(final Throwable throwable) {
		return
			!(throwable instanceof VirtualMachineError) &&
			!(throwable instanceof ThreadDeath) &&
			!(throwable instanceof InterruptedException) &&
			!(throwable instanceof LinkageError);
	}

	public static <T extends Throwable, R> R
	nonFatal(T t, Function<? super Throwable, ? extends R> block) {
		if (nonFatal(t)) {
			return block.apply(t);
		}

		throw sneakyThrow(t);
	}

	private static RuntimeException sneakyThrow(Throwable t) {
		return sneakyThrow0(requireNonNull(t));
	}

	@SuppressWarnings("unchecked")
	private static <T extends Throwable> T sneakyThrow0(Throwable t) throws T {
		throw (T)t;
	}

	public static void main(final String[] args) {
		try {
			new URL("asdf");
		} catch (NullPointerException e) {
			System.out.println(e);
		} catch (Throwable t) {
			nonFatal(t, error -> {
				return null;
			});
		}
	}

}
