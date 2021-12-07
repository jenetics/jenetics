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

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Represents a <em>context-free</em> grammar
 * (<a href="https://en.wikipedia.org/wiki/Context-free_grammar"><b>CFG</b></a>).
 * <p>
 * <b>Formal definition</b>
 * <p>
 * A context-free grammar {@code G} is defined by the 4-tuple
 * {@code G = (V, Σ, R, S)}, where
 * <ul>
 *     <li>{@code V} is a finite set; each element {@code v ∈ V} is called a
 *     non-terminal ({@link NonTerminal}) character or a variable. Each
 *     variable represents a different type of phrase or clause in the sentence.
 *     Variables are also sometimes called syntactic categories. Each variable
 *     defines a sub-language of the language defined by {@code G}.
 *     </li>
 *     <li>{@code Σ} is a finite set of terminals ({@link Terminal}) disjoint
 *     from {@code V}, which make up the actual content of the sentence. The set
 *     of terminals is the alphabet of the language defined by the grammar
 *     {@code G}.
 *     </li>
 *     <li>{@code R} is a finite relation in {@code V × (V ∪ Σ)∗}, where the
 *     asterisk represents the <a href="https://en.wikipedia.org/wiki/Kleene_star">
 *     Kleene star</a> operation. The members of {@code R} are called the
 *     (rewrite) rules ({@link Rule}) or productions of the grammar.
 *     </li>
 *     <li>{@code S} is the start variable (or start symbol), used to represent
 *     the whole sentence (or program). It must be an element of {@code V}
 *     ({@link NonTerminal})
 *     .</li>
 * </ul>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 7.0
 * @version 7.0
 */
public record Cfg(
	List<NonTerminal> nonTerminals,
	List<Terminal> terminals,
	List<Rule> rules,
	NonTerminal start
) {

	/**
	 * Represents the <em>symbols</em> the BNF grammar consists.
	 */
	public sealed interface Symbol {

		/**
		 * Return the value of the symbol.
		 *
		 * @return the value of the symbol
		 */
		String value();
	}

	/**
	 * Represents the non-terminal symbols of the grammar ({@code V}).
	 */
	public record NonTerminal(String value) implements Symbol {

		/**
		 * @param value the value of the non-terminal symbol
		 * @throws IllegalArgumentException if the given {@code value} is not
		 *         a valid <em>non-terminal</em> name
		 */
		public NonTerminal {
			if (value.isEmpty()) {
				throw new IllegalArgumentException(
					"Non-terminal value must not be empty."
				);
			}
		}
	}

	/**
	 * Represents a terminal symbols of the grammar ({@code Σ}).
	 */
	public record Terminal(String value) implements Symbol {

		/**
		 * @param value the value of the terminal symbol
		 */
		public Terminal {
			if (value.isEmpty()) {
				throw new IllegalArgumentException(
					"Terminal value must not be empty."
				);
			}
		}
	}

	/**
	 * Represents one <em>expression</em> a production rule consists of.
	 */
	public record Expression(List<Symbol> symbols) {

		/**
		 * @param symbols the list of symbols of the expression
		 * @throws IllegalArgumentException if the list of {@code symbols} is
		 *         empty
		 */
		public Expression {
			if (symbols.isEmpty()) {
				throw new IllegalArgumentException(
					"The list of symbols must not be empty."
				);
			}
			symbols = List.copyOf(symbols);
		}
	}

	/**
	 * Represents a production rule of the grammar ({@code R}).
	 */
	public record Rule(NonTerminal start, List<Expression> alternatives) {

		/**
		 * Creates a new rule object.
		 *
		 * @param start the start symbol of the rule
		 * @param alternatives the list af alternative rule expressions
		 * @throws IllegalArgumentException if the given list of
		 *         {@code alternatives} is empty
		 * @throws NullPointerException if one of the arguments is {@code null}
		 */
		public Rule {
			requireNonNull(start);
			if (alternatives.isEmpty()) {
				throw new IllegalArgumentException(
					"Rule alternatives must not be empty."
				);
			}
			alternatives = List.copyOf(alternatives);
		}
	}

	/**
	 * Create a new <em>context-free</em> grammar object.
	 *
	 * @param nonTerminals the non-terminal symbols of {@code this} grammar
	 * @param terminals the terminal symbols of {@code this} grammar
	 * @param rules the <em>production</em> rules of {@code this} grammar
	 * @param start the start symbol of {@code this} grammar
	 * @throws NullPointerException if one of the argumens is {@code null}
	 */
	public Cfg {
		if (rules.isEmpty()) {
			throw new IllegalArgumentException(
				"The given list of rules must not be empty."
			);
		}

		nonTerminals = List.copyOf(nonTerminals);
		terminals = List.copyOf(terminals);
		rules = List.copyOf(rules);
		requireNonNull(start);
	}

	/**
	 * Return the rule for the given {@code start} symbol.
	 *
	 * @param start the start symbol of the rule
	 * @return the rule for the given {@code start} symbol
	 * @throws NullPointerException if the given {@code start} symbol is
	 *         {@code null}
	 */
	public Optional<Rule> rule(final NonTerminal start) {
		requireNonNull(start);
		for (var rule : rules) {
			if (rule.start().equals(start)) {
				return Optional.of(rule);
			}
		}
		return Optional.empty();
	}

	/**
	 * Create a grammar object with the given rules.
	 *
	 * @param rules the rules the grammar consists of
	 * @throws IllegalArgumentException if the list of rules is empty
	 * @throws NullPointerException if the list of rules is {@code null}
	 */
	public static Cfg of(final List<Rule> rules) {
		if (rules.isEmpty()) {
			throw new IllegalArgumentException(
				"The list of rules must not be empty."
			);
		}

		final List<Symbol> symbols = rules.stream()
			.flatMap(rule ->
				Stream.concat(
					Stream.of(rule.start),
					rule.alternatives.stream()
						.flatMap(e -> e.symbols.stream()))
			)
			.distinct()
			.toList();

		return new Cfg(
			symbols.stream()
				.filter(NonTerminal.class::isInstance)
				.map(NonTerminal.class::cast)
				.toList(),
			symbols.stream()
				.filter(Terminal.class::isInstance)
				.map(Terminal.class::cast)
				.toList(),
			rules.stream()
				.map(r -> normalize(r, symbols))
				.toList(),
			(NonTerminal)normalize(rules.get(0).start(), symbols)
		);
	}

	private static Rule
	normalize(final Rule rule, final List<Symbol> symbols) {
		return new Rule(
			(NonTerminal)normalize(rule.start, symbols),
			rule.alternatives.stream()
				.map(e -> normalize(e, symbols))
				.toList()
		);
	}

	private static Expression
	normalize(final Expression expression, final List<Symbol> symbols) {
		return new Expression(
			expression.symbols.stream()
				.map(s -> normalize(s, symbols))
				.toList()
		);
	}

	private static Symbol
	normalize(final Symbol symbol, final List<Symbol> symbols) {
		for (var s : symbols) {
			if (s.equals(symbol)) {
				return s;
			}
		}
		throw new AssertionError("Symbol not found: " + symbol);
	}

}
