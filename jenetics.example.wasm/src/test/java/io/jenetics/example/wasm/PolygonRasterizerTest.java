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

import org.testng.annotations.Test;

public class PolygonRasterizerTest {

	@Test
	public void clearAndFillFullRectangle() {
		final PolygonRasterizer rasterizer = new PolygonRasterizer(10, 10);
		rasterizer.clear(0xFFFFFFFF); // White

		// Full-canvas rectangle
		final float[] xs = {0.0F, 1.0F, 1.0F, 0.0F};
		final float[] ys = {0.0F, 0.0F, 1.0F, 1.0F};
		rasterizer.fillPolygon(xs, ys, 4, 1.0F, 0.0F, 0.0F, 1.0F); // Opaque red

		final int[] pixels = rasterizer.pixels();
		assertThat(pixels).hasSize(100);
		for (final int pixel : pixels) {
			final int r = (pixel >>> 16) & 0xFF;
			final int g = (pixel >>> 8) & 0xFF;
			final int b = pixel & 0xFF;
			assertThat(r).isEqualTo(255);
			assertThat(g).isEqualTo(0);
			assertThat(b).isEqualTo(0);
		}
	}

	@Test
	public void alphaBlending() {
		final PolygonRasterizer rasterizer = new PolygonRasterizer(10, 10);
		rasterizer.clear(0xFFFFFFFF); // White: 255, 255, 255

		// Full-canvas rectangle with 50% red
		final float[] xs = {0.0F, 1.0F, 1.0F, 0.0F};
		final float[] ys = {0.0F, 0.0F, 1.0F, 1.0F};
		rasterizer.fillPolygon(xs, ys, 4, 1.0F, 0.0F, 0.0F, 0.5F);

		final int[] pixels = rasterizer.pixels();
		for (final int pixel : pixels) {
			final int r = (pixel >>> 16) & 0xFF;
			final int g = (pixel >>> 8) & 0xFF;
			final int b = pixel & 0xFF;
			// 1.0*0.5*255 + 255*(1 - 0.5) = 128 + 127.5 = 255 for R
			// 0*0.5 + 255*0.5 = 128 for G and B
			assertThat(r).isEqualTo(255);
			assertThat(g).isBetween(127, 128);
			assertThat(b).isBetween(127, 128);
		}
	}

	@Test
	public void triangleAreaCoverage() {
		final int size = 20;
		final PolygonRasterizer rasterizer = new PolygonRasterizer(size, size);
		rasterizer.clear(0x00000000); // Transparent black

		// Triangle covering half of the 20x20 canvas (expected ~200 pixels)
		final float[] xs = {0.0F, 1.0F, 0.0F};
		final float[] ys = {0.0F, 0.0F, 1.0F};
		rasterizer.fillPolygon(xs, ys, 3, 0.0F, 1.0F, 0.0F, 1.0F);

		int count = 0;
		for (final int p : rasterizer.pixels()) {
			if (((p >>> 8) & 0xFF) == 255) {
				count++;
			}
		}

		// Expected theoretical area is 200 pixels; scanline discretisation gives ~200-210
		assertThat(count).isBetween(190, 220);
	}

	@Test
	public void clipping() {
		final PolygonRasterizer rasterizer = new PolygonRasterizer(10, 10);
		rasterizer.clear(0xFFFFFFFF);

		// Polygon partially outside bounds [-0.5, 1.5]
		final float[] xs = {-0.5F, 1.5F, 1.5F, -0.5F};
		final float[] ys = {-0.5F, -0.5F, 1.5F, 1.5F};
		rasterizer.fillPolygon(xs, ys, 4, 0.0F, 0.0F, 1.0F, 1.0F); // Blue

		for (final int pixel : rasterizer.pixels()) {
			final int b = pixel & 0xFF;
			assertThat(b).isEqualTo(255);
		}
	}

	@Test
	public void degeneratePolygon() {
		final PolygonRasterizer rasterizer = new PolygonRasterizer(10, 10);
		rasterizer.clear(0xFFFFFFFF);

		// Collinear / identical points
		final float[] xs = {0.5F, 0.5F, 0.5F};
		final float[] ys = {0.5F, 0.5F, 0.5F};
		rasterizer.fillPolygon(xs, ys, 3, 1.0F, 0.0F, 0.0F, 1.0F);

		// Canvas remains unchanged (white)
		for (final int pixel : rasterizer.pixels()) {
			assertThat(pixel).isEqualTo(0xFFFFFFFF);
		}
	}
}
