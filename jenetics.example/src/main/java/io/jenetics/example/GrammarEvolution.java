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

import io.jenetics.IntegerGene;
import io.jenetics.engine.Codec;
import io.jenetics.util.IntRange;

import io.jenetics.ext.grammar.Bnf;
import io.jenetics.ext.grammar.Cfg;
import io.jenetics.ext.grammar.Cfg.Symbol;
import io.jenetics.ext.grammar.Mappers;
import io.jenetics.ext.grammar.SentenceGenerator;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.1
 * @since 7.1
 */
public class GrammarEvolution {

	private static final Cfg<String> GRAMMAR = Bnf.parse("""
		<expr> ::= x | <num> | <expr> <op> <expr>
		<op>   ::= + | - | * | /
		<num>  ::= 2 | 3 | 4
		"""
	);

	private static final Codec<String, IntegerGene> CODEC = Mappers
		.multiIntegerChromosomeMapper(
			GRAMMAR,
			rule -> IntRange.of(rule.alternatives().size()*25),
			index -> new SentenceGenerator<>(index, 50)
		)
		.map(terminals -> terminals.stream().map(Symbol::name).collect(joining()));


	public static void main(final String[] args) {

	}

}
