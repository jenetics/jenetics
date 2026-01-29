package io.jenetics.incubator.web.openapi;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.writer.JCMWriter;
import com.helger.jcodemodel.writer.OutputStreamCodeWriter;

import java.io.IOException;
import java.nio.charset.Charset;

public class Generator {

	static void main() throws IOException, JCodeModelException {
		final var cm = new JCodeModel();
		final var cls = cm._class("io.jenetics.Foo");

		new DataClassGenerator(cm, cls)
			.property(p -> p.name("count").type(type(cm,"int")))
			.property(p -> p.name("metric").type(type(cm,"double")))
			.property(p -> p.name("name").type(type(cm,"java.lang.String")))
			.property(p -> p.name("values").type(type(cm,"java.util.List<java.math.BigDecimal>")))
			.equalsAndHashCode();

		var writer = new JCMWriter(cm);
		writer.build(new OutputStreamCodeWriter(System.out, Charset.defaultCharset()));
	}

	static AbstractJType type(JCodeModel model, final String name) {
			return model.parseType(name);
	}


}
