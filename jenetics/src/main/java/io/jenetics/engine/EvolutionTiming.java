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
package io.jenetics.engine;

import java.time.InstantSource;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.0
 * @since 5.0
 */
final class EvolutionTiming {

	final Timing offspringSelection;
	final Timing survivorsSelection;
	final Timing offspringAlter;
	final Timing offspringFilter;
	final Timing survivorFilter;
	final Timing evaluation;
	final Timing evolve;

	EvolutionTiming(final InstantSource clock) {
		offspringSelection = Timing.of(clock);
		survivorsSelection = Timing.of(clock);
		offspringAlter = Timing.of(clock);
		offspringFilter = Timing.of(clock);
		survivorFilter = Timing.of(clock);
		evaluation = Timing.of(clock);
		evolve = Timing.of(clock);
	}

	EvolutionDurations toDurations() {
		return new EvolutionDurations(
			offspringSelection.duration(),
			survivorsSelection.duration(),
			offspringAlter.duration(),
			offspringFilter.duration(),
			survivorFilter.duration(),
			evaluation.duration(),
			evolve.duration()
		);
	}

}
