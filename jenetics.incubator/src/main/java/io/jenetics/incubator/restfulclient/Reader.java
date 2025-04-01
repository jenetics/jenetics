package io.jenetics.incubator.restfulclient;

import java.io.IOException;
import java.io.InputStream;

interface Reader {
	<T> T read(final InputStream input, Class<T> type) throws IOException;
}
