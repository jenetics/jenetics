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
package org.jenetics.tool.optimizer;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.function.Function;

import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.engine.Codec;
import org.jenetics.util.Factory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class NamedCodec<T, G extends Gene<?, G>> implements Codec<T, G> {

	private final String _name;
	private final Factory<Genotype<G>> _encoding;
	private final Function<Genotype<G>, T> _decoder;

	public NamedCodec(
		final String name,
		final Factory<Genotype<G>> encoding,
		final Function<Genotype<G>, T> decoder
	) {
		_name = requireNonNull(name);
		_encoding = requireNonNull(encoding);
		_decoder = requireNonNull(decoder);
	}

	public String getName() {
		return _name;
	}

	@Override
	public Factory<Genotype<G>> encoding() {
		return _encoding;
	}

	@Override
	public Function<Genotype<G>, T> decoder() {
		return _decoder;
	}

	@Override
	public String toString() {
		return format("NamedCodec[%s]", _name);
	}

	public static <T, G extends Gene<?, G>> NamedCodec<T, G> of(
		final String name,
		final Factory<Genotype<G>> encoding,
		final Function<Genotype<G>, T> decoder
	) {
		return new NamedCodec<>(name, encoding, decoder);
	}

}
