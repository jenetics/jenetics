package io.jenetics.incubator.web.openapi.codegenerator.model;

import io.swagger.v3.oas.models.media.Schema;

import io.jenetics.incubator.web.openapi.codegenerator.Qname;

public record GenericSchema(Schema<?> schema) implements TypedSchema {

	@Override
	public Qname name() {
		return Qname.of(schema.getName());
	}

}
