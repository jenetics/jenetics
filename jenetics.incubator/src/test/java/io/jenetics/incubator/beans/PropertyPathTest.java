package io.jenetics.incubator.beans;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import io.jenetics.incubator.beans.property.Property;


public class PropertyPathTest {

	@Test
	public void ofEmptyString() {
		final var fp = java.nio.file.Path.of("/").normalize();
		System.out.println(fp.getNameCount());

		final var path = Property.Path.of("");

		assertThat(path.count()).isEqualTo(0);
		assertThat(path.isEmpty()).isTrue();
	}

	@Test
	public void ofString() {
		final var path = Property.Path.of("person.name");
		assertThat(path)
			.isEqualTo(Property.Path.of("person").append("name"));
	}

	@Test
	public void head() {
		final var path = Property.Path.of("person.name.title");
		final var head = path.head();

		assertThat(head).isEqualTo(Property.Path.of("title"));
	}

	@Test
	public void subPath() {
		final var path = Property.Path.of("a.b.c.d.e.f");
		final var sub = path.subPath(1, 4);

		assertThat(sub).isEqualTo(Property.Path.of("b.c.d"));
	}

	@Test(dataProvider = "paths")
	public void name(String value) {
		final var path = Property.Path.of(value);
		final var file = java.nio.file.Path.of(value.replace('.', '/'));

		assertThat(path.head().toString()).isEqualTo(file.getFileName().toString());
	}

	@Test(dataProvider = "paths")
	public void count(String value) {
		final var path = Property.Path.of(value);
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
		final var path = Property.Path.of(value);
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
		final var path = Property.Path.of("a.b.c");

		assertThat(path.parent())
			.isEqualTo(Optional.of(Property.Path.of("a.b")));
	}

	@Test
	public void emptyParent() {
		final var path = Property.Path.of("a");

		assertThat(path.parent()).isEqualTo(Optional.empty());
	}

}
