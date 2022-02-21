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

import static java.util.Objects.requireNonNull;
import static io.jenetics.engine.Limits.byFixedGeneration;

import java.util.function.Function;

import io.jenetics.AnyChromosome;
import io.jenetics.AnyGene;
import io.jenetics.Genotype;
import io.jenetics.RouletteWheelSelector;
import io.jenetics.SinglePointCrossover;
import io.jenetics.SwapMutator;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Problem;
import io.jenetics.engine.RetryConstraint;
import io.jenetics.example.RectFill.Rect;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.1
 * @since 4.0
 */
public final class RectFill
	implements Problem<ISeq<Rect>, AnyGene<Rect>, Double>
{

	private static final int MAX_RECT_COUNT = 100;

	record Rect(int x1, int x2, int y1, int y2) {
		static final Rect EMPTY = new Rect(-1, -1, -1, -1);

		Rect(final int x1, final int x2, final int y1, final int y2) {
			this.x1 = Math.min(x1, x2);
			this.x2 = Math.max(x1, x2);
			this.y1 = Math.min(y1, y2);
			this.y2 = Math.min(y1, y2);
		}

		static Rect newInstance(final Rect bounds) {
			final var random = RandomRegistry.random();
			return new Rect(
				random.nextInt(bounds.x2 - bounds.x1) + bounds.x1,
				random.nextInt(bounds.x2 - bounds.x1) + bounds.x1,
				random.nextInt(bounds.y2 - bounds.y1) + bounds.y1,
				random.nextInt(bounds.y2 - bounds.y1) + bounds.y1
			);
		}
	}

	private final Rect _outer;

	public RectFill(final Rect outer) {
		_outer = requireNonNull(outer);
	}

	@Override
	public Function<ISeq<Rect>, Double> fitness() {
		return null;
	}

	@Override
	public Codec<ISeq<Rect>, AnyGene<Rect>> codec() {
		return Codec.of(
			Genotype.of(AnyChromosome.of(
				() -> Rect.newInstance(_outer),
				a -> true,
				a -> true,
				MAX_RECT_COUNT
			)),
			gt -> gt.chromosome().stream()
						.map(AnyGene::allele)
						.filter(r -> r != Rect.EMPTY)
						.collect(ISeq.toISeq())
		);
	}

	public static void main(final String[] args) {
		final RectFill problem = new RectFill(new Rect(0, 100, 0, 100));

		final Engine<AnyGene<Rect>, Double> engine = Engine.builder(problem)
			.constraint(RetryConstraint.of(pt -> true))
			.offspringSelector(new RouletteWheelSelector<>())
			.alterers(
				new SwapMutator<>(),
				new SinglePointCrossover<>())
			.build();

		final ISeq<Rect> best = problem.codec().decode(
			engine.stream()
				.limit(byFixedGeneration(10))
				.collect(EvolutionResult.toBestGenotype())
		);

		System.out.println(best);
	}

}
