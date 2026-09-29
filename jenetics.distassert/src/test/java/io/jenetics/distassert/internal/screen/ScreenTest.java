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
package io.jenetics.distassert.internal.screen;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.testng.annotations.Test;

public class ScreenTest {

	@Test
	void drawRectangle() {
		final var screen = new Screen(5, 4);
		screen.draw(new Rectangle(1, 0, 3, 4));

		assertThat(render(screen)).containsExactly(
			" ┏━┓ ",
			" ┃ ┃ ",
			" ┃ ┃ ",
			" ┗━┛ "
		);
	}

	@Test
	void clipRectangle() {
		final var screen = new Screen(3, 3);
		screen.draw(new Rectangle(-1, -1, 3, 3));

		assertThat(render(screen)).containsExactly(
			" ┃ ",
			"━┛ ",
			"   "
		);
	}

	@Test
	void composeCrossingRectangleStrokes() {
		final var screen = new Screen(9, 7);
		screen.draw(new Rectangle(0, 0, 9, 5));
		screen.draw(new Rectangle(2, 2, 5, 5));

		assertThat(render(screen).get(4)).isEqualTo("┗━╋━━━╋━┛");
	}

	@Test
	void drawBarUpwardsFromInclusiveOrigin() {
		final var screen = new Screen(3, 4);
		screen.draw(new Bar(1, 2, 3));

		assertThat(render(screen)).containsExactly(
			" █ ",
			" █ ",
			" █ ",
			"   "
		);
	}

	@Test
	void zeroHeightBarDrawsNothing() {
		final var screen = new Screen(3, 2);
		screen.draw(new Bar(1, 1, 0));

		assertThat(render(screen)).containsExactly("   ", "   ");
	}

	@Test
	void barReplacesExistingStrokes() {
		final var screen = new Screen(3, 3);
		screen.draw(new Rectangle(0, 0, 3, 3));
		screen.draw(new Bar(1, 1, 2));

		assertThat(render(screen)).containsExactly(
			"┏█┓",
			"┃█┃",
			"┗━┛"
		);
	}

	@Test
	void setClipsAndReplacesExistingStrokes() {
		final var screen = new Screen(3, 2);
		screen.draw(new Rectangle(0, 0, 3, 2));
		screen.set(1, 0, 'x');
		screen.set(-1, 0, 'a');
		screen.set(3, 1, 'b');

		assertThat(render(screen)).containsExactly("┏x┓", "┗━┛");
	}

	@Test
	void rejectInvalidDimensions() {
		assertThatIllegalArgumentException().isThrownBy(() -> new Screen(0, 1));
		assertThatIllegalArgumentException().isThrownBy(() -> new Screen(1, 0));
		assertThatIllegalArgumentException()
			.isThrownBy(() -> new Rectangle(0, 0, 1, 2));
		assertThatIllegalArgumentException()
			.isThrownBy(() -> new Rectangle(0, 0, 2, 1));
		assertThatIllegalArgumentException().isThrownBy(() -> new Bar(0, 0, -1));
	}

	@Test
	void useCorrectLightHorizontalCharacter() {
		assertThat(DrawChars.LIGHT_HORIZONTAL).isEqualTo('─');
		assertThat(DrawChars.BOX_CHARS[0]).isEqualTo('─');
	}

	private static List<String> render(final Screen screen) {
		final var bytes = new ByteArrayOutputStream();
		try (var out = new PrintStream(bytes, false, UTF_8)) {
			screen.print(out);
		}
		return bytes.toString(UTF_8).lines().toList();
	}

}
