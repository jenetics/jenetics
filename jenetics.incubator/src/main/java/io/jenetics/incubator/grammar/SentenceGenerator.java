package io.jenetics.incubator.grammar;

import java.util.List;

import io.jenetics.incubator.grammar.Cfg.Terminal;

@FunctionalInterface
public interface SentenceGenerator {

	List<Terminal> generate(final Cfg cfg, final SymbolIndex index);

}
