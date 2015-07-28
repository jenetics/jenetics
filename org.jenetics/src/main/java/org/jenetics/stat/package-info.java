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

/**
 * This package contains additional statistics classes which are not available
 * in the Java core library. Java only includes classes for calculating the sum
 * and the average of a given numeric stream (e. g.
 * {@code DoubleSummaryStatistics}). With the additions in this package it is
 * also possible to calculate the variance, skewness and kurtosis---using the
 * {@code DoubleMomentStatistics} class. The {@code EvolutionStatistics} object,
 * which can be calculated for every generation, relies on the classes of this
 * package.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 3.0
 */
package org.jenetics.stat;
