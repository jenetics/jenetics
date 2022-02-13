package io.jenetics.incubator.bean;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.testng.annotations.Test;

import io.jenetics.incubator.bean.Property.Reader;

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
				new Foo(null, 5, Arrays.asList(null, null))
			))
		));

		final var properties = Property.walk(
			foo,
			Reader.BEANS.filterPackage("io.jenetics"),
			p -> p.value() instanceof Collection<?> coll ? coll.stream() : Stream.empty()
			//Property.COLLECTION_FLATTENER
		);

		System.out.println("ASDFASDFASDF");
		properties.forEach(System.out::println);
	}

}
