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

import java.awt.Dimension;
import java.util.prefs.Preferences;

final class EngineParam {

	public static final int MIN_POPULATION_SIZE = 3;
	public static final int MAX_POPULATION_SIZE = Integer.MAX_VALUE;

	public static final int MIN_TOURNAMENT_SIZE = 2;
	public static final int MAX_TOURNAMENT_SIZE = Integer.MAX_VALUE;

	public static final double MIN_MUTATION_RATE = 0.0;
	public static final double MAX_MUTATION_RATE = 1.0;

	public static final double MIN_MUTATION_CHANGE = 0.0;
	public static final double MAX_MUTATION_CHANGE = 1.0;

	public static final int MIN_POLYGON_LENGTH = 3;
	public static final int MAX_POLYGON_LENGTH = Integer.MAX_VALUE;

	public static final int MIN_POLYGON_COUNT = 2;
	public static final int MAX_POLYGON_COUNT = Integer.MAX_VALUE;

	public static final Dimension MIN_REF_IMAGE_SIZE = new Dimension(2, 2);
	public static final Dimension MAX_REF_IMAGE_SIZE =
		new Dimension(Short.MAX_VALUE, Short.MAX_VALUE);

	public static EngineParam DEFAULT = EngineParam.of(
		40, 3, 0.02, 0.1, 6, 100, new Dimension(50, 50)
	);

	private final int _populationSize;
	private final int _tournamentSize;
	private final double _mutationRate;
	private final double _mutationChange;

	private final int _polygonLength;
	private final int _polygonCount;
	private final Dimension _referenceImageSize;

	private EngineParam(
		final int populationSize,
		final int tournamentSize,
		final double mutationRate,
		final double mutationChange,
		final int polygonLength,
		final int polygonCount,
		final Dimension referenceImageSize
	) {
		_populationSize = populationSize;
		_tournamentSize = tournamentSize;
		_mutationRate = mutationRate;
		_mutationChange = mutationChange;
		_polygonLength = polygonLength;
		_polygonCount = polygonCount;
		_referenceImageSize = (Dimension)referenceImageSize.clone();
	}

	public int getPopulationSize() {
		return _populationSize;
	}

	public int getTournamentSize() {
		return _tournamentSize;
	}

	public double getMutationRate() {
		return _mutationRate;
	}

	public double getMutationChange() {
		return _mutationChange;
	}

	public int getPolygonLength() {
		return _polygonLength;
	}

	public int getPolygonCount() {
		return _polygonCount;
	}

	public Dimension getReferenceImageSize() {
		return (Dimension)_referenceImageSize.clone();
	}

	@Override
	public String toString() {
		return "Population size: " + _populationSize + "\n" +
			"Tournament size: " + _tournamentSize + "\n" +
			"Mutation rate: " + _mutationRate + "\n" +
			"Mutation change: " + _mutationChange + "\n" +
			"Polygon length: " + _polygonLength + "\n" +
			"Polygon count: " + _polygonCount + "\n" +
			"Reference image size: " + _referenceImageSize.width +
				"x" + _referenceImageSize.height;
	}

	public void store(final Preferences prefs) {
		prefs.putInt("population_size", _populationSize);
		prefs.putInt("tournament_size", _tournamentSize);
		prefs.putDouble("mutation_rate", _mutationRate);
		prefs.putDouble("mutation_change", _mutationChange);
		prefs.putInt("polygon_length", _polygonLength);
		prefs.putInt("polygon_count", _polygonCount);
		prefs.putInt("reference_image_width", _referenceImageSize.width);
		prefs.putInt("reference_image_height", _referenceImageSize.height);
	}

	public static EngineParam load(final Preferences prefs) {
		return of(
			prefs.getInt("population_size", DEFAULT._populationSize),
			prefs.getInt("tournament_size", DEFAULT._tournamentSize),
			prefs.getDouble("mutation_rate", DEFAULT._mutationRate),
			prefs.getDouble("mutation_change", DEFAULT._mutationChange),
			prefs.getInt("polygon_length", DEFAULT._polygonLength),
			prefs.getInt("polygon_count", DEFAULT._polygonCount),
			new Dimension(
				prefs.getInt("reference_image_width", DEFAULT._referenceImageSize.width),
				prefs.getInt("reference_image_height", DEFAULT._referenceImageSize.height)
			)
		);
	}

	public static EngineParam of(
		final int populationSize,
		final int tournamentSize,
		final double mutationRate,
		final double mutationChange,
		final int polygonLength,
		final int polygonCount,
		final Dimension referenceImageSize
	) {
		return new EngineParam(
			populationSize,
			tournamentSize,
			mutationRate,
			mutationChange,
			polygonLength,
			polygonCount,
			referenceImageSize
		);
	}

}
