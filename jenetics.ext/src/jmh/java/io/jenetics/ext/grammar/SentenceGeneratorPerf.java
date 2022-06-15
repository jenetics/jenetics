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
package io.jenetics.ext.grammar;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import io.jenetics.ext.grammar.Cfg.Symbol;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.1
 * @since 7.1
 */
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 15, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class SentenceGeneratorPerf {

	private static final Cfg<String> CFG = Bnf.parse("""
			<expr> ::= <num> | <var> | '(' <expr> <op> <expr> ')' | <expr> <op> <expr>
			<op>   ::= + | - | * | / | ^
			<var>  ::= x | y | z
			<num>  ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
			"""
	);

	private static final SentenceGenerator<String> GENERATOR = new SentenceGenerator<>(
		SymbolIndex.of(RandomGenerator.getDefault()),
		10_000
	);

	@Benchmark
	public Object linkedListGeneration() {
		final var sentence = new LinkedList<Symbol<String>>();
		GENERATOR.generate(CFG, sentence);
		return sentence;
	}

	@Benchmark
	public Object arrayListGeneration() {
		final var sentence = new ArrayList<Symbol<String>>();
		GENERATOR.generate(CFG, sentence);
		return sentence;
	}

	/* 7.1 / i7-6700HQ CPU @ 2.60GHz
Benchmark                                   Mode  Cnt  Score   Error  Units
SentenceGeneratorPerf.arrayListGeneration   avgt   45  3.298 ± 0.875  ms/op
SentenceGeneratorPerf.linkedListGeneration  avgt   45  6.603 ± 2.693  ms/op
	 */

}
