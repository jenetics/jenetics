package io.jenetics.incubator.grammar;

import io.jenetics.incubator.grammar.Grammar.Terminal;

import io.jenetics.ext.util.Tree;

@FunctionalInterface
public interface TreeGenerator {
	Tree<Terminal, ?> generate(final Grammar grammar, final SymbolIndex index);
}
