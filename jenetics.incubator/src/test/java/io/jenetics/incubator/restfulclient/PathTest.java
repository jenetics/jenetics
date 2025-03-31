package io.jenetics.incubator.restfulclient;

import org.testng.annotations.Test;

public class PathTest {

	@Test
	public void create() {
		var path = Path.of("//////foo/bar//.././{id_1}/author//");
		System.out.println(path);
		System.out.println(path.paramNames());

		System.out.println(path.resolve("id_1", "_%_"));
	}

}
