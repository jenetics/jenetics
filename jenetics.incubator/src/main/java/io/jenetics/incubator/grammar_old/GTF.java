package io.jenetics.incubator.grammar_old;

import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.incubator.grammar_old.Grammar.Terminal;
import io.jenetics.util.Factory;

import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.TreeNode;

import io.jenetics.prog.ProgramGene;

public class GTF {

	private static final String OPEN_BRACKET = "(";
	private static final String CLOSE_BRACKET = ")";
	private static final String COMMA = ",";


	public static <G extends Gene<?, G>> Factory<Genotype<G>> gtf(final List<Terminal> list) {
		return () -> {
			return null;
		};
	}


	public static Genotype<ProgramGene<Double>> toProgram(final List<Terminal> terminals) {
		return null;
	}

	public static <N> Tree<N, ?> toTree(
		final List<Terminal> terminals,
		final Function<? super Terminal, ? extends N> nodes
	) {
		final TreeNode<N> root = TreeNode.of();

		final Iterator<Terminal> it = terminals.iterator();
		while (it.hasNext()){
			final Terminal terminal = it.next();

			switch (terminal.value()) {
				case OPEN_BRACKET, COMMA -> {
				}
				case CLOSE_BRACKET -> {

				}
			}
		}

		return root;
	}

	public static Factory<Genotype<ProgramGene<Double>>> programFactory(final Grammar grammar) {
		return () -> {
			return null;
		};
	}

}
