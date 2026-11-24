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

import static java.lang.Math.max;
import static java.lang.String.format;

import java.util.random.RandomGenerator;

import io.jenetics.internal.util.Requires;
import io.jenetics.util.Mean;
import io.jenetics.util.RandomRegistry;

public final class Polygon implements Mean<Polygon> {

	// The polygon in packed representation:
	// index | data
	//    0  | red component
	//    1  | green component
	//    2  | blue component
	//    3  | alpha channel
	//    4  | first x coordinate
	//    5  | first y coordinate
	//    6  | second x coordinate
	//  ...
	//    N  | last y coordinate
	// ---------------------------
	/// size = 4 + 2*polygon-length
	private final float[] _data;
	private final int _length;

	private Polygon(final int length) {
		final int polygonSize = 4 + 2 * Requires.positive(length);
		_data = new float[polygonSize];
		_length = length;
	}

	public int length() {
		return _length;
	}

	public float red() {
		return _data[0];
	}

	public float green() {
		return _data[1];
	}

	public float blue() {
		return _data[2];
	}

	public float alpha() {
		return _data[3];
	}

	public float x(final int index) {
		return _data[4 + 2 * index];
	}

	public float y(final int index) {
		return _data[5 + 2 * index];
	}

	/**
	 * Returns a copy of the packed polygon data.
	 *
	 * @return a copy of the packed RGBA and coordinate data
	 */
	public float[] data() {
		return _data.clone();
	}

	public void writeTo(final double[] dst, final int offset) {
		for (int i = 0; i < _data.length; i++) {
			dst[offset + i] = _data[i];
		}
	}

	@Override
	public Polygon mean(final Polygon other) {
		if (other.length() != length()) {
			throw new IllegalArgumentException(format(
				"Polygon must have the same length: %d != %d",
				length(), other.length()
			));
		}

		final Polygon mean = new Polygon(length());
		for (int i = _data.length; --i >= 0;) {
			mean._data[i] = (_data[i] + other._data[i]) * 0.5F;
		}

		return mean;
	}

	/**
	 * Return a new Polygon, mutated with the given rate and amount.
	 * <p>
	 * Each component of the Polygon may be mutated according to the specified
	 * mutation rate. In case a component is going to be mutated, its value will
	 * be randomly modified in the uniform range of
	 * {@code [-magnitude, +magnitude]}.
	 *
	 * @param rate the mutation rate
	 * @param magnitude the mutation amount
	 * @return a new Polygon
	 */
	public Polygon mutate(final float rate, final float magnitude) {
		final var random = RandomRegistry.random();
		final Polygon mutated = new Polygon(length());

		for (int i = 0; i < _data.length; ++i) {
			if (random.nextFloat() < rate) {
				mutated._data[i] =
					clamp(_data[i] + (random.nextFloat() * 2F - 1F) * magnitude);
			} else {
				mutated._data[i] = _data[i];
			}
		}

		return mutated;
	}

	/**
	 * Draw the Polygon to the buffer of the given rasterizer.
	 */
	public void fill(final PolygonRasterizer rasterizer) {
		rasterizer.fillPolygon(_data, _length);
	}

	/**
	 * Creates a polygon from packed RGBA and coordinate data.
	 *
	 * @param data packed polygon data; its length must be {@code 4 + 2 * n}
	 * @return a polygon containing a copy of {@code data}
	 */
	public static Polygon of(final float[] data) {
		Requires.positive((data.length - 4) / 2);
		final int length = (data.length - 4) / 2;
		final Polygon p = new Polygon(length);
		System.arraycopy(data, 0, p._data, 0, data.length);
		return p;
	}

	/**
	 * Creates a new random Polygon of the given length.
	 */
	public static Polygon newRandom(final int length, final RandomGenerator random) {
		Requires.positive(length);
		final Polygon p = new Polygon(length);

		p._data[0] = random.nextFloat(); // r
		p._data[1] = random.nextFloat(); // g
		p._data[2] = random.nextFloat(); // b
		p._data[3] = max(0.2F, random.nextFloat() * random.nextFloat()); // a

		float px = 0.5F;
		float py = 0.5F;
		for (int k = 0; k < length; k++) {
			p._data[4 + 2 * k] = px = clamp(px + random.nextFloat() - 0.5F);
			p._data[5 + 2 * k] = py = clamp(py + random.nextFloat() - 0.5F);
		}
		return p;
	}

	private static float clamp(final float a) {
		return a < 0.0F ? 0.0F : Math.min(a, 1.0F);
	}

	public static Polygon newRandom(final int length) {
		return newRandom(length, RandomRegistry.random());
	}
}
