package io.jenetics.incubator.parser;

import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;

public class BnfTokenizerTest {

	private static final String GRAMMAR_STRING = """
		<expr> ::= (<expr> <op> <expr>) | <num-val> | <var> | ( <ex&pr> <op> <expr> )
		<op>   ::= '+' | - | '*' | / | '('
		<var>  ::= 'x' | y
		<num>  ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
		""";

	private static final String FOO = """
		<opt-whitespace> ::= ' ' <opt-whitespace> | '\\''
		<expression>     ::= <list>
		""";

	@Test
	public void tokenize() {
		final var tokenizer = new BnfTokenizer(FOO);

		final List<Token> tokens = tokenizer.tokens().toList();

		final var string = tokens.stream()
			.map(Object::toString)
			.collect(Collectors.joining(" "));

		System.out.println(string);
	}

}
