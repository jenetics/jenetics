package io.jenetics.incubator.grammar;

import java.util.List;

import io.jenetics.incubator.grammar.Cfg.Terminal;

public interface Generator {

	List<Terminal> generate(final Cfg cfg);

}
