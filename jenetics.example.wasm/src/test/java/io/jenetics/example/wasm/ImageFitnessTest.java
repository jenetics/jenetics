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

import java.util.Arrays;

import org.testng.annotations.Test;

import io.jenetics.util.ISeq;

public class ImageFitnessTest {

	private static PolygonChromosome solidChromosome(
		final float r,
		final float g,
		final float b,
		final float a
	) {
		final float[] data = new float[] {
			r, g, b, a,
			0.0F, 0.0F,
			1.0F, 0.0F,
			1.0F, 1.0F,
			0.0F, 1.0F
		};
		return new PolygonChromosome(ISeq.of(PolygonGene.of(Polygon.of(data))));
	}

	@Test
	public void whiteReferenceWithWhiteChromosome() {
		final int width = 10;
		final int height = 10;
		final int[] whiteRef = new int[width * height * 4];
		Arrays.fill(whiteRef, 255); // Opaque white RGBA

		final ImageFitness fitness = new ImageFitness(width, height, whiteRef);
		final PolygonChromosome whiteCh = solidChromosome(1.0F, 1.0F, 1.0F, 1.0F);
		final double fit = fitness.apply(whiteCh);

		assertThat(fit).isEqualTo(1.0);
	}

	@Test
	public void blackReferenceWithWhiteChromosome() {
		final int width = 10;
		final int height = 10;
		final int[] blackRef = new int[width * height * 4];
		for (int i = 0; i < width * height; i++) {
			blackRef[i * 4] = 0;     // R
			blackRef[i * 4 + 1] = 0; // G
			blackRef[i * 4 + 2] = 0; // B
			blackRef[i * 4 + 3] = 255; // A (should be ignored)
		}

		final ImageFitness fitness = new ImageFitness(width, height, blackRef);
		final PolygonChromosome whiteCh = solidChromosome(1.0F, 1.0F, 1.0F, 1.0F);
		final double fit = fitness.apply(whiteCh);

		// 1.0 - (10*10*3*255) / (10*10*3*256) = 1.0 - 255/256 = 1/256 ≈ 0.0039
		assertThat(fit).isCloseTo(1.0 / 256.0, org.assertj.core.data.Offset.offset(0.0001));
	}

	@Test
	public void blackReferenceWithBlackChromosome() {
		final int width = 10;
		final int height = 10;
		final int[] blackRef = new int[width * height * 4];
		for (int i = 0; i < width * height; i++) {
			blackRef[i * 4] = 0;
			blackRef[i * 4 + 1] = 0;
			blackRef[i * 4 + 2] = 0;
			blackRef[i * 4 + 3] = 255;
		}

		final ImageFitness fitness = new ImageFitness(width, height, blackRef);
		final PolygonChromosome blackCh = solidChromosome(0.0F, 0.0F, 0.0F, 1.0F);
		final double fit = fitness.apply(blackCh);

		assertThat(fit).isEqualTo(1.0);
	}

	@Test
	public void randomChromosomeFitnessInRange() {
		final int width = 20;
		final int height = 20;
		final int[] ref = new int[width * height * 4];
		Arrays.fill(ref, 128);

		final ImageFitness fitness = new ImageFitness(width, height, ref);
		final PolygonChromosome chromosome = new PolygonChromosome(50, 4);

		final double fit = fitness.apply(chromosome);
		assertThat(fit).isBetween(0.0, 1.0);
	}
}
