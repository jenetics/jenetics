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
package io.jenetics.example.wasm;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Random;

import org.testng.annotations.Test;

public class PolygonTest {

	@Test
	public void createAndAccessors() {
		final Polygon p = Polygon.newRandom(4, new Random(123));
		assertThat(p.length()).isEqualTo(4);
		assertThat(p.red()).isBetween(0.0F, 1.0F);
		assertThat(p.green()).isBetween(0.0F, 1.0F);
		assertThat(p.blue()).isBetween(0.0F, 1.0F);
		assertThat(p.alpha()).isBetween(0.0F, 1.0F);

		for (int i = 0; i < 4; i++) {
			assertThat(p.x(i)).isBetween(0.0F, 1.0F);
			assertThat(p.y(i)).isBetween(0.0F, 1.0F);
		}
	}

	@Test
	public void mean() {
		final Polygon p1 = Polygon.newRandom(4, new Random(1));
		final Polygon p2 = Polygon.newRandom(4, new Random(2));
		final Polygon mean = p1.mean(p2);

		assertThat(mean.length()).isEqualTo(4);
		assertThat(mean.red()).isEqualTo((p1.red() + p2.red()) * 0.5F);
		assertThat(mean.green()).isEqualTo((p1.green() + p2.green()) * 0.5F);
		assertThat(mean.blue()).isEqualTo((p1.blue() + p2.blue()) * 0.5F);
		assertThat(mean.alpha()).isEqualTo((p1.alpha() + p2.alpha()) * 0.5F);
	}

	@Test
	public void mutate() {
		final Polygon p = Polygon.newRandom(4, new Random(1));
		final Polygon mutated = p.mutate(1.0F, 0.2F);

		assertThat(mutated.length()).isEqualTo(4);
		assertThat(mutated.red()).isBetween(0.0F, 1.0F);
		assertThat(mutated.green()).isBetween(0.0F, 1.0F);
		assertThat(mutated.blue()).isBetween(0.0F, 1.0F);
		assertThat(mutated.alpha()).isBetween(0.0F, 1.0F);
	}

	@Test
	public void writeTo() {
		final Polygon p = Polygon.newRandom(4, new Random(1));
		final double[] dst = new double[12];
		p.writeTo(dst, 0);

		assertThat(dst[0]).isEqualTo(p.red());
		assertThat(dst[1]).isEqualTo(p.green());
		assertThat(dst[2]).isEqualTo(p.blue());
		assertThat(dst[3]).isEqualTo(p.alpha());
		assertThat(dst[4]).isEqualTo(p.x(0));
		assertThat(dst[5]).isEqualTo(p.y(0));
	}
}
