package io.jenetics.incubator.restful.generator;

import static java.time.ZoneOffset.UTC;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.writer.OutputStreamCodeWriter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import javax.annotation.processing.Generated;

import org.apache.commons.lang3.StringUtils;

public class CodeModel {

	public static void main(String[] args) throws Exception {
		JCodeModel cm = new JCodeModel();

		JDefinedClass type = cm._class("com.example.MyClass");
		type.annotate(Generated.class)
			.param("value", CodeModel.class.getCanonicalName())
			.param("date", OffsetDateTime.now(UTC).toString());

		var interf = cm._class("FooBar", ClassType.INTERFACE);

		property(type, "startedAt", LocalDateTime.class);
		property(type, "stoppedAt", LocalDateTime.class);


		JMethod method = type.method(JMod.PUBLIC, cm.VOID, "myMethod");
		method.body().directStatement("System.out.println(\"Hello, World!\");");

		// Write the generated code to a file.
		cm.build(new OutputStreamCodeWriter(System.out, "UTF-8"));
	}

	private static void property(JDefinedClass clazz, String name, Class<?> type) {
		clazz.field(JMod.PRIVATE, type, name);

		var getter = clazz.method(JMod.PUBLIC, type, "get" + StringUtils.capitalize(name));
		getter.annotate(Override.class);
		getter.body().directStatement("return " + name + ";");


		var setter = clazz.method(JMod.PUBLIC, type, "set" + StringUtils.capitalize(name));
		setter.param(type, name);
		setter.body().directStatement("this." + name + " = " + name + ";");
	}

}
