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
package io.jenetics.incubator.grammar;

import java.util.Random;
import java.util.stream.Collectors;

import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.incubator.grammar.Cfg.Symbol;
import io.jenetics.incubator.grammar.bnf.Bnf;
import io.jenetics.util.IntRange;

import io.jenetics.prog.op.MathExpr;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public class Example {

	private static final Cfg CFG = Bnf.parse("""
		<expr> ::= (<expr><op><expr>) | <pre-op>(<expr>) | <var>
		<op> ::= + | - | / | *
		<pre-op> ::= sin | cos | exp | log
		<var> ::= x | 1.0
		"""
	);

	private static final Codec<MathExpr, IntegerGene> CODEC = Sentence
		.codec(CFG, IntRange.of(0, 265), IntRange.of(15, 30), 1_000)
		.map(list -> list.stream()
			.map(Symbol::value)
			.collect(Collectors.joining()))
		.map(e -> {
			//System.out.println(e);
			return e;
		})
		.map(e -> e.isEmpty() ? null : MathExpr.parse(e));

	private static double fitness(final MathExpr expr) {
		if (expr == null) {
			return 0;
		} else {
			final var result = expr.eval(13);
			if (Double.isFinite(result)) {
				return result;
			} else {
				return 0;
			}
		}
	}

	public static void main(final String[] args) {
		System.out.println(IntegerChromosome.of(0, 265, 10));
		final Engine<IntegerGene, Double> engine = Engine.builder(Example::fitness, CODEC)
			.executor(Runnable::run)
			.build();

		final var bgt = engine.stream()
			.limit(100)
			.collect(EvolutionResult.toBestGenotype());

		final var best = CODEC.decode(bgt);

		System.out.println(bgt);
		System.out.println(best + " = " + fitness(best));
	}

}
