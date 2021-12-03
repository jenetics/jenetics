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
package io.jenetics.incubator.grammar_old;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class BnfParserTest {

	@Test(dataProvider = "tokens")
	public void tokenize(final String bnf, final List<String> tokens) {
		final var actual = BnfParser.tokenize(bnf).stream()
			.map(BnfParser.Token::value)
			.toList();

		Assert.assertEquals(actual, tokens);
	}

	@DataProvider(name = "tokens")
	public Object[][] tokens() {
		return new Object[][] {
			{"", List.of()},
			{" ", List.of(" ")},
			{"<prog>", List.of("<prog>")},
			{"::=", List.of("::=")},
			{"|", List.of("|")},
			{"<prog>::=<expr>", List.of("<prog>", "::=", "<expr>")},
			{"<prog>::=\"<expr>\"", List.of("<prog>", "::=", "\"<expr>\"")},
			{"<prog>  ::=<expr>", List.of("<prog>", "  ", "::=", "<expr>")}
		};
	}

}
