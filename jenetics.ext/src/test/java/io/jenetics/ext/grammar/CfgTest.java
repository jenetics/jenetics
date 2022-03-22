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

import java.util.IdentityHashMap;
import java.util.List;
import java.util.stream.Stream;

import org.testng.annotations.Test;

import io.jenetics.ext.grammar.Cfg.NonTerminal;
import io.jenetics.ext.grammar.Cfg.Symbol;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class CfgTest {

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void creatWithMissingSymbol() {
		final var template = Bnf.parse("""
			<expr> ::= <num> | <var> | '(' <expr> <op> <expr> ')'
			<op>   ::= + | - | * | /
			<var>  ::= x | y
			<num>  ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
			"""
		);

		new Cfg<>(
			template.nonTerminals(),
			template.terminals().subList(1, 10),
			template.rules(),
			template.start()
		);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void nonRuleForStartSymbol() {
		final var template = Bnf.parse("""
			<expr> ::= <num> | <var> | '(' <expr> <op> <expr> ')'
			<op>   ::= + | - | * | /
			<var>  ::= x | y
			<num>  ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
			"""
		);

		new Cfg<>(
			template.nonTerminals(),
			template.terminals(),
			template.rules(),
			new NonTerminal<>("foo_bar")
		);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void duplicateSymbolName() {
		Bnf.parse("""
			<expr> ::= <num> | <var> | '(' <expr> <op> <expr> ')'
			<op>   ::= + | - | * | / | op | var
			<var>  ::= x | y
			<num>  ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
			"""
		);
	}

	@Test
	public void normalization() {
		final var cfg = Bnf.parse("""
			<expr> ::= <num> | <var> | '(' <expr> <op> <expr> ')'
			<op>   ::= + | - | * | /
			<var>  ::= x | y
			<num>  ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
			"""
		);

		final List<Symbol<String>> symbols = cfg.rules().stream()
			.flatMap(rule -> Stream.concat(
					Stream.of(rule.start()),
					rule.alternatives().stream()
						.flatMap(expr -> expr.symbols().stream())
				)
			)
			.toList();

		assertThat(symbols.size()).isEqualTo(27);

		final var map = new IdentityHashMap<>();
		symbols.forEach(s -> map.put(s, ""));
		assertThat(map.size()).isEqualTo(22);
	}

	/*
	public void builderSyntax() {
		final Cfg grammar = Cfg.builder()
			.rule(rule -> rule
				.start("expr")
				.expr(expr -> expr.n("num"))
				.epxr(expr -> expr.n("var"))
				.expr(expr -> expr.t("(").n("expr", "op", "expr").t(")")))
			.rule(rule -> rule
				.start("op")
				.expr(expr -> expr.t("+"))
				.expr(expr -> expr.t("-"))
				.expr(expr -> expr.t("*"))
				.expr(expr -> expr.t("/")))
			.rule(rule -> rule
				.start("var")
				.expr(expr -> expr.t("x"))
				.expr(expr -> expr.t("y")))
			.build();
	}
	 */

}
