package io.jenetics.ext.util;

import java.util.List;
import java.util.Set;

import org.testng.annotations.Test;

public class FormulaTest {

	@Test
	public void parse() {
		final var formula = new Formula<>(
			"(",
			")",
			",",
			List.of(
				Set.of("+", "-"),  // Operations with the lowest precedence.
				Set.of("*", "/", "%"),
				Set.of("^", "**")  // Operations with the highest precedence.
			),
			Set.of("+", "-"), // Unary operations
			Set.of("x", "y", "z"), // Identifiers
			Set.of("pow", "sin", "cos") // Functions
		);

		final var expr = List.of(
			"x", "*", "x", "+", "sin", "(", "z", ")");
		final var tree = formula.parse(expr);
		System.out.println(tree);
	}

}
