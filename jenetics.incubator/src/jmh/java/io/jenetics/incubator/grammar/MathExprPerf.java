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
package io.jenetics.incubator.grammar;

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

import io.jenetics.incubator.mathexpr.MathExpr;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 15, time = 1)
@Fork(value = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class MathExprPerf {

	String value;

	@Setup
	public void setup() {
		value =  "x*x/4^cos(z) * x*32 + sin(z) - cos(x)*y*pow(z*x + y, pow(pow(z*x + y, pow(z*x + y, x)), x))";
	}

	@Benchmark
	public Object oldMathExpr() {
		return io.jenetics.prog.op.MathExpr.eval(value, 1.0, 2.0, 3.0);
	}

	@Benchmark
	public Object newMathExpr() {
		return MathExpr.eval(value, 1.0, 2.0, 3.0);
	}

}

/*
Benchmark                 Mode  Cnt      Score     Error  Units
MathExprPerf.newMathExpr  avgt   45  12757.125 ±  75.947  ns/op
MathExprPerf.oldMathExpr  avgt   45  67691.606 ± 384.562  ns/op
 */
