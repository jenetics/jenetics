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

import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.stream.Stream;

import io.jenetics.Mutator;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.FitnessNullifier;
import io.jenetics.util.ISeq;
import io.jenetics.util.StreamPublisher;
import io.jenetics.util.Streams;

import io.jenetics.ext.SingleNodeCrossover;
import io.jenetics.ext.util.Tree;

import io.jenetics.prog.ProgramGene;
import io.jenetics.prog.op.Op;
import io.jenetics.prog.regression.Regression;
import io.jenetics.prog.regression.Sample;
import io.jenetics.prog.regression.SampleBuffer;

/**
 * Reactive version.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class RegressionPublisher<T>
	implements
		Publisher<Tree<Op<T>, ?>>,
		AutoCloseable
{

	private final SampleBuffer<T> _samples = new SampleBuffer<>(50);

	private final FitnessNullifier<ProgramGene<T>, Double> _nullifier = new FitnessNullifier<>();

	private final ISeq<Op<T>> _operations = ISeq.of();

	private final ISeq<Op<T>> _terminals = ISeq.of();

	private final Regression<T> _regression = Regression.of(
		Regression.codecOf(
			_operations,
			_terminals,
			5
		),
		(t, e, a) -> 5.5,
		_samples
	);

	private final Engine<ProgramGene<T>, Double> _engine = Engine
		.builder(_regression)
		.interceptor(_nullifier)
		.minimizing()
		.alterers(
			new SingleNodeCrossover<>(),
			new Mutator<>())
		.build();

	private final Stream<EvolutionResult<ProgramGene<T>, Double>> _stream =
		_engine.stream()
			.flatMap(Streams.toIntervalMax(100));

	private final StreamPublisher<EvolutionResult<ProgramGene<T>, Double>> _publisher =
		new StreamPublisher<>();

	@Override
	public void subscribe(Subscriber<? super Tree<Op<T>, ?>> subscriber) {
		_publisher.subscribe(new Subscriber<>() {
			@Override
			public void onSubscribe(final Subscription subscription) {
				subscriber.onSubscribe(subscription);
			}

			@Override
			public void onNext(final EvolutionResult<ProgramGene<T>, Double> result) {
				subscriber.onNext(result.bestPhenotype().genotype().gene());
			}

			@Override
			public void onError(final Throwable throwable) {
				subscriber.onError(throwable);
			}

			@Override
			public void onComplete() {
				subscriber.onComplete();
			}
		});
	}

	@Override
	public void close() {
		_publisher.close();
	}

	/**
	 * Add a new sample point to the regression analysis.
	 *
	 * @param sample the sample point to add
	 */
	public void add(final Sample<T> sample) {
		_samples.add(sample);
	}

}
