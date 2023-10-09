package io.jenetics.prog.op;

import static io.jenetics.ext.grammar.Bnf.BNF;

import java.util.Random;

import org.testng.annotations.Test;

import io.jenetics.ext.grammar.Bnf;
import io.jenetics.ext.grammar.Cfg;
import io.jenetics.ext.grammar.DerivationTreeGenerator;
import io.jenetics.ext.grammar.SymbolIndex;
import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.TreeFormatter;
import io.jenetics.ext.util.TreeNode;

public class TypedBnfTest {

	@Test
	public void grammar() {

		@SuppressWarnings("unchecked")
		final Cfg<Op<Double>> cfg = (Cfg<Op<Double>>)BNF."""
			<expr> ::= <num> | <var> | <fun> <arg> <arg>
			<arg>  ::= <expr> | <var> | <num> | <expr> | <expr>
			<fun>  ::= \{MathOp.ADD}
			         | \{MathOp.SUB}
			         | \{MathOp.MUL}
			         | \{MathOp.DIV}
			<var>  ::= \{Var.of("x", 0)}
			         | \{Var.of("y", 1)}
			<num>  ::= \{Const.of(0.0)}
					 | \{Const.of(1.0)}
					 | \{Const.of(2.0)}
					 | \{Const.of(3.0)}
					 | \{Const.of(4.0)}
					 | \{Const.of(5.0)}
					 | \{Const.of(6.0)}
					 | \{Const.of(7.0)}
					 | \{Const.of(8.0)}
					 | \{Const.of(9.0)}
			""";

		System.out.println(Bnf.format(cfg));

		final var generator = new DerivationTreeGenerator<Op<Double>>(
			SymbolIndex.of(new Random()),
			Integer.MAX_VALUE
		);

		for (int i = 0; i < 10; ++i) {
			final Tree<Cfg.Symbol<Op<Double>>, ?> dtree = generator.generate(cfg);

			System.out.println(TreeFormatter.TREE.format(dtree));
			System.out.println("----------------");

			Tree<Cfg.Terminal<Op<Double>>, ?> ast = DerivationTreeGenerator.toAst(dtree);

			Tree<Op<Double>, ?> prog = TreeNode.ofTree(ast, Cfg.Terminal::value);
			System.out.println(TreeFormatter.PARENTHESES.format(prog));
			System.out.println(MathExpr.eval(prog, 3.0, 4.0));

			final var string = TreeFormatter.TREE.format(ast);
			System.out.println(string);
			System.out.println("===================================");
		}

	}

}
