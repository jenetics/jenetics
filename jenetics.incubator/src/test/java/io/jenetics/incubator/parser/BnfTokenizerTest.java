package io.jenetics.incubator.parser;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class BnfTokenizerTest {

	private static final String GRAMMAR_STRING = """
		<expr> ::= (<expr> <op> <expr>) | <num-val> | <var> | ( <ex&pr> <op> <expr> )
		<op>   ::= '+' | - | '*' | / | '('
		<var>  ::= 'x' | y
		<num>  ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
		""";
    //   0 1 2 3 4 5 6 7 8
	// ' a s f \ ' f d d d '

	private static final String FOO = """
		'asf\\'fddd' 'asf\\\\ad'
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

	@Test(dataProvider = "tokenizingData")
	public void tokenizingQuotedString(
		final String string,
		final List<String> expectedTokens
	) {
		final List<String> tokens = new BnfTokenizer(string).tokens()
			.map(Token::value)
			.toList();

		assertThat(tokens).isEqualTo(expectedTokens);
	}

	@DataProvider
	public Object[][] tokenizingData() {
		return new Object[][] {
			{"", List.of()},
			{"' '", List.of(" ")},
			{"a", List.of("a")},
			{"'a'", List.of("a")},
			{"value", List.of("value")},
			{"'value'", List.of("value")},
			{"a b", List.of("a", "b")},
			{"'a' 'b'", List.of("a", "b")},
			{"'a' b", List.of("a", "b")},
			{"a 'b'", List.of("a", "b")},
			{"<prog>", List.of("<", "prog", ">")},
			{"::=", List.of("::=")},
			{"|", List.of("|")},
			{"<prog>::=<expr>", List.of("<", "prog", ">", "::=", "<", "expr", ">")},
			{"<prog>::=    '<expr>'   ", List.of("<", "prog", ">", "::=", "<expr>")},
			{"<prog>  ::=<expr>", List.of("<", "prog", ">", "::=", "<", "expr", ">")},
			{"'va\\'lue'", List.of("va'lue")},
			{"'va\\'lu\"e'", List.of("va'lu\"e")},
			{"'va\\'l\\\\ue'", List.of("va'l\\ue")},
			{"'va\\'l\\\\u\\'\\'\\'e'", List.of("va'l\\u'''e")},
			{"pre 'va\\'l\\\\u\\'\\'\\'e' post", List.of("pre", "va'l\\u'''e", "post")}
		};
	}

}
