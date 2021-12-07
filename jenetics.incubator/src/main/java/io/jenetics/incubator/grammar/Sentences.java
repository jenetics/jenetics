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

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;

import io.jenetics.incubator.grammar.Cfg.NonTerminal;
import io.jenetics.incubator.grammar.Cfg.Symbol;
import io.jenetics.incubator.grammar.Cfg.Terminal;

public class Sentences {

	public static List<Terminal> generate(
		final Cfg cfg,
		final RandomGenerator random,
		final Supplier<? extends List<Symbol>> listFactory
	) {
		final NonTerminal start = cfg.start();
		final var symbols = listFactory.get();
		symbols.addAll(expand(cfg, start, random));

		//final List<Symbol> symbols = new LinkedList<>(expand(cfg, start, random));

		boolean expanded = true;
		while (expanded) {
			expanded = false;

			final var it = symbols.listIterator();
			while (it.hasNext()) {
				final var symbol = it.next();

				if (symbol instanceof NonTerminal) {
					it.remove();
					expand(cfg, (NonTerminal)symbol, random).forEach(it::add);
					expanded = true;
				}
			}
		}

		return symbols.stream()
			.map(Terminal.class::cast)
			.toList();
	}

	public static List<Terminal> generate(final Cfg cfg, final RandomGenerator random) {
		return generate(cfg, random, LinkedList::new);
	}

	private static List<Symbol> expand(
		final Cfg grammar,
		final NonTerminal symbol,
		RandomGenerator random
	) {
		final var rule = grammar.rule(symbol);
		return rule
			.map(r -> r.alternatives()
				.get(random.nextInt(r.alternatives().size()))
				.symbols())
			.orElse(List.of(symbol));
	}

}
