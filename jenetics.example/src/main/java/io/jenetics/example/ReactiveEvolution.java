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
package io.jenetics.example;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import io.jenetics.DoubleGene;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Codecs;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.stat.MinMax;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.StreamPublisher;

public class ReactiveEvolution implements AutoCloseable {

	private final Codec<Double, DoubleGene> _codec = Codecs
		.ofScalar(new DoubleRange(0.0, 2.0*PI));

	private final Engine<DoubleGene, Double> _engine = Engine
		.builder(ReactiveEvolution::fitness, _codec)
		.build();

	private StreamPublisher<EvolutionResult<DoubleGene, Double>> _publisher;

	private static double fitness(final double x) {
		return cos(0.5 + sin(x))*cos(x);
	}

	public synchronized void
	evolve(final Subscriber<EvolutionResult<DoubleGene, Double>> subscriber) {
		if (_publisher != null) {
			throw new IllegalStateException("Evolution publisher already started.");
		}

		final var stream = _engine.stream()
			.limit(r -> !Thread.currentThread().isInterrupted())
			// Only emit results which are better then the previous one.
			.flatMap(MinMax.toStrictlyIncreasing());

		_publisher = new StreamPublisher<>();
		_publisher.subscribe(subscriber);
		_publisher.attach(stream);
	}

	@Override
	public synchronized void close() {
		if (_publisher != null) {
			_publisher.close();
		}
	}

	public static void main(final String[] args) throws InterruptedException {
		try (var evolution = new ReactiveEvolution()) {
			evolution.evolve(new SimpleSubscriber());
			Thread.sleep(500);
		}
	}
}

final class SimpleSubscriber
	implements Subscriber<EvolutionResult<DoubleGene, Double>>
{
	private Subscription _subscription;
	@Override
	public void onSubscribe(final Subscription subscription) {
		_subscription = subscription;
		_subscription.request(1);
	}
	@Override
	public void onNext(final EvolutionResult<DoubleGene, Double> result) {
		System.out.println(result.bestPhenotype());
		_subscription.request(1);
	}
	@Override
	public void onError(final Throwable throwable) {}
	@Override
	public void onComplete() {
		System.out.println("Finished evolution.");
	}
}
