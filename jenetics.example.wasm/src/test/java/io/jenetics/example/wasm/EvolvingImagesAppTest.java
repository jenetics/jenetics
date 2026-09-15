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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;

import org.testng.annotations.Test;

public class EvolvingImagesAppTest {

	private static int[] createReference(final int width, final int height) {
		final int[] ref = new int[width * height * 4];
		for (int i = 0; i < width * height; ++i) {
			ref[i * 4] = (i * 37) % 256;
			ref[i * 4 + 1] = (i * 59) % 256;
			ref[i * 4 + 2] = (i * 83) % 256;
			ref[i * 4 + 3] = 255;
		}
		return ref;
	}

	@Test
	public void uninitializedEvolveThrowsException() {
		final var app = new EvolvingImagesApp();
		assertThat(app.status()).isEqualTo("ready");
		assertThatThrownBy(() -> app.evolve(1))
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("Engine not initialized");
	}

	@Test
	public void initializationAndEvolution() {
		final var app = new EvolvingImagesApp();
		final int w = 8;
		final int h = 8;
		final int[] ref = createReference(w, h);

		app.init(20, 3, 0.025, 0.15, 4, 15, w, h, ref);
		assertThat(app.status()).isEqualTo("initialized");
		assertThat(app.generation()).isEqualTo(0);
		assertThat(app.polygonCount()).isEqualTo(15);
		assertThat(app.polygonLength()).isEqualTo(4);
		assertThat(app.referenceWidth()).isEqualTo(w);
		assertThat(app.referenceHeight()).isEqualTo(h);

		final int gen1 = app.evolve(3);
		assertThat(gen1).isEqualTo(3);
		assertThat(app.generation()).isEqualTo(3);
		assertThat(app.bestGeneration()).isBetween(1, 3);
		final double best1 = app.bestFitness();
		assertThat(best1).isBetween(0.0, 1.0);
		assertThat(app.currentFitness()).isBetween(0.0, 1.0);
		assertThat(best1).isGreaterThanOrEqualTo(app.currentFitness());
		assertThat(app.currentPopulationAge()).isGreaterThanOrEqualTo(0.0);
		assertThat(app.bestPopulationAge()).isGreaterThanOrEqualTo(0.0);
		assertThat(app.currentEvaluationMillis()).isGreaterThanOrEqualTo(0.0);
		assertThat(app.bestEvaluationMillis()).isGreaterThanOrEqualTo(0.0);

		final int gen2 = app.evolve(2);
		assertThat(gen2).isEqualTo(5);
		assertThat(app.generation()).isEqualTo(5);
		final double best2 = app.bestFitness();
		assertThat(best2).isGreaterThanOrEqualTo(best1);
	}

	@Test
	public void zeroGenerationsEvolveIsNoop() {
		final var app = new EvolvingImagesApp();
		final int w = 6;
		final int h = 6;
		final int[] ref = createReference(w, h);

		app.init(10, 3, 0.025, 0.15, 3, 5, w, h, ref);
		final int gen = app.evolve(0);
		assertThat(gen).isEqualTo(0);
		assertThat(app.generation()).isEqualTo(0);
	}

	@Test
	public void reinitializationResetsState() {
		final var app = new EvolvingImagesApp();
		final int w = 8;
		final int h = 8;
		final int[] ref = createReference(w, h);

		app.init(10, 3, 0.025, 0.15, 4, 10, w, h, ref);
		app.evolve(5);
		assertThat(app.generation()).isEqualTo(5);

		// Re-initialize with different polygon count
		app.init(10, 3, 0.025, 0.15, 3, 8, w, h, ref);
		assertThat(app.generation()).isEqualTo(0);
		assertThat(app.bestFitness()).isEqualTo(0.0);
		assertThat(app.currentFitness()).isEqualTo(0.0);
		assertThat(app.polygonCount()).isEqualTo(8);
		assertThat(app.polygonLength()).isEqualTo(3);
		assertThat(app.bestPolygons()).isEmpty();
	}

	@Test
	public void polygonDimensionsAndBounds() {
		final var app = new EvolvingImagesApp();
		final int w = 10;
		final int h = 10;
		final int polyCount = 20;
		final int polyLength = 5;
		final int[] ref = createReference(w, h);

		app.init(15, 3, 0.025, 0.15, polyLength, polyCount, w, h, ref);
		app.evolve(2);

		final double[] polygons = app.bestPolygons();
		final int expectedSize = polyCount * (4 + 2 * polyLength);
		assertThat(polygons).hasSize(expectedSize);

		for (final double val : polygons) {
			assertThat(val).isBetween(0.0, 1.0);
		}
	}

	@Test
	public void renderBestReturnsPixels() {
		final var app = new EvolvingImagesApp();
		final int w = 8;
		final int h = 8;
		final int[] ref = createReference(w, h);

		app.init(10, 3, 0.025, 0.15, 4, 10, w, h, ref);

		// Before evolve: returns white buffer
		final int[] before = app.renderBest(16, 16);
		assertThat(before).hasSize(16 * 16);
		for (final int pixel : before) {
			assertThat(pixel).isEqualTo(0xFFFFFFFF);
		}

		app.evolve(2);
		final int[] after = app.renderBest(16, 16);
		assertThat(after).hasSize(16 * 16);

		// Check edge case: invalid dimensions
		assertThat(app.renderBest(0, 10)).isEmpty();
		assertThat(app.renderBest(10, -1)).isEmpty();
	}

	@Test
	public void nonSquareReferenceImage() {
		final var app = new EvolvingImagesApp();
		final int w = 4;
		final int h = 12;
		final int[] ref = createReference(w, h);

		app.init(10, 3, 0.025, 0.15, 3, 10, w, h, ref);
		app.evolve(3);

		assertThat(app.generation()).isEqualTo(3);
		assertThat(app.bestFitness()).isBetween(0.0, 1.0);
	}

	@Test
	public void minimumPopulationSize() {
		final var app = new EvolvingImagesApp();
		final int w = 2;
		final int h = 2;
		final int[] ref = createReference(w, h);

		// Minimum allowed bounds: pop=3, tournament=2, length=3, count=2, size=2x2
		app.init(3, 2, 0.001, 0.01, 3, 2, w, h, ref);
		app.evolve(2);

		assertThat(app.generation()).isEqualTo(2);
		assertThat(app.bestFitness()).isBetween(0.0, 1.0);
	}
}
