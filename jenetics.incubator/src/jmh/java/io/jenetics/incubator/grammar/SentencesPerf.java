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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;

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
import org.openjdk.jmh.infra.Blackhole;

import io.jenetics.IntegerChromosome;

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
	public Object linkedListSentence() {
		//random.setSeed(29022156195143L);
		return Sentences.generate(cfg, random, LinkedList::new);
	}

	@Benchmark
	public Object arrayListSentence() {
		//random.setSeed(29022156195143L);
		return Sentences.generate(cfg, random, ArrayList::new);
	}

//	@Benchmark
//	public Object vectorSentence() {
//		//random.setSeed(29022156195143L);
//		return Sentences.generate(cfg, random, Vector::new);
//	}
//
//	@Benchmark
//	public Object stackSentence() {
//		//random.setSeed(29022156195143L);
//		return Sentences.generate(cfg, random, Stack::new);
//	}

}
