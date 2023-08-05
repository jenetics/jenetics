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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a <em>context-free</em> grammar
 * (<a href="https://en.wikipedia.org/wiki/Context-free_grammar"><b>CFG</b></a>).
 * <p>
 * <b>Formal definition</b>
 * <p>
 * A context-free grammar {@code G} is defined by the 4-tuple
 * {@code G = (N, T, R, S)}, where
 * <ul>
 *     <li>{@code N} is a finite set; each element {@code n ∈ N} is called a
 *     non-terminal ({@link NonTerminal}) character or a variable. Each
 *     variable represents a different type of phrase or clause in the sentence.
 *     Variables are also sometimes called syntactic categories. Each variable
 *     defines a sub-language of the language defined by {@code G}.
 *     </li>
 *     <li>{@code T} is a finite set of terminals ({@link Terminal}) disjoint
 *     from {@code N}, which make up the actual content of the sentence. The set
 *     of terminals is the alphabet of the language defined by the grammar
 *     {@code G}.
 *     </li>
 *     <li>{@code R} is a finite relation in {@code N × (N ∪ T)∗}, where the
 *     asterisk represents the <a href="https://en.wikipedia.org/wiki/Kleene_star">
 *     Kleene star</a> operation. The members of {@code R} are called the
 *     (rewrite) rules ({@link Rule}) or productions of the grammar.
 *     </li>
 *     <li>{@code S} is the start variable (or start symbol), used to represent
 *     the whole sentence (or program). It must be an element of {@code N}
 *     ({@link NonTerminal})
 *     .</li>
 * </ul>
 *
 * You can easily create a <em>Cfg</em> object from a given BNF grammar.
 * <pre>{@code
 * final Cfg<String> grammar = Bnf.parse("""
 *     <expr> ::= <num> | <var> | '(' <expr> <op> <expr> ')'
 *     <op>   ::= + | - | * | /
 *     <var>  ::= x | y
 *     <num>  ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
 *     """
 * );
 * }</pre>
 *
 * It is also possible to create the grammar above programmatically.
 * <pre>{@code
 * final Cfg<String> grammar = Cfg.of(
 *     R("expr",
 *         E(NT("num")),
 *         E(NT("var")),
 *         E(T("("), NT("expr"), NT("op"), NT("expr"), T(")"))
 *     ),
 *     R("op", E(T("+")), E(T("-")), E(T("*")), E(T("/"))),
 *     R("var", E(T("x")), E(T("y"))),
 *     R("num",
 *         E(T("0")), E(T("1")), E(T("2")), E(T("3")),
 *         E(T("4")), E(T("5")), E(T("6")), E(T("7")),
 *         E(T("8")), E(T("9"))
 *     )
 * );
 * }</pre>
 *
 * @see Bnf#parse(String)
 *
 * @param <T> the terminal symbol value type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 7.1
 * @version 7.1
 */
public record Cfg<T>(
	List<NonTerminal<T>> nonTerminals,
	List<Terminal<T>> terminals,
	List<Rule<T>> rules,
	NonTerminal<T> start
) {

	/**
	 * Represents the <em>symbols</em> the BNF grammar consists.
	 *
	 * @param <T> the terminal symbol value type
	 */
	public sealed interface Symbol<T> {

		/**
		 * Return the name of the symbol.
		 *
		 * @return the name of the symbol
		 */
		String name();

	}

	/**
	 * Represents the non-terminal symbols of the grammar ({@code NT}).
	 *
	 * @param <T> the terminal symbol value type
	 */
	public record NonTerminal<T>(String name) implements Symbol<T> {

		/**
		 * @param name the name of the non-terminal symbol
		 * @throws IllegalArgumentException if the given {@code name} is not
		 *         a valid <em>non-terminal</em> name
		 * @throws NullPointerException if one of the arguments is {@code null}
		 */
		public NonTerminal {
			if (name.isEmpty()) {
				throw new IllegalArgumentException(
					"Non-terminal value must not be empty."
				);
			}
		}
	}

	/**
	 * Represents a terminal symbols of the grammar ({@code T}).
	 *
	 * @param <T> the terminal symbol value type
	 */
	public record Terminal<T>(String name, T value) implements Symbol<T> {

		/**
		 * @param name the name of the terminal symbol
		 * @param value the value of the terminal symbol
		 * @throws IllegalArgumentException if the given terminal {@code name}
		 *         is empty
		 */
		public Terminal {
			if (name.isEmpty()) {
				throw new IllegalArgumentException(
					"Terminal value must not be empty."
				);
			}
		}

		/**
		 * Return a new terminal symbol where the name of the symbol is equal
		 * to its value.
		 *
		 * @param name the name (and value) of the terminal symbol
		 * @return a new terminal symbol with the given {@code name}
		 * @throws IllegalArgumentException if the given terminal {@code name}
		 *         is empty
		 */
		public static Terminal<String> of(final String name) {
			return new Terminal<>(name, name);
		}

	}

	/**
	 * Represents one <em>expression</em> (list of alternative symbols) a
	 * production rule consists of.
	 *
	 * @param <T> the terminal symbol value type
	 */
	public record Expression<T>(List<Symbol<T>> symbols) {

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
	 *
	 * @param <T> the terminal symbol value type
	 */
	public record Rule<T>(NonTerminal<T> start, List<Expression<T>> alternatives) {

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
			.map(rule -> rule.get(0).start.name)
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
		final Set<Symbol<T>> required = rules.stream()
			.flatMap(Cfg::ruleSymbols)
			.collect(Collectors.toUnmodifiableSet());

		final Set<Symbol<T>> available = Stream
			.concat(nonTerminals.stream(), terminals.stream())
			.collect(Collectors.toUnmodifiableSet());

		final var missing = new HashSet<>(required);
		missing.removeAll(available);

		if (!missing.isEmpty()) {
			throw new IllegalArgumentException(
				"Unknown symbols defined in rules: " + missing
			);
		}

		// Check if the name of terminals and non-terminals are distinct.
		final var terminalNames = terminals.stream()
			.map(Symbol::name)
			.collect(Collectors.toSet());

		final var nonTerminalNames = nonTerminals.stream()
			.map(Symbol::name)
			.collect(Collectors.toSet());

		terminalNames.retainAll(nonTerminalNames);
		if (!terminalNames.isEmpty()) {
			throw new IllegalArgumentException(format(
				"Terminal and non-terminal symbols with same name: %s",
				terminalNames.stream().sorted().toList()
			));
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
	public Optional<Rule<T>> rule(final NonTerminal<?> start) {
		requireNonNull(start);
		for (var rule : rules) {
			if (rule.start().name().equals(start.name())) {
				return Optional.of(rule);
			}
		}
		return Optional.empty();
	}

	/**
	 * Maps the values of the terminal symbols from type {@code T} to type
	 * {@code A}.
	 *
	 * @param mapper the mapper function
	 * @param <A> the new value type of the terminal symbols
	 * @return the mapped grammar
	 * @throws NullPointerException if the given mapper is {@code null}
	 */
	public <A> Cfg<A> map(final Function<? super Terminal<T>, ? extends A> mapper) {
		requireNonNull(mapper);

		final var cache = new HashMap<Terminal<T>, Terminal<A>>();
		final Function<Terminal<T>, Terminal<A>> mapping = t -> cache
			.computeIfAbsent(t, t2 -> new Terminal<>(t2.name(), mapper.apply(t2)));

		@SuppressWarnings("unchecked")
		final List<Rule<A>> rules = rules().stream()
			.map(rule -> new Rule<>(
				(NonTerminal<A>)rule.start(),
				rule.alternatives().stream()
					.map(expr -> new Expression<>(
						expr.symbols().stream()
							.map(sym -> sym instanceof Cfg.Terminal<T> t
								? mapping.apply(t) : (Symbol<A>)sym)
							.toList()
						))
					.toList()
				))
			.toList();

		return Cfg.of(rules);
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
	public static <T> Cfg<T> of(final List<Rule<T>> rules) {
		if (rules.isEmpty()) {
			throw new IllegalArgumentException(
				"The list of rules must not be empty."
			);
		}

		final List<Rule<T>> normalizedRules = normalize(rules);

		final List<Symbol<T>> symbols = normalizedRules.stream()
			.flatMap(Cfg::ruleSymbols)
			.distinct()
			.toList();

		final List<NonTerminal<T>> nonTerminals = symbols.stream()
			.filter(NonTerminal.class::isInstance)
			.map(nt -> (NonTerminal<T>)nt)
			.toList();

		final List<Terminal<T>> terminals = symbols.stream()
			.filter(Terminal.class::isInstance)
			.map(nt -> (Terminal<T>)nt)
			.toList();

		return new Cfg<>(
			nonTerminals,
			terminals,
			normalizedRules.stream()
				.map(r -> rebuild(r, symbols))
				.toList(),
			(NonTerminal<T>)select(normalizedRules.get(0).start(), symbols)
		);
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
	@SafeVarargs
	public static <T> Cfg<T> of(final Rule<T>... rules) {
		return Cfg.of(List.of(rules));
	}

	private static <T> List<Rule<T>> normalize(final List<Rule<T>> rules) {
		final Map<NonTerminal<T>, List<Rule<T>>> grouped = rules.stream()
			.collect(groupingBy(
				Rule::start,
				LinkedHashMap::new,
				toCollection(ArrayList::new)));

		return grouped.entrySet().stream()
			.map(entry -> merge(entry.getKey(), entry.getValue()))
			.toList();
	}

	private static <T> Rule<T> merge(final NonTerminal<T> start, final List<Rule<T>> rules) {
		return new Rule<>(
			start,
			rules.stream()
				.flatMap(rule -> rule.alternatives().stream())
				.toList()
		);
	}

	private static <T> Stream<Symbol<T>> ruleSymbols(final Rule<T> rule) {
		return Stream.concat(
			Stream.of(rule.start),
			rule.alternatives.stream()
				.flatMap(expr -> expr.symbols().stream())
		);
	}

	private static <T> Rule<T> rebuild(final Rule<T> rule, final List<Symbol<T>> symbols) {
		return new Rule<>(
			(NonTerminal<T>)select(rule.start, symbols),
			rule.alternatives.stream()
				.map(e -> rebuild(e, symbols))
				.toList()
		);
	}

	private static <T> Expression<T>
	rebuild(final Expression<T> expression, final List<Symbol<T>> symbols) {
		return new Expression<>(
			expression.symbols.stream()
				.map(s -> select(s, symbols))
				.toList()
		);
	}

	private static <T> Symbol<T> select(
		final Symbol<T> symbol,
		final List<Symbol<T>> symbols
	) {
		for (var s : symbols) {
			if (s.name().equals(symbol.name())) {
				return s;
			}
		}
		throw new AssertionError("Symbol not found: " + symbol);
	}

	@SuppressWarnings("unchecked")
	static <A, B extends A> Cfg<A> upcast(final Cfg<B> seq) {
		return (Cfg<A>)seq;
	}


	/* *************************************************************************
	 * Static factory methods for rule creation.
	 * ************************************************************************/

	/**
	 * Factory method for creating a terminal symbol with the given
	 * {@code name} and {@code value}.
	 *
	 * @param name the name of the terminal symbol
	 * @param value the value of the terminal symbol
	 * @param <T> the terminal symbol value type
	 * @return a new terminal symbol
	 */
	public static <T> Terminal<T> T(final String name, final T value) {
		return new Terminal<>(name, value);
	}

	/**
	 * Factory method for creating a terminal symbol with the given
	 * {@code name}.
	 *
	 * @param name the name of the terminal symbol
	 * @return a new terminal symbol
	 */
	public static Terminal<String> T(final String name) {
		return new Terminal<>(name, name);
	}

	/**
	 * Factory method for creating non-terminal symbols.
	 *
	 * @param name the name of the symbol.
	 * @param <T> the terminal symbol value type
	 * @return a new non-terminal symbol
	 */
	public static <T> NonTerminal<T> N(final String name) {
		return new NonTerminal<>(name);
	}

	/**
	 * Factory method for creating an expression with the given
	 * {@code symbols}.
	 *
	 * @param symbols the list of symbols of the expression
	 * @throws IllegalArgumentException if the list of {@code symbols} is
	 *         empty
	 * @param <T> the terminal symbol value type
	 * @return a new expression
	 */
	@SafeVarargs
	public static <T> Expression<T> E(final Symbol<T>... symbols) {
		return new Expression<>(List.of(symbols));
	}

	/**
	 * Factory method for creating a new rule.
	 *
	 * @param name the name of start symbol of the rule
	 * @param alternatives the list af alternative rule expressions
	 * @throws IllegalArgumentException if the given list of
	 *         {@code alternatives} is empty
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @param <T> the terminal symbol value type
	 * @return a new rule
	 */
	@SafeVarargs
	public static <T> Rule<T> R(
		final String name,
		final Expression<T>... alternatives
	) {
		return new Rule<>(new NonTerminal<>(name), List.of(alternatives));
	}

}
