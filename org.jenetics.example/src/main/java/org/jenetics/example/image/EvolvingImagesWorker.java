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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.example.image;

import static java.util.Objects.requireNonNull;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;

import org.jenetics.Genotype;
import org.jenetics.MeanAlterer;
import org.jenetics.Optimize;
import org.jenetics.TournamentSelector;
import org.jenetics.TruncationSelector;
import org.jenetics.engine.Codec;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.stat.MinMax;

/**
 * Performs the actual evolution.
 */
final class EvolvingImagesWorker {

	private final BufferedImage _image;
	private final BufferedImage _refImage;
	private final int[] _refImagePixels;
	private final ThreadLocal<BufferedImage> _workingImage;
	private final Engine<PolygonGene, Double> _engine;

	private volatile Thread _thread;

	private boolean _paused = false;
	private final Lock _pauseLock = new ReentrantLock();
	private final Condition _pauseCondition = _pauseLock.newCondition();

	/**
	 * Create an new worker instance with the given parameter and for the given
	 * image.
	 *
	 * @param param the GA engine parameter
	 * @param image the image to polygonize
	 */
	private EvolvingImagesWorker(
		final EngineParam param,
		final BufferedImage image
	) {
		_image = requireNonNull(image);

		_refImage = resizeImage(
			_image,
			param.getReferenceImageSize().width,
			param.getReferenceImageSize().height,
			BufferedImage.TYPE_INT_ARGB
		);

		_workingImage = ThreadLocal.withInitial(() -> new BufferedImage(
			_refImage.getWidth(),
			_refImage.getHeight(),
			BufferedImage.TYPE_INT_ARGB
		));

		_refImagePixels = _refImage.getData().getPixels(
			0, 0, _refImage.getWidth(), _refImage.getHeight(), (int[])null
		);

		final Codec<PolygonChromosome, PolygonGene> codec = Codec.of(
			Genotype.of(new PolygonChromosome(
				param.getPolygonCount(), param.getPolygonLength()
			)),
			gt -> (PolygonChromosome) gt.getChromosome()
		);

		_engine = Engine.builder(this::fitness, codec)
			.populationSize(param.getPopulationSize())
			.optimize(Optimize.MAXIMUM)
			.maximalPhenotypeAge(50)
			.survivorsSelector(new TruncationSelector<>())
			.offspringSelector(new TournamentSelector<>(param.getTournamentSize()))
			.alterers(
				new MeanAlterer<>(0.175),
				new PolygonMutator<>(param.getMutationRate(), param.getMutationMultitude()),
				new UniformCrossover<>(0.5))
			.build();
	}

	private static BufferedImage resizeImage(
		final BufferedImage image,
		final int width,
		final int height,
		final int type
	) {
		final BufferedImage resizedImage = new BufferedImage(width, height, type);
		final Graphics2D g = resizedImage.createGraphics();
		g.drawImage(image, 0, 0, width, height, null);
		g.dispose();
		return resizedImage;
	}

	BufferedImage getImage() {
		return _image;
	}

	/**
	 * Calculate the fitness function for a Polygon chromosome.
	 * <p>
	 * For this purpose, we first draw the polygons on the test buffer,  and
	 * then compare the resulting image pixel by pixel with the  reference image.
	 */
	private double fitness(final PolygonChromosome chromosome) {
		final BufferedImage img = _workingImage.get();
		final Graphics2D g2 = img.createGraphics();
		final int width = img.getWidth();
		final int height = img.getHeight();

		chromosome.draw(g2, width, height);
		g2.dispose();

		final int[] refPixels = _refImagePixels;
		final int[] testPixels = img.getData()
			.getPixels(0, 0, width, height, (int[])null);

		int diff = 0;
		int p = width*height*4 - 1; // 4 channels: rgba
		int idx = 0;
		do {
			if (idx++%4 != 0) { // ignore the alpha channel for fitness
				int dp = testPixels[p] - refPixels[p];
				diff += (dp < 0) ? -dp : dp;
			}
		} while (--p > 0);

		return 1.0 - diff/(width*height*3.0*256);
	}

	/**
	 * Starts the evolution worker with the given evolution result callback. The
	 * callback may be null.
	 *
	 * @param callback the {@code EvolutionResult} callback. The first parameter
	 *        contains the current result and the second the best.
	 */
	public void start(
		final BiConsumer<
			EvolutionResult<PolygonGene, Double>,
			EvolutionResult<PolygonGene, Double>> callback
	) {
		final Thread thread = new Thread(() -> {
			final MinMax<EvolutionResult<PolygonGene, Double>> best = MinMax.of();

			_engine.stream()
				.limit(result -> !Thread.currentThread().isInterrupted())
				.peek(best)
				.forEach(r -> {
					waiting();
					if (callback != null) {
						callback.accept(r, best.getMax());
					}
				});
		});
		thread.start();
		_thread = thread;
	}

	private void waiting() {
		_pauseLock.lock();
		try {
			while (_paused) {
				try {
					_pauseCondition.await();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		} finally {
			_pauseLock.unlock();
		}
	}

	/**
	 * Stops the current evolution, if running.
	 */
	public void stop() {
		resume();
		final Thread thread = _thread;
		if (thread != null) {
			thread.interrupt();
			try {
				thread.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} finally {
				_thread = null;
			}
		}
	}

	/**
	 * Waits for the evolution thread.
	 *
	 * @throws InterruptedException if the calling thread has been interrupted.
	 */
	public void join() throws InterruptedException {
		final Thread thread = _thread;
		if (thread != null) {
			thread.join();
		}
	}

	/**
	 * Pauses the current evolution run.
	 */
	public void pause() {
		_pauseLock.lock();
		try {
			_paused =  true;
		} finally {
			_pauseLock.unlock();
		}
	}

	/**
	 * Resumes the current evolution run.
	 */
	public void resume() {
		_pauseLock.lock();
		try {
			_paused = false;
			_pauseCondition.signalAll();
		} finally {
			_pauseLock.unlock();
		}
	}

	/**
	 * Return an new worker instance with the given parameter and for the given
	 * image.
	 *
	 * @param param the GA engine parameter
	 * @param image the image to polygonize
	 * @return a new evolving image instance
	 */
	public static EvolvingImagesWorker of(
		final EngineParam param,
		final BufferedImage image
	) {
		return new EvolvingImagesWorker(param, image);
	}

}
