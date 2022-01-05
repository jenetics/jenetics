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
package io.jenetics.incubator.mathexpr;

import java.util.Set;

import org.testng.annotations.Test;

import io.jenetics.ext.util.TreeFormatter;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class MathExprParserTest {

	@Test
	public void parsing() {
		final var string = "3 + -5 * -7^43**43+98 -(+4) - -sin(x) - cos(3*y, -4, -x)";
		final var tokenizer = new MathStringTokenizer(string);
//		final var parser = new MathExprParser<>(
//			tokenizer,
//			Set.of("x", "y"),
//			Set.of("sin", "cos")
//		);
//
//		//new MathExprTokenizer(string).tokens().forEach(System.out::println);
//
//		final var expr = parser.parse();
//		System.out.println(TreeFormatter.TREE.format(expr));
//		System.out.println(expr);
	}

}
