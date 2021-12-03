package io.jenetics.incubator.grammar_old;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import io.jenetics.incubator.grammar_old.Grammar.Symbol;
import io.jenetics.incubator.grammar_old.Grammar.Terminal;

import io.jenetics.ext.util.TreeFormatter;
import io.jenetics.ext.util.TreeNode;

public class SymbolListParserTest {

	@Test
	public void foo() {
		final var grammar = Grammar.parse("""
			<expr> ::= ( <expr> <op> <expr> ) | <num> | <var> |  <fun> ( <arg>, <arg> )
			<fun> ::= FUN1 | FUN2
			<arg> ::= <expr> | <var> | <num>
			<op> ::= + | - | * | /
			<var> ::= x | y
			<num> ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
			"""
		);

		final List<Terminal> list = grammar.generate(new Random(29022156195143L)::nextInt);
		final var string = list.stream()
			.map(Symbol::toString)
			.collect(Collectors.joining());

		System.out.println(string);

		final Deque<String> expr = list.stream()
			.map(Object::toString)
			.collect(Collectors.toCollection(ArrayDeque::new));

		System.out.println(expr);

		final TreeNode<String> tree = SymbolListParser.parse(expr);
		System.out.println(TreeFormatter.TREE.format(tree));

		System.out.println(tree);

		final var node = StandardGenerators.generateTree(grammar, new Random(29022156195143L)::nextInt);

		//final var node = grammar.parse(grammar.rules().get(0), new ArrayList<>(list));
		System.out.println(TreeFormatter.TREE.format(node));
	}

}
