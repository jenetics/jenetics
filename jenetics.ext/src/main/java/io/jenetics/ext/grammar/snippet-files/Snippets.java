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

import static io.jenetics.ext.grammar.Cfg.E;
import static io.jenetics.ext.grammar.Cfg.N;
import static io.jenetics.ext.grammar.Cfg.R;
import static io.jenetics.ext.grammar.Cfg.T;

class Snippets {

	static class CfgSnippets {

		void parseBnf() {
			// @start region="parseBnf"
			final Cfg<String> cfg = Bnf.parse("""
				<expr> ::= <num> | <var> | '(' <expr> <op> <expr> ')'
				<op>   ::= + | - | * | /
				<var>  ::= x | y
				<num>  ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
				"""
			);
			// @end
		}

		void buildWithoutBuilder() {
			// @start region="cfgWithoutBuilder"
			final Cfg<String> cfg = Cfg.of(
				R("expr",
					N("num"),
					N("var"),
					E(T("("), N("expr"), N("op"), N("expr"), T(")"))
				),
				R("op", T("+"), T("-"), T("*"), T("/")),
				R("var", T("x"), T("y")),
				R("num",
					T("0"), T("1"), T("2"), T("3"),
					T("4"), T("5"), T("6"), T("7"),
					T("8"), T("9")
				)
			);
			// @end
		}

		void buildWithBuilder() {
			// @start region="cfgWithBuilder"
			final Cfg<String> cfg = Cfg.<String>builder()
				.R(N("expr", "Rule start annotation"), rule -> rule
					.N("num", "Non-terminal annotation 1")
					.N("var", "Non-terminal annotation 2")
					.E(exp -> exp
						.add(T("(").at("Terminal annotation"))
						.N("expr").N("op").N("expr")
						.T(")")
						.at("Expression annotation")))
				.R("op", rule -> rule.T("+").T("-").T("*").T("/"))
				.R("var", rule -> rule.T("x").T("y"))
				.R("num", rule -> rule
					.T("0").T("1").T("2").T("3").T("4")
					.T("5").T("6").T("7").T("8").T("9")
					.at("Rule annotation")
				)
				.build();
			// @end
		}
	}

}
