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
package io.jenetics.incubator.metamodel;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.TreeSet;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class PathTest {

	@Test
	public void ofEmptyString() {
		final var path = Path.of("");
		assertThat(path.count()).isEqualTo(0);
		assertThat(path.isEmpty()).isTrue();
	}

	@Test
	public void ofString() {
		final var path = Path.of("person.name");
		assertThat((Object)path)
			.isEqualTo(Path.of("person").append("name"));
	}

	@Test
	public void head() {
		final var path = Path.of("person.name.title");
		final var head = path.head();

		assertThat((Object)head).isEqualTo(Path.of("title"));
	}

	@Test
	public void subPath() {
		final var path = Path.of("a.b.c.d.e.f");
		final var sub = path.subPath(1, 4);

		assertThat((Object)sub).isEqualTo(Path.of("b.c.d"));
	}

	@Test(dataProvider = "paths")
	public void name(String value) {
		final var path = Path.of(value);
		final var file = java.nio.file.Path.of(value.replace('.', '/'));

		assertThat(path.head().toString()).isEqualTo(file.getFileName().toString());
	}

	@Test(dataProvider = "paths")
	public void count(String value) {
		final var path = Path.of(value);
		final var file = java.nio.file.Path.of(value.replace('.', '/'));

		assertThat(path.count()).isEqualTo(file.getNameCount());
	}

	@DataProvider
	public Object[][] paths() {
		return new Object[][] {
			{"a"},
			{"a.b"},
			{"a.b.c"}
		};
	}

	@Test(dataProvider = "stringPaths")
	public void toString(final String value) {
		final var path = Path.of(value);
		assertThat(path.toString()).isEqualTo(value);
	}

	@DataProvider
	public Object[][] stringPaths() {
		return new Object[][] {
			{"a"},
			{"a[2]"},
			{"a.b"},
			{"a[5].b[1]"},
			{"a.b.c"},
			{"a.b[4].{c}"},
			{"a.b[3][3].c[0][1][2]"}
		};
	}

	@Test
	public void parent() {
		final var path = Path.of("a.b.c");

		assertThat((Object)path.parent())
			.isEqualTo(Path.of("a.b"));
	}

	@Test
	public void emptyParent() {
		final var path = Path.of("a");

		assertThat((Object)path.parent()).isEqualTo(null);
	}

	@Test
	public void treeSetPath() {
		final var set = new TreeSet<Path>();
		set.add(Path.of("a.d"));
		set.add(Path.of("a.c"));
		set.add(Path.of("b"));
		set.add(Path.of("a.b.a"));
		set.add(Path.of("a.b[0].c"));
		set.add(Path.of("a.b[1].c"));
		set.add(Path.of("a.b[2].c"));
		set.add(Path.of("a.b[0]"));

		System.out.println(set.size());
		set.forEach(System.out::println);
		System.out.println("------");

		final var tail = set.tailSet(Path.of("a.b[1].c"));
		System.out.println(tail.size());
		tail.forEach(System.out::println);
		System.out.println("----");
	}

	@Test
	public void random() {
		final var random = RandomGenerator.getDefault();
		for (int i = 0; i < 10; ++i) {
			final var path = next(random);
			System.out.println(path);
			System.out.println(toPath(path));
			System.out.println();
		}
	}

	private static Path next(final RandomGenerator random) {
		final int count = random.nextInt(20);
		final var elements = new Path.Element[count];

		for (int i = 0; i < count; ++i) {
			if (random.nextBoolean()) {
				elements[i] = new Path.Field("name_" + random.nextInt(10, 1000));
			} else {
				elements[i] = new Path.Index(random.nextInt(1000));
			}
		}

		return Path.of(elements);
	}

	private static Path toPath(final java.nio.file.Path path) {
		return Path.of(path.toString().replace(File.separatorChar, '.'));
	}

	private static java.nio.file.Path toPath(final Path path) {
		final var string = path.stream()
			.map(Path::toString)
			.collect(Collectors.joining(File.separator));

		return java.nio.file.Path.of(string);
	}

}
