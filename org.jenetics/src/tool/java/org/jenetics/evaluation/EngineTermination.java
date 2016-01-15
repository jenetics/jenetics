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

import java.io.File;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jenetics.Gene;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.engine.Problem;
import org.jenetics.trial.TrialMeter;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class EngineTermination<
	T,
	G extends Gene<?, G>,
	C extends Comparable<? super C>,
	P
> {

	private final Engine<G, C> _engine;
	private final Problem<T, G, C> _problem;
	private final Function<P, Predicate<? super EvolutionResult<G, Double>>> _termination;
	private final TrialMeter<P> _trialMeter;
	private final File _measurementFile;

	public EngineTermination(
		final Engine<G, C> engine,
		final Problem<T, G, C> problem,
		final Function<P, Predicate<? super EvolutionResult<G, Double>>> termination,
		final TrialMeter<P> trialMeter,
		final File measurementFile
	) {
		_engine = requireNonNull(engine);
		_problem = requireNonNull(problem);
		_termination = requireNonNull(termination);
		_trialMeter = requireNonNull(trialMeter);
		_measurementFile = requireNonNull(measurementFile);
	}


	public <T> void samples() {

	}

}
