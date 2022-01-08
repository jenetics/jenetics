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
package io.jenetics.incubator.prog;

import static io.jenetics.incubator.mathexpr.MathTokenType.COMMA;
import static io.jenetics.incubator.mathexpr.MathTokenType.DIV;
import static io.jenetics.incubator.mathexpr.MathTokenType.IDENTIFIER;
import static io.jenetics.incubator.mathexpr.MathTokenType.LPAREN;
import static io.jenetics.incubator.mathexpr.MathTokenType.MINUS;
import static io.jenetics.incubator.mathexpr.MathTokenType.MOD;
import static io.jenetics.incubator.mathexpr.MathTokenType.NUMBER;
import static io.jenetics.incubator.mathexpr.MathTokenType.PLUS;
import static io.jenetics.incubator.mathexpr.MathTokenType.POW;
import static io.jenetics.incubator.mathexpr.MathTokenType.RPAREN;
import static io.jenetics.incubator.mathexpr.MathTokenType.TIMES;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.jenetics.incubator.grammar.Cfg.Symbol;
import io.jenetics.incubator.grammar.Cfg.Terminal;
import io.jenetics.incubator.mathexpr.MathExprParsing;

import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.TreeNode;

import io.jenetics.prog.op.MathExpr;
import io.jenetics.prog.op.MathOp;
import io.jenetics.prog.op.Op;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public final class Ast {

	private static final MathExprParsing<Terminal, Op<Double>> PARSING = new MathExprParsing<>(
		null,
		LPAREN,
		RPAREN,
		COMMA,
		List.of(
			EnumSet.of(PLUS, MINUS),
			EnumSet.of(TIMES, DIV, MOD),
			EnumSet.of(POW)
		),
		EnumSet.of(PLUS, MINUS),
		EnumSet.of(IDENTIFIER, NUMBER),
		fun -> MathOp.NAMES.contains(fun.value())
	);

	public Ast(
		final Predicate<? super String> binary,
		final Predicate<? super String> unary
	) {

	}


	public Tree<Op<Double>, ?> toTree(final List<Terminal> sentence) {
		final String expression = sentence.stream()
			.map(Symbol::value)
			.collect(Collectors.joining());

		return MathExpr.parseTree(expression);
	}

	public static TreeNode<Terminal> parse(final List<Terminal> tokens) {
		return TreeNode.of(null);
	}

	public static void main(final String[] args) {
		final var ast = new Ast(
			string -> switch(string) {
				case "+", "-", "*", "/" -> true;
				default -> false;
			},
			string -> false
		);
	}

}
