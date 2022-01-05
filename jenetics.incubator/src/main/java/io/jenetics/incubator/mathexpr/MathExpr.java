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

import static io.jenetics.incubator.mathexpr.MathStringTokenizer.MathTokenType.COMMA;
import static io.jenetics.incubator.mathexpr.MathStringTokenizer.MathTokenType.DIV;
import static io.jenetics.incubator.mathexpr.MathStringTokenizer.MathTokenType.ID;
import static io.jenetics.incubator.mathexpr.MathStringTokenizer.MathTokenType.LPAREN;
import static io.jenetics.incubator.mathexpr.MathStringTokenizer.MathTokenType.MINUS;
import static io.jenetics.incubator.mathexpr.MathStringTokenizer.MathTokenType.MOD;
import static io.jenetics.incubator.mathexpr.MathStringTokenizer.MathTokenType.NUMBER;
import static io.jenetics.incubator.mathexpr.MathStringTokenizer.MathTokenType.PLUS;
import static io.jenetics.incubator.mathexpr.MathStringTokenizer.MathTokenType.POW;
import static io.jenetics.incubator.mathexpr.MathStringTokenizer.MathTokenType.RPAREN;
import static io.jenetics.incubator.mathexpr.MathStringTokenizer.MathTokenType.TIMES;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.jenetics.incubator.parser.ParsingException;
import io.jenetics.incubator.parser.Token;

import io.jenetics.ext.util.Tree;

import io.jenetics.prog.op.Const;
import io.jenetics.prog.op.MathOp;
import io.jenetics.prog.op.Op;
import io.jenetics.prog.op.Program;
import io.jenetics.prog.op.Var;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 7.0
 * @version 7.0
 */
public final class MathExpr {
	private MathExpr() {
	}

	private static final Set<String> FUNCTIONS = Stream.of(MathOp.values())
		.map(MathOp::toString)
		.collect(Collectors.toUnmodifiableSet());

	private static final List<? extends Set<? extends Token.Type>> OPERATIONS = List.of(
		EnumSet.of(PLUS, MINUS),
		EnumSet.of(TIMES, DIV, MOD),
		EnumSet.of(POW)
	);

	private static final Set<? extends Token.Type> UNARIES = EnumSet.of(PLUS, MINUS);

	private static final MathExprParsing<String, Op<Double>> PARSING = new MathExprParsing<>(
		MathExpr::toOp,
		LPAREN,
		RPAREN,
		COMMA,
		OPERATIONS,
		UNARIES,
		NUMBER,
		ID,
		FUNCTIONS
	);

	public static Tree<Op<Double>, ?> parse(final String string) {
		final var tokenizer = new MathStringTokenizer(string);
		final var parser = new MathExprParser<>(tokenizer, PARSING);

		final var expr = parser.parse();
		Var.reindex(expr);
		return expr;
	}

	public static double eval(final String expr, final double... args) {
		return Program.eval(MathExpr.parse(expr), box(args));
	}

	private static Double[] box(final double... values) {
		final Double[] result = new Double[values.length];
		for (int i = values.length; --i >= 0;) {
			result[i] = values[i];
		}
		return result;
	}

	private static Op<Double> toOp(final Token<String> token) {
		if (token.type().code() == PLUS.code()) {
			return MathOp.ADD;
		} else if (token.type().code() == MINUS.code()) {
			return MathOp.SUB;
		} else if (token.type().code() == TIMES.code()) {
			return MathOp.MUL;
		} else if (token.type().code() == DIV.code()) {
			return MathOp.DIV;
		} else if (token.type().code() == MOD.code()) {
			return MathOp.MOD;
		} else if (token.type().code() == POW.code()) {
			return MathOp.POW;
		} else if (token.type().code() == NUMBER.code()) {
			return Const.of(Double.parseDouble(token.value()));
		} else if (FUNCTIONS.contains(token.value())) {
			return MathOp.toMathOp(token.value());
		} else if (token.type().code() == ID.code()) {
			return Var.of(token.value());
		}

		throw new ParsingException("Unknown token: " + token);
	}

}
