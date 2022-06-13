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
import static java.util.stream.Collectors.joining;
import static io.jenetics.example.SymbolicRegression.SAMPLES;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import io.jenetics.IntegerGene;
import io.jenetics.Phenotype;
import io.jenetics.SinglePointCrossover;
import io.jenetics.SwapMutator;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Limits;
import io.jenetics.engine.Problem;
import io.jenetics.util.IntRange;

import io.jenetics.ext.grammar.Bnf;
import io.jenetics.ext.grammar.Cfg;
import io.jenetics.ext.grammar.Cfg.Terminal;
import io.jenetics.ext.grammar.Mappers;
import io.jenetics.ext.grammar.SentenceGenerator;
import io.jenetics.ext.util.TreeNode;

import io.jenetics.prog.regression.Error;
import io.jenetics.prog.regression.LossFunction;
import io.jenetics.prog.regression.Sample;
import io.jenetics.prog.regression.Sampling;
import io.jenetics.prog.regression.Sampling.Result;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.1
 * @since 7.1
 */
public class GrammaticalJavaScriptEvolution
	implements Problem<ScriptFunction, IntegerGene, Double>
{

	// Create the script engine of your choice.
	private static final ScriptEngine SCRIPT_ENGINE = new ScriptEngineManager()
		.getEngineByName("nashorn");

	// Define a grammar which creates a valid script for the script engine.
	private static final Cfg<String> GRAMMAR = Bnf.parse("""
		<expr>         ::= <var> | <num> | <op-expr> | <ternary-expr>
		<ternary-expr> ::= '(' <boolean-expr> ') ? (' <expr> ') : (' <expr> ')'
		<op-expr>      ::= <expr> <op> <expr>
		<boolean-expr> ::= (<expr>) <relation> (<expr>)
		<relation>     ::= ' < ' | ' > ' | ' <= ' | ' >= ' | ' != ' | ' == '
		<op>           ::= + | - | * | /
		<var>          ::= x
		<num>          ::= 2.0 | 3.0 | 4.0
		"""
	);

	private static final Codec<ScriptFunction, IntegerGene> CODEC = Mappers
		// Creating a GE mapper/codec: `Codec<ScriptFunction, IntegerGene>`
		.multiIntegerChromosomeMapper(
			GRAMMAR,
			// The length of the chromosome is 25 times the length of the
			// alternatives of a given rule. Every rule gets its own chromosome.
			// It would also be possible to define variable chromosome length
			// with the returned integer range.
			rule -> IntRange.of(rule.alternatives().size()*25),
			// The used generator defines the generated data type, which is
			// `List<Terminal<String>>`.
			index -> new SentenceGenerator<>(index, 50)
		)
		// Map the type of the codec from `Codec<List<Terminal<String>, IntegerGene>`
		// to `Codec<String, IntegerGene>`
		.map(s -> s.stream().map(Terminal::value).collect(joining()))
		.map(script -> new ScriptFunction(script, SCRIPT_ENGINE));

	private static final Error<Double> ERROR = Error.of(LossFunction::mse);

	private final Sampling<Double> _sampling;

	public GrammaticalJavaScriptEvolution(final Sampling<Double> sampling) {
		_sampling = requireNonNull(sampling);
	}

	public GrammaticalJavaScriptEvolution(final List<Sample<Double>> samples) {
		this(Sampling.of(samples));
	}

	@Override
	public Codec<ScriptFunction, IntegerGene> codec() {
		return CODEC;
	}

	@Override
	public Function<ScriptFunction, Double> fitness() {
		return script -> {
			final Result<Double> result = _sampling.eval(args -> {
				final var value = (Double)script.apply(Map.of("x", args[0]));
				return value != null ? value : Double.NaN;
			});
			return ERROR.apply(TreeNode.of(), result.calculated(), result.expected());
		};
	}

	public static void main(final String[] args) {
		// 4*x^3 - 3*x^2 + x
		final var regression = new GrammaticalJavaScriptEvolution(SAMPLES);

		final Engine<IntegerGene, Double> engine = Engine.builder(regression)
			.alterers(new SwapMutator<>(), new SinglePointCrossover<>())
			.minimizing()
			.build();

		final EvolutionResult<IntegerGene, Double> result = engine.stream()
			.limit(Limits.byFitnessThreshold(0.05))
			.limit(1000)
			.collect(EvolutionResult.toBestEvolutionResult());

		final Phenotype<IntegerGene, Double> best = result.bestPhenotype();
		final ScriptFunction program = CODEC.decode(best.genotype());

		System.out.println("Generations: " + result.totalGenerations());
		System.out.println("Function:    " + program);
		System.out.println("Error:       " + regression.fitness().apply(program));
	}

}
