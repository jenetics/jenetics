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
package io.jenetics.incubator.grammar;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import io.jenetics.incubator.grammar.Cfg.Symbol;
import io.jenetics.incubator.grammar.Cfg.Terminal;

// https://eli.thegreenplace.net/2010/01/28/generating-random-sentences-from-a-context-free-grammar
public class RecursiveSentenceGenerator implements SentenceGenerator {

	private final SymbolIndex _index;
	private final int _limit;

	public RecursiveSentenceGenerator(final SymbolIndex index, final int limit) {
		_index = requireNonNull(index);
		_limit = limit;
	}

	@Override
	public List<Terminal> generate(final Cfg cfg) {
		final var symbols = new ArrayList<Terminal>();
		generate(cfg, cfg.start(), _index, _limit, symbols);
		return List.copyOf(symbols);
	}

	private static void generate(
		final Cfg cfg,
		final Symbol symbol,
		final SymbolIndex index,
		final int limit,
		final List<Terminal> sentence
	) {
		if (symbol instanceof Terminal terminal) {
			sentence.add(terminal);
		} else {
			final List<Symbol> symbols =  cfg.rule((Cfg.NonTerminal)symbol)
				.map(rule -> rule.alternatives()
					.get(index.next(rule.alternatives().size()))
					.symbols())
				.orElse(List.of());

			for (var sym : symbols) {
				if (sym instanceof Terminal terminal) {
					sentence.add(terminal);
				} else {
					if (symbols.size() > limit) {
						symbols.clear();
					}
					generate(cfg, sym, index, limit, sentence);
				}
			}
		}
	}

}
