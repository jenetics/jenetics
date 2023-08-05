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
package io.jenetics.ext.internal.util;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.jenetics.ext.internal.parser.Parser;
import io.jenetics.ext.internal.parser.ParsingException;
import io.jenetics.ext.util.TreeNode;

/**
 * This class allows you to convert a sequence of <em>tokens</em>, which
 * represents some kind of (mathematical) formula, into a tree structure. To do
 * this, it is assumed that the given tokens can be categorised. The two main
 * categories are <em>structural</em> tokens and <em>operational</em> tokens.
 *
 * <p><b>Structural tokens</b></p>
 * Structural tokens are used to influence the hierarchy of the parsed tokens
 * and are also part of function definitions. This kind of tokens will not be
 * part of the generated tree representation.
 * <ol>
 *     <li><em>lparen</em>: Represents left parentheses, which starts
 *     sub-trees or opens function argument lists.</li>
 *     <li><em>rparen</em>: Represents right parentheses, which closes
 *     sub-trees or function argument lists. <em>lparen</em> and
 *     <em>rparen</em> must be balanced.</li>
 *     <li><em>comma</em>: Separator token for function arguments.</li>
 * </ol>
 *
 * <p><b>Operational tokens</b></p>
 * Operational tokens define the actual <em>behaviour</em> of the created tree.
 * <ol>
 *     <li><em>identifier</em>: This kind of tokens usually represents variable
 *     names or numbers.</li>
 *     <li><em>function</em>: Function tokens represents identifiers for
 *     functions. Valid functions have the following form: {@code 'fun' 'lparen'
 *     arg ['comma' args]* 'rparen'}</li>
 *     <li><em>binary operator</em>: Binary operators are defined in infix
 *     order and have a precedence. Typical examples are the arithmetic
 *     operators '+' and '*', where the '*' have a higher precedence than '+'.</li>
 *     <li><em>unary operator</em>: Unary operators are prefix operators. A
 *     typical example is the arithmetic negation operator '-'. Unary
 *     operators have all the same precedence, which is higher than the
 *     precedence of all binary operators.</li>
 * </ol>
 *
 * This class is only responsible for the parsing step. The tokenization must
 * be implemented separately. Another possible token source would be a generating
 * grammar, where the output is already a list of tokens (aka sentence). The
 * following example parser can be used to parse arithmetic expressions.
 *
 * <pre>{@code
 * final FormulaParser<String> parser = FormulaParser.<String>builder()
 *     // Structural tokens.
 *     .lparen("(")
 *     .rparen(")")
 *     .separator(",")
 *     // Operational tokens.
 *     .unaryOperators("+", "-")
 *     .binaryOperators(ops -> ops
 *         .add(11, "+", "-")
 *         .add(12, "*", "/")
 *         .add(14, "^", "**"))
 *     .identifiers("x", "y", "z")
 *     .functions("pow", "sin", "cos")
 *     .build();
 * }</pre>
 * This parser allows you to parse the following token list
 * <pre>{@code
 * final List<String> tokens = List.of(
 *     "x", "*", "x", "+", "sin", "(", "z", ")", "-", "cos", "(", "x",
 *     ")", "+", "y", "/", "z", "-", "pow", "(", "z", ",", "x", ")"
 * );
 * final Tree<String, ?> tree = parser.parse(tokens);
 * }</pre>
 * which will result in the following parsed tree:
 * <pre>{@code
 * "-"
 * ├── "+"
 * │   ├── "-"
 * │   │   ├── "+"
 * │   │   │   ├── "*"
 * │   │   │   │   ├── "x"
 * │   │   │   │   └── "x"
 * │   │   │   └── "sin"
 * │   │   │       └── "z"
 * │   │   └── "cos"
 * │   │       └── "x"
 * │   └── "/"
 * │       ├── "y"
 * │       └── "z"
 * └── "pow"
 *     ├── "z"
 *     └── "x"
 * }</pre>
 * Note that the generated (parsed) tree is of type {@code Tree<String, ?>}. To
 * <em>evaluate</em> this tree, additional steps are necessary. If you want to
 * create an <em>executable</em> tree, you have to use the
 * {@link #parse(Iterable, TokenConverter)} function for parsing the tokens.
 * <p>
 * The following code snippet shows how to create an <em>executable</em> AST
 * from a token list. The {@code MathExpr} class in the {@code io.jenetics.prog}
 * module uses a similar {@link TokenConverter}.
 * <pre>{@code
 * final Tree<Op<Double>, ?> tree = formula.parse(
 *     tokens,
 *     (token, type) -> switch (token) {
 *         case "+" -> type == TokenType.UNARY_OPERATOR ? MathOp.ID : MathOp.ADD;
 *         case "-" -> type == TokenType.UNARY_OPERATOR ? MathOp.NEG : MathOp.SUB;
 *         case "*" -> MathOp.MUL;
 *         case "/" -> MathOp.DIV;
 *         case "^", "**", "pow" -> MathOp.POW;
 *         case "sin" -> MathOp.SIN;
 *         case "cos" -> MathOp.COS;
 *         default -> type == TokenType.IDENTIFIER
 *             ? Var.of(token);
 *             : throw new IllegalArgumentException("Unknown token: " + token);
 *     }
 * );
 * }</pre>
 *
 * @param <T> the token type used as input for the parser
 *
 * @implNote
 * This class is immutable and thread-safe.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 7.1
 * @version 7.1
 */
public final class FormulaParser<T> {

	/**
	 * The token types the parser recognizes during the parsing process.
	 */
	public enum TokenType {

		/**
		 * Indicates an unary operator.
		 */
		UNARY_OPERATOR,

		/**
		 * Indicates a binary operator.
		 */
		BINARY_OPERATOR,

		/**
		 * Indicates a function token.
		 */
		FUNCTION,

		/**
		 * Indicates an identifier token.
		 */
		IDENTIFIER
	}

	/**
	 * Conversion function which is used for converting tokens into another
	 * type.
	 *
	 * @param <T> the token type
	 * @param <V> the converted value type
	 */
	@FunctionalInterface
	public interface TokenConverter<T, V> {

		/**
		 * Convert the given {@code token} into another value. The conversion
		 * can use the token type, recognized during the parsing process.
		 *
		 * @param token the token value to convert
		 * @param type the token type, recognized during the parsing process
		 * @return the converted value
		 */
		V convert(final T token, final TokenType type);
	}


	private final Predicate<? super T> _lparen;
	private final Predicate<? super T> _rparen;
	private final Predicate<? super T> _separator;
	private final Predicate<? super T> _uops;
	private final Predicate<? super T> _identifiers;
	private final Predicate<? super T> _functions;

	// The processed binary operators.
	private final Term<T> _term;

	/**
	 * Creates a new general expression parser object. The parser is not bound
	 * to a specific source and target type or concrete token types.
	 *
	 * @param lparen the token type specifying the left parentheses, '('
	 * @param rparen the token type specifying the right parentheses, ')'
	 * @param separator the token type specifying the function parameter
	 *        separator, ','
	 * @param bops the list of binary operators, according its
	 *        precedence. The first list element contains the operations with
	 *        the lowest precedence and the last list element contains the
	 *        operations with the highest precedence.
	 * @param uops the token types representing the unary operations
	 * @param identifiers the token type representing identifier, like variable
	 *        names, constants or numbers
	 * @param functions predicate which tests whether a given identifier value
	 *        represents a known function name
	 */
	private FormulaParser(
		final Predicate<? super T> lparen,
		final Predicate<? super T> rparen,
		final Predicate<? super T> separator,
		final List<? extends Predicate<? super T>> bops,
		final Predicate<? super T> uops,
		final Predicate<? super T> identifiers,
		final Predicate<? super T> functions
	) {
		_lparen = requireNonNull(lparen);
		_rparen = requireNonNull(rparen);
		_separator = requireNonNull(separator);
		_uops = requireNonNull(uops);
		_identifiers = requireNonNull(identifiers);
		_functions = requireNonNull(functions);

		final Term<T> oterm = BopTerm.build(bops);
		final Term<T> fterm = new Term<>() {
			@Override
			<V> TreeNode<V> term(
				final Parser<T> parser,
				final TokenConverter<? super T, ? extends V> mapper
			) {
				return function(parser, mapper);
			}
		};
		if (oterm != null) {
			oterm.append(fterm);
			_term = oterm;
		} else {
			_term = fterm;
		}
	}

	private <V> TreeNode<V> function(
		final Parser<T> parser,
		final TokenConverter<? super T, ? extends V> mapper
	) {
		final var token = parser.LT(1);

		if (_functions.test(token)) {
			parser.consume();
			final TreeNode<V> node = TreeNode
				.of(mapper.convert(token, TokenType.FUNCTION));

			parser.match(_lparen);
			node.attach(_term.expr(parser, mapper));
			while (_separator.test(parser.LT(1))) {
				parser.consume();
				node.attach(_term.expr(parser, mapper));
			}
			parser.match(_rparen);

			return node;
		} else if (_lparen.test(token)) {
			parser.consume();
			final TreeNode<V> node = _term.expr(parser, mapper);
			parser.match(_rparen);
			return node;
		} else {
			return unary(() -> atom(parser, mapper), parser, mapper);
		}
	}

	private <V> TreeNode<V> atom(
		final Parser<T> parser,
		final TokenConverter<? super T, ? extends V> mapper
	) {
		final var token = parser.LT(1);

		if (_identifiers.test(token)) {
			parser.consume();
			return TreeNode.of(mapper.convert(token, TokenType.IDENTIFIER));
		} else if (token == null) {
			throw new ParsingException("Unexpected end of input.");
		} else {
			throw new ParsingException(
				"Unexpected symbol found: %s.".formatted(parser.LT(1))
			);
		}
	}

	private <V> TreeNode<V> unary(
		final Supplier<TreeNode<V>> other,
		final Parser<T> parser,
		final TokenConverter<? super T, ? extends V> mapper
	) {
		final var token = parser.LT(1);

		if (_uops.test(token)) {
			parser.consume();
			return TreeNode
				.<V>of(mapper.convert(token, TokenType.UNARY_OPERATOR))
				.attach(other.get());
		} else {
			return other.get();
		}
	}

	/**
	 * Parses the given token sequence according {@code this} formula definition.
	 * If the given {@code tokens} supplier returns null, no further token is
	 * available.
	 *
	 * @param tokens the tokens which forms the formula
	 * @param mapper the mapper function which maps the token type to the parse
	 *        tree value type
	 * @return the parsed formula as tree
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if the given {@code tokens} can't be
	 *         parsed
	 */
	public <V> TreeNode<V> parse(
		final Supplier<? extends T> tokens,
		final TokenConverter<? super T, ? extends V> mapper
	) {
		requireNonNull(tokens);
		requireNonNull(mapper);

		return _term.expr(new Parser<T>(tokens::get, 1), mapper);
	}

	/**
	 * Parses the given token sequence according {@code this} formula definition.
	 * If the given {@code tokens} supplier returns null, no further token is
	 * available.
	 *
	 * @param tokens the tokens which forms the formula
	 * @return the parsed formula as tree
	 * @throws NullPointerException if the arguments is {@code null}
	 * @throws IllegalArgumentException if the given {@code tokens} can't be
	 *         parsed
	 */
	public TreeNode<T> parse(final Supplier<? extends T> tokens) {
		return parse(tokens, (token, type) -> token);
	}

	/**
	 * Parses the given token sequence according {@code this} formula definition.
	 *
	 * @param tokens the tokens which forms the formula
	 * @param mapper the mapper function which maps the token type to the parse
	 *        tree value type
	 * @return the parsed formula as tree
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if the given {@code tokens} can't be
	 *         parsed
	 */
	public <V> TreeNode<V> parse(
		final Iterable<? extends T> tokens,
		final TokenConverter<? super T, ? extends V> mapper
	) {
		final var it = tokens.iterator();
		return parse(() -> it.hasNext() ? it.next() : null, mapper);
	}

	/**
	 * Parses the given token sequence according {@code this} formula definition.
	 *
	 * @param tokens the tokens which forms the formula
	 * @return the parsed formula as tree
	 * @throws NullPointerException if the arguments is {@code null}
	 * @throws IllegalArgumentException if the given {@code tokens} can't be
	 *         parsed
	 */
	public TreeNode<T> parse(final Iterable<? extends T> tokens) {
		return parse(tokens, (token, type) -> token);
	}

	/**
	 * Return a new builder class for building new formula parsers.
	 *
	 * @param <T> the token type
	 * @return a new formula parser builder
	 */
	public static <T> Builder<T> builder() {
		return new Builder<>();
	}


	/* *************************************************************************
	 * FormulaParser helper classes
	 * ************************************************************************/

	/**
	 * General term object to be parsed.
	 *
	 * @param <T> the token value type used as input for the parser
	 */
	private static abstract class Term<T> {
		Term<T> _next;
		Term<T> _last;

		<V> TreeNode<V> op(
			final TreeNode<V> expr,
			final Parser<T> parser,
			final TokenConverter<? super T, ? extends V> mapper
		) {
			return expr;
		}

		abstract <V> TreeNode<V> term(
			final Parser<T> parser,
			final TokenConverter<? super T, ? extends V> mapper
		);

		<V> TreeNode<V> expr(
			final Parser<T> parser,
			final TokenConverter<? super T, ? extends V> mapper
		) {
			return op(term(parser, mapper), parser, mapper);
		}

		void append(final Term<T> term) {
			if (_next == null) {
				_next = term;
				_last = term;
			} else {
				_last.append(term);
			}
		}
	}

	/**
	 * Represents a binary (mathematical) operation.
	 *
	 * @param <T> the token value type used as input for the parser
	 */
	private static class BopTerm<T> extends Term<T> {
		private final Predicate<? super T> _tokens;

		BopTerm(final Predicate<? super T> tokens) {
			_tokens = requireNonNull(tokens);
		}

		@Override
		<V> TreeNode<V> op(
			final TreeNode<V> expr,
			final Parser<T> parser,
			final TokenConverter<? super T, ? extends V> mapper
		) {
			var result = expr;

			final var token = parser.LT(1);
			if (token != null && _tokens.test(token)) {
				parser.consume();

				final TreeNode<V> node = TreeNode
					.<V>of(mapper.convert(token, TokenType.BINARY_OPERATOR))
					.attach(expr)
					.attach(term(parser, mapper));

				result = op(node, parser, mapper);
			}
			return result;
		}

		@Override
		<V> TreeNode<V> term(
			final Parser<T> parser,
			final TokenConverter<? super T, ? extends V> mapper
		) {
			return _next.op(_next.term(parser, mapper), parser, mapper);
		}

		/**
		 * Builds a linked chain of binary operations. Operations with lower
		 * <em>precedence</em> are at the beginning of the chain and operations
		 * with higher <em>precedence</em> are appended to the end of the linked
		 * operation term chain.
		 *
		 * @param bops the list of binary operations with a given precedence
		 * @param <T> the token value type used as input for the parser
		 * @return the linked operation term
		 */
		static <T> BopTerm<T> build(final List<? extends Predicate<? super T>> bops) {
			BopTerm<T> start = null;
			for (var tokens : bops) {
				final BopTerm<T> term = new BopTerm<>(tokens);
				if (start == null) {
					start = term;
				} else {
					start.append(term);
				}
			}

			return start;
		}
	}

	/* *************************************************************************
	 * FormulaParser builder class
	 * ************************************************************************/


	/**
	 * Builder for building new {@link FormulaParser} instances.
	 *
	 * @param <T> the token type
	 */
	public static final class Builder<T> {

		private Predicate<? super T> _lparen = token -> false;
		private Predicate<? super T> _rparen = token -> false;
		private Predicate<? super T> _separator = token -> false;
		private List<? extends Predicate<? super T>> _bops = List.of();
		private Predicate<? super T> _uops = token -> false;
		private Predicate<? super T> _identifiers = token -> false;
		private Predicate<? super T> _functions = token -> false;


		private Builder() {
		}

		/**
		 * Set the predicate which defines {@code lparen} tokens. If the given
		 * predicate returns {@code true} for a token, it is treated as
		 * <em>lparen</em>.
		 *
		 * @param lparen the {@code lparen} token
		 * @return {@code this} builder, for method chaining
		 * @throws NullPointerException if the {@code lparen} is {@code null}
		 */
		public Builder<T> lparen(final Predicate<? super T> lparen) {
			_lparen = requireNonNull(lparen);
			return this;
		}

		/**
		 * Set the <em>prototype</em> for the {@code lparen} token. A given
		 * token is treated as  {@code lparen} if {@code Objects.equals(token, lparen)}
		 * returns {@code true}.
		 *
		 * @param lparen the {@code lparen} prototype
		 * @return {@code this} builder, for method chaining
		 */
		public Builder<T> lparen(final T lparen) {
			return lparen(token -> Objects.equals(token, lparen));
		}

		/**
		 * Set the predicate which defines {@code rparen} tokens. If the given
		 * predicate returns {@code true} for a token, it is treated as
		 * <em>rparen</em>.
		 *
		 * @param rparen the {@code rparen} token
		 * @return {@code this} builder, for method chaining
		 * @throws NullPointerException if the {@code rparen} is {@code null}
		 */
		public Builder<T> rparen(final Predicate<? super T> rparen) {
			_rparen = requireNonNull(rparen);
			return this;
		}

		/**
		 * Set the <em>prototype</em> for the {@code rparen} token. A given
		 * token is treated as  {@code rparen} if {@code Objects.equals(token, rparen)}
		 * returns {@code true}.
		 *
		 * @param rparen the {@code rparen} prototype
		 * @return {@code this} builder, for method chaining
		 */
		public Builder<T> rparen(final T rparen) {
			return rparen(token -> Objects.equals(token, rparen));
		}

		/**
		 * Set the predicate which defines {@code separator} tokens. If the given
		 * predicate returns {@code true} for a token, it is treated as
		 * <em>separator</em>.
		 *
		 * @param separator the {@code separator} token
		 * @return {@code this} builder, for method chaining
		 * @throws NullPointerException if the {@code separator} is {@code null}
		 */
		public Builder<T> separator(final Predicate<? super T> separator) {
			_separator = requireNonNull(separator);
			return this;
		}

		/**
		 * Set the <em>prototype</em> for the {@code separator} token. A given
		 * token is treated as  {@code separator} if {@code Objects.equals(token, separator)}
		 * returns {@code true}.
		 *
		 * @param separator the {@code separator} prototype
		 * @return {@code this} builder, for method chaining
		 */
		public Builder<T> separator(final T separator) {
			return separator(token -> Objects.equals(token, separator));
		}

		/**
		 * Set the predicate which defines the unary operator tokens. If the
		 * given predicate returns {@code true} for a token, it is treated as
		 * unary operator.
		 *
		 * @param ops the {@code comma} token
		 * @return {@code this} builder, for method chaining
		 * @throws NullPointerException if the {@code ops} is {@code null}
		 */
		public Builder<T> unaryOperators(final Predicate<? super T> ops) {
			_uops = requireNonNull(ops);
			return this;
		}

		/**
		 * Set all unary operator tokens.
		 *
		 * @param ops the unary operator tokens
		 * @return {@code this} builder, for method chaining
		 * @throws NullPointerException if the {@code ops} is {@code null}
		 */
		public Builder<T> unaryOperators(final Set<? extends T> ops) {
			return unaryOperators(Set.copyOf(ops)::contains);
		}

		/**
		 * Set all unary operator tokens.
		 *
		 * @param ops the unary operator tokens
		 * @return {@code this} builder, for method chaining
		 * @throws NullPointerException if the {@code ops} is {@code null}
		 */
		@SafeVarargs
		public final Builder<T> unaryOperators(final T... ops) {
			return unaryOperators(Set.of(ops));
		}

		/**
		 * Set the list of predicates which defines the binary ops. The
		 * predicate indexes of the list represents the precedence of the binary
		 * ops. {@code ops.get(0)} has the lowest precedence and
		 * {@code ops.get(ops.size() - 1)} has the highest precedence
		 *
		 * @param ops the predicates defining the binary operator tokens
		 * @return {@code this} builder, for method chaining
		 * @throws NullPointerException if the {@code ops} is {@code null}
		 */
		public Builder<T> binaryOperators(final List<? extends Predicate<? super T>> ops) {
			_bops = List.copyOf(ops);
			return this;
		}

		/**
		 * Set the list of predicates which defines the binary ops. The
		 * predicate indexes of the list represents the precedence of the binary
		 * ops. {@code ops.get(0)} has the lowest precedence and
		 * {@code ops.get(ops.size() - 1)} has the highest precedence
		 *
		 * @param ops the predicates defining the binary operator tokens
		 * @return {@code this} builder, for method chaining
		 * @throws NullPointerException if the {@code ops} is {@code null}
		 */
		@SafeVarargs
		public final Builder<T> binaryOperators(final Predicate<? super T>... ops) {
			_bops = List.of(ops);
			return this;
		}

		/**
		 * Method for defining the binary operators and its precedence.
		 *
		 * @param ops the predicates defining the binary operator tokens
		 * @return {@code this} builder, for method chaining
		 */
		public Builder<T> binaryOperators(final Consumer<? super Bops<T>> ops) {
			final var builder = new Bops<T>();
			ops.accept(builder);
			_bops = builder.build();
			return this;
		}

		/**
		 * Set the predicate which defines identifier tokens.
		 *
		 * @param identifiers the identifier predicate
		 * @return {@code this} builder, for method chaining
		 * @throws NullPointerException if the {@code identifiers} is {@code null}
		 */
		public Builder<T> identifiers(final Predicate<? super T> identifiers) {
			_identifiers = requireNonNull(identifiers);
			return this;
		}

		/**
		 * Set all identifier tokens.
		 *
		 * @param identifiers the identifier tokens
		 * @return {@code this} builder, for method chaining
		 * @throws NullPointerException if the {@code identifiers} is {@code null}
		 */
		public Builder<T> identifiers(final Set<? extends T> identifiers) {
			return identifiers(Set.copyOf(identifiers)::contains);
		}

		/**
		 * Set all identifier tokens.
		 *
		 * @param identifiers the identifier tokens
		 * @return {@code this} builder, for method chaining
		 * @throws NullPointerException if the {@code identifiers} is {@code null}
		 */
		@SafeVarargs
		public final Builder<T> identifiers(final T... identifiers) {
			return identifiers(Set.of(identifiers));
		}

		/**
		 * Set the predicate which defines function tokens.
		 *
		 * @param functions the function predicate
		 * @return {@code this} builder, for method chaining
		 * @throws NullPointerException if the {@code functions} is {@code null}
		 */
		public Builder<T> functions(final Predicate<? super T> functions) {
			_functions = requireNonNull(functions);
			return this;
		}

		/**
		 * Set all functions tokens.
		 *
		 * @param functions the functions tokens
		 * @return {@code this} builder, for method chaining
		 * @throws NullPointerException if the {@code functions} is {@code null}
		 */
		public Builder<T> functions(final Set<? extends T> functions) {
			return functions(Set.copyOf(functions)::contains);
		}

		/**
		 * Set all functions tokens.
		 *
		 * @param functions the functions tokens
		 * @return {@code this} builder, for method chaining
		 * @throws NullPointerException if the {@code functions} is {@code null}
		 */
		@SafeVarargs
		public final Builder<T> functions(final T... functions) {
			return functions(Set.of(functions));
		}

		/**
		 * Create a new formula parser with the defined values.
		 *
		 * @return a new formula parser
		 */
		public FormulaParser<T> build() {
			return new FormulaParser<>(
				_lparen,
				_rparen,
				_separator,
				_bops,
				_uops,
				_identifiers,
				_functions
			);
		}

		/**
		 * Builder class for building binary operators with its precedence.
		 *
		 * @param <T> the token type
		 */
		public static final class Bops<T> {
			private final Map<Integer, Predicate<? super T>> _operations = new HashMap<>();

			private Bops() {
			}

			/**
			 * Add a new operator predicate with its precedence.
			 *
			 * @param precedence the precedence of the operators
			 * @param operators the operators predicate
			 * @return {@code this} builder, for method chaining
			 */
			public Bops<T> add(
				final int precedence,
				final Predicate<? super T> operators
			) {
				Predicate<? super T> ops = _operations.get(precedence);
				if (ops != null) {
					final Predicate<? super T> prev = ops;
					ops = token -> prev.test(token) || operators.test(token);
				} else {
					ops = operators;
				}
				_operations.put(precedence, ops);

				return this;
			}

			/**
			 * Add a new operator tokens with its precedence.
			 *
			 * @param precedence the precedence of the operators
			 * @param operators the operators
			 * @return {@code this} builder, for method chaining
			 */
			@SafeVarargs
			public final Bops<T> add(
				final int precedence,
				final T... operators
			) {
				return add(precedence, Set.of(operators)::contains);
			}

			private List<? extends Predicate<? super T>> build() {
				return _operations.entrySet().stream()
					.sorted(Entry.comparingByKey())
					.map(Entry::getValue)
					.toList();
			}

		}
	}

}
