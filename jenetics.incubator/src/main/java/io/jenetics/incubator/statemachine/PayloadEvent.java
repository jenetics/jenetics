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

/**
 * Wraps a given {@code payload} and a {@link Fsm.Symbol} into an event.
 *
 * @param kind the event kind
 * @param payload the payload of the event
 * @param <SY> the symbol type
 * @param <P> the payload type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 9.1
 * @since 9.1
 */
public record PayloadEvent<SY extends Fsm.Symbol, P>(SY kind, P payload)
	implements Fsm.Event<SY>
{

	public PayloadEvent {
		requireNonNull(kind);
		requireNonNull(payload);
	}

}
