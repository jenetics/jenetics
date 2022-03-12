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
package io.jenetics.prog.op;

import java.util.random.RandomGenerator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

//import io.jenetics.incubator.grammar.Cfg;
//import io.jenetics.incubator.grammar.Sentence;
//import io.jenetics.incubator.grammar.SymbolIndex;
//import io.jenetics.incubator.grammar.bnf.Bnf;

/**
 * This class generates valid math expressions for testing purpose.
 *
 * @since 7.0
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public final class MathExprGenerator {
	private MathExprGenerator() {
	}

	/*
	private static final Cfg GRAMMAR = Bnf.parse("""
		<expr> ::= <num>
				| <var>
				| <expr> <op> <expr>
				| ( <expr> <op> <expr> )
				| <fun1> ( <expr> )
				| <fun2> ( <expr>, <expr> )
		<op>   ::= + | - | * | / | %
		<fun1> ::= sin | cos | abs | rint
		<fun2> ::= pow | min | max
		<var>  ::= x | y | z
		<num>  ::= 3.123312 | 6.345345 | 23.43e-03 | 1.4e3
		"""
	);
	 */


	public static void main(final String[] args) {
		final var random = RandomGenerator.getDefault();
		final var sentences = Stream.generate(() -> sentence(random))
			.limit(150)
			.toList();

		System.out.println("public static final List<String> EXPRESSIONS = List.of(");
		System.out.println(
			sentences.stream()
				.collect(Collectors.joining("\",\n\t\"", "\t\"", "\""))
		);
		System.out.println(");");
		System.out.println();

		System.out.println("public static final List<Fun3> FUNCTIONS = List.of(");
		System.out.println(
			IntStream.range(0, sentences.size())
				.mapToObj("MathExprTestData::fun_%d"::formatted)
				.collect(Collectors.joining(",\n\t", "\t", ""))
		);
		System.out.println(");");
		System.out.println();

		for (int i = 0; i < sentences.size(); ++i) {
			System.out.println(toFun(i, sentences.get(i)));
		}
	}

	private static String toFun(final int index, final String expr) {
		return """
			private static double fun_%d(final double x, final double y, final double z) {
				return %s;
			}
			""".formatted(index, expr);
	}

	private static String sentence(final RandomGenerator random) {
		String expr = "";
		/*
		do {
			expr = Sentence.generate(GRAMMAR, SymbolIndex.of(random), 1000).stream()
				.map(Cfg.Symbol::value)
				.collect(Collectors.joining());
		} while (expr.length() < 250);
		 */

		return expr;
	}

}
