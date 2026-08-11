package io.jenetics.incubator.web.openapi.codegenerator;

import com.helger.jcodemodel.JCodeModel;

@FunctionalInterface
public interface CodeBuilder {
	void build(final JCodeModel model);
}
