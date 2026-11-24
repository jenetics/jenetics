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

import java.util.Arrays;

/**
 * Pure-Java scanline polygon rasterizer into an ARGB {@code int[]} buffer.
 * <p>
 * Polygons use normalized coordinates and are filled with an even-odd
 * scanline rule. Colors are alpha-blended over the existing buffer and
 * coordinates outside the image are clipped.
 */
public final class PolygonRasterizer {

	private final int _width;
	private final int _height;
	private final int[] _pixels;

	private float[] _px = new float[64];
	private float[] _py = new float[64];
	private float[] _nodeX = new float[64];

	/**
	 * Creates a rasterizer with a fixed-size destination buffer.
	 *
	 * @param width image width in pixels
	 * @param height image height in pixels
	 * @throws IllegalArgumentException if either dimension is not positive
	 */
	public PolygonRasterizer(final int width, final int height) {
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException("Width and height must be positive.");
		}
		_width = width;
		_height = height;
		_pixels = new int[width * height];
	}

	public int width() {
		return _width;
	}

	public int height() {
		return _height;
	}

	/**
	 * Returns the mutable backing pixel buffer.
	 *
	 * @return the ARGB pixel buffer in row-major order
	 */
	public int[] pixels() {
		return _pixels;
	}

	/**
	 * Fills the complete buffer with one ARGB color.
	 *
	 * @param argb the color in {@code 0xAARRGGBB} format
	 */
	public void clear(final int argb) {
		Arrays.fill(_pixels, argb);
	}

	/**
	 * Fills a polygon described by separate normalized coordinate arrays.
	 *
	 * @param xs normalized x coordinates
	 * @param ys normalized y coordinates
	 * @param n number of vertices to read from each coordinate array
	 * @param r normalized red component
	 * @param g normalized green component
	 * @param b normalized blue component
	 * @param a normalized alpha component
	 */
	public void fillPolygon(
		final float[] xs,
		final float[] ys,
		final int n,
		final float r,
		final float g,
		final float b,
		final float a
	) {
		if (n < 3 || a <= 0.0F) {
			return;
		}

		ensureCapacity(n);

		for (int i = 0; i < n; i++) {
			_px[i] = xs[i] * _width;
			_py[i] = ys[i] * _height;
		}

		rasterize(n, r, g, b, a);
	}

	/**
	 * Fills a polygon in the packed representation used by {@link Polygon}.
	 *
	 * @param data packed RGBA and normalized coordinate data
	 * @param length number of vertices in the packed polygon
	 */
	public void fillPolygon(final float[] data, final int length) {
		if (length < 3) {
			return;
		}
		final float a = data[3];
		if (a <= 0.0F) {
			return;
		}

		ensureCapacity(length);

		final float r = data[0];
		final float g = data[1];
		final float b = data[2];

		for (int i = 0; i < length; i++) {
			_px[i] = data[4 + 2 * i] * _width;
			_py[i] = data[5 + 2 * i] * _height;
		}

		rasterize(length, r, g, b, a);
	}

	private void ensureCapacity(final int n) {
		if (_px.length < n) {
			final int newCap = Math.max(n * 2, _px.length * 2);
			_px = new float[newCap];
			_py = new float[newCap];
			_nodeX = new float[newCap];
		}
	}

	private void rasterize(
		final int n,
		final float r,
		final float g,
		final float b,
		final float a
	) {
		float minY = Float.MAX_VALUE;
		float maxY = -Float.MAX_VALUE;

		for (int i = 0; i < n; i++) {
			final float py = _py[i];
			if (py < minY) minY = py;
			if (py > maxY) maxY = py;
		}

		final int startY = Math.max(0, (int) Math.floor(minY));
		final int endY = Math.min(_height - 1, (int) Math.ceil(maxY));

		if (startY > endY) {
			return;
		}

		final int sr = (int) (clamp(r) * 255.0F + 0.5F);
		final int sg = (int) (clamp(g) * 255.0F + 0.5F);
		final int sb = (int) (clamp(b) * 255.0F + 0.5F);
		final int sa = (int) (clamp(a) * 255.0F + 0.5F);
		final int invA = 255 - sa;

		for (int y = startY; y <= endY; y++) {
			final float scanY = y + 0.5F;
			int nodes = 0;

			for (int i = 0; i < n; i++) {
				final int j = (i + 1 == n) ? 0 : i + 1;
				final float y1 = _py[i];
				final float y2 = _py[j];

				if ((y1 <= scanY && y2 > scanY) || (y2 <= scanY && y1 > scanY)) {
					if (nodes >= _nodeX.length) {
						_nodeX = Arrays.copyOf(_nodeX, _nodeX.length * 2);
					}
					final float x1 = _px[i];
					final float x2 = _px[j];
					_nodeX[nodes++] = x1 + (scanY - y1) / (y2 - y1) * (x2 - x1);
				}
			}

			if (nodes < 2) {
				continue;
			}

			Arrays.sort(_nodeX, 0, nodes);

			final int rowOffset = y * _width;

			for (int k = 0; k < nodes - 1; k += 2) {
				final float xStart = _nodeX[k];
				final float xEnd = _nodeX[k + 1];

				final int x0 = Math.max(0, (int) Math.ceil(xStart - 0.5F));
				final int x1 = Math.min(_width - 1, (int) Math.floor(xEnd - 0.5F));

				for (int x = x0; x <= x1; x++) {
					final int idx = rowOffset + x;
					final int dst = _pixels[idx];

					final int dr = (dst >>> 16) & 0xFF;
					final int dg = (dst >>> 8) & 0xFF;
					final int db = dst & 0xFF;
					final int da = (dst >>> 24) & 0xFF;

					final int outR = (sr * sa + dr * invA + 127) / 255;
					final int outG = (sg * sa + dg * invA + 127) / 255;
					final int outB = (sb * sa + db * invA + 127) / 255;
					final int outA = Math.min(255, sa + (da * invA + 127) / 255);

					_pixels[idx] = (outA << 24) | (outR << 16) | (outG << 8) | outB;
				}
			}
		}
	}

	private static float clamp(final float v) {
		return v < 0.0F ? 0.0F : Math.min(1.0F, v);
	}
}
