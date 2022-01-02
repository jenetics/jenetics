package io.jenetics.incubator.mathexpr;

import java.util.Set;

import org.testng.annotations.Test;

public class MathExprParserTest {

	@Test
	public void parsing() {
		final var string = "3 + 5 * -7^43**43+98 +  sin(x)-cos(3*y, -4, +x)";
		final var tokenizer = new MathExprTokenizer(string);
		final var parser = new MathExprParser(
			tokenizer,
			Set.of("x", "y"),
			Set.of("sin", "cos")
		);

		//new MathExprTokenizer(string).tokens().forEach(System.out::println);

		final var expr = parser.parse();
		System.out.println(expr);
	}

}
