/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jenetics.example.image;

import static java.lang.Math.max;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.util.Random;

import org.jenetics.internal.util.require;

import org.jenetics.util.RandomRegistry;

public final class Polygon {

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

	private Polygon(int length) {
		final int polygonSize = 4 + 2*length;
		_data = new float[polygonSize];
		_length = length;
	}

	public int length() {
		return _length;
	}

	/**
	 * Return a new Polygon, mutated with the given rate and amount.
	 * <p>
	 * Each component of the Polygon may be mutated according to the specified
	 * mutation rate. In case a component is going to be mutated, its value will
	 * be randomly modified in the uniform range of
	 * {@code [-mutationAmount, +mutationAmount]}.
	 *
	 * @param mutationRate the mutation rate
	 * @param mutationAmount the mutation amount
	 * @return a new Polygon
	 */
	public Polygon mutate(final float mutationRate, final float mutationAmount) {
		final Random random = RandomRegistry.getRandom();
		final Polygon mutated = new Polygon(length());
		final int size = _data.length;

		for (int i = 0; i < size; i++) {
			float val = _data[i];
			if (random.nextFloat() < mutationRate) {
				val += random.nextFloat()*mutationAmount*2 - mutationAmount;
				if (val < 0f) {
					val = 0f;
				} else if (val > 1f) {
					val = 1f;
				}
			}
			mutated._data[i] = val;
		}

		return mutated;
	}

	/**
	 * Draw the Polygon to the buffer of the given size.
	 */
	public void draw(final Graphics2D g, final int width, final int height) {
		g.setColor(new Color(_data[0], _data[1], _data[2], _data[3]));

		final GeneralPath path = new GeneralPath();
		path.moveTo(_data[4]*width, _data[5]*height);
		for (int j = 1; j < _length; j++) {
			path.lineTo(_data[4 + j*2]*width, _data[5 + j*2]*height);
		}
		path.closePath();
		g.fill(path);
	}

	/**
	 * Creates a new random Polygon of the given length.
	 */
	public static Polygon newRandom(final int length, final Random random) {
		require.positive(length);
		final Polygon p = new Polygon(length);

		p._data[0] = random.nextFloat(); // r
		p._data[1] = random.nextFloat(); // g
		p._data[2] = random.nextFloat(); // b
		p._data[3] = max(0.2f, random.nextFloat()*random.nextFloat()); // a

		float px = random.nextFloat();
		float py = random.nextFloat();
		for (int k = 0; k < length; k++) {
			p._data[4 + 2*k] = px + (random.nextFloat() - 0.5f);
			p._data[5 + 2*k] = py + (random.nextFloat() - 0.5f);
		}
		return p;
	}

	public static Polygon newRandom(final int length) {
		return newRandom(length, RandomRegistry.getRandom());
	}

}
