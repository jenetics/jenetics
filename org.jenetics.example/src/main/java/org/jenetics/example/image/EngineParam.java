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

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.prefs.Preferences;

/**
 * Collects the configurable GA engine parameters.
 */
final class EngineParam {

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

	public static final Dimension MIN_REF_IMAGE_SIZE = new Dimension(2, 2);
	public static final Dimension MAX_REF_IMAGE_SIZE =
		new Dimension(Short.MAX_VALUE, Short.MAX_VALUE);

	public static final EngineParam DEFAULT;

	// Load the default properties from the resource file.
	static {
		final Properties props = new Properties();
		try (final InputStream in = EngineParam.class.getResourceAsStream(
						"/org/jenetics/example/image/engine.properties"))
		{
			props.load(in);
		} catch (IOException e) {
			throw new AssertionError(e);
		}

		DEFAULT = EngineParam.load(props);
	}

	private static final String POPULATION_SIZE_KEY = "population_size";
	private static final String TOURNAMENT_SIZE_KEY = "tournament_size";
	private static final String MUTATION_RATE_KEY = "mutation_rate";
	private static final String MUTATION_MULTITUDE_KEY = "mutation_multitude";
	private static final String POLYGON_LENGTH_KEY = "polygon_length";
	private static final String POLYGON_COUNT_KEY = "polygon_count";
	private static final String REFERENCE_IMAGE_WIDTH_KEY = "reference_image_width";
	private static final String REFERENCE_IMAGE_HEIGHT_KEY = "reference_image_height";

	private final int _populationSize;
	private final int _tournamentSize;
	private final float _mutationRate;
	private final float _mutationMultitude;

	private final int _polygonLength;
	private final int _polygonCount;
	private final Dimension _referenceImageSize;

	private EngineParam(
		final int populationSize,
		final int tournamentSize,
		final float mutationRate,
		final float mutationMultitude,
		final int polygonLength,
		final int polygonCount,
		final Dimension referenceImageSize
	) {
		_populationSize = populationSize;
		_tournamentSize = tournamentSize;
		_mutationRate = mutationRate;
		_mutationMultitude = mutationMultitude;
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

	public float getMutationRate() {
		return _mutationRate;
	}

	public float getMutationMultitude() {
		return _mutationMultitude;
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
			"Mutation multitude: " + _mutationMultitude + "\n" +
			"Polygon length: " + _polygonLength + "\n" +
			"Polygon count: " + _polygonCount + "\n" +
			"Reference image size: " + _referenceImageSize.width +
				"x" + _referenceImageSize.height;
	}

	public void store(final Preferences prefs) {
		prefs.putInt(POPULATION_SIZE_KEY, _populationSize);
		prefs.putInt(TOURNAMENT_SIZE_KEY, _tournamentSize);
		prefs.putFloat(MUTATION_RATE_KEY, _mutationRate);
		prefs.putFloat(MUTATION_MULTITUDE_KEY, _mutationMultitude);
		prefs.putInt(POLYGON_LENGTH_KEY, _polygonLength);
		prefs.putInt(POLYGON_COUNT_KEY, _polygonCount);
		prefs.putInt(REFERENCE_IMAGE_WIDTH_KEY, _referenceImageSize.width);
		prefs.putInt(REFERENCE_IMAGE_HEIGHT_KEY, _referenceImageSize.height);
	}

	public static EngineParam load(final Preferences prefs) {
		return of(
			prefs.getInt(POPULATION_SIZE_KEY, DEFAULT._populationSize),
			prefs.getInt(TOURNAMENT_SIZE_KEY, DEFAULT._tournamentSize),
			prefs.getFloat(MUTATION_RATE_KEY, DEFAULT._mutationRate),
			prefs.getFloat(MUTATION_MULTITUDE_KEY, DEFAULT._mutationMultitude),
			prefs.getInt(POLYGON_LENGTH_KEY, DEFAULT._polygonLength),
			prefs.getInt(POLYGON_COUNT_KEY, DEFAULT._polygonCount),
			new Dimension(
				prefs.getInt(REFERENCE_IMAGE_WIDTH_KEY, DEFAULT._referenceImageSize.width),
				prefs.getInt(REFERENCE_IMAGE_HEIGHT_KEY, DEFAULT._referenceImageSize.height)
			)
		);
	}

	public void store(final Properties props) {
		props.put(POPULATION_SIZE_KEY, _populationSize);
		props.put(TOURNAMENT_SIZE_KEY, _tournamentSize);
		props.put(MUTATION_RATE_KEY, _mutationRate);
		props.put(MUTATION_MULTITUDE_KEY, _mutationMultitude);
		props.put(POLYGON_LENGTH_KEY, _polygonLength);
		props.put(POLYGON_COUNT_KEY, _polygonCount);
		props.put(REFERENCE_IMAGE_WIDTH_KEY, _referenceImageSize.width);
		props.put(REFERENCE_IMAGE_HEIGHT_KEY, _referenceImageSize.height);
	}

	public static EngineParam load(final Properties props) {
		return of(
			parseInt(props.getProperty(POPULATION_SIZE_KEY)),
			parseInt(props.getProperty(TOURNAMENT_SIZE_KEY)),
			parseFloat(props.getProperty(MUTATION_RATE_KEY)),
			parseFloat(props.getProperty(MUTATION_MULTITUDE_KEY)),
			parseInt(props.getProperty(POLYGON_LENGTH_KEY)),
			parseInt(props.getProperty(POLYGON_COUNT_KEY)),
			new Dimension(
				parseInt(props.getProperty(REFERENCE_IMAGE_WIDTH_KEY)),
				parseInt(props.getProperty(REFERENCE_IMAGE_HEIGHT_KEY))
			)
		);
	}

	public static EngineParam of(
		final int populationSize,
		final int tournamentSize,
		final float mutationRate,
		final float mutationChange,
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
