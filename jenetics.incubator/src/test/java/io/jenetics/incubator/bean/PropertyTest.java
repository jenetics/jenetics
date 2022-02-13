package io.jenetics.incubator.bean;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

public class PropertyTest {

	record Foo(String getFooValue, int getFooIndex, List<Foo> getFoos) {
		@Override
		public String toString() {
			return "Foo@" + Integer.toHexString(System.identityHashCode(this));
		}
	}

	@Test
	public void walk() {
		final var foo = new Foo("A", 1, List.of(
			new Foo("B", 2, List.of()),
			new Foo("C", 3, List.of()),
			new Foo("D", 4, List.of(
				new Foo(null, 5, Arrays.asList(null, null)),
				new Foo("D", 6, Arrays.asList())
			))
		));

		final List<Property> properties = Property
			.walk(foo,"io.jenetics")
			.toList();

		properties.forEach(System.out::println);

		properties.stream()
			.filter(Property.Path.matcher("*.foos[*].fooIndex"))
			.forEach(System.out::println);
	}

}
