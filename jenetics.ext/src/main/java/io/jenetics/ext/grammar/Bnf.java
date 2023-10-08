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

import static java.lang.Character.isDigit;
import static java.lang.Character.isWhitespace;
import static java.lang.StringTemplate.RAW;
import static io.jenetics.ext.internal.parser.CharSequenceTokenizer.isAlphabetic;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import io.jenetics.ext.internal.parser.ParsingException;

/**
 * This class contains methods for parsing and formatting <em>context-free</em>
 * grammars in
 * <a href="https://en.wikipedia.org/wiki/Backus%E2%80%93Naur_form">BNF</a>
 * format.
 * {@snippet lang="java":
 * final Cfg<String> grammar = Bnf.parse("""
 *     <expr> ::= <num> | <var> | '(' <expr> <op> <expr> ')'
 *     <op>   ::= + | - | * | /
 *     <var>  ::= x | y
 *     <num>  ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
 *     """
 * );
 * }
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 7.1
 * @version 7.1
 */
public final class Bnf {

	public static final StringTemplate.Processor<Cfg<?>, RuntimeException>
	BNF = template -> {
		final List<String> fragments = template.fragments();
		final List<Object> values = template.values();

		if (values.isEmpty()) {
			return Bnf.parse(fragments.getFirst());
		}

		final var bnf = new StringBuilder();
		bnf.append(fragments.get(0));

		final var terminals = new HashMap<String, Cfg.Terminal<?>>();

		for (int i = 0; i < values.size(); ++i) {
			final var value = values.get(i);

			switch (value) {
				case String string -> bnf.append(string);
				case Cfg.NonTerminal<?> nt -> bnf.append(format(nt));
				case Cfg.Terminal<?> t -> {
					final var name = UUID.randomUUID().toString();
					terminals.put(name, t);
					bnf.append(name);
				}
				case Object object -> {
					final var name = UUID.randomUUID().toString();
					terminals.put(name, Cfg.T(object));
					bnf.append(name);
				}
			}

			bnf.append(fragments.get(i + 1));
		}

		return Bnf.parse(bnf)
			.flatMap(t -> terminals.getOrDefault(t.name(), t));
	};

	private Bnf() {}

	static boolean isSymbolChar(final int ch) {
		return switch (ch) {
			case '<', '>', '|', ':', '=' -> true;
			default -> false;
		};
	}

	void foo(StringTemplate template) {

	}

	void bar() {
		foo(RAW."");
	}

	static boolean isStringChar(final char c) {
		return !isWhitespace(c) && !isSymbolChar(c);
	}

	static boolean isIdChar(final char c) {
		return isAlphabetic(c) || isDigit(c) || (c == '-');
	}

	/**
	 * Parses the given BNF {@code grammar} string to a {@link Cfg} object. The
	 * following example shows the grammar of a simple arithmetic expression.
	 *
	 * <pre>{@code
	 * <expr> ::= <num> | <var> | '(' <expr> <op> <expr> ')'
	 * <op>   ::= + | - | * | /
	 * <var>  ::= x | y
	 * <num>  ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
	 * }</pre>
	 *
	 * @param grammar the BNF {@code grammar} string
	 * @return the parsed {@code BNF} object
	 * @throws ParsingException if the given <em>grammar</em> is invalid
	 * @throws NullPointerException it the given {@code grammar} string is
	 *         {@code null}
	 */
	public static Cfg<String> parse(final CharSequence grammar) {
		final var tokenizer = new BnfTokenizer(grammar);
		final var parser = new BnfParser(tokenizer);

		return parser.parse();
	}

	/**
	 * Formats the given <em>CFG</em> as BNF grammar string.
	 *
	 * @param grammar the CFG to format as BNF
	 * @return the BNF formatted grammar string
	 * @throws NullPointerException if the give {@code grammar} is {@code null}
	 */
	public static String format(final Cfg<?> grammar) {
		return grammar.rules().stream()
			.map(Bnf::format)
			.collect(Collectors.joining("\n"));
	}

	private static String format(final Cfg.Rule<?> rule) {
		return String.format(
			"%s ::= %s",
			format(rule.start()),
			rule.alternatives().stream()
				.map(Bnf::format)
				.collect(Collectors.joining("\n    | "))
		);
	}

	private static String format(final Cfg.Expression<?> expr) {
		return expr.symbols().stream()
			.map(Bnf::format)
			.collect(Collectors.joining(" "));
	}

	private static String format(final Cfg.Symbol<?> symbol) {
		if (symbol instanceof Cfg.NonTerminal<?> nt) {
			return String.format("<%s>", nt.name());
		} else if (symbol instanceof Cfg.Terminal<?> t) {
			return "'" + t.name()
				.replace("\\", "\\\\")
				.replace("'", "\\'") + "'";
		}
		throw new AssertionError();
	}

}
