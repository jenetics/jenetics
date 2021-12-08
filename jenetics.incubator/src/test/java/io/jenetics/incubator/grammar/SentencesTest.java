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

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import io.jenetics.incubator.grammar.Cfg.Symbol;
import io.jenetics.incubator.grammar.Cfg.Terminal;
import io.jenetics.incubator.grammar.bnf.Bnf;


public class SentencesTest {

	final Cfg CFG = Bnf.parse("""
		<expr> ::= ( <expr> <op> <expr> ) | <num> | <var> |  <fun> ( <arg>, <arg> )
		<fun>  ::= FUN1 | FUN2
		<arg>  ::= <expr> | <var> | <num>
		<op>   ::= + | - | * | /
		<var>  ::= x | y
		<num>  ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
		"""
	);

	@Test(invocationCount = 50)
	public void create() {
		final var random = new Random(-8564585140851778291L);
		List<Terminal> list = Sentences.generate(CFG, random::nextInt);

		var string = list.stream()
			.map(Symbol::value)
			.collect(Collectors.joining());

		System.out.println(string);

		final var seed = new Random().nextLong();
		System.out.println(seed);
		random.setSeed(29022156195143L);
		list = Sentences.infixGenerate(CFG, random::nextInt, new LinkedList<>());
		string = list.stream()
			.map(Symbol::value)
			.collect(Collectors.joining());
		System.out.println(string);
		System.out.println();
	}

	@Test(invocationCount = 1000)
	public void foo() {
		final Cfg cfg = Bnf.parse("""
			<expr> ::= ( <expr> <op> <expr> ) | <num> | <var> |  <fun> ( <arg>, <arg> )
			<fun>  ::= FUN1 | FUN2
			<arg>  ::= <expr> | <var> | <num>
			<op>   ::= + | - | * | /
			<var>  ::= x | y
			<num>  ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
			"""
		);

		final RandomGenerator random = RandomGenerator.of("L64X256MixRandom");
		final List<Terminal> sentence = Sentences.generate(cfg, random::nextInt);
		final String string = sentence.stream()
			.map(Symbol::value)
			.collect(Collectors.joining());

		System.out.println(string);
	}

}
