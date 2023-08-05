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

import static java.nio.file.Files.exists;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import io.jenetics.BitGene;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Problem;
import io.jenetics.example.Knapsack.Item;
import io.jenetics.util.IO;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;

/**
 * This example shows how to write intermediate evolution results to disk and
 * continue the evolution with the stored population.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.1
 * @since 3.8
 */
public class EvolutionResume {

	// The problem definition.
	private static final Problem<ISeq<Item>, BitGene, Double> KNAPSACK =
		Knapsack.of(250, RandomRegistry.random());

	// The evolution engine.
	private static final Engine<BitGene, Double> ENGINE =
		Engine.builder(KNAPSACK)
			.populationSize(100)
			.build();

	// Run the evolution.
	private EvolutionResult<BitGene, Double> run(
		final EvolutionResult<BitGene, Double> last,
		final AtomicBoolean proceed
	) {
		System.out.println("Starting evolution with existing result.");

		return (last != null ? ENGINE.stream(last) : ENGINE.stream())
			.limit(r -> proceed.get())
			.collect(EvolutionResult.toBestEvolutionResult());
	}

	public static void main(final String[] args)
		throws IOException, InterruptedException, ExecutionException
	{
		if (args.length == 0) {
			System.out.println("Missing evolution result path.");
			System.exit(1);
		}

		// The result path.
		final Path resultPath = Paths.get(args[0]);

		@SuppressWarnings("unchecked")
		final EvolutionResult<BitGene, Double> result = exists(resultPath)
			? (EvolutionResult<BitGene, Double>)IO.object.read(resultPath)
			: null;

		final AtomicBoolean proceed = new AtomicBoolean(true);
		final EvolutionResume resume = new EvolutionResume();

		final CompletableFuture<EvolutionResult<BitGene, Double>> future =
			CompletableFuture.supplyAsync(() -> resume.run(result, proceed));
		System.out.println("Evolution started.");

		// Read console command: type 'exit' for stopping evolution.
		while (!System.console().readLine().equals("exit")) {}
		proceed.set(false);

		System.out.println("Evolution stopped.");

		// Writing the best evolution result to file.
		System.out.println("Writing evolution result.");
		IO.object.write(future.get(), resultPath);

		System.out.println("Best fitness: " + future.get().bestFitness());
	}
}


