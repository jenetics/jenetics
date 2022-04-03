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
import static io.jenetics.ext.grammar.SentenceGeneratorTest.CFG;

import java.util.Random;

import org.testng.annotations.Test;

import io.jenetics.ext.grammar.Cfg.Symbol;
import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class DerivationTreeGeneratorTest {

	@Test
	public void generate() {
		final var seed = 29022156195143L;
		final var random = new Random(seed);

		var generator = new DerivationTreeGenerator<String>(
			SymbolIndex.of(random),
			MAX_VALUE
		);

		final Tree<String, ?> tree = TreeNode.ofTree(generator.generate(CFG))
			.map(Symbol::name);

		assertThat(tree).isEqualTo(
			TreeNode.parse(
				"expr(fun(FUN1),\\(,arg(var(y)),\\,,arg(expr(fun(FUN1),\\(,arg" +
					"(var(x)),\\,,arg(expr(fun(FUN1),\\(,arg(expr(\\(,expr(num(3))," +
					"op(/),expr(var(y)),\\))),\\,,arg(expr(\\(,expr(\\(,expr(\\(" +
					",expr(fun(FUN1),\\(,arg(expr(fun(FUN1),\\(,arg(var(y)),\\,," +
					"arg(num(9)),\\))),\\,,arg(var(y)),\\)),op(+),expr(\\(,expr" +
					"(num(4)),op(/),expr(var(x)),\\)),\\)),op(-),expr(fun(FUN2)," +
					"\\(,arg(var(y)),\\,,arg(var(x)),\\)),\\)),op(*),expr(var(y))," +
					"\\))),\\))),\\))),\\))")
		);
	}

}
