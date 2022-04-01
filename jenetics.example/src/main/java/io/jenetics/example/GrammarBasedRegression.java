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

import static java.util.stream.Collectors.joining;

import java.util.List;

import io.jenetics.IntegerGene;
import io.jenetics.Phenotype;
import io.jenetics.SwapMutator;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Limits;
import io.jenetics.util.IntRange;

import io.jenetics.ext.grammar.Bnf;
import io.jenetics.ext.grammar.Cfg;
import io.jenetics.ext.grammar.Cfg.Terminal;
import io.jenetics.ext.grammar.Mappers;
import io.jenetics.ext.grammar.SentenceGenerator;
import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.TreeNode;

import io.jenetics.prog.op.Const;
import io.jenetics.prog.op.MathExpr;
import io.jenetics.prog.op.Op;
import io.jenetics.prog.regression.Error;
import io.jenetics.prog.regression.LossFunction;
import io.jenetics.prog.regression.Sample;
import io.jenetics.prog.regression.Sampling;
import io.jenetics.prog.regression.Sampling.Result;

/**
 * Symbolic regression example, based on Grammatical Evolution.
 *
 * @see SymbolicRegression
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class GrammarBasedRegression {
	private GrammarBasedRegression() {
	}

	private static final Cfg<String> CFG = Bnf.parse("""
		<expr> ::= x | <num> | <expr> <op> <expr>
		<op>   ::= + | - | * | /
		<num>  ::= 2 | 3 | 4
		"""
	);

	private static final Codec<Tree<Op<Double>, ?>, IntegerGene> CODEC = Mappers
		.multiIntegerChromosomeMapper(
			CFG,
			rule -> IntRange.of(rule.alternatives().size()*100),
			index -> new SentenceGenerator<>(index, 50)
		)
		.map(sentence -> sentence.stream().map(Terminal::value).collect(joining()))
		.map(expr -> expr.isEmpty()
			? TreeNode.of(Const.of(0.0))
			: MathExpr.parseTree(expr));

	private static final Error<Double> ERROR = Error.of(LossFunction::mse);

	// Lookup table for 4*x^3 - 3*x^2 + x
	private static final Sampling<Double> SAMPLES = Sampling.of(List.of(
		Sample.ofDouble(-1.0, -8.0000),
		Sample.ofDouble(-0.9, -6.2460),
		Sample.ofDouble(-0.8, -4.7680),
		Sample.ofDouble(-0.7, -3.5420),
		Sample.ofDouble(-0.6, -2.5440),
		Sample.ofDouble(-0.5, -1.7500),
		Sample.ofDouble(-0.4, -1.1360),
		Sample.ofDouble(-0.3, -0.6780),
		Sample.ofDouble(-0.2, -0.3520),
		Sample.ofDouble(-0.1, -0.1340),
		Sample.ofDouble(0.0, 0.0000),
		Sample.ofDouble(0.1, 0.0740),
		Sample.ofDouble(0.2, 0.1120),
		Sample.ofDouble(0.3, 0.1380),
		Sample.ofDouble(0.4, 0.1760),
		Sample.ofDouble(0.5, 0.2500),
		Sample.ofDouble(0.6, 0.3840),
		Sample.ofDouble(0.7, 0.6020),
		Sample.ofDouble(0.8, 0.9280),
		Sample.ofDouble(0.9, 1.3860),
		Sample.ofDouble(1.0, 2.0000)
	));

	private static double error(final Tree<? extends Op<Double>, ?> program) {
		final Result<Double> result = SAMPLES.eval(program);
		return ERROR.apply(program, result.calculated(), result.expected());
	}

	public static void main(final String[] args) {
		final Engine<IntegerGene, Double> engine = Engine
			.builder(GrammarBasedRegression::error, CODEC)
			.alterers(new SwapMutator<>())
			.minimizing()
			.build();

		final EvolutionResult<IntegerGene, Double> result = engine.stream()
			.limit(Limits.byFitnessThreshold(0.01))
			.limit(5000)
			.collect(EvolutionResult.toBestEvolutionResult());

		final Phenotype<IntegerGene, Double> best = result.bestPhenotype();
		final Tree<Op<Double>, ?> program = CODEC.decode(best.genotype());

		System.out.println("Generations: " + result.totalGenerations());
		System.out.println("Function:    " + new MathExpr(program).simplify());
		System.out.println("Error:       " + error(program));
	}

}
