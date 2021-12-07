package io.jenetics.incubator.grammar_old;

import io.jenetics.incubator.grammar.SymbolIndex;
import io.jenetics.incubator.grammar_old.Grammar.Symbol;

import io.jenetics.ext.util.Tree;

@FunctionalInterface
public interface TreeGenerator {
	Tree<Symbol, ?> generate(final Grammar grammar, final SymbolIndex index);
}
