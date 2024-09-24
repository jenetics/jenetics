package io.jenetics.ext.grammar;

import java.util.Optional;

public interface Acfg<A> {

	Optional<A> annotationAt(final Cfg.Path path);

}
