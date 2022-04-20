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
import static io.jenetics.ext.grammar.Cfg.E;
import static io.jenetics.ext.grammar.Cfg.N;
import static io.jenetics.ext.grammar.Cfg.R;
import static io.jenetics.ext.grammar.Cfg.T;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import org.testng.annotations.Test;

import io.jenetics.ext.grammar.Cfg.Expression;
import io.jenetics.ext.grammar.Cfg.NonTerminal;
import io.jenetics.ext.grammar.Cfg.Rule;
import io.jenetics.ext.grammar.Cfg.Symbol;
import io.jenetics.ext.grammar.Cfg.Terminal;

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
		assertThat(getSymbolInstances(cfg).size()).isEqualTo(22);

		final Cfg<String> cfg2 = new Cfg<>(
			List.of(
				new NonTerminal<>("expr"),
				new NonTerminal<>("num"),
				new NonTerminal<>("var"),
				new NonTerminal<>("op")
			),
			List.of(
				new Terminal<>("(", "("),
				new Terminal<>(")", ")"),
				new Terminal<>("+", "+"),
				new Terminal<>("-", "-"),
				new Terminal<>("*", "*"),
				new Terminal<>("/", "/"),
				new Terminal<>("x", "x"),
				new Terminal<>("y", "y"),
				new Terminal<>("0", "0"),
				new Terminal<>("1", "1"),
				new Terminal<>("2", "2"),
				new Terminal<>("3", "3"),
				new Terminal<>("4", "4"),
				new Terminal<>("5", "5"),
				new Terminal<>("6", "6"),
				new Terminal<>("7", "7"),
				new Terminal<>("8", "8"),
				new Terminal<>("9", "9")
			),
			List.of(
				new Rule<>(
					new NonTerminal<>("expr"),
					List.of(
						new Expression<>(
							List.of(
								new NonTerminal<>("num")
							)
						),
						new Expression<>(
							List.of(
								new NonTerminal<>("var")
							)
						),
						new Expression<>(
							List.of(
								new Terminal<>("(", "("),
								new NonTerminal<>("expr"),
								new NonTerminal<>("op"),
								new NonTerminal<>("expr"),
								new Terminal<>(")", ")")
							)
						)
					)
				),
				new Rule<>(
					new NonTerminal<>("op"),
					List.of(
						new Expression<>(
							List.of(
								new Terminal<>("+", "+")
							)
						),
						new Expression<>(
							List.of(
								new Terminal<>("-", "-")
							)
						),
						new Expression<>(
							List.of(
								new Terminal<>("*", "*")
							)
						),
						new Expression<>(
							List.of(
								new Terminal<>("/", "/")
							)
						)
					)
				),
				new Rule<>(
					new NonTerminal<>("var"),
					List.of(
						new Expression<>(
							List.of(
								new Terminal<>("x", "x")
							)
						),
						new Expression<>(
							List.of(
								new Terminal<>("y", "y")
							)
						)
					)
				),
				new Rule<>(
					new NonTerminal<>("num"),
					List.of(
						new Expression<>(
							List.of(
								new Terminal<>("0", "0")
							)
						),
						new Expression<>(
							List.of(
								new Terminal<>("1", "1")
							)
						),
						new Expression<>(
							List.of(
								new Terminal<>("2", "2")
							)
						),
						new Expression<>(
							List.of(
								new Terminal<>("3", "3")
							)
						),
						new Expression<>(
							List.of(
								new Terminal<>("4", "4")
							)
						),
						new Expression<>(
							List.of(
								new Terminal<>("5", "5")
							)
						),
						new Expression<>(
							List.of(
								new Terminal<>("6", "6")
							)
						),
						new Expression<>(
							List.of(
								new Terminal<>("7", "7")
							)
						),
						new Expression<>(
							List.of(
								new Terminal<>("8", "8")
							)
						),
						new Expression<>(
							List.of(
								new Terminal<>("9", "9")
							)
						)
					)
				)
			),
			new NonTerminal<>("expr")
		);

		assertThat(cfg).isEqualTo(cfg2);
		assertThat(getSymbolInstances(cfg2).size()).isEqualTo(50);
		assertThat(getUniqueSymbols(cfg2).size()).isEqualTo(22);

		final var cfg3 = Cfg.of(cfg2.rules());
		assertThat(cfg3).isEqualTo(cfg2);
		assertThat(getSymbolInstances(cfg3).size()).isEqualTo(22);
	}

	private static Set<Symbol<?>> getSymbolInstances(final Cfg<?> cfg) {
		final var symbols = new IdentityHashMap<Symbol<?>, String>();
		cfg.nonTerminals().forEach(nt -> symbols.put(nt, ""));
		cfg.terminals().forEach(t -> symbols.put(t, ""));
		symbols.put(cfg.start(), "");
		for (var rule : cfg.rules()) {
			symbols.put(rule.start(), "");
			rule.alternatives().forEach(expr ->
				expr.symbols().forEach(s ->
					symbols.put(s, "")
				)
			);
		}

		return symbols.keySet();
	}

	private static Set<Symbol<?>> getUniqueSymbols(final Cfg<?> cfg) {
		return new HashSet<>(getSymbolInstances(cfg));
	}

	@Test
	public void building() {
		final var cfg = Bnf.parse("""
			<expr> ::= <num> | <var> | '(' <expr> <op> <expr> ')'
			<op>   ::= + | - | * | /
			<var>  ::= x | y
			<num>  ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
			"""
		);
		assertThat(getSymbolInstances(cfg).size()).isEqualTo(22);

		final Cfg<String> cfg2 = Cfg.of(
			R("expr",
				E(N("num")),
				E(N("var")),
				E(T("("), N("expr"), N("op"), N("expr"), T(")"))
			),
			R("op", E(T("+")), E(T("-")), E(T("*")), E(T("/"))),
			R("var", E(T("x")), E(T("y"))),
			R("num",
				E(T("0")), E(T("1")), E(T("2")), E(T("3")),
				E(T("4")), E(T("5")), E(T("6")), E(T("7")),
				E(T("8")), E(T("9"))
			)
		);

		assertThat(getSymbolInstances(cfg2).size()).isEqualTo(22);
		assertThat(cfg2).isEqualTo(cfg);
	}

	@Test
	public void map() {
		final var cfg = Bnf.parse("""
			<expr> ::= <num> | <var> | '(' <expr> <op> <expr> ')'
			<op>   ::= + | - | * | /
			<var>  ::= x | x | y
			<num>  ::= 0 | 1 | 2
			"""
		);

		record Value(String value) {}

		final Cfg<Value> cfg2 = cfg.map(t -> new Value("__" + t.value()));

		for (var t : cfg2.terminals()) {
			assertThat(t.value().value()).startsWith("__");
		}
		for (var t : cfg2.nonTerminals()) {
			assertThat(t.name()).doesNotContain("__");
		}
	}

}
