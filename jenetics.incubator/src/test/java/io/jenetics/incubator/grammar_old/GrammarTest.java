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

import static io.jenetics.incubator.grammar_old.StandardGenerators.generateTree;

import io.jenetics.prog.op.MathExpr;

import org.testng.annotations.Test;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import io.jenetics.incubator.grammar_old.Grammar.Symbol;
import io.jenetics.incubator.grammar_old.Grammar.Terminal;

import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.TreeFormatter;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class GrammarTest {

	@Test
	public void tokenize() {
		final var tokens = BnfParser.tokenize("""
			<prog>::=<expr>
			<expr> ::= (<expr> <op> <expr>)
						| <var>
			<op> ::= + | * | "<asd>"
			<var> ::= 0.5
			""");

		tokens.forEach(System.out::println);
	}

	@Test
	public void parse() {
		final var grammar = Grammar.parse("""
			<prog>::=<expr>
			<expr> ::= (<expr><op><expr>) ":"
						| <var>
			<op> ::= + | * | "<asd>" | "sdf      sdf"
			<var> ::= 0.5
			"""
		);

		System.out.println(grammar);
	}

	@Test
	public void generate() {
		final var grammar = Grammar.parse("""
			<expr> ::=  <expr> <op> <expr>  | <num> | <var> | ( <expr> <op> <expr> )
			<op> ::= + | - | * | /
			<var> ::= x | y
			<num> ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
			"""
		);

		final List<Terminal> list = grammar.generate(new Random(12345689013L)::nextInt);
		final var string = list.stream()
			.map(Symbol::toString)
			.collect(Collectors.joining());

		System.out.println(string);
		//Assert.assertEquals(string, "(9*((((4+8)+3)/(y*y))/y))");

		final var expr = MathExpr.parse(string);
		System.out.println(expr);
		System.out.println(expr.simplify());
		System.out.println(TreeFormatter.TREE.format(expr.toTree()));

		final Tree<Symbol, ?> tree = generateTree(grammar, new Random(12345689013L)::nextInt);
		System.out.println(TreeFormatter.TREE.format(tree));
		System.out.println(
			tree.depthFirstStream()
				.filter(Tree::isLeaf)
				.map(node -> node.value().toString())
				.collect(Collectors.joining())
		);
	}

}
