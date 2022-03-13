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
package io.jenetics.incubator.prog;

import java.util.List;

import io.jenetics.ext.grammar.Cfg.Terminal;

import io.jenetics.ext.util.Tree;

import io.jenetics.prog.op.Op;

/**
 * Helper method for converting a <em>generated</em> mathematical expression,
 * which is given in the form a list of terminal symbols, into an AST of
 * mathematical operations.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 7.0
 * @version 7.0
 */
public final class MathSentence {

	private MathSentence() {
	}

	/**
	 * Converts the given <em>sentence</em> into an AST of mathematical
	 * operations.
	 *
	 * @param sentence the sentence to parse
	 * @return the parsed sentence
	 */
	public static Tree<Op<Double>, ?> parse(final List<Terminal<String>> sentence) {
		//final Tokenizer<Token<String>> tokenizer = new MathSentenceTokenizer(sentence);
		//return MathExpr.parseTree(tokenizer::next);
		return null;
	}

}
