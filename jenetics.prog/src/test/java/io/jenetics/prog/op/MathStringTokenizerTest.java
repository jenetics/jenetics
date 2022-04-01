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
package io.jenetics.prog.op;

import static org.assertj.core.api.Assertions.assertThat;
import static io.jenetics.prog.op.MathExprTestData.EXPRESSIONS;
import static io.jenetics.prog.op.MathExprTestData.EXPRESSIONS_TOKENS;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.ext.internal.parser.Token;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class MathStringTokenizerTest {

	@Test(dataProvider = "expressions")
	public void tokenize(final String expression, final List<String> tokens) {
		final var tokenizer = new MathStringTokenizer(expression);

		assertThat(
			tokenizer.tokens()
				.map(Token::value)
				.toList()
		).isEqualTo(tokens);
	}

	@DataProvider
	public Object[][] expressions() {
		return IntStream.range(0, EXPRESSIONS.size())
			.mapToObj(i -> new Object[] { EXPRESSIONS.get(i), EXPRESSIONS_TOKENS.get(i) })
			.toArray(Object[][]::new);
	}

	public static void main(final String[] args) {
		MathExprTestData.EXPRESSIONS.forEach(expr -> {
			final var tokenizer = new MathStringTokenizer(expr);
			final var tokens = tokenizer.tokens()
				.map(Token::value)
				.collect(Collectors.joining("|"));

			System.out.println(tokens);
		});
	}

}
