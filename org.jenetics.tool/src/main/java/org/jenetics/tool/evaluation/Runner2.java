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
package org.jenetics.tool.evaluation;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.jenetics.internal.util.Args;

import org.jenetics.Gene;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.tool.trial.TrialMeter;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.5
 * @since 3.5
 */
public class Runner2<
	P,
	G extends Gene<?, G>,
	N extends Number &  Comparable<? super N>
>
	extends AbstractRunner<P>
{

	private final Engine<G, N> _engine1;
	private final Function<? super P, Predicate<? super EvolutionResult<G, N>>> _terminator1;
	private final Engine<G, N> _engine2;
	private final Function<? super P, Predicate<? super EvolutionResult<G, N>>> _terminator2;

	public Runner2(
		final Engine<G, N> engine1,
		final Function<? super P, Predicate<? super EvolutionResult<G, N>>> terminator1,
		final Engine<G, N> engine2,
		final Function<? super P, Predicate<? super EvolutionResult<G, N>>> terminator2,
		final Supplier<TrialMeter<P>> trialMeter,
		final int sampleCount,
		final Path resultPath
	) {
		super(trialMeter, sampleCount, resultPath);
		_engine1 = requireNonNull(engine1);
		_terminator1 = requireNonNull(terminator1);
		_engine2 = requireNonNull(engine2);
		_terminator2 = requireNonNull(terminator2);
	}

	protected double[] fitness(final P param) {
		final Predicate<? super EvolutionResult<G, N>> terminator1 =
			_terminator1.apply(param);

		final long start1 = System.currentTimeMillis();
		final EvolutionResult<G, N> result1 = _engine1.stream()
			.limit(terminator1)
			.collect(EvolutionResult.toBestEvolutionResult());
		final long end1 = System.currentTimeMillis();

		final Predicate<? super EvolutionResult<G, N>> terminator2 =
			_terminator2.apply(param);

		final long start2 = System.currentTimeMillis();
		final EvolutionResult<G, N> result2 = _engine2.stream()
			.limit(terminator2)
			.collect(EvolutionResult.toBestEvolutionResult());
		final long end2 = System.currentTimeMillis();

		return new double[] {
			result1.getTotalGenerations(),
			result1.getBestFitness() != null
				? result1.getBestFitness().doubleValue()
				: Double.NEGATIVE_INFINITY,
			end1 - start1,

			result2.getTotalGenerations(),
			result2.getBestFitness() != null
				? result2.getBestFitness().doubleValue()
				: Double.NEGATIVE_INFINITY,
			end2 - start2
		};
	}

	public static <P, G extends Gene<?, G>, N extends Number &  Comparable<? super N>>
	Runner2<P, G, N> of(
		final Engine<G, N> engine1,
		final Function<? super P, Predicate<? super EvolutionResult<G, N>>> terminator1,
		final Engine<G, N> engine2,
		final Function<? super P, Predicate<? super EvolutionResult<G, N>>> terminator2,
		final Supplier<TrialMeter<P>> trialMeter,
		final String[] arguments
	) {
		final Args args = Args.of(arguments);

		return new Runner2<>(
			engine1,
			terminator1,
			engine2,
			terminator2,
			trialMeter,
			args.intArg("sample-count")
				.orElse(50),
			args.arg("result-file")
				.map(f -> Paths.get(f))
				.orElse(Paths.get("trial_meter.xml"))
		);
	}
}
