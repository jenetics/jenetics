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
package io.jenetics.incubator.util;

import java.util.List;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public final class Grammar {

	public sealed interface Symbol {
		String name();
	}

	public static record Terminal(String name) implements Symbol {
	}

	public static record NonTerminal(String name) implements Symbol {
	}

	public static record Expression(List<Symbol> symbols) {
		public Expression {
			symbols = List.copyOf(symbols);
		}
	}

	public static record Rule(NonTerminal start, List<Expression> alternatives) {
		public Rule {
			alternatives = List.copyOf(alternatives);
		}
	}

	//private NonTerminal start;
	//private List<NonTerminal> nonTerminals;
	//private List<Terminal> terminals;

	private final List<Rule> rules;

	public Grammar(final List<Rule> rules) {
		this.rules = List.copyOf(rules);
	}

	public List<Rule> rules() {
		return rules;
	}

	public static Grammar parse(final String bnf) {

		return null;
	}

}
