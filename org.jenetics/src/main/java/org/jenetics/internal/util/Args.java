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

import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__! &mdash; <em>$Date$</em>
 * @since !__version__!
 */
public class Args {

	private final ISeq<String> _args;

	private Args(final String[] args) {
		_args = ISeq.of(requireNonNull(args));
	}

	public Optional<String> arg(final String name) {
		int index = _args.indexOf("--" + name);
		if (index == -1) index = _args.indexOf("-" + name);

		return index >= 0 && index < _args.length() - 1
			? Optional.of(_args.get(index + 1))
			: Optional.empty();
	}

	public Optional<Integer> intArg(final String name) {
		return arg(name)
			.flatMap(s -> parse(s, Integer::new));
	}

	public Optional<Long> longArg(final String name) {
		return arg(name)
			.flatMap(s -> parse(s, Long::new));
	}

	public Optional<Double> doubleArg(final String name) {
		return arg(name)
			.flatMap(s -> parse(s, Double::new));
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

	public static Args of(final String[] args) {
		return new Args(args);
	}

}
