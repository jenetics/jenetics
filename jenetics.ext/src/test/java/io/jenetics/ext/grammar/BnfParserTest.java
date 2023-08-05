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

import java.util.random.RandomGenerator;

import org.testng.annotations.Test;

import io.jenetics.ext.internal.parser.ParsingException;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class BnfParserTest {

	private static final String BNF_STRING = """
		<expr> ::= <num> | <var> | '(' <expr> <op> <expr> ')'
		<op>   ::= + | - | * | /
		<var>  ::= x | y
		<num>  ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
		""";

	@Test
	public void parse() {
		final var cfg = Bnf.parse(BNF_STRING);
		assertThat(cfg).isNotNull();
	}

	@Test(expectedExceptions = ParsingException.class)
	public void parseWithParseException() {
		Bnf.parse("""
			<expr> ::= <num> | <var> | '(' <expr> <op> <expr> ')'
			<op>   ::= + | - | * | /
			<var>  :r:= x | y
			<num>  ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
			"""
		);
	}

	@Test
	public void parseWithDuplicateRules() {
		final var cfg1 = Bnf.parse("""
			<expr> ::= <num> | <var> | '(' <expr> <op> <expr> ')'
			<op>   ::= + | - | * | /
			<var>  ::= x
			<var>  ::= y
			<num>  ::= 0 | 1 | 2 | 3 | 4
			<num>  ::= 5 | 6 | 7 | 8 | 9
			"""
		);
		final var cfg2 = Bnf.parse("""
			<expr> ::= <num> | <var> | '(' <expr> <op> <expr> ')'
			<op>   ::= + | - | * | /
			<var>  ::= x | y
			<num>  ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
			"""
		);

		assertThat(cfg1).isEqualTo(cfg2);
	}

	@Test(invocationCount = 30)
	public void randomBnfParsing() {
		final var cfg = RandomCfg.next(RandomGenerator.getDefault());

		final var tokenizer = new BnfTokenizer(Bnf.format(cfg));
		final var parser = new BnfParser(tokenizer);
		final var parsedBnf = parser.parse();

		assertThat(parsedBnf).isEqualTo(cfg);
	}

}
