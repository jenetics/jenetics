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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import io.jenetics.incubator.grammar.Cfg.NonTerminal;
import io.jenetics.incubator.grammar.Cfg.Rule;
import io.jenetics.incubator.grammar.Cfg.Symbol;
import io.jenetics.incubator.grammar.Cfg.Terminal;
import io.jenetics.incubator.grammar.bnf.Bnf;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 15, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class SentencesPerf {

	public Random random;
	public Cfg cfg;

	@Setup
	public void setup() {
		random = new Random(1234);
		cfg = Bnf.parse("""
			<expr> ::= ( <expr> <op> <expr> ) | <num> | <var> |  <fun> ( <arg>, <arg> )
			<fun>  ::= FUN1 | FUN2
			<arg>  ::= <expr> | <var> | <num>
			<op>   ::= + | - | * | /
			<var>  ::= x | y
			<num>  ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
			"""
		);
	}

	@Benchmark
	public Object leftGeneratelinkedListSentence() {
		random.setSeed(-8564585140851778291L);
		return Sentences.leftFirstExpansion(cfg, random::nextInt, new LinkedList<>());
	}

	@Benchmark
	public Object leftGeneratearrayListSentence() {
		random.setSeed(-8564585140851778291L);
		return Sentences.leftFirstExpansion(cfg, random::nextInt, new ArrayList<>(100));
	}

	@Benchmark
	public Object linkedListSentence() {
		random.setSeed(29022156195143L);
		return infixGenerate(cfg, random::nextInt, new LinkedList<>());
	}

	@Benchmark
	public Object arrayListSentence() {
		random.setSeed(29022156195143L);
		return infixGenerate(cfg, random::nextInt, new ArrayList<>(100));
	}

	private static List<Symbol> expand(
		final Cfg cfg,
		final NonTerminal symbol,
		final SymbolIndex index
	) {
		return cfg.rule(symbol)
			.map(r -> expand(r, index))
			.orElse(List.of(symbol));
	}

	private static List<Symbol> expand(final Rule rule, final SymbolIndex index) {
		final int size = rule.alternatives().size();
		return rule.alternatives()
			.get(index.next(size))
			.symbols();
	}

	static List<Terminal> infixGenerate(
		final Cfg cfg,
		final SymbolIndex index,
		final List<Symbol> symbols
	) {
		final NonTerminal start = cfg.start();
		symbols.addAll(expand(cfg, start, index));

		boolean expanded = true;
		while (expanded) {
			expanded = false;

			final ListIterator<Symbol> sit = symbols.listIterator();
			while (sit.hasNext()) {
				if (sit.next() instanceof NonTerminal nt) {
					sit.remove();
					final List<Symbol> exp = expand(cfg, nt, index);
					exp.forEach(sit::add);

					expanded = true;
				}
			}
		}

		return symbols.stream()
			.map(Terminal.class::cast)
			.toList();
	}

}
