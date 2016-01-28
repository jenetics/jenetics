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
package org.jenetics.evaluation;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;

import org.jenetics.trial.IO;
import org.jenetics.trial.Params;
import org.jenetics.trial.SampleSummary;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class Diagram {

	public static enum Template {
		EXECUTION_TIME("execution_time_termination.gp"),
		FITNESS_THRESHOLD("fitness_threshold_termination.gp"),
		FIXED_GENERATION("fixed_generation_termination.gp"),
		STEADY_FITNESS("steady_fitness_termination.gp");

		private static final String BASE = "org/jenetics/evaluation";

		private final String _path;

		private Template(final String path) {
			_path = requireNonNull(path);
		}

		public String text() {
			final String rsc = BASE + "/" + _path;
			try (InputStream stream = getClass().getResourceAsStream(rsc)) {
				return IO.toText(stream);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}

	public static <T> void create(
		final Template template,
		final Params<T> params,
		final SampleSummary generation,
		final SampleSummary fitness
	) {

	}

}
