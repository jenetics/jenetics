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

/**
 * This package contains a simple Finit State Machine implementation.
 *
 * <h1>Architecture</h1>
 * The implementation separates the FSM definition from the execution. This
 * allows an easy to read FSM definition, which is not <em>polluted</em> with
 * configuration for the execution part.
 * <p>
 * <b>Definition</b>
 * {@link Fsm} is the immutable FSM definition.
 * <p>
 * <b>Execution</b>
 * {@link Stepper} is responsible for executing the FSM and also hold the current
 * state. Allows the implementation of different execution strategies, e.g. how
 * are invalid transitions handled, ignored or throwing an exception. It also
 * allows the implementation of different programming models, streaming, reactive
 * or event based.
 */
package io.jenetics.incubator.statemachine;
