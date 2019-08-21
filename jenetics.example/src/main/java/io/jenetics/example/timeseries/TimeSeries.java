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
package io.jenetics.example.timeseries;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import io.jenetics.engine.Evolution;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStart;

import io.jenetics.prog.ProgramGene;
import io.jenetics.prog.regression.Sample;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class TimeSeries<T> implements Evolution<ProgramGene<T>, Double> {

	private Supplier<Optional<List<Sample<T>>>> _samples;

	@Override
	public EvolutionResult<ProgramGene<T>, Double>
	evolve(final EvolutionStart<ProgramGene<T>, Double> start) {
		return null;
	}

}
