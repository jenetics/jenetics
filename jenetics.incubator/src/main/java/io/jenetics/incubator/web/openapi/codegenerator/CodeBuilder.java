package io.jenetics.incubator.web.openapi.codegenerator;

import com.helger.jcodemodel.JCodeModel;

import io.jenetics.incubator.web.openapi.codegenerator.model.TypedSchema;

@FunctionalInterface
public interface CodeBuilder {
	<S extends TypedSchema> void build(final S schema, final JCodeModel model);
}
