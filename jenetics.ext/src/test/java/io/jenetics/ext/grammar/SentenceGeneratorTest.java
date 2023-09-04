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

import static java.lang.Integer.MAX_VALUE;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.ext.grammar.Cfg.Symbol;
import io.jenetics.ext.grammar.SentenceGenerator.Expansion;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class SentenceGeneratorTest {

	static final Cfg<String> CFG = Bnf.parse("""
		<expr> ::= ( <expr> <op> <expr> ) | <num> | <var> |  <fun> ( <arg>, <arg> )
		<fun>  ::= FUN1 | FUN2
		<arg>  ::= <expr> | <var> | <num>
		<op>   ::= + | - | * | /
		<var>  ::= x | y
		<num>  ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
		"""
	);

	@Test
	public void generate() {
		final var random = new Random(-8564585140851778291L);

		var generator = new SentenceGenerator<String>(
			SymbolIndex.of(random),
			MAX_VALUE
		);

		var string = generator.generate(CFG).stream()
			.map(Symbol::name)
			.collect(Collectors.joining());

		assertThat(string)
			.isEqualTo("FUN1(8,FUN1((5-FUN1(((6/FUN2(y,y))*FUN2(FUN1(y,y),y)),FUN1(3,y))),5))");

		random.setSeed(29022156195143L);
		generator = new SentenceGenerator<>(
			SymbolIndex.of(random),
			Expansion.LEFT_TO_RIGHT,
			MAX_VALUE
		);

		string = generator.generate(CFG).stream()
			.map(Symbol::name)
			.collect(Collectors.joining());

		assertThat(string)
			.isEqualTo("FUN1(y,FUN1(x,FUN1((3/y),(((FUN1(FUN1(y,9),y)+(4/x))-FUN2(y,x))*y))))");
	}

	@Test(dataProvider = "sentencesLeftToRight")
	public void compatibleLeftToRightSentenceGeneration(
		final long seed,
		final String sentence
	) {
		compatibleSentenceGeneration(seed, sentence, Expansion.LEFT_TO_RIGHT);
	}

	@Test(dataProvider = "sentencesLeftFirst")
	public void compatibleLeftFirstSentenceGeneration(
		final long seed,
		final String sentence
	) {
		compatibleSentenceGeneration(seed, sentence, Expansion.LEFT_MOST);
	}

	private void compatibleSentenceGeneration(
		final long seed,
		final String sentence,
		Expansion expansion
	) {
		final var random = new Random(seed);
		final var generator = new SentenceGenerator<String>(
			SymbolIndex.of(random),
			expansion,
			MAX_VALUE
		);
		final var terminals = generator.generate(CFG);

		final String string = terminals.stream()
			.map(Symbol::name)
			.collect(Collectors.joining());
		assertThat(string).isEqualTo(sentence);
	}

	@DataProvider
	public Object[][] sentencesLeftToRight() throws IOException {
		return read("/io/jenetics/incubator/grammar/sentences-left_to_right.csv");
	}

	@DataProvider
	public Object[][] sentencesLeftFirst() throws IOException {
		return read("/io/jenetics/incubator/grammar/sentences-left_first.csv");
	}

	private static Object[][] read(final String resource) throws IOException {
		final List<Object[]> values = new ArrayList<>();
		try (var in = SentenceGeneratorTest.class.getResourceAsStream(resource);
			 var reader = new InputStreamReader(in);
			 var br = new BufferedReader(reader))
		{
			String line;
			while ((line = br.readLine()) != null) {
				final var parts = line.split("\t");
				values.add(new Object[] {Long.parseLong(parts[0]), parts[1]});
			}
		}

		return values.toArray(Object[][]::new);
	}

}
