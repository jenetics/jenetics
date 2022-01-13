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

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 15, time = 1)
@Fork(value = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class SentencePerf {

//	public Random random;
//	public Cfg cfg;
//
//	@Setup
//	public void setup() {
//		random = new Random(1234);
//		cfg = Bnf.parse("""
//			<expr> ::= ( <expr> <op> <expr> ) | <num> | <var> |  <fun> ( <arg>, <arg> )
//			<fun>  ::= FUN1 | FUN2
//			<arg>  ::= <expr> | <var> | <num>
//			<op>   ::= + | - | * | /
//			<var>  ::= x | y
//			<num>  ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
//			"""
//		);
//	}
//
//	@Benchmark
//	public Object leftToRightExpansion() {
//		random.setSeed(29022156195143L);
//
//		final var sentence = new LinkedList<Symbol>();
//		Sentence.expand(cfg, SymbolIndex.of(random), sentence, Expansion.LEFT_TO_RIGHT, Integer.MAX_VALUE);
//		return sentence;
//	}
//
//	@Benchmark
//	public Object leftFirstExpansion() {
//		random.setSeed(-8564585140851778291L);
//
//		final var sentence = new LinkedList<Symbol>();
//		Sentence.expand(cfg, SymbolIndex.of(random), sentence, Expansion.LEFT_FIRST, Integer.MAX_VALUE);
//		return sentence;
//	}

	/*
Benchmark                                     Mode  Cnt     Score    Error  Units
SentencesPerf.arrayListSentence               avgt   45  6353.975 ± 53.933  ns/op
SentencesPerf.leftGeneratearrayListSentence   avgt   45  9298.912 ± 60.637  ns/op
SentencesPerf.leftGeneratelinkedListSentence  avgt   45  9492.518 ± 55.839  ns/op
SentencesPerf.linkedListSentence              avgt   45  5327.885 ± 43.201  ns/op
	 */

	/*
Benchmark                           Mode  Cnt     Score    Error  Units
SentencesPerf.leftFirstExpansion    avgt   45  9238.491 ± 56.459  ns/op
SentencesPerf.leftToRightExpansion  avgt   45  4973.686 ± 24.957  ns/op
	 */

}
