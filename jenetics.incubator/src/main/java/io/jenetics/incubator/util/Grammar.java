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
package io.jenetics.incubator.util;

import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.TreeNode;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public final class Grammar {

	public sealed interface Symbol {
		String name();
	}

	public static record Terminal(String name) implements Symbol {
		@Override
		public String toString() {
			return BnfParser.escape(name);
		}
	}

	public static record NonTerminal(String name) implements Symbol {
		@Override
		public String toString() {
			return format("<%s>", name);
		}
	}

	public static record Expression(List<Symbol> symbols) {
		public Expression {
			symbols = List.copyOf(symbols);
		}

		@Override
		public String toString() {
			return symbols.stream()
				.map(Object::toString)
				.collect(Collectors.joining(" "));
		}
	}

	public static record Rule(NonTerminal start, List<Expression> alternatives) {
		public Rule {
			alternatives = List.copyOf(alternatives);
		}

		@Override
		public String toString() {
			return format(
				"%s ::= %s",
				start,
				alternatives.stream()
					.map(Objects::toString)
					.collect(Collectors.joining("\n    | "))
			);
		}
	}

	private final List<NonTerminal> nonTerminals;
	private final List<Terminal> terminals;
	private final NonTerminal start;
	private final List<Rule> rules;

	/**
	 * Create a grammar object with the given rules.
	 *
	 * @param rules the rules the grammar consists of
	 * @throws IllegalArgumentException if the list of rules is empty
	 * @throws NullPointerException if the list of rules is {@code null}
	 */
	public Grammar(final List<Rule> rules) {
		if (rules.isEmpty()) {
			throw new IllegalArgumentException(
				"The given list of rules must not be empty."
			);
		}

		this.nonTerminals = nonTerminals(rules);
		this.terminals = terminals(rules);
		this.start = rules.get(0).start();
		this.rules = List.copyOf(rules);
	}

	private static List<NonTerminal> nonTerminals(final List<Rule> rules) {
		final var nonTerminals = new ArrayList<NonTerminal>();
		final var distinct = new HashSet<NonTerminal>();

		for (var rule : rules) {
			if (!distinct.contains(rule.start())) {
				nonTerminals.add(rule.start());
				distinct.add(rule.start());
			}
			rule.alternatives().stream()
				.flatMap(e -> e.symbols().stream())
				.filter(s -> s instanceof NonTerminal)
				.map(NonTerminal.class::cast)
				.forEach(nt -> {
					if (!distinct.contains(nt)) {
						nonTerminals.add(nt);
						distinct.add(nt);
					}
				});
		}

		return List.copyOf(nonTerminals);
	}

	private static List<Terminal> terminals(final List<Rule> rules) {
		final var terminals = new ArrayList<Terminal>();
		final var distinct = new HashSet<Terminal>();

		for (var rule : rules) {
			rule.alternatives().stream()
				.flatMap(e -> e.symbols().stream())
				.filter(s -> s instanceof Terminal)
				.map(Terminal.class::cast)
				.forEach(nt -> {
					if (!distinct.contains(nt)) {
						terminals.add(nt);
						distinct.add(nt);
					}
				});
		}

		return List.copyOf(terminals);
	}

	public List<NonTerminal> nonTerminals() {
		return nonTerminals;
	}

	public List<Terminal> terminals() {
		return terminals;
	}

	public NonTerminal start() {
		return start;
	}

	public List<Rule> rules() {
		return rules;
	}

	public Optional<Rule> rule(final NonTerminal nt) {
		requireNonNull(nt);
		return rules.stream()
			.filter(rule -> rule.start().equals(nt))
			.findFirst();
	}

	public List<Terminal> build(final Random random) {
		Rule rule = rules.get(0);

		List<Symbol> symbols = expand(rule.start(), random);

		boolean expanded = true;
		while (expanded) {
			expanded = false;

			final List<Symbol> result = new ArrayList<>();
			for (var symbol : symbols) {
				if (symbol instanceof NonTerminal) {
					result.addAll(expand((NonTerminal)symbol, random));
					expanded = true;
				} else {
					result.add(symbol);
				}
			}

			symbols = result;
		}

		return (List<Terminal>)(Object)symbols;
	}

	private List<Symbol> expand(final NonTerminal nt, final Random random) {
		final var rule = rule(nt);
		return rule
			.map(r -> r.alternatives()
				.get(random.nextInt(r.alternatives().size()))
				.symbols())
			.orElse(List.of(nt));
	}

	public static Grammar parse(final String bnf) {
		return BnfParser.parse(bnf);
	}

	@Override
	public String toString() {
		return rules.stream()
			.map(Object::toString)
			.collect(Collectors.joining("\n"));
	}

}
