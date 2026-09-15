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

import static java.lang.String.format;

/**
 * Collects the configurable GA engine parameters.
 *
 * @param populationSize number of phenotypes in each population
 * @param tournamentSize number of candidates considered by tournament selection
 * @param mutationRate probability of mutating each polygon component
 * @param mutationMultitude maximum absolute amount added to a mutated component
 * @param polygonLength number of vertices in each polygon
 * @param polygonCount number of polygons in each chromosome
 * @param referenceWidth width of the down-scaled reference image used for fitness evaluation
 * @param referenceHeight height of the down-scaled reference image used for fitness evaluation
 */
public record EngineParam(
	int populationSize,
	int tournamentSize,
	float mutationRate,
	float mutationMultitude,
	int polygonLength,
	int polygonCount,
	int referenceWidth,
	int referenceHeight
) {
	public static final int MIN_POPULATION_SIZE = 3;
	public static final int MAX_POPULATION_SIZE = Integer.MAX_VALUE;

	public static final int MIN_TOURNAMENT_SIZE = 2;
	public static final int MAX_TOURNAMENT_SIZE = Integer.MAX_VALUE;

	public static final float MIN_MUTATION_RATE = 0.0F;
	public static final float MAX_MUTATION_RATE = 1.0F;

	public static final float MIN_MUTATION_CHANGE = 0.0F;
	public static final float MAX_MUTATION_CHANGE = 1.0F;

	public static final int MIN_POLYGON_LENGTH = 3;
	public static final int MAX_POLYGON_LENGTH = Integer.MAX_VALUE;

	public static final int MIN_POLYGON_COUNT = 2;
	public static final int MAX_POLYGON_COUNT = Integer.MAX_VALUE;

	public static final int MIN_REF_IMAGE_WIDTH = 2;
	public static final int MAX_REF_IMAGE_WIDTH = Short.MAX_VALUE;

	public static final int MIN_REF_IMAGE_HEIGHT = 2;
	public static final int MAX_REF_IMAGE_HEIGHT = Short.MAX_VALUE;

	public static final EngineParam DEFAULT = new EngineParam(
		50,
		3,
		0.025F,
		0.15F,
		4,
		250,
		60,
		60
	);

	/**
	 * Validates all parameters against the supported bounds.
	 *
	 * @throws IllegalArgumentException if a parameter is outside its bounds
	 */
	public void validate() {
		check("Population size", populationSize, MIN_POPULATION_SIZE, MAX_POPULATION_SIZE);
		check("Tournament size", tournamentSize, MIN_TOURNAMENT_SIZE, MAX_TOURNAMENT_SIZE);
		check("Mutation rate", mutationRate, MIN_MUTATION_RATE, MAX_MUTATION_RATE);
		check("Mutation multitude", mutationMultitude, MIN_MUTATION_CHANGE, MAX_MUTATION_CHANGE);
		check("Polygon length", polygonLength, MIN_POLYGON_LENGTH, MAX_POLYGON_LENGTH);
		check("Polygon count", polygonCount, MIN_POLYGON_COUNT, MAX_POLYGON_COUNT);
		check("Reference width", referenceWidth, MIN_REF_IMAGE_WIDTH, MAX_REF_IMAGE_WIDTH);
		check("Reference height", referenceHeight, MIN_REF_IMAGE_HEIGHT, MAX_REF_IMAGE_HEIGHT);
	}

	private static void check(final String name, final int val, final int min, final int max) {
		if (val < min || val > max) {
			throw new IllegalArgumentException(format(
				"%s must be between %d and %d: %d", name, min, max, val
			));
		}
	}

	private static void check(final String name, final float val, final float min, final float max) {
		if (val < min || val > max || Float.isNaN(val)) {
			throw new IllegalArgumentException(format(
				"%s must be between %f and %f: %f", name, min, max, val
			));
		}
	}
}
