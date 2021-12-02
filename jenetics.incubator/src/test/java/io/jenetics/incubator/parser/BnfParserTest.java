package io.jenetics.incubator.parser;

import org.testng.annotations.Test;

public class BnfParserTest {

	private static final String BNF_STRING = """
		<expr> ::= (<expr> <op> <expr>) | "5"
		<op>   ::= + | - | * | / | '('
		<var>  ::= x | y
		<num>  ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
		""";

	@Test
	public void parse() {
		final var tokenizer = new BnfTokenizer(BNF_STRING);
		final var parser = new BnfParser(tokenizer);
		final var bnf = parser.parse();

		parser.symbols.forEach(System.out::print);
		System.out.println();
		System.out.println(bnf);
	}

}
