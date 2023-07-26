package io.jenetics.incubator.property;

import io.jenetics.incubator.property.Property.Path;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertyPathTest {

//	@Test
//	public void ofEmptyString() {
//		final var fp = java.nio.file.Path.of("/").normalize();
//		System.out.println(fp.getNameCount());
//
//		final var path = Path.of(".");
//
//		assertThat(path.count()).isEqualTo(0);
//		assertThat(path.isEmpty()).isTrue();
//		assertThat(path.name()).isEqualTo("");
//		assertThat(path.index()).isNull();
//		assertThat(path.isListPath()).isFalse();
//	}
//
//	@Test
//	public void ofString() {
//		final var path = Path.of("person.name");
//		assertThat(path).isEqualTo(Path.of("person").append("name"));
//	}
//
//	@Test
//	public void head() {
//		final var path = Path.of("person.name.title");
//		final var head = path.head();
//
//		assertThat(head).isEqualTo(Path.of("title"));
//	}
//
//	@Test
//	public void subpath() {
//		final var path = Path.of("a.b.c.d.e.f");
//		final var sub = path.subpath(1, 4);
//		System.out.println(sub);
//	}

	@Test
	public void nioPathElements() {
		final var path = java.nio.file.Path.of("a/b/c/d/e/f/g/h/i");
		System.out.println(path.getNameCount());
		System.out.println(path.getFileName());
		for (var p : path) {
			System.out.println(p + ": " + p.getNameCount());
		}
		System.out.println("ASDF: " + path.getName(4));
	}

}
