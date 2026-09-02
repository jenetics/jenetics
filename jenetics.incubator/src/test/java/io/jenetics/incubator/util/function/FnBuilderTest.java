package io.jenetics.incubator.util.function;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Function;

import org.testng.annotations.Test;

import io.jenetics.incubator.util.function.MapType.ValueMap;

public class FnBuilderTest {

	@Test
	public void buildEmpty() {
		@SuppressWarnings("auxiliaryclass")
		final Function<Object, Object> fn = FnBuilder.of().build();
		final var arg = "ARG";
		final var result = fn.apply(arg);
		assertThat(result).isSameAs(arg);
	}

	@Test
	public void buildOne() {
		@SuppressWarnings("auxiliaryclass")
		final Function<String, Integer> fn = FnBuilder.<String>of()
			.then(Integer::parseInt, new ValueMap<>())
			.build();

		final var arg = "1234";
		final var result = fn.apply(arg);
		assertThat(result).isEqualTo(1234);
	}

	@Test
	public void buildMany() {
		@SuppressWarnings("auxiliaryclass")
		final Function<String, String> fn = FnBuilder.<String>of()
			.then(Integer::parseInt, new ValueMap<>())
			.then(i -> (double)(i + 1), new ValueMap<>())
			.then(v -> v/4.3, new ValueMap<>())
			.then(Double::floatValue, new ValueMap<>())
			.then(Float::longValue, new ValueMap<>())
			.then(Long::intValue, new ValueMap<>())
			.then(String::valueOf, new ValueMap<>())
			.build();

		final var arg = "1234";
		final var result = fn.apply(arg);
		assertThat(result).isEqualTo("287");
	}

}
