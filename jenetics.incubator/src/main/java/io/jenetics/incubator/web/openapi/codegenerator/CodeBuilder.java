package io.jenetics.incubator.web.openapi.codegenerator;

import com.helger.jcodemodel.JCodeModel;

import io.jenetics.incubator.web.openapi.codegenerator.model.SchemaModel;

@FunctionalInterface
public interface CodeBuilder {
	<S extends SchemaModel> void build(final S schema, final JCodeModel model);
}
