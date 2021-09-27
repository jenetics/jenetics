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
public class BnfParser extends Parser {

	protected BnfParser(final BnfTokenizer tokenizer) {
		super(tokenizer);
	}
}
