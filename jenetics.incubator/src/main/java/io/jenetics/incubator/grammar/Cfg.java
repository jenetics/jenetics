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
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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
 * You ca easily create a <em>Cfg</em> object from a given BNF grammar.
 * <pre>{@code
 * final Cfg grammar = Bnf.parse("""
 *     <expr> ::= <num> | <var> | '(' <expr> <op> <expr> ')'
 *     <op>   ::= + | - | * | /
 *     <var>  ::= x | y
 *     <num>  ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
 *     """
 * );
 * }</pre>
 *
 * @see io.jenetics.incubator.grammar.bnf.Bnf#parse(String)
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
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if a rule is defined more than once, the
	 *         start symbol points to a missing rule or the rules uses symbols
	 *         not defined in the list of {@link #nonTerminals()} or
	 *         {@link #terminals()}
	 */
	public Cfg {
		if (rules.isEmpty()) {
			throw new IllegalArgumentException(
				"The given list of rules must not be empty."
			);
		}

		// Check uniqueness of the rules.
		final var duplicatedRules = rules.stream()
			.collect(Collectors.groupingBy(Rule::start))
			.values().stream()
			.filter(values -> values.size() > 1)
			.map(rule -> rule.get(0).start.value)
			.toList();

		if (!duplicatedRules.isEmpty()) {
			throw new IllegalArgumentException(
				"Found duplicate rule(s), " + duplicatedRules + "."
			);
		}

		// Check if start symbol points to an existing rule.
		final var startRule = rules.stream()
			.filter(r -> start.equals(r.start))
			.findFirst();
		if (startRule.isEmpty()) {
			throw new IllegalArgumentException(
				"No rule found for start symbol %s.".formatted(start)
			);
		}

		// Check that all symbols used in the given rules are also defined
		// in the list of non-terminals and terminals.
		final Set<Symbol> required = rules.stream()
			.flatMap(Cfg::ruleSymbols)
			.collect(Collectors.toUnmodifiableSet());

		final Set<Symbol> available = Stream
			.concat(nonTerminals.stream(), terminals.stream())
			.collect(Collectors.toUnmodifiableSet());

		final var missing = new HashSet<>(required);
		missing.removeAll(available);

		if (!missing.isEmpty()) {
			throw new IllegalArgumentException(
				"Unknown symbols defined in rules: " + missing
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
	 * Create a grammar object with the given rules. Duplicated rules are merged
	 * into one rule. The <em>start</em> symbol of the first rule is chosen as
	 * the start symbol of the created CFG
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

		final List<Rule> normalizedRules = normalize(rules);

		final List<Symbol> symbols = normalizedRules.stream()
			.flatMap(Cfg::ruleSymbols)
			.distinct()
			.toList();

		final List<NonTerminal> nonTerminals = symbols.stream()
			.filter(NonTerminal.class::isInstance)
			.map(NonTerminal.class::cast)
			.toList();

		final List<Terminal> terminals = symbols.stream()
			.filter(Terminal.class::isInstance)
			.map(Terminal.class::cast)
			.toList();

		return new Cfg(
			nonTerminals,
			terminals,
			normalizedRules.stream()
				.map(r -> rebuild(r, symbols))
				.toList(),
			(NonTerminal)select(normalizedRules.get(0).start(), symbols)
		);
	}

	private static List<Rule> normalize(final List<Rule> rules) {
		final Map<NonTerminal, List<Rule>> grouped = rules.stream()
			.collect(groupingBy(
				Rule::start,
				LinkedHashMap::new,
				toCollection(ArrayList::new)));

		return grouped.entrySet().stream()
			.map(entry -> merge(entry.getKey(), entry.getValue()))
			.toList();
	}

	private static Rule merge(final NonTerminal start, final List<Rule> rules) {
		return new Rule(
			start,
			rules.stream()
				.flatMap(rule -> rule.alternatives().stream())
				.toList()
		);
	}

	private static Stream<Symbol> ruleSymbols(final Rule rule) {
		return Stream.concat(
			Stream.of(rule.start),
			rule.alternatives.stream()
				.flatMap(expr -> expr.symbols().stream())
		);
	}

	private static Rule rebuild(final Rule rule, final List<Symbol> symbols) {
		return new Rule(
			(NonTerminal)select(rule.start, symbols),
			rule.alternatives.stream()
				.map(e -> rebuild(e, symbols))
				.toList()
		);
	}

	private static Expression
	rebuild(final Expression expression, final List<Symbol> symbols) {
		return new Expression(
			expression.symbols.stream()
				.map(s -> select(s, symbols))
				.toList()
		);
	}

	private static Symbol select(final Symbol symbol, final List<Symbol> symbols) {
		for (var s : symbols) {
			if (s.equals(symbol)) {
				return s;
			}
		}
		throw new AssertionError("Symbol not found: " + symbol);
	}

}
