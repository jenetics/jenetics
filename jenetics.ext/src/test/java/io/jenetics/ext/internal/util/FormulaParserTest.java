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
package io.jenetics.ext.internal.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.testng.annotations.Test;

import io.jenetics.ext.internal.util.FormulaParser;
import io.jenetics.ext.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class FormulaParserTest {

	@Test
	public void parse() {
		final FormulaParser<String> formula = FormulaParser.<String>builder()
			.lparen("(")
			.rparen(")")
			.separator(",")
			.unaryOperators("+", "-", "!")
			.binaryOperators(ops -> ops
				.add(11, "+", "-")
				.add(12, "*", "/", "%")
				.add(14, "^", "**"))
			.identifiers("x", "y", "z")
			.functions("pow", "sin", "cos")
			.build();

		final var expr = List.of(
			"x", "*", "x", "+", "sin", "(", "z", ")", "-", "cos", "(", "x", ")",
			"+", "y", "/", "z", "-", "pow", "(", "z", ",", "x", ")");

		final var tree = formula.parse(expr);
		assertThat(tree)
			.isEqualTo(TreeNode.parse("-(+(-(+(*(x,x),sin(z)),cos(x)),/(y,z)),pow(z,x))"));
	}

}
