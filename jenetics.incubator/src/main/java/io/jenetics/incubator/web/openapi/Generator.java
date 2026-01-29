package io.jenetics.incubator.web.openapi;

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
			.property(p -> p.name("count").type(int.class))
			.property(p -> p.name("metric").type(double.class))
			.property(p -> p.name("name").type(String.class))
			.property(p -> p.name("values").type(String[].class))
			.equalsAndHashCode();

		var writer = new JCMWriter(cm);
		writer.build(new OutputStreamCodeWriter(System.out, Charset.defaultCharset()));
	}


}
