package io.jenetics.incubator.grammar;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import io.jenetics.incubator.grammar.Grammar.Symbol;
import io.jenetics.incubator.grammar.Grammar.Terminal;

import io.jenetics.ext.util.TreeNode;

public class SymbolListParser {

	static final class Token {
		private Token() {}

		static final int EPSILON = 0;
		static final int PLUS = 1;
		static final int MINUS = 2;
		static final int MUL = 3;
		static final int DIV = 4;
		static final int MOD = 5;
		static final int POWER = 6;
		static final int FUNCTION = 7;
		static final int OPEN_BRACKET = 8;
		static final int CLOSE_BRACKET = 9;
		static final int NUMBER = 10;
		static final int VARIABLE = 11;
		static final int COMMA = 12;
	}

	private static final String EPSILON = "";
	private static final String PLUS = "+";
	private static final String MINUS = "-";
	private static final String MUL = "*";
	private static final String DIV = "/";
	private static final String MOD = "%";
	private static final String POWER = "^";
	private static final String COMMA = ",";
	private static final String OPEN_BRACKET = "(";
	private static final String CLOSE_BRACKET = ")";

	private static final Map<Integer, List<String>> PRECEDENCE = Map.of(
		6, List.of("^"),
		11, List.of("+", "-"),
		12, List.of("*", "/", "%"),
		14, List.of("++", "--", "+", "-", "!", "~")
	);

	private static final String LIST_OP = "---";

	private static final Set<String> FUNCTIONS = Set.of(
		"FUN1", "FUN2", "FUN3"
	);


	private final Deque<String> _tokens;
	private String _next;

	private SymbolListParser(final Deque<String> tokens) {
		_tokens = requireNonNull(tokens);
		_next = _tokens.getFirst();
	}

	static TreeNode<String> parse(final Deque<String> expr) {
		if (expr.isEmpty()) {
			throw new IllegalArgumentException(
				"Expression list is empty: " + expr
			);
		}


		return new SymbolListParser(expr).parse();
	}

	private TreeNode<String> parse() {
		final TreeNode<String> expr = expression();

		/*
		if (_next.token != Token.EPSILON) {
			throw new IllegalArgumentException(format(
				"Unexpected symbol %s found.", _next
			));
		}
		 */

		//Var.reindex(expr);

		return expr;
	}

	private TreeNode<String> expression() {
		return sumPrecedenceOp(signedTerm());
	}

	private TreeNode<String> sumPrecedenceOp(final TreeNode<String> expr) {
		TreeNode<String> result = expr;

		if (_next.equals(PLUS) || _next.equals(MINUS)) {
			final TreeNode<String> node = TreeNode.of(_next).attach(expr);

			nextToken();
			node.attach(term());
			result = sumPrecedenceOp(node);
		}

		return result;
	}

	private TreeNode<String> signedTerm() {
		if (_next.equals(MINUS)) {
			nextToken();
			return TreeNode.of(MINUS).attach(term());
		} else if (_next.equals(PLUS)) {
			nextToken();
		}

		return term();
	}

	private TreeNode<String> term() {
		return multPrecedenceOp(factor());
	}

	private TreeNode<String> multPrecedenceOp(final TreeNode<String> expr) {
		TreeNode<String> result = expr;

		if (_next.equals(MUL) || _next.equals(DIV) || _next.equals(MOD)) {
			final TreeNode<String> node = TreeNode.of(_next).attach(expr);

			nextToken();
			node.attach(signedFactor());
			result = multPrecedenceOp(node);
		}

		/*
		if (_next.token == Token.MUL) {
			final TreeNode<Op<Double>> prod = TreeNode
				.<Op<Double>>of(MathOp.MUL)
				.attach(expr);

			nextToken();
			prod.attach(signedFactor());
			result = multPrecedenceOp(prod);
		} else if (_next.token == Token.DIV) {
			final TreeNode<Op<Double>> prod = TreeNode
				.<Op<Double>>of(MathOp.DIV)
				.attach(expr);

			nextToken();
			prod.attach(signedFactor());
			result = multPrecedenceOp(prod);
		} else if (_next.token == Token.MOD) {
			final TreeNode<Op<Double>> prod = TreeNode
				.<Op<Double>>of(MathOp.MOD)
				.attach(expr);

			nextToken();
			prod.attach(signedFactor());
			result = multPrecedenceOp(prod);
		}
		 */

		return result;
	}

	private TreeNode<String> signedFactor() {
		if (_next.equals(MINUS)) {
			nextToken();
			return TreeNode.of(_next).attach(factor());
		} else if (_next.equals(PLUS)) {
			nextToken();
		}

		return factor();
	}

	private TreeNode<String> factor() {
		return factorOp(argument());
	}

	private TreeNode<String> factorOp(final TreeNode<String> expr) {
		TreeNode<String> result = expr;

		if (_next.equals(POWER)) {
			nextToken();

			result = TreeNode.of(_next)
				.attach(expr)
				.attach(signedFactor());
		}

		return result;
	}

	private TreeNode<String> argument() {

		if (FUNCTIONS.contains(_next)) {
			final TreeNode<String> node = TreeNode.of(_next);
			nextToken();
			list(argument(), new ArrayList<>()).forEach(node::attach);
			return node;
		} else if (_next.equals(COMMA) ||
			_next.equals(OPEN_BRACKET))
		{
			nextToken();
			TreeNode<String> expr = expression();
			if (_next.equals(COMMA)) {
				expr = TreeNode
					.of(LIST_OP)
					.attach(expr)
					.attach(argument());

				return expr;
			}

			if (!_next.equals(CLOSE_BRACKET)) {
				throw new IllegalArgumentException(format(
					"Closing brackets expected: %s", _next));
			}

			nextToken();
			return expr;
		}

		return value();
	}

	private static List<TreeNode<String>> list(
		final TreeNode<String> tree,
		final List<TreeNode<String>> list
	) {
		if (tree.value().equals(LIST_OP)) {
			tree.childStream().forEach(child -> list(child, list));
		} else {
			list.add(tree);
		}
		return list;
	}

	private TreeNode<String> value() {
		final TreeNode<String> result = TreeNode.of(_next);
		nextToken();
		return result;

		/*
		final String value = _next.sequence;

		if (_next.token == Token.NUMBER) {
			final TreeNode<Op<Double>> node =
				TreeNode.of(Const.of(Double.valueOf(value)));

			nextToken();
			return node;
		}

		if (_next.token == Token.VARIABLE) {
			final TreeNode<Op<Double>> node = CONST.containsKey(value)
				? TreeNode.of(CONST.get(value))
				: TreeNode.of(Var.of(value, 0));

			nextToken();
			return node;
		}

		if (_next.token == Token.EPSILON) {
			throw new IllegalArgumentException("Unexpected end of input.");
		} else {
			throw new IllegalArgumentException(format(
				"Unexpected symbol %s found.", _next
			));
		}

		 */
	}

	private void nextToken() {
		_tokens.pop();
		_next = _tokens.isEmpty() ? "" : _tokens.getFirst();
	}


}
