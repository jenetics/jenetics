package io.jenetics.incubator.web.openapi.codegenerator;

public record Qname(String namespace, String name) {
	public Qname(String name) {
		this("", name);
	}

	@Override
	public String toString() {
		if (namespace.isEmpty()) {
			return name;
		} else {
			return namespace + "." + name;
		}
	}

}
