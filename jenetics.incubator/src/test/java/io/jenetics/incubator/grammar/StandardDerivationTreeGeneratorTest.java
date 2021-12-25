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

import static io.jenetics.incubator.grammar.StandardSentenceGeneratorTest.CFG;

import java.util.Random;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import io.jenetics.incubator.grammar.Cfg.Symbol;

import io.jenetics.ext.util.TreeFormatter;
import io.jenetics.ext.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class StandardDerivationTreeGeneratorTest {

	@Test
	public void create() {
		final var seed = 29022156195143L;
		final var random = new Random(seed);

		final String sentence = Sentence.generate(CFG, SymbolIndex.of(random), 100).stream()
			.map(Symbol::value)
			.collect(Collectors.joining());

		System.out.println(sentence);

		random.setSeed(seed);
		final var generator = new StandardDerivationTreeGenerator(SymbolIndex.of(random));
		final TreeNode<String> tree = generator.generate(CFG)
		//final TreeNode<String> tree = ParseTree.apply(CFG, SymbolIndex.of(random))
			.map(Symbol::value);

		System.out.println(TreeFormatter.TREE.format(tree));
	}

}
