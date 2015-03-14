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
package org.jenetics.diagram;

import static java.lang.Math.log10;
import static java.lang.Math.max;
import static java.lang.Math.pow;
import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.LongStream;

import org.jenetics.BitGene;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.engine.limit;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class KnapsackExecutionTime extends Knapsack {

	public static void main(final String[] args) throws IOException {
		final KnapsackSteadyFitness instance = new KnapsackSteadyFitness();

		final double base = pow(10, log10(100)/20.0);

		RandomRegistry.setRandom(new LCG64ShiftRandom.ThreadLocal());
		final int samples = 500;

		final Function<Duration, Predicate<? super EvolutionResult<BitGene, Double>>>
			terminator = limit::byExecutionTime;

		final TerminationStatistics<BitGene, Duration> statistics =
			new TerminationStatistics<>(
				samples,
				instance.engine(),
				terminator,
				Duration::toMillis
			);

		statistics.warmup(instance.engine());

		final long start = System.nanoTime();
		final long time = LongStream.rangeClosed(1, 15)
			.peek(i -> System.out.print(i + ": "))
			.map(i -> max((long) pow(base, i), i))
			.peek(i -> System.out.println(
				"Execution time: " + DurationFormat.format(Duration.ofMillis(i))))
			.peek(d -> statistics.accept(Duration.ofMillis(d)))
			.sum();
		final long end = System.nanoTime();

		System.out.println(format(
			"Executed %s execution time in %s",
			DurationFormat.format(Duration.ofMillis(time)),
			DurationFormat.format(Duration.ofNanos(end - start))
		));

		statistics.write(new File(
			"org.jenetics/src/test/scripts/diagram/" +
				"execution_time_termination1.dat"
		));
		System.out.println("Ready");

	}

}
