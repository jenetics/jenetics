package io.jenetics.incubator.restfulclient;

import java.io.IOException;
import java.io.OutputStream;

public interface Writer {
	void write(OutputStream out, Object value) throws IOException;
}
