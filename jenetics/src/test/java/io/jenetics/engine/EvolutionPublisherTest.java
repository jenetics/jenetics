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

import static java.util.Objects.requireNonNull;

import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import org.testng.annotations.Test;

import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class EvolutionPublisherTest {

	private final Problem<Integer, IntegerGene, Integer> _problem = Problem.of(
		a -> a,
		Codec.of(
			Genotype.of(IntegerChromosome.of(0, 1000)),
			g -> g.gene().allele()
		)
	);

	private final Engine<IntegerGene, Integer> _engine = Engine
		.builder(_problem)
		.build();

	@Test
	public void publishing() {
		final var publisher = new EvolutionPublisher<IntegerGene, Integer>();
		final var stream = _engine.stream();

		publisher.subscribe(new Subscriber<>() {
			private int _count;
			private Subscription _subscription;
			@Override
			public void onSubscribe(Subscription subscription) {
				_subscription = requireNonNull(subscription);
				_subscription.request(1);
			}

			@Override
			public void onNext(EvolutionResult<IntegerGene, Integer> result) {
				System.out.println("" + ++_count + ": " + result.bestPhenotype());
				_subscription.request(1);
			}

			@Override
			public void onError(Throwable throwable) {
			}

			@Override
			public void onComplete() {
				System.out.println("FINISHED");
			}
		});

		publisher.attach(stream);
		System.out.println("END");
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		publisher.close();
	}

}
