package io.jenetics.ext.util;

import java.util.List;

import org.testng.annotations.Test;

public class FormulaParserTest {

	@Test
	public void parse() {
		final FormulaParser<String> formula = FormulaParser.<String>builder()
			.lparen("(")
			.rparen(")")
			.comma(",")
			.unaryOperators("+", "-", "!")
			.binaryOperators(ops -> ops
				.add(11, "+", "-")
				.add(12, "*", "/", "%")
				.add(14, "^", "**"))
			.identifiers("x", "y", "z")
			.functions("pow", "sin", "cos")
			.build();

		final var expr = List.of(
			"x", "*", "x", "+", "sin", "(", "z", ")");
		final var tree = formula.parse(expr);
		System.out.println(tree);

		/*
				new BinaryOperators<>(11, "+", "-"),
				new BinaryOperators<>(12, "*", "/", "%"),
				new BinaryOperators<>(14, "^", "**")
		 */
	}

}
