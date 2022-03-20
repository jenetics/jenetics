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
package io.jenetics.ext.grammar;

import java.util.List;
import java.util.function.Function;

import io.jenetics.BitGene;
import io.jenetics.IntegerGene;
import io.jenetics.engine.Codec;
import io.jenetics.util.IntRange;

import io.jenetics.ext.grammar.Cfg.Rule;
import io.jenetics.ext.grammar.Cfg.Terminal;

public final class Mappers {
	private Mappers() {
	}

	public static <T> Codec<List<Terminal<T>>, IntegerGene> of(
		final Cfg<? extends T> cfg,
		final Function<? super Rule<?>, IntRange> length,
		final Function<? super SymbolIndex, ? extends Generator<T, List<Terminal<T>>>> generator
	) {
		return new Mapper<>(cfg, length, generator);
	}

	public static <T> Codec<List<Terminal<T>>, BitGene> of(
		final Cfg<? extends T> cfg,
		final int length,
		final Function<? super SymbolIndex, ? extends Generator<T, List<Terminal<T>>>> generator
	) {
		return new BitGeneSentenceCodec<>(cfg, length, generator);
	}

	public static <T> Codec<List<Terminal<T>>, IntegerGene> of(
		final Cfg<? extends T> cfg,
		final IntRange range,
		final IntRange length,
		final Function<? super SymbolIndex, ? extends Generator<T, List<Terminal<T>>>> generator
	) {
		return new IntegerGeneSentenceCodec<>(cfg, range, length, generator);
	}

}
