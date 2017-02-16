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
package org.jenetics.example;

import static java.nio.file.Files.exists;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jenetics.BitGene;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.engine.Problem;
import org.jenetics.example.Knapsack.Item;
import org.jenetics.util.IO;
import org.jenetics.util.ISeq;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.8
 * @since 3.8
 */
public class EvolutionResume {
	// The problem definition.
	private final Problem<ISeq<Item>, BitGene, Double> knapsack =
		Knapsack.of(250, RandomRegistry.getRandom());

	// The evolution engine.
	private final Engine<BitGene, Double> engine = Engine.builder(knapsack)
		.populationSize(100)
		.build();

	// Run the evolution.
	private EvolutionResult<BitGene, Double> run(
		final EvolutionResult<BitGene, Double> last,
		final AtomicBoolean proceed
	) {
		System.out.println("Starting evolution with existing result.");

		return (last != null ? engine.stream(last) : engine.stream())
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
		while (!System.console().readLine().equals("exit"))
		proceed.set(false);
		System.out.println("Evolution stopped.");

		// Writing the best evolution result to file.
		System.out.println("Writing evolution result.");
		IO.object.write(future.get(), resultPath);

		System.out.println("Best fitness: " + future.get().getBestFitness());
	}
}


