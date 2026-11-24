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

import java.util.function.Function;

/**
 * Image fitness function comparing a rendered polygon chromosome with a
 * reference image.
 * <p>
 * The chromosome is rendered into a reusable ARGB buffer and compared with
 * the reference pixel by pixel. The alpha channel is ignored. The returned
 * value is {@code 1 - sum(channel differences) / (width * height * 3 * 256)}.
 */
public final class ImageFitness implements Function<PolygonChromosome, Double> {

	private final int _width;
	private final int _height;
	private final int[] _refRgba;
	private final PolygonRasterizer _rasterizer;

	/**
	 * Creates a new fitness evaluator.
	 *
	 * @param width reference width
	 * @param height reference height
	 * @param refRgba flat reference pixel array [r, g, b, a, r, g, b, a, ...]
	 */
	public ImageFitness(final int width, final int height, final int[] refRgba) {
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException("Width and height must be positive.");
		}
		if (refRgba.length < width * height * 4) {
			throw new IllegalArgumentException(
				"Reference pixel array must have at least width * height * 4 elements: " +
				refRgba.length + " < " + (width * height * 4)
			);
		}
		_width = width;
		_height = height;
		_refRgba = refRgba.clone();
		_rasterizer = new PolygonRasterizer(width, height);
	}

	/**
	 * Calculate the fitness function for a Polygon chromosome.
	 * <p>
	 * For this purpose, we first render the polygons on the test buffer, and
	 * then compare the resulting image pixel by pixel with the reference image.
	 *
	 * @param chromosome the chromosome to evaluate
	 * @return a fitness value where {@code 1.0} is an exact RGB match
	 */
	@Override
	public Double apply(final PolygonChromosome chromosome) {
		chromosome.render(_rasterizer);
		final int[] testPixels = _rasterizer.pixels();

		int diff = 0;
		final int pixelCount = _width * _height;

		for (int i = 0; i < pixelCount; i++) {
			final int argb = testPixels[i];
			final int tr = (argb >>> 16) & 0xFF;
			final int tg = (argb >>> 8) & 0xFF;
			final int tb = argb & 0xFF;

			final int refOffset = i * 4;
			final int rr = _refRgba[refOffset] & 0xFF;
			final int rg = _refRgba[refOffset + 1] & 0xFF;
			final int rb = _refRgba[refOffset + 2] & 0xFF;

			diff += Math.abs(tr - rr);
			diff += Math.abs(tg - rg);
			diff += Math.abs(tb - rb);
		}

		return 1.0 - diff / (double) (_width * _height * 3 * 256);
	}

	public int width() {
		return _width;
	}

	public int height() {
		return _height;
	}

	public PolygonRasterizer rasterizer() {
		return _rasterizer;
	}
}
