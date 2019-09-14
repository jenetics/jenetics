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
package io.jenetics.tool.evaluation;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.jenetics.Gene;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.internal.util.Args;
import io.jenetics.tool.trial.TrialMeter;

import io.jenetics.xml.stream.Reader;
import io.jenetics.xml.stream.Writer;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.0
 * @since 3.4
 */
public class Runner<
	P,
	G extends Gene<?, G>,
	N extends Number &  Comparable<? super N>
>
	extends AbstractRunner<P>
{

	private final Function<? super P, Engine<G, N>> _engine;
	private final Function<? super P, Predicate<? super EvolutionResult<G, N>>> _terminator;

	public Runner(
		final Function<? super P, Engine<G, N>> engine,
		final Function<? super P, Predicate<? super EvolutionResult<G, N>>> terminator,
		final Supplier<TrialMeter<P>> trialMeter,
		final Writer<P> writer,
		final Reader<P> reader,
		final int sampleCount,
		final Path resultPath
	) {
		super(trialMeter, writer, reader, sampleCount, resultPath);
		_engine = requireNonNull(engine);
		_terminator = requireNonNull(terminator);
	}

	protected double[] fitness(final P param) {
		final Predicate<? super EvolutionResult<G, N>> terminator =
			_terminator.apply(param);

		final long start = System.currentTimeMillis();
		final EvolutionResult<G, N> result = _engine.apply(param).stream()
			.limit(terminator)
			.collect(EvolutionResult.toBestEvolutionResult());
		final long end = System.currentTimeMillis();

		return new double[] {
			result.getTotalGenerations(),
			result.getBestFitness() != null
				? result.getBestFitness().doubleValue()
				: Double.NEGATIVE_INFINITY,
			end - start
		};
	}

	public static <P, G extends Gene<?, G>, N extends Number &  Comparable<? super N>>
	Runner<P, G, N> of(
		final Function<? super P, Engine<G, N>> engine,
		final Function<? super P, Predicate<? super EvolutionResult<G, N>>> terminator,
		final Supplier<TrialMeter<P>> trialMeter,
		final Writer<P> writer,
		final Reader<P> reader,
		final String[] arguments
	) {
		final Args args = Args.of(arguments);

		return new Runner<>(
			engine,
			terminator,
			trialMeter,
			writer,
			reader,
			args.intArg("sample-count")
				.orElse(50),
			args.arg("result-file")
				.map(f -> Paths.get(f))
				.orElse(Paths.get("trial_meter.xml"))
		);
	}

}
