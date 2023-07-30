package io.jenetics.incubator.beans;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.TreeSet;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


public class PropertyPathTest {

	@Test
	public void ofEmptyString() {
		final var fp = java.nio.file.Path.of("/").normalize();
		System.out.println(fp.getNameCount());

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
	public void filter() {
		assertThat(
				Path
					.filter("a.b[*]")
					.test(Path.of("a.b[9]"))
			)
			.isTrue();
	}

}
