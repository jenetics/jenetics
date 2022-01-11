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

import static io.jenetics.incubator.mathexpr.MathTokenType.DIV;
import static io.jenetics.incubator.mathexpr.MathTokenType.IDENTIFIER;
import static io.jenetics.incubator.mathexpr.MathTokenType.MINUS;
import static io.jenetics.incubator.mathexpr.MathTokenType.MOD;
import static io.jenetics.incubator.mathexpr.MathTokenType.NUMBER;
import static io.jenetics.incubator.mathexpr.MathTokenType.PLUS;
import static io.jenetics.incubator.mathexpr.MathTokenType.POW;
import static io.jenetics.incubator.mathexpr.MathTokenType.TIMES;
import static io.jenetics.incubator.mathexpr.MathTokenType.UNARY_OPERATOR;

import io.jenetics.incubator.parser.ParsingException;
import io.jenetics.incubator.parser.Token;
import io.jenetics.incubator.parser.Tokenizer;

import io.jenetics.ext.util.Tree;

import io.jenetics.prog.op.Const;
import io.jenetics.prog.op.MathOp;
import io.jenetics.prog.op.Op;
import io.jenetics.prog.op.Program;
import io.jenetics.prog.op.Var;

/**
 * Contains methods for parsing mathematical expression.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 7.0
 * @version 7.0
 */
public final class MathExpr {
	private MathExpr() {
	}

	private static final MathExprParsing<String, Op<Double>> PARSING =
		MathExprParsing.of(MathExpr::toOp, MathOp.NAMES::contains);


	public static <V> Tree<Op<Double>, ?>
	parse(final Tokenizer<String> tokenizer) {
		final var expr = new MathExprParser<>(tokenizer, PARSING).parse();
		Var.reindex(expr);
		return expr;
	}

	public static Tree<Op<Double>, ?> parse(final String string) {
		return parse(new MathStringTokenizer(string));
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

	public static Op<Double> toOp(final Token<String> token, final Token.Type type) {
		if (token.type().code() == PLUS.code()) {
			if (type.code() == UNARY_OPERATOR.code()) {
				return MathOp.ID;
			} else {
				return MathOp.ADD;
			}
		} else if (token.type().code() == MINUS.code()) {
			if (type.code() == UNARY_OPERATOR.code()) {
				return MathOp.NEG;
			} else {
				return MathOp.SUB;
			}
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
		} else if (MathOp.NAMES.contains(token.value())) {
			return MathOp.toMathOp(token.value());
		} else if (token.type().code() == IDENTIFIER.code()) {
			return Var.of(token.value());
		}

		throw new ParsingException("Unknown token: " + token);
	}

}
