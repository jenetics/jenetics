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

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static java.lang.Math.max;
import static java.lang.Math.round;
import static java.lang.String.format;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import javax.imageio.ImageIO;

import org.jenetics.Phenotype;

/**
 * Command line version of the Evolving images example.
 */
final class EvolvingImagesCmd {

	private static final String PARAM_KEY = "--params";
	private static final String IMAGE_KEY = "--image";
	private static final String OUTPUT_DIR_KEY = "--output-dir";
	private static final String GENERATION_COUNT_KEY = "--generation-count";
	private static final String GENERATION_IMAGE_GAP_KEY = "--image-gap";

	private static final String USAGE = "EvolvingImages evolve \n" +
		"    [--params <engine param properties file>]\n" +
		"    [--image <image file>]\n" +
		"    [--output-dir <image output directory>]\n" +
		"    [--generation-count <generation count>]\n" +
		"    [--image-gap <generation-gap between images>]";

	private static final String IMAGE_PATTERN = "image-%06d[%1.4f].png";

	private EngineParam _param;
	private BufferedImage _image;
	private File _outputDir;
	private int _generationCount;
	private int _generationImageGap;

	public EvolvingImagesCmd(final String[] args) {
		if (args.length >= 1 && "evolve".equalsIgnoreCase(args[0])) {
			final Map<String, String> params = toMap(args);
			if (params.containsKey("--help")) {
				System.out.println(USAGE);
				System.exit(0);
			}

			_param = Optional.ofNullable(params.get(PARAM_KEY))
				.map(this::readEngineParam)
				.orElse(EngineParam.DEFAULT);

			_image = Optional.ofNullable(params.get(IMAGE_KEY))
				.map(this::readImage)
				.orElseGet(this::defaultImage);

			_outputDir = Optional.ofNullable(params.get(OUTPUT_DIR_KEY))
				.map(File::new)
				.orElse(new File(System.getProperty("user.dir"), "EvolvingImages"));

			_generationCount = Optional.ofNullable(params.get(GENERATION_COUNT_KEY))
				.map(Integer::parseInt)
				.orElse(10_000);

			_generationImageGap = Optional.ofNullable(params.get(GENERATION_IMAGE_GAP_KEY))
				.map(Integer::parseInt)
				.orElse(100);
		}
	}

	private static Map<String, String> toMap(final String[] args) {
		final Map<String, String> props = new HashMap<>();
		for (int i = 1; i < args.length; i += 2) {
			props.put(args[i], i + 1 < args.length ? args[i + 1] : null);
		}

		return props;
	}

	private EngineParam readEngineParam(final String name) {
		try (InputStream in = new FileInputStream(name)) {
			final Properties props = new Properties();
			props.load(in);

			return EngineParam.load(props);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private BufferedImage readImage(final String name) {
		try {
			return ImageIO.read(new File(name));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private BufferedImage defaultImage() {
		try (InputStream in = getClass().getClassLoader()
								.getResourceAsStream("monalisa.png"))
		{
			return ImageIO.read(in);
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}

	public boolean run() {
		if (_param != null) {
			if (!_outputDir.isDirectory()) {
				if (!_outputDir.mkdirs()) {
					throw new IllegalArgumentException(
						"Can't create output directory " + _outputDir
					);
				}
			}

			System.out.println("Starting evolution:");
			System.out.println("    * Output dir:           " + _outputDir);
			System.out.println("    * Generation count:     " + _generationCount);
			System.out.println("    * Generation image gap: " + _generationImageGap);

			evolve(_param, _image, _outputDir, _generationCount, _generationImageGap);
		}

		return _param != null;
	}

	private static void evolve(
		final EngineParam params,
		final BufferedImage image,
		final File outputDir,
		final long generations,
		final int generationGap
	) {
		System.out.println("Starting evolution.");
		final EvolvingImagesWorker worker = EvolvingImagesWorker.of(params, image);

		final AtomicReference<Phenotype<PolygonGene, Double>> latest =
			new AtomicReference<>();

		worker.start((current, best) -> {
			final long generation = current.getGeneration();

			if (generation%generationGap == 0 || generation == 1) {
				final File file = new File(
					outputDir,
					format(IMAGE_PATTERN, generation, best.getBestFitness())
				);

				final Phenotype<PolygonGene, Double> pt = best.getBestPhenotype();
				if (latest.get() == null || latest.get().compareTo(pt) < 0) {
					System.out.println(format("Writing '%s'.", file));

					latest.set(pt);
					final PolygonChromosome ch =
						(PolygonChromosome)pt.getGenotype().getChromosome();

					writeImage(file, ch, image.getWidth(), image.getHeight());
				} else {
					System.out.println(format(
						"No improvement - %06d[%1.4f]",
						generation, pt.getFitness()
					));
				}
			}

			if (generation >= generations) {
				worker.stop();
			}
		});

		try {
			worker.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	static void writeImage(
		final File file,
		final PolygonChromosome chromosome,
		final int width,
		final int height
	) {
		final double MIN_SIZE = 500;
		final double scale = max(max(MIN_SIZE/width, MIN_SIZE/height), 1.0);
		final int w = (int)round(scale*width);
		final int h = (int)round(scale*height);

		try {
			final BufferedImage image = new BufferedImage(w, h, TYPE_INT_ARGB);
			final Graphics2D graphics = image.createGraphics();
			chromosome.draw(graphics, w, h);

			ImageIO.write(image, "png", file);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

}
