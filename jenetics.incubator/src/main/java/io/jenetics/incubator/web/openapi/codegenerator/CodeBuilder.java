package io.jenetics.incubator.web.openapi.codegenerator;

import com.helger.jcodemodel.JCodeModel;

@FunctionalInterface
public interface CodeBuilder {
	<S extends SchemaModel> void build(final S schema, final JCodeModel model);
}
