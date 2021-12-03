package io.jenetics.incubator.grammar_old;

import java.util.List;
import java.util.stream.Collectors;

import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.engine.Codec;
import io.jenetics.incubator.grammar_old.Grammar.Symbol;
import io.jenetics.incubator.grammar_old.Grammar.Terminal;
import io.jenetics.util.IntRange;

import io.jenetics.prog.op.MathExpr;

public class Mapper {

	static Codec<List<Terminal>, IntegerGene> codec(final Grammar grammar) {
		return Codec.of(
			Genotype.of(IntegerChromosome.of(IntRange.of(0, 100), IntRange.of(50, 200))),
			gt -> {
				final var index = new SymbolIndex() {
					int pos = 0;
					@Override
					public int next(final int bound) {
						final int value = gt.chromosome().get(pos).intValue()%bound;
						pos = (pos + 1)%gt.chromosome().length();
						return value;
					}
				};

				return grammar.generate(index);
			}
		);
	}

	static MathExpr toMathExpr(final List<Terminal> sentence) {
		final String value = sentence.stream()
			.map(Symbol::toString)
			.collect(Collectors.joining());

		return MathExpr.parse(value);
	}

	public static void main(final String[] args) {
/*		Grammar.builder()
			.rule("expr", rule -> rule
				.expression(exp -> exp.T("(").NT("exp").NT("op").T("exp"))
				.expression(exp -> exp.nt("num").nt("var")))
			.build();

		Grammar.builder()
			.rule("expr", rule -> rule
				.exp("(", "<expr>", "<op>", "<expr>", ")")
				.exp("<num>", "<var>")
				.exp("(", "<expr>", "<op>", "<expr>", ")"))
			.rule("op", rule -> rule
				.exp("+").exp("-").exp("*").exp("/"))
			.build();

		final Rule rule = new Rule(
			new NonTerminal("expr"),
			List.of(
				new Expression(List.of(
					new Terminal("("),
					new NonTerminal("expr"),
					new NonTerminal("op"),
					new NonTerminal("expr"),
					new Terminal(")")
				)),
				new Expression(List.of(
					new NonTerminal("num"),
					new NonTerminal("var")
				))
			)
		);*/


		final Grammar grammar = Grammar.parse("""
			<expr> ::= ( <expr> <op> <expr> ) | <num> | <var> | ( <expr> <op> <expr> )
			<op> ::= + | - | * | /
			<var> ::= x | y
			<num> ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
			"""
		);

		final Codec<MathExpr, IntegerGene> codec = codec(grammar).map(Mapper::toMathExpr);

	}

}
