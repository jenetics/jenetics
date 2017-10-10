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

import static java.lang.Math.abs;
import static java.lang.Math.acos;
import static java.lang.Math.asin;
import static java.lang.Math.atan;
import static java.lang.Math.cbrt;
import static java.lang.Math.ceil;
import static java.lang.Math.cos;
import static java.lang.Math.cosh;
import static java.lang.Math.exp;
import static java.lang.Math.floor;
import static java.lang.Math.hypot;
import static java.lang.Math.log;
import static java.lang.Math.log10;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.rint;
import static java.lang.Math.signum;
import static java.lang.Math.sin;
import static java.lang.Math.sinh;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;
import static java.lang.Math.tanh;

import java.util.function.Function;

/**
 * This class contains operations for performing basic numeric operations.
 *
 * @see Math
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.9
 * @since 3.9
 */
public enum MathOp implements Op<Double> {


	/* *************************************************************************
	 * Arithmetic operations
	 * ************************************************************************/

	/**
	 * Return the absolute value of a double value.
	 * <em>This operation has arity 1.</em>
	 *
	 * @see Math#abs(double)
	 */
	ABS("abs", 1, v -> abs(v[0])),

	/**
	 * Return the negation value of a double value.
	 * <em>This operation has arity 1.</em>
	 */
	NEG("neg", 1, v -> -v[0]),

	/**
	 * Return the minimum of two values.
	 * <em>This operation has arity 2.</em>
	 *
	 * @see Math#min(double, double)
	 */
	MIN("min", 2, v -> min(v[0], v[1])),

	/**
	 * Return the maximum of two values
	 * <em>This operation has arity 2.</em>
	 *
	 * @see Math#max(double, double)
	 */
	MAX("max", 2, v -> max(v[0], v[1])),

	/**
	 * Returns the smallest (closest to negative infinity) double value that is
	 * greater than or equal to the argument and is equal to a mathematical
	 * integer.
	 * <em>This operation has arity 1.</em>
	 *
	 * @see Math#ceil(double)
	 */
	CEIL("ceil", 1, v -> ceil(v[0])),

	/**
	 * Returns the largest (closest to positive infinity) double value that is
	 * less than or equal to the argument and is equal to a mathematical integer.
	 * <em>This operation has arity 1.</em>
	 *
	 * @see Math#floor(double)
	 */
	FLOOR("floor", 1, v -> floor(v[0])),

	/**
	 * Returns the signum function of the argument; zero if the argument is
	 * zero, 1.0 if the argument is greater than zero, -1.0 if the argument is
	 * less than zero.
	 * <em>This operation has arity 1.</em>
	 *
	 * @see Math#signum(double)
	 */
	SIGNUM("signum", 1, v -> signum(v[0])),

	/**
	 * Returns the double value that is closest in value to the argument and is
	 * equal to a mathematical integer.
	 * <em>This operation has arity 1.</em>
	 *
	 * @see Math#rint(double)
	 */
	RINT("rint", 1, v -> rint(v[0])),

	/**
	 * Returns the sum of its arguments.
	 * <em>This operation has arity 2.</em>
	 */
	ADD("add", 2, v -> v[0] + v[1]),

	/**
	 * Return the diff of its arguments.
	 * <em>This operation has arity 2.</em>
	 */
	SUB("sub", 2, v -> v[0] - v[1]),

	/**
	 * Returns the product of its arguments.
	 * <em>This operation has arity 2.</em>
	 */
	MUL("mul", 2, v -> v[0]*v[1]),

	/**
	 * Returns the quotient of its arguments.
	 * <em>This operation has arity 2.</em>
	 */
	DIV("div", 2, v -> v[0]/v[1]),

	/**
	 * Returns the modulo of its arguments.
	 * <em>This operation has arity 2.</em>
	 */
	MOD("mod", 2, v -> v[0]%v[1]),

	/**
	 * Returns the value of the first argument raised to the power of the second
	 * argument.
	 * <em>This operation has arity 2.</em>
	 *
	 * @see Math#pow(double, double)
	 */
	POW("pow", 2, v -> pow(v[0], v[1])),

	/**
	 * Returns the square value of a given double value.
	 * <em>This operation has arity 1.</em>
	 */
	SQR("sql", 1, v -> v[0]*v[0]),

	/**
	 * Returns the correctly rounded positive square root of a double value.
	 * <em>This operation has arity 1.</em>
	 *
	 * @see Math#sqrt(double)
	 */
	SQRT("sqrt", 1, v -> sqrt(v[0])),

	/**
	 * Returns the cube root of a double value.
	 * <em>This operation has arity 1.</em>
	 *
	 * @see Math#cbrt(double)
	 */
	CBRT("cbrt", 1, v -> cbrt(v[0])),

	/**
	 * Returns sqrt(<i>x</i><sup>2</sup>&nbsp;+<i>y</i><sup>2</sup>) without
	 * intermediate overflow or underflow.
	 * <em>This operation has arity 2.</em>
	 *
	 * @see Math#hypot(double, double)
	 */
	HYPOT("hypot", 2, v -> hypot(v[0], v[1])),


	/* *************************************************************************
	 * Exponential/logarithmic operations
	 * ************************************************************************/

	/**
	 * Returns Euler's number e raised to the power of a double value.
	 * <em>This operation has arity 1.</em>
	 *
	 * @see Math#exp(double)
	 */
	EXP("exp", 1, v -> exp(v[0])),

	/**
	 * Returns the natural logarithm (base e) of a double value.
	 * <em>This operation has arity 1.</em>
	 *
	 * @see Math#log(double)
	 */
	LOG("log", 1, v -> log(v[0])),

	/**
	 * Returns the base 10 logarithm of a double value.
	 * <em>This operation has arity 1.</em>
	 *
	 * @see Math#log10(double)
	 */
	LOG10("log10", 1, v -> log10(v[0])),


	/* *************************************************************************
	 * Trigonometric operations
	 * ************************************************************************/

	/**
	 * Returns the trigonometric sine of an angle.
	 * <em>This operation has arity 1.</em>
	 *
	 * @see Math#sin(double)
	 */
	SIN("sin", 1, v -> sin(v[0])),

	/**
	 * Returns the trigonometric cosine of an angle.
	 * <em>This operation has arity 1.</em>
	 *
	 * @see Math#cos(double)
	 */
	COS("cos", 1, v -> cos(v[0])),

	/**
	 * Returns the trigonometric tangent of an angle.
	 * <em>This operation has arity 1.</em>
	 *
	 * @see Math#tan(double)
	 */
	TAN("tan", 1, v -> tan(v[0])),

	/**
	 * Returns the arc cosine of a double value.
	 * <em>This operation has arity 1.</em>
	 *
	 * @see Math#acos(double)
	 */
	ACOS("acos", 1, v -> acos(v[0])),

	/**
	 * Returns the arc sine of a double value.
	 * <em>This operation has arity 1.</em>
	 *
	 * @see Math#asin(double)
	 */
	ASIN("asin", 1, v -> asin(v[0])),

	/**
	 * Returns the arc tangent of a value.
	 * <em>This operation has arity 1.</em>
	 *
	 * @see Math#atan(double)
	 */
	ATAN("atan", 1, v -> atan(v[0])),

	/**
	 * Returns the hyperbolic cosine of a double value.
	 * <em>This operation has arity 1.</em>
	 *
	 * @see Math#cosh(double)
	 */
	COSH("cosh", 1, v -> cosh(v[0])),

	/**
	 * Returns the hyperbolic sine of a double value.
	 * <em>This operation has arity 1.</em>
	 *
	 * @see Math#sinh(double)
	 */
	SINH("sinh", 1, v -> sinh(v[0])),

	/**
	 * Returns the hyperbolic tangent of a double value.
	 * <em>This operation has arity 1.</em>
	 *
	 * @see Math#tanh(double)
	 */
	TANH("tanh", 1, v -> tanh(v[0]));


	/**
	 * The double value that is closer than any other to pi, the ratio of the
	 * circumference of a circle to its diameter. <em>This is a terminal
	 * operation.</em>
	 *
	 * @see Math#PI
	 */
	public static final Const<Double> PI = Const.of("π", Math.PI);

	/**
	 * The double value that is closer than any other to e, the base of the
	 * natural logarithms. <em>This is a terminal operation.</em>
	 *
	 * @see Math#E
	 */
	public static final Const<Double> E = Const.of("e", Math.E);


	private final String _name;
	private final int _arity;
	private final Function<Double[], Double> _function;

	private MathOp(
		final String name,
		final int arity,
		final Function<Double[], Double> function
	) {
		assert name != null;
		assert arity >= 0;
		assert function != null;

		_name = name;
		_function = function;
		_arity = arity;
	}

	@Override
	public int arity() {
		return _arity;
	}

	@Override
	public Double apply(final Double[] doubles) {
		return _function.apply(doubles);
	}

	@Override
	public String toString() {
		return _name;
	}

}
