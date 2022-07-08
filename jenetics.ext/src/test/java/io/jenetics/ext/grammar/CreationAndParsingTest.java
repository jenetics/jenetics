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

import static java.lang.Integer.MAX_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static io.jenetics.ext.grammar.Cfg.E;
import static io.jenetics.ext.grammar.Cfg.N;
import static io.jenetics.ext.grammar.Cfg.R;
import static io.jenetics.ext.grammar.Cfg.T;

import java.util.List;
import java.util.Random;
import java.util.Set;

import org.testng.annotations.Test;

import io.jenetics.ext.internal.util.FormulaParser;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class CreationAndParsingTest {

	interface Op {
		double apply(final Double... args);
	}

	static final Cfg<Op> CFG = Cfg.of(
		R("expr",
			E(N("num")),
			E(T("(", null), N("expr"), N("op"), N("expr"), T(")", null))
		),
		R("op",
			E(T("+", v -> v[0] + v[1])),
			E(T("-", v -> v[0] - v[1])),
			E(T("*", v -> v[0] * v[1])),
			E(T("/", v -> v[0] / v[1]))
		),
		R("num",
			E(T("0", v -> 0.0)),
			E(T("1", v -> 1.0)),
			E(T("2", v -> 2.0)),
			E(T("3", v -> 3.0)),
			E(T("4", v -> 4.0)),
			E(T("5", v -> 5.0)),
			E(T("6", v -> 6.0)),
			E(T("7", v -> 7.0)),
			E(T("8", v -> 8.0)),
			E(T("9", v -> 9.0))
		)
	);

	static final Set<String> NUMS = Set.of(
		"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
	);

	static final FormulaParser<Cfg.Terminal<Op>> FORMULA_PARSER = FormulaParser.<Cfg.Terminal<Op>>builder()
		.lparen(s -> s.name().equals("("))
		.rparen(s -> s.name().equals(")"))
		.separator(s -> s.name().equals(","))
		.binaryOperators(ops -> ops
			.add(11, s -> s.name().equals("+") || s.name().equals("-"))
			.add(12, s -> s.name().equals("*") || s.name().equals("/")))
		.identifiers(s -> NUMS.contains(s.name()))
		.build();

	@Test
	public void generateAndParse() {
		final var random = new Random(-8564585140851778291L);

		final var generator = new SentenceGenerator<Op>(
			SymbolIndex.of(random),
			MAX_VALUE
		);

		// (8-((5+5)-(3+7)))
		final List<Cfg.Terminal<Op>> sentence = generator.generate(CFG);
		final double result = FORMULA_PARSER
			.parse(sentence, (token, type) -> token.value())
			.reduce(new Double[0], Op::apply);

		assertThat(result).isEqualTo(8.0);
	}

}
