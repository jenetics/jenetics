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

import static java.util.Objects.requireNonNull;

import java.util.SplittableRandom;

import org.graalvm.webimage.api.JS;

import io.jenetics.Genotype;
import io.jenetics.MeanAlterer;
import io.jenetics.Optimize;
import io.jenetics.TournamentSelector;
import io.jenetics.TruncationSelector;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStart;
import io.jenetics.stat.MinMax;
import io.jenetics.util.RandomRegistry;

/**
 * Step-wise evolution application facade exported to JavaScript.
 * <p>
 * The facade keeps the evolution state between calls, allowing a browser
 * animation loop to advance the engine a small number of generations at a
 * time. The engine and fitness evaluation are deliberately synchronous for
 * the WebAssembly runtime.
 */
public final class EvolvingImagesApp {

	private Engine<PolygonGene, Double> _engine;
	private EvolutionStart<PolygonGene, Double> _start;
	private EvolutionResult<PolygonGene, Double> _current;
	private MinMax<EvolutionResult<PolygonGene, Double>> _best;

	private int _polygonCount;
	private int _polygonLength;
	private int _referenceWidth;
	private int _referenceHeight;

	public EvolvingImagesApp() {
	}

	/**
	 * Initializes or re-initializes the evolution engine.
	 * <p>
	 * The reference pixels are expected in RGBA order and are copied by the
	 * fitness evaluator. Calling this method resets the generation and best
	 * result state.
	 *
	 * @param populationSize the number of phenotypes in each population
	 * @param tournamentSize the number of candidates used by tournament selection
	 * @param mutationRate the mutation probability for each polygon component
	 * @param mutationMultitude the maximum mutation amount
	 * @param polygonLength the number of vertices in each polygon
	 * @param polygonCount the number of polygons in each chromosome
	 * @param refWidth reference image width in pixels
	 * @param refHeight reference image height in pixels
	 * @param refRgba reference pixels in flat RGBA order
	 */
	@JS.Coerce
	public void init(
		final int populationSize,
		final int tournamentSize,
		final double mutationRate,
		final double mutationMultitude,
		final int polygonLength,
		final int polygonCount,
		final int refWidth,
		final int refHeight,
		final int[] refRgba
	) {
		final var param = new EngineParam(
			populationSize,
			tournamentSize,
			(float) mutationRate,
			(float) mutationMultitude,
			polygonLength,
			polygonCount,
			refWidth,
			refHeight
		);
		param.validate();
		requireNonNull(refRgba, "refRgba must not be null");

		RandomRegistry.random(new SplittableRandom());

		final var fitness = new ImageFitness(refWidth, refHeight, refRgba);

		final Codec<PolygonChromosome, PolygonGene> codec = Codec.of(
			Genotype.of(new PolygonChromosome(polygonCount, polygonLength)),
			gt -> (PolygonChromosome) gt.chromosome()
		);

		_engine = Engine.builder(fitness, codec)
			.populationSize(populationSize)
			.optimize(Optimize.MAXIMUM)
			.maximalPhenotypeAge(50)
			.survivorsSelector(new TruncationSelector<>())
			.offspringSelector(new TournamentSelector<>(tournamentSize))
			.alterers(
				new MeanAlterer<>(0.175),
				new PolygonMutator<>((float) mutationRate, (float) mutationMultitude),
				new UniformCrossover<>(0.5)
			)
			.executor(Runnable::run)
			.fitnessExecutor(batch -> batch.forEach(Runnable::run))
			.build();

		_start = EvolutionStart.empty();
		_current = null;
		_best = MinMax.of();

		_polygonCount = polygonCount;
		_polygonLength = polygonLength;
		_referenceWidth = refWidth;
		_referenceHeight = refHeight;
	}

	/**
	 * Initializes the engine from a parameter record.
	 *
	 * @param param the engine and reference-image parameters
	 * @param refRgba reference pixels in flat RGBA order
	 */
	public void init(final EngineParam param, final int[] refRgba) {
		requireNonNull(param, "param must not be null");
		init(
			param.populationSize(),
			param.tournamentSize(),
			param.mutationRate(),
			param.mutationMultitude(),
			param.polygonLength(),
			param.polygonCount(),
			param.referenceWidth(),
			param.referenceHeight(),
			refRgba
		);
	}

	/**
	 * Evolves the engine by the requested number of generations.
	 *
	 * @param generations number of generations to calculate; non-positive
	 *        values leave the state unchanged
	 * @return the current generation number
	 * @throws IllegalStateException if the engine has not been initialized
	 */
	@JS.Coerce
	public int evolve(final int generations) {
		if (_engine == null) {
			throw new IllegalStateException("Engine not initialized. Call init() first.");
		}
		if (generations <= 0) {
			return generation();
		}

		for (int i = 0; i < generations; ++i) {
			_current = _engine.evolve(_start);
			_best.accept(_current);
			_start = _current.next();
		}

		return generation();
	}

	/**
	 * Returns the current generation, or zero before the first evolution step.
	 *
	 * @return the current generation number
	 */
	@JS.Coerce
	public int generation() {
		return _current != null ? (int) _current.generation() : 0;
	}

	/**
	 * Returns the generation containing the best result seen so far.
	 *
	 * @return the best generation number, or zero before evolution
	 */
	@JS.Coerce
	public int bestGeneration() {
		return _best != null && _best.max() != null
			? (int) _best.max().generation()
			: 0;
	}

	/**
	 * Returns the best fitness in the current population.
	 *
	 * @return the current population fitness, or {@code 0.0} before evolution
	 */
	@JS.Coerce
	public double currentFitness() {
		return _current != null && _current.bestFitness() != null
			? _current.bestFitness()
			: 0.0;
	}

	/**
	 * Returns the best fitness observed across all completed generations.
	 *
	 * @return the best observed fitness, or {@code 0.0} before evolution
	 */
	@JS.Coerce
	public double bestFitness() {
		return _best != null && _best.max() != null && _best.max().bestFitness() != null
			? _best.max().bestFitness()
			: 0.0;
	}

	/**
	 * Returns the average age of phenotypes in the current population.
	 *
	 * @return the current average population age
	 */
	@JS.Coerce
	public double currentPopulationAge() {
		return _current != null
			? _current.population().stream()
				.mapToLong(p -> p.age(_current.generation()))
				.average()
				.orElse(0.0)
			: 0.0;
	}

	/**
	 * Returns the average age of phenotypes in the best population.
	*
	 * @return the best average population age
	 */
	@JS.Coerce
	public double bestPopulationAge() {
		return _best != null && _best.max() != null
			? _best.max().population().stream()
				.mapToLong(p -> p.age(_best.max().generation()))
				.average()
				.orElse(0.0)
			: 0.0;
	}

	/**
	 * Returns the evaluation duration of the current population in milliseconds.
	 *
	 * @return the current evaluation duration, or {@code 0.0} before evolution
	 */
	@JS.Coerce
	public double currentEvaluationMillis() {
		return _current != null
			? _current.durations().evaluationDuration().toNanos() / 1_000_000.0
			: 0.0;
	}

	/**
	 * Returns the evaluation duration of the best population in milliseconds.
	 *
	 * @return the best evaluation duration, or {@code 0.0} before evolution
	 */
	@JS.Coerce
	public double bestEvaluationMillis() {
		return _best != null && _best.max() != null
			? _best.max().durations().evaluationDuration().toNanos() / 1_000_000.0
			: 0.0;
	}

	/**
	 * Returns the number of polygons in each chromosome.
	 *
	 * @return the polygon count
	 */
	@JS.Coerce
	public int polygonCount() {
		return _polygonCount;
	}

	/**
	 * Returns the number of vertices in each polygon.
	 *
	 * @return the polygon vertex count
	 */
	@JS.Coerce
	public int polygonLength() {
		return _polygonLength;
	}

	/**
	 * Returns the reference image width used by the fitness function.
	 *
	 * @return the reference width in pixels
	 */
	@JS.Coerce
	public int referenceWidth() {
		return _referenceWidth;
	}

	/**
	 * Returns the reference image height used by the fitness function.
	 *
	 * @return the reference height in pixels
	 */
	@JS.Coerce
	public int referenceHeight() {
		return _referenceHeight;
	}

	/**
	 * Returns the best chromosome as packed polygon data.
	 *
	 * @return flat data containing one RGBA polygon record after another, or an
	 *         empty array before the first generation
	 */
	@JS.Coerce
	public double[] bestPolygons() {
		if (_best != null && _best.max() != null && _best.max().bestPhenotype() != null) {
			final var gt = _best.max().bestPhenotype().genotype();
			final var chromosome = (PolygonChromosome) gt.chromosome();
			return chromosome.toArray();
		}
		return new double[0];
	}

	/**
	 * Renders the best chromosome to an ARGB pixel buffer.
	 *
	 * @param width output width in pixels
	 * @param height output height in pixels
	 * @return row-major ARGB pixels, or an empty array for invalid dimensions
	 */
	@JS.Coerce
	public int[] renderBest(final int width, final int height) {
		if (width <= 0 || height <= 0) {
			return new int[0];
		}
		final var rasterizer = new PolygonRasterizer(width, height);
		if (_best != null && _best.max() != null && _best.max().bestPhenotype() != null) {
			final var gt = _best.max().bestPhenotype().genotype();
			final var chromosome = (PolygonChromosome) gt.chromosome();
			chromosome.render(rasterizer);
		} else {
			rasterizer.clear(0xFFFFFFFF);
		}
		return rasterizer.pixels();
	}

	/**
	 * Returns whether the evolution engine has been initialized.
	 *
	 * @return {@code "initialized"} after {@link #init}, otherwise {@code "ready"}
	 */
	@JS.Coerce
	public String status() {
		return _engine != null ? "initialized" : "ready";
	}
}
