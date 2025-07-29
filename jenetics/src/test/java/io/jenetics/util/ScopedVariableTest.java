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
package io.jenetics.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class ScopedVariableTest {

	private static final ScopedVariable<String> USER = new ScopedVariable<>("default_user");
	private static final ScopedVariable<String> TOKEN = new ScopedVariable<>("default_token");

	@Test
	public void with() {
		assertThat(USER.get()).isEqualTo("default_user");
		assertThat(TOKEN.get()).isEqualTo("default_token");

		USER.set("martin");
		TOKEN.set("other_token");

		ScopedVariable.with(USER.value("otto"), TOKEN.value("3973hj2l34i92j"))
			.run(() -> {
				assertThat(USER.get()).isEqualTo("otto");
				assertThat(TOKEN.get()).isEqualTo("3973hj2l34i92j");

				USER.set("peter");
				TOKEN.set("other");

				assertThat(USER.get()).isEqualTo("peter");
				assertThat(TOKEN.get()).isEqualTo("other");
			}
		);

		assertThat(USER.get()).isEqualTo("martin");
		assertThat(TOKEN.get()).isEqualTo("other_token");
	}

}
