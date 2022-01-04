package io.jenetics.incubator.mathexpr;

import static java.lang.Math.pow;
import static java.lang.Math.sin;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.prog.op.Program;


public class MathExprTest {

	@Test
	public void parse() {

	}

	@Test
	public void specialEval3() {
		final String expr = "5 + 6*x + sin(x)^34 + (1 + sin(x*5)/4)/6";
		final var p1 = io.jenetics.prog.op.MathExpr.parseTree(expr);

		System.out.println(p1);

		final var p2 = MathExpr.parse(expr);
		System.out.println(p2);

		final var result = Program.eval(p2, 4.32);
		System.out.println(result);

		/*
		Assert.assertEquals(
			io.jenetics.prog.op.MathExpr.eval(expr, 4.32),
			31.170600453465315
		);
		Assert.assertEquals(
			io.jenetics.prog.op.MathExpr.eval(expr, 4.32),
			5.0 + 6*4.32 + pow(sin(4.32), 34) + (1 + sin(4.32*5)/4.0)/6.0
		);
		 */
	}

}
