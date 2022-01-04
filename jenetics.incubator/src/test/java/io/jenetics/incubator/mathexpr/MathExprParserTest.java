package io.jenetics.incubator.mathexpr;

import java.util.Set;

import org.testng.annotations.Test;

import io.jenetics.ext.util.TreeFormatter;

public class MathExprParserTest {

	@Test
	public void parsing() {
		final var string = "3 + -5 * -7^43**43+98 -(+4) - -sin(x) - cos(3*y, -4, -x)";
		final var tokenizer = new MathStringTokenizer(string);
//		final var parser = new MathExprParser<>(
//			tokenizer,
//			Set.of("x", "y"),
//			Set.of("sin", "cos")
//		);
//
//		//new MathExprTokenizer(string).tokens().forEach(System.out::println);
//
//		final var expr = parser.parse();
//		System.out.println(TreeFormatter.TREE.format(expr));
//		System.out.println(expr);
	}

}
