package io.jenetics.incubator.grammar;

import io.jenetics.incubator.grammar.Grammar.Symbol;

import io.jenetics.ext.util.Tree;

@FunctionalInterface
public interface TreeGenerator {
	Tree<Symbol, ?> generate(final Grammar grammar, final SymbolIndex index);
}
