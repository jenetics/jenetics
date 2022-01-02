package io.jenetics.incubator.mathexpr;

import org.testng.annotations.Test;

public class MathExprTokenizerTest {

	@Test
	public void number() {
		final var value = "a*3 -b 6z*sin(34) +3.3 c 3e+3 d * -3.5e-34 f +45.113E-234 g -45.113E234 _asf-fd 4**23 5^3";
		final var tokenizer = new MathExprTokenizer(value);

		tokenizer.tokens().forEach(System.out::println);
	}

}
