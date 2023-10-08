package io.jenetics.prog.op;

import static io.jenetics.ext.grammar.Bnf.BNF;

import java.util.Random;

import org.testng.annotations.Test;

import io.jenetics.ext.grammar.Bnf;
import io.jenetics.ext.grammar.Cfg;
import io.jenetics.ext.grammar.DerivationTreeGenerator;
import io.jenetics.ext.grammar.SymbolIndex;
import io.jenetics.ext.util.TreeFormatter;
import io.jenetics.ext.util.TreeNode;

public class TypedBnfTest {

	@Test
	public void grammar() {
		final Cfg<?> cfg = BNF."""
			<expr> ::= <num> | <var> | <fun> <arg> <arg>
			<arg>  ::= <expr> | <var> | <num> | <expr> | <expr>
			<fun>  ::= \{MathOp.ADD}
			         | \{MathOp.SUB}
			         | \{MathOp.MUL}
			         | \{MathOp.DIV}
			<var>  ::= \{Var.of("x")}
			         | \{Var.of("y")}
			<num>  ::= \{Const.of(0)}
					 | \{Const.of(1)}
					 | \{Const.of(2)}
					 | \{Const.of(3)}
					 | \{Const.of(4)}
					 | \{Const.of(5)}
					 | \{Const.of(6)}
					 | \{Const.of(7)}
					 | \{Const.of(8)}
					 | \{Const.of(9)}
			""";

		System.out.println(Bnf.format(cfg));

		final var generator = new DerivationTreeGenerator<>(
			SymbolIndex.of(new Random()),
			Integer.MAX_VALUE
		);

		for (int i = 0; i < 10; ++i) {
			final var dtree = generator.generate(cfg);
			System.out.println(TreeFormatter.TREE.format(dtree));
			System.out.println("----------------");

			TreeNode<Object> tree = TreeNode.of();
			TreeNode.prune(dtree, tree, value -> value instanceof Cfg.Terminal<?>);

			final var string = TreeFormatter.TREE.format(tree);
			System.out.println(string);
			System.out.println("===================================");
		}

	}

}
