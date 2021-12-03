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
package io.jenetics.incubator.grammar_old;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.jenetics.ext.util.TreeNode;

/**
 * Represents a context-free grammar.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public final class Grammar {

	/**
	 * Represents the <em>symbols</em> the (context-free) grammar consists.
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
	 * Represents the non-terminal symbols of the grammar.
	 */
	public static record NonTerminal(String value) implements Symbol {

		/**
		 * @param value the value of the non-terminal symbol
		 */
		public NonTerminal {
			if (value.isBlank()) {
				throw new IllegalArgumentException(
					"Non-terminal must not be blank: '" + value + "'."
				);
			}
		}

		@Override
		public String toString() {
			return format("<%s>", value);
		}
	}

	/**
	 * Represents a terminal symbols of the grammar.
	 */
	public static record Terminal(String value) implements Symbol {

		/**
		 * @param value the value of the terminal symbol
		 */
		public Terminal {
			if (value.isBlank()) {
				throw new IllegalArgumentException(
					"Non-terminal must not be null."
				);
			}
		}

		@Override
		public String toString() {
			return BnfParser.escape(value);
		}
	}

	/**
	 * Represents one <em>expression</em> a production rule consists of.
	 */
	public static record Expression(List<Symbol> symbols) {

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

		@Override
		public String toString() {
			return symbols.stream()
				.map(Object::toString)
				.collect(Collectors.joining(" "));
		}
	}

	/**
	 * Represents a production rule of the grammar.
	 */
	public static record Rule(NonTerminal start, List<Expression> expressions) {

		/**
		 * Creates a new rule object.
		 *
		 * @param start the start symbol of the rule
		 * @param expressions the list af expressions of the rules
		 * @throws IllegalArgumentException if the given list of
		 *         {@code expressions} is empty
		 * @throws NullPointerException if one of the arguments is {@code null}
		 */
		public Rule {
			requireNonNull(start);
			if (expressions.isEmpty()) {
				throw new IllegalArgumentException(
					"Rule expressions must not be empty."
				);
			}
			expressions = List.copyOf(expressions);
		}

		@Override
		public String toString() {
			return format(
				"%s ::= %s",
				start,
				expressions.stream()
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

		nonTerminals = toDistinctSymbols(rules, NonTerminal.class);
		terminals = toDistinctSymbols(rules, Terminal.class);
		this.start = rules.get(0).start();
		this.rules = List.copyOf(rules);
	}

	private static <S extends Symbol> List<S>
	toDistinctSymbols(final List<Rule> rules, final Class<S> type) {
		return rules.stream()
			.flatMap(rule -> Stream.concat(
					Stream.of(rule.start),
					rule.expressions.stream().flatMap(e -> e.symbols.stream())
				))
			.filter(type::isInstance)
			.map(type::cast)
			.distinct()
			.toList();
	}

	/**
	 * Return the non-terminal symbols of {@code this} grammar.
	 *
	 * @return the non-terminal symbols of {@code this} grammar
	 */
	public List<NonTerminal> nonTerminals() {
		return nonTerminals;
	}

	/**
	 * Return the terminal symbols of {@code this} grammar.
	 *
	 * @return the terminal symbols of {@code this} grammar
	 */
	public List<Terminal> terminals() {
		return terminals;
	}

	/**
	 * Return the start symbol of {@code this} grammar.
	 *
	 * @return the start symbol of {@code this} grammar
	 */
	public NonTerminal start() {
		return start;
	}

	/**
	 * Return the <em>production</em> rules of {@code this} grammar.
	 *
	 * @return the <em>production</em> rules of {@code this} grammar
	 */
	public List<Rule> rules() {
		return rules;
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
		return rules.stream()
			.filter(rule -> rule.start().equals(start))
			.findFirst();
	}

	public List<Terminal> generate(final SymbolIndex index) {
		return StandardGenerators.generateList(this, index);
	}

	public TreeNode<Terminal> parse(final Rule rule, final List<Terminal> symbols) {
		TreeNode<Terminal> node = null;

		return node;

//		TreeNode<String> out_node = null;
//		//MutableString n_input = new MutableString(input);
//		Symbol n_input = null;
//		boolean wrong_symbol = true;
//		boolean read_epsilon = false;
//		//log("Considering input '" + input + "' with rule " + rule, level);
//		for (Expression alt : rule.expressions()) {
//			out_node = TreeNode.of();
//
//			NonTerminal left_hand_side = rule.start();
//			out_node.value(left_hand_side.toString());
//
//			Expression new_alt = alt;
//			Iterator<Symbol> alt_it = new_alt.symbols().iterator();
//			//n_input = new MutableString(input);
//			wrong_symbol = false;
//			while (alt_it.hasNext() && !wrong_symbol)
//			{
//				//n_input.trim();
//				Symbol alt_tok = alt_it.next();
//				if (alt_tok instanceof Terminal)
//				{
//					/*
//					if (alt_tok instanceof EpsilonTerminalToken)
//					{
//						// Epsilon always works
//						ParseNode child = new ParseNode();
//						child.setToken("");
//						out_node.addChild(child);
//						read_epsilon = true;
//						break;
//					}
//					 */
//
//					if (symbols.isEmpty()) {
//						// Rule expects a token, string has no more: NO MATCH
//						wrong_symbol = true;
//						break;
//					}
//					//int match_prefix_size = alt_tok.match(n_input.toString());
//
//					//if (match_prefix_size > 0)
//					if (((Terminal) alt_tok).value.equals(symbols.get(0).value()))
//					{
//						//ParseNode child = new ParseNode();
//						TreeNode<String> child = TreeNode.of(symbols.get(0).value());
//						/*
//						MutableString input_tok = n_input.truncateSubstring(0, match_prefix_size);
//						if (alt_tok instanceof RegexTerminalToken)
//						{
//							// In the case of a regex, create children with each capture block
//							child = appendRegexChildren(child, (RegexTerminalToken) alt_tok, input_tok);
//						}
//						child.setToken(input_tok.toString());
//
//						 */
//						out_node.attach(child);
//						symbols.remove(0);
//					}
//					else
//					{
//						System.out.println("ASDFADFASFASDF");
//						// Rule expects a token, token in string does not match: NO MATCH
//						wrong_symbol = true;
//						out_node = null;
//						break;
//					}
//				}
//				else
//				{
//					TreeNode<String> child = null;
//					// Non-terminal token: recursively try to parse it
//					String alt_tok_string = alt_tok.value();
//
//					Rule new_rule = getRule(alt_tok);
//					if (new_rule == null)
//					{
//						// No rule found for non-terminal symbol:
//						// there is an error in the grammar
//						throw new IllegalArgumentException("Cannot find rule for token " + alt_tok);
//
//					}
//					child = parse(new_rule, symbols);
//					if (child == null)
//					{
//						// Parsing failed
//						wrong_symbol = true;
//						out_node = null;
//						break;
//					}
//
//					out_node.attach(child);
//				}
//			}
//			if (!wrong_symbol)
//			{
//				if (!alt_it.hasNext())
//				{
//					// We succeeded in parsing the complete string: done
//					//if (level > 0 || (level == 0 && n_input.toString().trim().length() == 0))
//					if (symbols.isEmpty())
//					{
//						break;
//					}
//				}
//				else
//				{
//					// The rule expects more symbols, but there are none
//					// left in the input; set wrong_symbol back to true to
//					// force exploring the next alternative
//					wrong_symbol = true;
//					//n_input = new MutableString(input);
//					break;
//				}
//			}
//		}
////		int chars_consumed = input.length() - n_input.s();
////		if (wrong_symbol)
////		{
////			// We did not consume anything, and the symbol was not epsilon: fail
////			log("FAILED: expected more symbols with rule " + rule, level);
////			return null;
////		}
////		if (chars_consumed == 0 && !read_epsilon)
////		{
////			// We did not consume anything, and the symbol was not epsilon: fail
////			log("FAILED: did not consume anything of " + input + " with rule " + rule, level);
////			return null;
////		}
////		input.truncateSubstring(chars_consumed);
////		if (level == 0 && !input.isEmpty())
////		{
////			// The top-level rule must parse the complete string
////			log("FAILED: The top-level rule must parse the complete string", level);
////			return null;
////		}
//		return out_node;
	}

	private Rule getRule(final Symbol symbol) {
		if (symbol == null) return null;

		for (Rule rule : rules) {
			if (rule.start.value.equals(symbol.value())) {
				return rule;
			}
		}

		return null;
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
