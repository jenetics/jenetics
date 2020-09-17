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
package io.jenetics.incubator.timeseries;

import java.util.function.Function;
import java.util.stream.Stream;

import io.jenetics.Mutator;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.Engine.Setup;
import io.jenetics.engine.EvolutionStream;
import io.jenetics.engine.FitnessNullifier;

import io.jenetics.ext.SingleNodeCrossover;
import io.jenetics.ext.util.Tree;

import io.jenetics.prog.ProgramGene;
import io.jenetics.prog.op.Op;
import io.jenetics.prog.regression.Error;
import io.jenetics.prog.regression.Regression;
import io.jenetics.prog.regression.Sample;
import io.jenetics.prog.regression.SampleBuffer;
import io.jenetics.prog.regression.Sampling;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class TimeSeries<T> implements Sampling<T> {

	@Override
	public Result<T> eval(final Tree<? extends Op<T>, ?> program) {
		return null;
	}

	public <R extends Comparable<? super R>> Setup<ProgramGene<T>, R> setup() {
		return null;
	}

}

class Series<T> implements Function<Sample<T>, Stream<Tree<Op<T>, ?>>> {

	private Codec<Tree<Op<T>, ?>, ProgramGene<T>> codec;
	private Error<T> error;

	private final SampleBuffer<T> samples = new SampleBuffer<>(50);
	private final FitnessNullifier<ProgramGene<T>, Double> nullifier = new FitnessNullifier<>();

	private final Regression<T> regression;
	private final Engine<ProgramGene<T>, Double> engine;
	private final EvolutionStream<ProgramGene<T>, Double> stream;

	Series() {
		regression = Regression.of(codec, error, samples);
		engine = Engine
			.builder(regression)
			.interceptor(nullifier)
			.minimizing()
			.alterers(
				new SingleNodeCrossover<>(),
				new Mutator<>())
			.build();

		stream = engine.stream();
	}

	@Override
	public Stream<Tree<Op<T>, ?>> apply(final Sample<T> sample) {
		samples.add(sample);

		return Stream.empty();
	}
}

class Test {
	static void main(final String[] args) {
		final Stream<Sample<Double>> data = Stream.empty();

		final Stream<Tree<Op<Double>, ?>> programs = data
			.flatMap(new Series<>());

	}
}
