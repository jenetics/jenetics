package io.jenetics.incubator.parser;

import static java.lang.Character.isJavaIdentifierPart;
import static java.lang.Character.isJavaIdentifierStart;
import static java.lang.Character.isWhitespace;
import static java.lang.String.format;

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
 *
 * ASSIGN: '::=';
 * LPAREN: ')';
 * RPAREN: '(';
 * LBRACE: '}';
 * RBRACE: '{';
 * LEND: ']';
 * REND: '[';
 * BAR: '|';
 * GT: '>';
 * LT: '<';
 * STRING: ( '%s' | '%i' )? '"' ( ~ '"' )* '"';
 * ID: ('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9'|'-'|' ')+;
 * WS: [ \r\n\t] -> skip;
 */
public class BnfParser {

	static final class BnfTokenizer extends Tokenizer {
		static final int ASSIGN = 2;
		static final int LPAREN = 3;
		static final int RPAREN = 4;
		static final int LBRACE = 5;
		static final int RBRACE = 6;
		static final int LEND = 7;
		static final int REND = 8;
		static final int BAR = 9;
		static final int GT = 10;
		static final int LT = 11;
		static final int STRING = 12;
		static final int ID = 13;

		protected BnfTokenizer(final CharSequence input) {
			super(input);
		}

		@Override
		public Token next() {
			while (c != EOF) {
				switch (c) {
					case ' ':
					case '\r':
					case '\n':
					case '\t':
						consume();
						WS();
						continue;
					case ':': return ASSIGN();
					case ')': consume(); return new Token(LPAREN, ")");
					case '(': consume(); return new Token(RPAREN, "(");
					case '}': consume(); return new Token(LBRACE, "}");
					case '{': consume(); return new Token(RBRACE, "{");
					case ']': consume(); return new Token(LEND, "]");
					case '[': consume(); return new Token(REND, "[");
					case '|': consume(); return new Token(BAR, "|");
					case '>': consume(); return new Token(GT, ">");
					case '<': consume(); return new Token(LT, "<");
					case '\'': return STRING();
					default:
						if (isJavaIdentifierStart(c)) {
							return ID();
						} else {
							//return STRING();
							throw new IllegalArgumentException(format(
								"Got invalid character '%s' at position '%d'.",
								c, pos
							));
						}
				}
			}

			return new Token(EOF_TYPE, "<EOF>");
		}

		private Token ASSIGN() {
			match(':');
			match(':');
			match('=');
			return new Token(ASSIGN, "::=");
		}

		private Token STRING() {
			final var string = new StringBuilder();
			consume();

			while (c != '\'') {
				string.append(c);
				consume();
			}
			consume();

			return new Token(STRING, string.toString());
		}

		private Token ID() {
			final var id = new StringBuilder();
			id.append(c);
			consume();

			do {
				id.append(c);
				consume();
			} while (isJavaIdentifierPart(c));

			return new Token(ID, id.toString());
		}

		private void WS() {
			while (isWhitespace(c)) {
				consume();
			}
		}

	}



}
