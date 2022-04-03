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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.ext.internal.parser.Token;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class BnfTokenizerTest {

	private static final String GRAMMAR_STRING = """
		<expr> ::= (<expr> <op> <expr>) | <num-val> | <var> | ( <ex&pr> <op> <expr> )
		<op>   ::= '+' | - | '*' | / | '('
		<var>  ::= 'x' | y
		<num>  ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
		""";
    //   0 1 2 3 4 5 6 7 8
	// ' a s f \ ' f d d d '

	//   0 1 2 3 4 5 6 7 8
	//   ' \ ' '

	private static final String FOO = """
		'asf\\'fddd' 'asf\\\\ad'
		""";

	@Test
	public void tokenize() {
		final var tokenizer = new BnfTokenizer(FOO);

		final List<Token<String>> tokens = tokenizer.tokens().toList();

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
			{"|\\", List.of("|", "\\")},
			{"<prog>::=<expr>", List.of("<", "prog", ">", "::=", "<", "expr", ">")},
			{"<prog>::=    '<expr>'   ", List.of("<", "prog", ">", "::=", "<expr>")},
			{"<prog>  ::=<expr>", List.of("<", "prog", ">", "::=", "<", "expr", ">")},
			{"'\\''", List.of("'")},
			{"'\\'\\''", List.of("''")},
			{"'\\'\\'\\\\'", List.of("''\\")},
			{"'\\\\\\'\\'\\\\'", List.of("\\''\\")},
			{"'va\\'lue'", List.of("va'lue")},
			{"'va\\'lu\"e'", List.of("va'lu\"e")},
			{"'va\\'l\\\\ue'", List.of("va'l\\ue")},
			{"'va\\'l\\\\u\\'\\'\\'e'", List.of("va'l\\u'''e")},
			{"pre 'va\\'l\\\\u\\'\\'\\'e' post", List.of("pre", "va'l\\u'''e", "post")},
			{"\\pre 'va\\'l\\\\u\\'\\'\\'e' post", List.of("\\pre", "va'l\\u'''e", "post")},
			{"\\pre 'va\\'l\\\\u\\'\\'\\'e' post\\\\\\", List.of("\\pre", "va'l\\u'''e", "post", "\\\\\\")},
			{"'post\\\\'", List.of("post\\")}
		};
	}

}
