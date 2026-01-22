package io.jenetics.incubator.restful.generator;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.writer.OutputStreamCodeWriter;

public class Generator {

	public static void main(String[] args) throws Exception {
		var model = new JCodeModel();
		var cls = model._class("com.foo.SomeClass");
		cls.generify("T");
		cls.field(1, String.class, "foo");
		model.build(new OutputStreamCodeWriter(System.out, "UTF-8"));
	}

}
