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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.jenetics.internal.math.Randoms;

import io.jenetics.ext.grammar.Cfg.Expression;
import io.jenetics.ext.grammar.Cfg.NonTerminal;
import io.jenetics.ext.grammar.Cfg.Rule;
import io.jenetics.ext.grammar.Cfg.Symbol;
import io.jenetics.ext.grammar.Cfg.Terminal;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public final class RandomCfg {
	private RandomCfg() {
	}

	public static Cfg<String> next(final RandomGenerator random) {
		final var nonTerminals = Stream.generate(() -> nextNonTerminal(random))
			.limit(10)
			.toList();

		final var terminals = Stream.generate(() -> nextTerminal(random))
			.limit(15)
			.toList();

		final Supplier<List<Expression<String>>> expressions = () -> Stream.generate(() -> {
				final var nterms = Stream.generate(() -> {
						final var index = random.nextInt(nonTerminals.size());
						return nonTerminals.get(index);
					})
					.limit(random.nextInt(3, 13))
					.toList();
				final var terms = Stream.generate(() -> {
						final var index = random.nextInt(terminals.size());
						return terminals.get(index);
					})
					.limit(random.nextInt(3, 13))
					.toList();

				final var symbols = new ArrayList<Symbol<String>>();
				symbols.addAll(nterms);
				symbols.addAll(terms);
				return new Expression<>(symbols);
			})
			.limit(random.nextInt(3, 7))
			.toList();

		final var rules = Stream.generate(() -> {
				final var start = nonTerminals.get(random.nextInt(nonTerminals.size()));
				return new Rule<>(nonTerminals.get(random.nextInt(nonTerminals.size())), expressions.get());
			})
			.limit(random.nextInt(5, 15))
			.collect(Collectors.groupingBy(Rule::start))
			.values().stream()
			.map(list -> list.get(0))
			.toList();

		return Cfg.of(rules);
	}

	public static NonTerminal<String> nextNonTerminal(final RandomGenerator random) {
		return new NonTerminal<>(ruleid(random));
	}

	public static Terminal<String> nextTerminal(final RandomGenerator random) {
		final var name = Randoms.nextASCIIString(random);
		return new Terminal<>(name, name);
	}

	private static String ruleid(final RandomGenerator random) {
		return "r%03d".formatted(random.nextInt(1_000));
	}

	public static void main(final String[] args) {
		final var bnf = next(RandomGenerator.getDefault());

	}

}
