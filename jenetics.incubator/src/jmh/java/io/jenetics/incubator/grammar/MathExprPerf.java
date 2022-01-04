package io.jenetics.incubator.grammar;

import java.util.Set;
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


@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 15, time = 1)
@Fork(value = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class MathExprPerf {

	static final Set<String> VARS = Set.of("x", "y", "z");
	static final Set<String> FUN = Set.of("sin", "cos", "pow");

	String value;

	@Setup
	public void setup() {
		value =  "x*x/4^cos(z) * x*32 + sin(z) - cos(x)*y*pow(z*x + y, pow(pow(z*x + y, pow(z*x + y, x)), x))";
	}

	@Benchmark
	public Object oldMathExpr() {
		return io.jenetics.prog.op.MathExpr.parseTree(value);
	}

	@Benchmark
	public Object newMathExpr() {
		return MathExpr.parse(value);
	}

}

/*
Benchmark                 Mode  Cnt      Score     Error  Units
MathExprPerf.newMathExpr  avgt   45  13517.062 ± 116.603  ns/op
MathExprPerf.oldMathExpr  avgt   45  63230.530 ± 563.707  ns/op
 */
