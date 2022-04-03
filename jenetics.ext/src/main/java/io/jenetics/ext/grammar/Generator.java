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

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Function;

import io.jenetics.ext.grammar.Cfg.NonTerminal;
import io.jenetics.ext.grammar.Cfg.Symbol;

/**
 * Generator interface for generating <em>sentences</em>/<em>derivation trees</em>
 * from a given grammar.
 *
 * @param <T> the terminal token type of the grammar
 * @param <R> the result type of the generator
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 7.1
 * @version 7.1
 */
@FunctionalInterface
public interface Generator<T, R> {

	/**
	 * Generates a new sentence from the given grammar. If the generation of the
	 * sentence fails, an empty list is returned.
	 *
	 * @param cfg the generating grammar
	 * @return a newly created result
	 */
	R generate(final Cfg<? extends T> cfg);

	/**
	 * Maps the generated result from type {@code R} to type {@code R1}.
	 *
	 * @param f the mapping function
	 * @param <R1> the target type
	 * @return a new generator with target type {@code R1}
	 * @throws NullPointerException if the mapping function is {@code null}
	 */
	default <R1> Generator<T, R1> map(final Function<? super R, ? extends R1> f) {
		requireNonNull(f);
		return cfg -> f.apply(generate(cfg));
	}

	/**
	 * Standard algorithm for selecting a list of alternative symbols from the
	 * given {@code rule}.
	 *
	 * @param rule the rule to select the alternative from
	 * @param cfg the grammar to select the alternative from
	 * @param index the symbol selection strategy
	 * @param <T> the terminal type
	 * @return the selected symbols
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	static <T> List<Symbol<T>> select(
		final NonTerminal<T> rule,
		final Cfg<T> cfg,
		final SymbolIndex index
	) {
		return cfg.rule(rule)
			.map(r -> r.alternatives()
				.get(index.next(r, r.alternatives().size()))
				.symbols())
			.orElse(List.of());
	}

}
