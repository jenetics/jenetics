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

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.SubmissionPublisher;
import java.util.stream.Stream;

import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.StreamPublisher;

import io.jenetics.prog.ProgramGene;
import io.jenetics.prog.regression.Sample;

/**
 * @param <T>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class RegressionResultPublisher<T>
	extends SubmissionPublisher<RegressionResult<T>>
	implements Flow.Processor<List<? extends Sample<? extends T>>, RegressionResult<T>>
{

	private final Stream<EvolutionResult<ProgramGene<T>, Double>> _evolution;

	private final StreamPublisher<RegressionResult<T>> _publisher;

	public RegressionResultPublisher(Stream<EvolutionResult<ProgramGene<T>, Double>> evolution) {
		_evolution = requireNonNull(evolution);
		_publisher = new StreamPublisher<>();
	}

	@Override
	public void onSubscribe(final Subscription subscription) {
	}

	@Override
	public void onNext(final List<? extends Sample<? extends T>> samples) {
	}

	@Override
	public void onError(final Throwable throwable) {
	}

	@Override
	public void onComplete() {
	}

}
