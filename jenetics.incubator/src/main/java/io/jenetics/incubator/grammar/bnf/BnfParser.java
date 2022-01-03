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
package io.jenetics.incubator.grammar.bnf;

import static java.lang.String.format;
import static io.jenetics.incubator.grammar.bnf.BnfTokenizer.BnfTokenType.ASSIGN;
import static io.jenetics.incubator.grammar.bnf.BnfTokenizer.BnfTokenType.BAR;
import static io.jenetics.incubator.grammar.bnf.BnfTokenizer.BnfTokenType.GT;
import static io.jenetics.incubator.grammar.bnf.BnfTokenizer.BnfTokenType.ID;
import static io.jenetics.incubator.grammar.bnf.BnfTokenizer.BnfTokenType.LT;
import static io.jenetics.incubator.grammar.bnf.BnfTokenizer.BnfTokenType.QUOTED_STRING;
import static io.jenetics.incubator.grammar.bnf.BnfTokenizer.BnfTokenType.STRING;
import static io.jenetics.incubator.parser.Token.Type.EOF;

import java.util.ArrayList;
import java.util.List;

import io.jenetics.incubator.grammar.Cfg;
import io.jenetics.incubator.grammar.Cfg.Expression;
import io.jenetics.incubator.grammar.Cfg.NonTerminal;
import io.jenetics.incubator.grammar.Cfg.Rule;
import io.jenetics.incubator.grammar.Cfg.Symbol;
import io.jenetics.incubator.grammar.Cfg.Terminal;
import io.jenetics.incubator.parser.Parser;
import io.jenetics.incubator.parser.ParsingException;

/**
 * Parser for BNF grammars.
 *
 * <pre>{@code
 * rulelist: rule_* EOF;
 * rule: lhs ASSIGN rhs;
 * lhs: id;
 * rhs: alternatives;
 * alternatives: alternative (BAR alternative)*;
 * alternative: element*;
 * element:  text | id;
 * text: STRING | QUOTED_STRING;
 * id: LT ruleid GT;
 * ruleid: ID;
 * }</pre>
 *
 * The BNF object is build from the following classes.
 * <ul>
 *     <li>{@link Symbol}: A symbol is either a {@link Terminal} or
 *     {@link NonTerminal} symbol.</li>
 *     <li>{@link NonTerminal}: Non-terminal symbols are parenthesised in angle
 *     brackets; {@code <expr>}, {@code num} or {@code var}. The name must start
 *     with a letter and contain only letters and digits:
 *     {@code ('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9'|'-')+}</li>
 *     <li>{@link Terminal}: Terminal symbols are simple string values, which
 *     can also be quoted; {@code x}, {@code 1}, {@code terminal} or
 *     {@code 'some $special value'}</li>
 *     <li>{@link Expression}: Consists of a list of symbols; {@code [num]},
 *     {@code [var]} or {@code [(, expr, op, expr, )]}</li>
 *     <li>{@link Rule}: A rule has a name, a non-terminal start symbol, and a
 *     list of <em>alternative</em> expressions;
 *     {@code <expr> ::= [[num], [var], [(, expr, op, expr, )]]}</li>
 *     <li>{@link Cfg}: A whole BNF grammar consists of one or more {@link Rule}s.</li>
 * </ul>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 7.0
 * @version 7.0
 */
final class BnfParser extends Parser<String> {

	NonTerminal start = null;
	final List<Rule> rules = new ArrayList<>();
	final List<Symbol> symbols = new ArrayList<>();
	final List<Expression> alternatives = new ArrayList<>();

	BnfParser(final BnfTokenizer tokenizer) {
		super(tokenizer, 4);
	}

	public Cfg parse() {
		rulelist();

		return Cfg.of(rules);
	}

	private void rulelist() {
		do {
			rule();
		} while (LA(1) != EOF.code());
	}

	private void rule() {
		start = lhs();
		match(ASSIGN);
		rhs();

		rules.add(new Rule(start, alternatives));
		start = null;
		alternatives.clear();
	}

	private NonTerminal lhs() {
		return id();
	}

	private void rhs() {
		alternatives();
	}

	private void alternatives() {
		alternative();
		if (!symbols.isEmpty()) {
			alternatives.add(new Expression(symbols));
			symbols.clear();
		}

		while (LA(1) == BAR.code()) {
			match(BAR);
			alternative();

			if (!symbols.isEmpty()) {
				alternatives.add(new Expression(symbols));
				symbols.clear();
			}
		}
	}

	private void alternative() {
		do {
			element();
		} while (
			LA(4) != ASSIGN.code() &&
			(
				LA(1) == STRING.code() ||
				LA(1) == QUOTED_STRING.code() ||
				LA(1) == ID.code() ||
				LA(1) == LT.code()
			)
		);
	}

	private void element() {
		if (LA(1) == STRING.code()) {
			symbols.add(text());
		} else if (LA(1) == QUOTED_STRING.code()) {
			symbols.add(text());
		} else if (LA(1) == ID.code()) {
			symbols.add(text());
		} else if (LA(1) == LT.code()) {
			symbols.add(id());
		} else {
			throw new ParsingException(format(
				"Expecting %s but found %s.",
				List.of(STRING, QUOTED_STRING, ID, LT), LT(1)
			));
		}
	}

	private Terminal text() {
		if (LA(1) == STRING.code()) {
			return terminal(match(STRING).value());
		} else if (LA(1) == QUOTED_STRING.code()) {
			return terminal(match(QUOTED_STRING).value());
		} else if (LA(1) == ID.code()) {
			return terminal(match(ID).value());
		} else {
			throw new ParsingException(format(
				"Expecting %s but found %s.",
				List.of(STRING, QUOTED_STRING, ID), LT(1)
			));
		}
	}

	private static Terminal terminal(final String value) {
		if (value.isEmpty()) {
			throw new ParsingException("Terminal value must not be empty.");
		}
		return new Terminal(value);
	}

	private NonTerminal id() {
		match(LT);
		final var result = ruleid();
		match(GT);
		return result;
	}

	private NonTerminal ruleid() {
		final var value = match(ID).value();
		if (value.isEmpty()) {
			throw new ParsingException("Rule id must not be empty.");
		}
		return new NonTerminal(value);
	}

}
