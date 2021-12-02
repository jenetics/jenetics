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

import java.util.List;

import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.ASSIGN;
import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.BAR;
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

/**
 * rulelist: rule_* EOF;
 * rule_: lhs ASSIGN rhs;
 * lhs: id_;
 * rhs: alternatives;
 * alternatives: alternative (BAR alternative)*;
 * alternative: element*;
 * element: optional_ | zeroormore | oneormore | text_ | id_;
 * optional_: LEND alternatives REND;
 * zeroormore: LBRACE alternatives RBRACE;
 * oneormore: LPAREN alternatives RPAREN;
 * text_: STRING;
 * id_: LT ruleid GT;
 * ruleid: ID;
 */
public class BnfParser extends Parser {

	protected BnfParser(final BnfTokenizer tokenizer) {
		super(tokenizer);
	}

	public void parse() {
		rulelist();
	}

	private void rulelist() {
		do {
			rule();
		} while (_lookahead.type().code() == EOF.code());
	}

	private void rule() {
		lhs();
		match(ASSIGN);
		rhs();
	}

	private void lhs() {
		id();
	}

	private void rhs() {
		alternatives();
	}

	private void alternatives() {
		alternative();
		while (_lookahead.type().code() == BAR.code()) {
			match(BAR);
			alternative();
		}
	}

	private void alternative() {
		do {
			element();
		} while (
			_lookahead.type().code() == LEND.code() ||
			_lookahead.type().code() == LBRACE.code() ||
			_lookahead.type().code() == LPAREN.code() ||
			_lookahead.type().code() == STRING.code() ||
			_lookahead.type().code() == QUOTED_STRING.code() ||
			_lookahead.type().code() == LT.code());
	}

	private void element() {
		if (_lookahead.type().code() == LEND.code()) {
			optional();
		} else if (_lookahead.type().code() == LBRACE.code()) {
			zeroormore();
		} else if (_lookahead.type().code() == LPAREN.code()) {
			oneormore();
		} else if (_lookahead.type().code() == STRING.code()) {
			text();
		} else if (_lookahead.type().code() == QUOTED_STRING.code()) {
			text();
		} else if (_lookahead.type().code() == LT.code()) {
			id();
		} else {
			throw new ParseException(format(
				"Expecting %s but found %s.",
				List.of(LEND, LBRACE, LPAREN, STRING, QUOTED_STRING, LT), _lookahead
			));
		}
	}

	private void optional() {
		match(LEND);
		alternatives();
		match(REND);
	}

	private void zeroormore() {
		match(LBRACE);
		alternatives();
		match(RBRACE);
	}

	private void oneormore() {
		match(LPAREN);
		alternatives();
		match(RPAREN);
	}

	private void text() {
		if (_lookahead.type().code() == STRING.code()) {
			match(STRING);
		} else if (_lookahead.type().code() == QUOTED_STRING.code()) {
			match(QUOTED_STRING);
		} else {
			throw new ParseException(format(
				"Expecting %s but found %s.",
				List.of(STRING, QUOTED_STRING), _lookahead
			));
		}
	}

	private void id() {
		match(BnfTokenizer.BnfTokenType.LT);
		ruleid();
		match(BnfTokenizer.BnfTokenType.GT);
	}

	private void ruleid() {
		match(BnfTokenizer.BnfTokenType.ID);
	}

}
