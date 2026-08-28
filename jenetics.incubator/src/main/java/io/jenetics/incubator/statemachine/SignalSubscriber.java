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
package io.jenetics.incubator.statemachine;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.Flow;
import java.util.function.Consumer;

/**
 *
 * @param <ST> the state type
 * @param <SI> the symbol type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 9.1
 * @since 9.1
 */
public class SignalSubscriber<ST extends Fsm.State, SI extends Fsm.Signal>
	implements Flow.Subscriber<Fsm.Transition<ST, SI>>
{

	private final Consumer<? super Fsm.Transition<ST, SI>> consumer;

	private Flow.Subscription subscription;

	public SignalSubscriber(Consumer<? super Fsm.Transition<ST, SI>> consumer) {
		this.consumer = requireNonNull(consumer);
	}

	@Override
	public void onSubscribe(Flow.Subscription subscription) {
		this.subscription = requireNonNull(subscription);
		this.subscription.request(1);
	}

	@Override
	public void onNext(Fsm.Transition<ST, SI> transition) {
		consumer.accept(transition);
		subscription.request(1);
	}

	@Override
	public void onError(Throwable throwable) {
		subscription.cancel();
		subscription = null;
	}

	@Override
	public void onComplete() {
		subscription = null;
	}
}
