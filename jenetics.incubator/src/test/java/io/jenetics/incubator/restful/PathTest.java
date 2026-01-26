package io.jenetics.incubator.restful;

import java.util.List;

import org.testng.annotations.Test;

public class PathTest {

	@Test
	public void create() {
		var path = Path.of("//////foo/bar//.././{id_1}/author//");
		System.out.println(path);
		System.out.println(path.parameterNames());

		System.out.println(path.resolve("id_1", "_%_"));
	}

	@Test
	public void resolve() {
		final var path = Path.of("/users/{user-id}/addresses/{address-id}/author/{user-id}/asfasdf");
		System.out.println(path.path() + " -> " + path);

		var path1 = path.resolve("user-id", "_abc_");
		System.out.println(path1.path() + " -> " + path1);

		path1 = path1.resolve("address-id", "_123_");
		System.out.println(path1.path() + " -> " + path1);

		final var parameters = List.of(
			Parameter.path("user-id", "_123_"),
			Parameter.path("address-id", "_abc_")
		);
		System.out.println(path.resolve(parameters));
	}

}
