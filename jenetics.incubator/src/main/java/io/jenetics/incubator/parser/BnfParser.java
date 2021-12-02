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
package io.jenetics.incubator.parser;

import java.util.ArrayList;
import java.util.List;

import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.ASSIGN;
import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.BAR;
import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.ID;
import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.LBRACE;
import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.LEND;
import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.LPAREN;
import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.LT;
import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.QUOTED_STRING;
import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.RBRACE;
import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.REND;
import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.RPAREN;
import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.STRING;
import static io.jenetics.incubator.parser.Token.Type.EOF;
import static java.lang.String.format;

import io.jenetics.incubator.parser.Bnf.Expression;
import io.jenetics.incubator.parser.Bnf.NonTerminal;
import io.jenetics.incubator.parser.Bnf.Repetition;
import io.jenetics.incubator.parser.Bnf.Rule;
import io.jenetics.incubator.parser.Bnf.Symbol;
import io.jenetics.incubator.parser.Bnf.Terminal;

/**
 * rulelist: rule_* EOF;
 * rule_: lhs ASSIGN rhs;
 * lhs: id_;
 * rhs: alternatives;
 * alternatives: alternative (BAR alternative)*;
 * alternative: element*;
 * element: optional_ | zeroormore | oneormore | text_ | id_;
 * optional_: REND alternatives LEND;
 * zeroormore: RBRACE alternatives LBRACE;
 * oneormore: RPAREN alternatives LPAREN;
 * text_: STRING;
 * id_: LT ruleid GT;
 * ruleid: ID;
 */
public class BnfParser extends Parser {

	NonTerminal start = null;
	Repetition repetition = new Repetition(1, 1);
	final List<Rule> rules = new ArrayList<>();
	final List<Symbol> symbols = new ArrayList<>();
	final List<Expression> alternatives = new ArrayList<>();

	protected BnfParser(final BnfTokenizer tokenizer) {
		super(tokenizer, 4);
	}

	public Bnf parse() {
		rulelist();
		return new Bnf(rules);
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
			alternatives.add(new Expression(symbols, repetition));
			symbols.clear();
		}

		while (LA(1) == BAR.code()) {
			match(BAR);
			alternative();

			if (!symbols.isEmpty()) {
				alternatives.add(new Expression(symbols, repetition));
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
				LA(1) == REND.code() ||
				LA(1) == RBRACE.code() ||
				LA(1) == RPAREN.code() ||
				LA(1) == STRING.code() ||
				LA(1) == QUOTED_STRING.code() ||
				LA(1) == ID.code() ||
				LA(1) == LT.code()
			)
		);
	}

	private void element() {
		if (LA(1) == REND.code()) {
			optional();
		} else if (LA(1) == RBRACE.code()) {
			zeroormore();
		} else if (LA(1) == RPAREN.code()) {
			oneormore();
		} else if (LA(1) == STRING.code()) {
			symbols.add(text());
		} else if (LA(1) == QUOTED_STRING.code()) {
			symbols.add(text());
		} else if (LA(1) == ID.code()) {
			symbols.add(text());
		} else if (LA(1) == LT.code()) {
			symbols.add(id());
		} else {
			throw new ParseException(format(
				"Expecting %s but found %s.",
				List.of(REND, RBRACE, RPAREN, STRING, QUOTED_STRING, ID, LT), LT(1)
			));
		}
	}

	private void optional() {
		match(REND);
		alternatives();
		match(LEND);
		repetition = new Repetition(0, 1);
	}

	private void zeroormore() {
		match(RBRACE);
		alternatives();
		match(LBRACE);
		repetition = new Repetition(0, Integer.MAX_VALUE);
	}

	private void oneormore() {
		match(RPAREN);
		alternatives();
		match(LPAREN);
		repetition = new Repetition(1, Integer.MAX_VALUE);
	}

	private Terminal text() {
		if (LA(1) == STRING.code()) {
			return new Terminal(match(STRING));
		} else if (LA(1) == QUOTED_STRING.code()) {
			return new Terminal(match(QUOTED_STRING));
		} else if (LA(1) == ID.code()) {
			return new Terminal(match(ID));
		} else {
			throw new ParseException(format(
				"Expecting %s but found %s.",
				List.of(STRING, QUOTED_STRING, ID), LT(1)
			));
		}
	}

	private NonTerminal id() {
		match(BnfTokenizer.BnfTokenType.LT);
		final var result = ruleid();
		match(BnfTokenizer.BnfTokenType.GT);
		return result;
	}

	private NonTerminal ruleid() {
		return new NonTerminal(match(BnfTokenizer.BnfTokenType.ID));
	}

}
