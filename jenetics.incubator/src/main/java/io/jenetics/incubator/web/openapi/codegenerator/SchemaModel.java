package io.jenetics.incubator.web.openapi.codegenerator;

public sealed interface SchemaModel
	permits EnumModel, StructuralTypeModel, TypedValueModel
{
	Qname name();
}
