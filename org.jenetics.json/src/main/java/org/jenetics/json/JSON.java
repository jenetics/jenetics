/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.json;

import static org.jenetics.internal.util.jaxb.adapterFor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLStreamReader;
import org.codehaus.jettison.mapped.MappedXMLStreamWriter;

import org.jenetics.util.IO;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__! &mdash; <em>$Date: 2015-01-07 $</em>
 * @since !__version__!
 */
public final class JSON {
	private JSON() {}

//	public static final IO json = new IO() {
//		@Override
//		public void write(Object object, OutputStream out) throws IOException {
//			try {
//				final Marshaller marshaller = context().createMarshaller();
//				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//
//				final XMLStreamWriter writer = new MappedXMLStreamWriter(
//					new MappedNamespaceConvention(),
//					new OutputStreamWriter(out)
//				);
//				try {
//					final XmlAdapter<Object, Object> adapter = adapterFor(object);
//					if (adapter != null) {
//						marshaller.marshal(adapter.marshal(object), writer);
//					} else {
//						marshaller.marshal(object, writer);
//					}
//				} finally {
//					writer.close();
//				}
//			} catch (Exception e) {
//				throw new IOException(e);
//			}
//		}
//
//		@Override
//		public <T> T read(Class<T> type, InputStream in) throws IOException {
//			try {
//				final Unmarshaller unmarshaller = context().createUnmarshaller();
//
//				final XMLStreamReader reader = new MappedXMLStreamReader(
//					new JSONObject(toText(in)),
//					new MappedNamespaceConvention(new Configuration())
//				);
//				try {
//					final Object object = unmarshaller.unmarshal(reader);
//					final XmlAdapter<Object, Object> adapter = adapterFor(object);
//					if (adapter != null) {
//						return type.cast(adapter.unmarshal(object));
//					} else {
//						return type.cast(object);
//					}
//				} finally {
//					reader.close();
//				}
//			} catch (Exception e) {
//				throw new IOException(e);
//			}
//		}
//	};
//
//	private static String toText(final InputStream in) throws IOException {
//		try (final Reader r = new InputStreamReader(in);
//			 final BufferedReader br = new BufferedReader(r))
//		{
//			final StringBuilder builder = new StringBuilder();
//			final char[] buffer = new char[2048];
//			int count = 0;
//			while ((count = br.read(buffer)) != -1) {
//				builder.append(buffer, 0, count);
//			}
//
//			return builder.toString();
//		}
//	}
}
