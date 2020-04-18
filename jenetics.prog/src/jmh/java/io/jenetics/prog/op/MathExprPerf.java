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

import static java.lang.Math.asin;
import static java.lang.Math.ceil;
import static java.lang.Math.cos;
import static java.lang.Math.cosh;
import static java.lang.Math.floor;
import static java.lang.Math.hypot;
import static java.lang.Math.log;
import static java.lang.Math.rint;
import static java.lang.Math.signum;
import static java.lang.Math.sinh;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class MathExprPerf {

	private static final MathExpr MATH_EXPR = MathExpr.parse(
		"cos(signum(tan(sqrt(asin(rint(sinh(log(floor(log(hypot(cosh(sinh(log(y)%" +
			"hypot(y, 1.0))), signum(tan(ceil(ceil(y)))))))))))))))"
	);

	private static double expr(final double x, final double y) {
		return cos(signum(tan(sqrt(asin(rint(sinh(log(floor(log(hypot(cosh(sinh(log(y)%
				hypot(y, 1.0))), signum(tan(ceil(ceil(y)))))))))))))));
	}

	double x;
	double y;

	@Setup
	public void setup() {
		final Random random = new Random();
		x = random.nextDouble()*10;
		y = random.nextDouble();
	}

	@Benchmark
	public double mathExpr() {
		return MATH_EXPR.eval(x, y);
	}

	//@Benchmark
	public double javaExpr() {
		return expr(x, y);
	}

}

/*
Benchmark              Mode  Cnt     Score    Error  Units
MathExprPerf.javaExpr  avgt   15   230.869 ±  6.066  ns/op
MathExprPerf.mathExpr  avgt   15  2434.524 ± 57.198  ns/op
 */

/*
Benchmark              Mode  Cnt     Score    Error  Units
MathExprPerf.javaExpr  avgt   15   234.484 ±  5.848  ns/op
MathExprPerf.mathExpr  avgt   15  1211.822 ± 46.275  ns/op
 */
