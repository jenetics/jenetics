package io.jenetics.incubator.grammar_old;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.jenetics.ext.util.TreeNode;

public class SymbolListParser {

	static final class Precedence {
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

	private final Set<String> unary = Set.of("++", "--", "-", "!", "~");


	private final Iterator<String> tokens;
	private String next;

	private SymbolListParser(final Collection<String> tokens) {
		this.tokens = tokens.iterator();
		nextToken();
	}

	static TreeNode<String> parse(final Collection<String> tokens) {
		if (tokens.isEmpty()) {
			throw new IllegalArgumentException(
				"Collection of tokens must not be empty."
			);
		}

		return new SymbolListParser(tokens).expression();
	}

	private TreeNode<String> expression() {
		final TreeNode<String> term = signedTerm();
		return sumPrecedenceOp(term);
	}

	private TreeNode<String> signedTerm() {
		if (unary.contains(next)) {
			nextToken();
			return TreeNode.of(next).attach(term());
		} else {
			return term();
		}
	}

	private TreeNode<String> sumPrecedenceOp(final TreeNode<String> expr) {
		TreeNode<String> result = expr;

		if (next.equals(PLUS) || next.equals(MINUS)) {
			final TreeNode<String> node = TreeNode.of(next).attach(expr);
			nextToken();
			node.attach(term());
			result = sumPrecedenceOp(node);
		}

		return result;
	}

	private TreeNode<String> term() {
		return multPrecedenceOp(factor());
	}

	private TreeNode<String> multPrecedenceOp(final TreeNode<String> expr) {
		TreeNode<String> result = expr;

		if (next.equals(MUL) || next.equals(DIV) || next.equals(MOD)) {
			final TreeNode<String> node = TreeNode.of(next).attach(expr);

			nextToken();
			node.attach(signedFactor());
			result = multPrecedenceOp(node);
		}

		return result;
	}

	private TreeNode<String> signedFactor() {
		if (unary.contains(next)) {
			nextToken();
			return TreeNode.of(next).attach(factor());
		} else {
			return factor();
		}
	}

	private TreeNode<String> factor() {
		return factorOp(argument());
	}

	private TreeNode<String> factorOp(final TreeNode<String> expr) {
		TreeNode<String> result = expr;

		if (next.equals(POWER)) {
			nextToken();

			result = TreeNode.of(next)
				.attach(expr)
				.attach(signedFactor());
		}

		return result;
	}

	private TreeNode<String> argument() {
		if (FUNCTIONS.contains(next)) {
			final TreeNode<String> node = TreeNode.of(next);
			nextToken();
			list(argument(), new ArrayList<>()).forEach(node::attach);
			return node;
		} else if (next.equals(COMMA) ||
					next.equals(OPEN_BRACKET))
		{
			nextToken();
			TreeNode<String> expr = expression();
			if (next.equals(COMMA)) {
				expr = TreeNode
					.of(LIST_OP)
					.attach(expr)
					.attach(argument());

				return expr;
			}

			if (!next.equals(CLOSE_BRACKET)) {
				throw new IllegalArgumentException(format(
					"Closing brackets expected: %s", next));
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
		final TreeNode<String> result = TreeNode.of(next);
		nextToken();
		return result;
	}

	private void nextToken() {
		next = tokens.hasNext() ? tokens.next() : EPSILON;
	}


}
