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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.internal.util;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jenetics.util.ISeq;

/**
 * Helper class for parsing command line arguments.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.4
 * @since 3.4
 */
public class Args {

	private final ISeq<String> _args;

	private Args(final String[] args) {
		_args = ISeq.of(requireNonNull(args));
	}

	/**
	 * Return the parameter with the given name.
	 *
	 * @param name the parameter name
	 * @return the parameter with the given name, if any
	 */
	public Optional<String> arg(final String name) {
		int index = _args.indexOf("--" + name);
		if (index == -1) index = _args.indexOf("-" + name);

		return index >= 0 && index < _args.length() - 1
			? Optional.of(_args.get(index + 1))
			: Optional.empty();
	}

	/**
	 * Return the int-argument with the given name.
	 *
	 * @param name the argument name
	 * @return the int argument value, if any
	 */
	public Optional<Integer> intArg(final String name) {
		return arg(name)
			.flatMap(s -> parse(s, Integer::valueOf));
	}

	public ISeq<Integer> intArgs(final String name) {
		return  arg(name)
			.map(Stream::of)
			.orElseGet(Stream::empty)
			.flatMap(a -> Stream.of(a.split("@")))
			.flatMap(s -> parse(s, Integer::valueOf)
				.map(Stream::of)
				.orElseGet(Stream::empty))
			.collect(ISeq.toISeq());
	}

	/**
	 * Return the long-argument with the given name.
	 *
	 * @param name the argument name
	 * @return the long argument value, if any
	 */
	public Optional<Long> longArg(final String name) {
		return arg(name)
			.flatMap(s -> parse(s, Long::valueOf));
	}

	/**
	 * Return the double-argument with the given name.
	 *
	 * @param name the argument name
	 * @return the double argument value, if any
	 */
	public Optional<Double> doubleArg(final String name) {
		return arg(name)
			.flatMap(s -> parse(s, Double::valueOf));
	}

	private static <T> Optional<T> parse(
		final String string,
		final Function<String, T> parser
	) {
		Optional<T> value = Optional.empty();
		try {
			value = Optional.of(parser.apply(string));
		} catch (Exception ignore) {
		}

		return value;
	}

	@Override
	public String toString() {
		return _args.toString();
	}

	/**
	 * Wraps the given argument array into an {@code Args} object.
	 *
	 * @param args the underlying command line arguments
	 * @return the wrapped argument object
	 */
	public static Args of(final String[] args) {
		return new Args(args);
	}

}
