/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.incubator.web.restful;

import java.util.List;

import org.testng.annotations.Test;

import io.jenetics.incubator.web.restful.Parameter;
import io.jenetics.incubator.web.restful.Path;

public class PathTest {

	@Test
	public void create() {
		var path = Path.of("//////foo/bar//.././{id_1}/author//");
		System.out.println(path);
		System.out.println(path.parameters());

		System.out.println(path.resolve("id_1", "_%_"));
	}

	@Test
	public void resolve() {
		final var path = Path.of("/users/{user-id}/addresses/{address-id}/author/{user-id}/asfasdf");
		System.out.println(path + " -> " + path);

		var path1 = path.resolve("user-id", "_abc_");
		System.out.println(path1 + " -> " + path1);

		path1 = path1.resolve("address-id", "_123_");
		System.out.println(path1 + " -> " + path1);

		final var parameters = List.of(
			Parameter.path("user-id", "_123_"),
			Parameter.path("address-id", "_abc_")
		);
		System.out.println(path.resolve(parameters));
	}

}
