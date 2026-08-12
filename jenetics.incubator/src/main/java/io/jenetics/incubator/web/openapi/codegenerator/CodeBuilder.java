package io.jenetics.incubator.web.openapi.codegenerator;

import com.helger.jcodemodel.JCodeModel;

import io.jenetics.incubator.web.openapi.codegenerator.model.ModelSchema;

@FunctionalInterface
public interface CodeBuilder {
	<S extends ModelSchema> void build(final S schema, final JCodeModel model);
}
