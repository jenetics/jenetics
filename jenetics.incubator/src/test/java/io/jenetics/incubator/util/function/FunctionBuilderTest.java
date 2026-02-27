package io.jenetics.incubator.util.function;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Function;

import org.testng.annotations.Test;

public class FunctionBuilderTest {

	@Test
	public void buildEmpty() {
		final Function<Object, Object> fn = FunctionBuilder.of().build();
		final var arg = "ARG";
		final var result = fn.apply(arg);
		assertThat(result).isSameAs(arg);
	}

	@Test
	public void buildOne() {
		final Function<String, Integer> fn = FunctionBuilder.<String>of()
			.then(Integer::parseInt)
			.build();

		final var arg = "1234";
		final var result = fn.apply(arg);
		assertThat(result).isEqualTo(1234);
	}

	@Test
	public void buildMany() {
		final Function<String, String> fn = FunctionBuilder.<String>of()
			.then(Integer::parseInt)
			.then(i -> (double)(i + 1))
			.then(v -> v/4.3)
			.then(Double::floatValue)
			.then(Float::longValue)
			.then(Long::intValue)
			.then(String::valueOf)
			.build();

		final var arg = "1234";
		final var result = fn.apply(arg);
		assertThat(result).isEqualTo("287");
	}

}
