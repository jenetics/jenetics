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
package io.jenetics.incubator.property;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Optional;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class PathNamePatternTest {

	@Test(dataProvider = "patterns")
	public void compile(final String pattern, final Optional<PathNamePattern> expected) {
		final var pnp = PathNamePattern.compile(pattern);

		assertThat(pnp).isEqualTo(expected);
	}

	@DataProvider
	public static Object[][] patterns() {
		return new Object[][] {
			{"", Optional.empty()},
			{"7name", Optional.empty()},
			{"1*", Optional.empty()},
			{"*", Optional.of(new PathNamePattern("*", null))},
			{"**", Optional.of(new PathNamePattern("**", null))},
			{"***", Optional.of(new PathNamePattern("***", null))},
			{"*1*", Optional.of(new PathNamePattern("*1*", null))},
			{"name", Optional.of(new PathNamePattern("name", null))},
			{"*name", Optional.of(new PathNamePattern("*name", null))},
			{"name[1]", Optional.of(new PathNamePattern("name", "1"))},
			{"name[123]", Optional.of(new PathNamePattern("name", "123"))},
			{"name[*]", Optional.of(new PathNamePattern("name", "*"))},
			{"name[*1]", Optional.of(new PathNamePattern("name", "*1"))},
			{"name[*1*]", Optional.of(new PathNamePattern("name", "*1*"))},
			{"name[*1**]", Optional.of(new PathNamePattern("name", "*1**"))},
			{"name[*1*12*12]", Optional.of(new PathNamePattern("name", "*1*12*12"))},
			{"*[12]", Optional.of(new PathNamePattern("*", "12"))},
			{"*[*]", Optional.of(new PathNamePattern("*", "*"))},
			{"*[**]", Optional.of(new PathNamePattern("*", "**"))},
			{"**[*]", Optional.of(new PathNamePattern("**", "*"))},
			{"name[a]", Optional.empty()},
			{"name[a*]", Optional.empty()},
			{"name[*a*]", Optional.empty()},
			{"name[?]", Optional.empty()},
			{"name[1", Optional.empty()},
			{"name1]", Optional.empty()}
		};
	}

}
