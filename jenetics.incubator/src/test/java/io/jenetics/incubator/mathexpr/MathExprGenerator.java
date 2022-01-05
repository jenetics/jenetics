package io.jenetics.incubator.mathexpr;

import io.jenetics.incubator.grammar.Cfg;
import io.jenetics.incubator.grammar.Sentence;
import io.jenetics.incubator.grammar.SymbolIndex;
import io.jenetics.incubator.grammar.bnf.Bnf;

import java.util.random.RandomGenerator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class MathExprGenerator {
	private MathExprGenerator() {
	}

	private static final Cfg GRAMMAR = Bnf.parse("""
			<expr> ::= <num>
					| <var>
					| <expr> <op> <expr>
					| ( <expr> <op> <expr> )
					| <fun1> ( <expr> )
					| <fun2> ( <expr>, <expr> )
			<op>   ::= + | - | * | / | %
			<fun1> ::= sin | cos | abs | sqrt | rint
			<fun2> ::= pow | min | max | hypot
			<var>  ::= x | y | z
			<num>  ::= 3.123312 | 6.345345 | 23.43e-03 | 1.4e3
			"""
	);


	public static void main(final String[] args) {
		final var random = RandomGenerator.getDefault();
		final var sentences = Stream.generate(() -> sentence(random))
			.limit(100)
			.toList();

		System.out.println("public static final List<String> EXPRESSIONS = List.of(");
		System.out.println(
			sentences.stream()
				.collect(Collectors.joining("\",\n\t\"", "\t\"", "\""))
		);
		System.out.println(");");
		System.out.println();

		System.out.println("public static final List<Fun3> FUNCTIONS = List.of(");
		System.out.println(
			IntStream.range(0, sentences.size())
				.mapToObj("MathExprTestData::fun_%d"::formatted)
				.collect(Collectors.joining(",\n\t", "\t", ""))
		);
		System.out.println(");");
		System.out.println();

		for (int i = 0; i < sentences.size(); ++i) {
			System.out.println(toFun(i, sentences.get(i)));
		}
	}

	private static String toFun(final int index, final String expr) {
		return """
			private static double fun_%d(final double x, final double y, final double z) {
				return %s;
			}
			""".formatted(index, expr);
	}

	private static String sentence(final RandomGenerator random) {
		String expr = "";
		do {
			expr = Sentence.generate(GRAMMAR, SymbolIndex.of(random), 1000).stream()
				.map(Cfg.Symbol::value)
				.collect(Collectors.joining());
		} while (expr.length() < 200);

		return expr;
	}

}
