package io.jenetics.incubator.property;

import org.testng.annotations.Test;

import io.jenetics.jpx.GPX;

public class PropertyDescriptionTests {

	@Test
	public void returnType() {
		GPX gpx = GPX.builder().build();
		final var props = PropertyDescriptionExtractor.extract(GPX.class);

		props.forEach(p -> {
			var foo = p.getter().getGenericReturnType();
			System.out.println(foo.getTypeName());
		});

		//props.forEach(System.out::println);
	}

}
