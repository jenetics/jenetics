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
package org.jenetics.diagram;

import static java.util.Objects.requireNonNull;

import java.io.File;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
final class GenerationParam {

	private final int _samples;
	private final int _generations;
	private final File _outputFile;

	private GenerationParam(
		final int samples,
		final int generations,
		final File outputFile
	) {
		_samples = samples;
		_generations = generations;
		_outputFile = requireNonNull(outputFile);
	}

	public int getSamples() {
		return _samples;
	}

	public int getGenerations() {
		return _generations;
	}

	public File getOutputFile() {
		return _outputFile;
	}

	public static GenerationParam of(
		final int samples,
		final int generations,
		final File outputFile
	) {
		return new GenerationParam(samples, generations, outputFile);
	}

	public static GenerationParam of(
		final String[] args,
		final int samples,
		final int generations,
		final File outputFile
	) {
		int s = samples;
		int g = generations;
		File f = outputFile;

		for (int i = 0; i < args.length; i += 2) {
			switch (args[i]) {
				case "-s": s = Integer.parseInt(args[i + 1]); break;
				case "-g": g = Integer.parseInt(args[i + 1]); break;
				case "-f": f = new File(args[i + 1]); break;
				default: break;
			}
		}

		return of(s, g, f);
	}

}
