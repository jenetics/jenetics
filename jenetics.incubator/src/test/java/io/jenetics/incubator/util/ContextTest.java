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
package io.jenetics.incubator.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class ContextTest {

	@Test
	public void setGet() {
		final var context = new Context<>("A");
		assertThat(context.get()).isEqualTo("A");

		context.set("B");
		assertThat(context.get()).isEqualTo("B");

		context.run("C", () -> {
			assertThat(context.get()).isEqualTo("C");
			context.set("D");
			assertThat(context.get()).isEqualTo("D");

			context.run("E", () -> {
				assertThat(context.get()).isEqualTo("E");
				context.set("F");
				assertThat(context.get()).isEqualTo("F");
			});

			context.set("G");
			assertThat(context.get()).isEqualTo("G");

			context.run(() -> {
				assertThat(context.get()).isEqualTo("G");
				context.set("H");
				assertThat(context.get()).isEqualTo("H");
			});

			assertThat(context.get()).isEqualTo("G");
		});

		assertThat(context.get()).isEqualTo("B");
	}

}
