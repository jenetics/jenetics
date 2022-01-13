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
		value =  "min(sin(z+max((23.43e-03+min(y%pow(z,6.345345),1.4e3)-" +
			"(abs(3.123312)%(min(pow(6.345345*1.4e3,(x*y)*pow(23.43e-03,1.4e3))," +
			"(23.43e-03-y))+(x%min(sin((3.123312*min(23.43e-03,z-3.123312-min(z,x))))," +
			"3.123312))))),(rint(3.123312)-max((x-y),z/abs(max(max(cos(1.4e3),23.43e-03)," +
			"y-sin(x)))))))%x,1.4e3)";
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
Benchmark                 Mode  Cnt       Score      Error  Units
MathExprPerf.newMathExpr  avgt   45   26900.373 ±  166.900  ns/op
MathExprPerf.oldMathExpr  avgt   45  159904.490 ± 2214.192  ns/op
 */
