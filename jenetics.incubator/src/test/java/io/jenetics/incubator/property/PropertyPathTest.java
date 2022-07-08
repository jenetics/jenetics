package io.jenetics.incubator.property;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.incubator.property.Property.Path;

public class PropertyPathTest {

	@Test(dataProvider = "validPaths")
	public void creation(final String path, final String expected) {
		assertThat(Path.of(path).toString()).isEqualTo(expected);
	}

	@DataProvider
	public Object[][] validPaths() {
		return new Object[][] {
			{ ".", "" },
			{ "path1", "path1" },
			{ "path1.path2", "path1.path2" },
			{ "path1..path2", "path1.path2" },
			{ "path1.....path2", "path1.path2" },
			{ "path1[0].....path2", "path1[0].path2" },
			{ "path1.path2[345]", "path1.path2[345]" },
			{ "path1.path2[345].index", "path1.path2[345].index" }
		};
	}

	@Test(dataProvider = "matchingPaths")
	public void matches(final String pattern, final String path, final boolean matching) {
		assertThat(Path.matcher(pattern).test(Path.of(path)))
			.withFailMessage("%s -> %s != %s", pattern, path, matching)
			.isEqualTo(matching);
	}

	@DataProvider
	public Object[][] matchingPaths() {
		return new Object[][] {
			{ "path1", "path1", true },
			{ "path1*", "path1", true },
			{ "pa***th1*", "path1", true },
			{ "pa***th1*", "path2", false },
			{ "path1.*", "path1.path2", true },
			{ "path1.*path2", "path1.path2", true },
			{ "path1.*.path3", "path1.path2.path3", true },
			{ "path1.**.path4", "path1.path2.path3.path4", true },
			{ "path1.**.path4[0]", "path1.path2.path3.path4[0]", true },
			{ "path1.**.path4*", "path1.path2.path3.path4[0]", true },
			{ "path1.**.path4[*]", "path1.path2.path3.path4[0]", true },
			{ "path1.**.path4[1]", "path1.path2.path3.path4[0]", false },
			{ "path1.path2[*].**.path4[0]", "path1.path2[1].path3.path4[0]", true }
		};
	}

	@Test
	public void head() {
		final var path = Path.of("path1.path2[1].path3.path4[0]");
		final var head = path.head();

		assertThat(path).isNotEqualTo(head);
		assertThat(path.count()).isEqualTo(4);
		assertThat(head.count()).isEqualTo(1);
		assertThat(path.head()).isEqualTo(Path.of("path4[0]"));
	}

	@Test
	public void get() {
		final var path = Path.of("path0[0].path1[1].path2[2].path3[3]");
		assertThat(path.count()).isEqualTo(4);

		for (int i = 0; i < path.count(); ++i) {
			assertThat(path.get(i)).isEqualTo(Path.of("path" + i + "[" + i + "]"));
			assertThat(path.get(i).index()).isEqualTo(i);
		}
	}

}
