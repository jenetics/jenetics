package io.jenetics.incubator.property;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import io.jenetics.incubator.property.Property.Path;

public class PropertyTest {

	record Foo(String fooValue, int fooIndex, List<Foo> foos) {
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
			.walk(foo, /*p -> Stream.empty(),*/ "io.jenetics")
			.toList();

		properties.forEach(System.out::println);

		properties.stream()
			.filter(Property.Path.matcher("foos[2].foos[*].**.fooValue"))
			.forEach(System.out::println);

		Property.walk(null);
	}

	@Test
	public void path() {
		final var path = new Path("a")
			.append("b")
			.append("c", 0)
			.append("d", 2)
			.append("e");

		System.out.println(path);

		for (Path value : path) {
			System.out.println(value);
		}
	}

	@Test
	public void matcher() {
		// a.b.c[0].d[2].e
		final var path = new Path("a")
			.append("b")
			.append("c", 0)
			.append("d", 2)
			.append("e");

		System.out.println(path);
		var pattern = PathPattern.compile("a.b.c[0].d[2].e");
		System.out.println(pattern.matches(path));

		pattern = PathPattern.compile("a.b.c[0].d[*].e");
		System.out.println(pattern.matches(path));

		pattern = PathPattern.compile("a.**.d[*].e");
		System.out.println(pattern.matches(path));
	}

}
