/*
 * Java Genetic Algorithm Library (@!identifier!@).
 * Copyright (c) @!year!@ Franz Wilhelmstötter
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *     
 */
package org.jenetics.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javolution.xml.XMLObjectReader;
import javolution.xml.XMLObjectWriter;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: XMLSerializer.java,v 1.4 2009-09-13 21:14:21 fwilhelm Exp $
 */
public class XMLSerializer {

	private XMLSerializer() {
	}
	
	/**
	 * Write the XML serializable object to the given output stream. The output
	 * stream is not closed by this method.
	 * 
	 * @param object the object to serialize.
	 * @param out the output stream.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws XMLStreamException if the object could not be serialized.
	 */
	public static <T extends XMLSerializable> void write(final T object, final OutputStream out) 
		throws XMLStreamException 
	{
		Validator.notNull(out, "Output stream");
		Validator.notNull(object, "Object");
		
		XMLObjectWriter writer = null;
		try {
			writer = XMLObjectWriter.newInstance(
				new NonClosableOutputStream(out)
			);
			writer.setIndentation("\t");
			writer.write(object);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
	
	/**
	 * Reads an object (which was serialized by the 
	 * {@link #write(XMLSerializable, OutputStream)} method) from the given
	 * input stream. The input stream is not closed by this method.
	 * 
	 * @param in the input stream to read from.
	 * @return the deserialized object.
	 * @throws NullPointerException if the input stream {@code in} is {@code null}.
	 * @throws XMLStreamException if the object could not be read.
	 */
	public static Object read(final InputStream in) throws XMLStreamException {
		Validator.notNull(in, "Input stream");
		
		final XMLObjectReader reader = XMLObjectReader.newInstance(
			new NonClosableInputStream(in)
		);
		return reader.read();
	}
	
	private static final class NonClosableOutputStream extends OutputStream {
		private final OutputStream _adoptee;
		
		public NonClosableOutputStream(final OutputStream adoptee) {
			_adoptee = adoptee;
		}

		@Override
		public void close() throws IOException {
			//Ignore close call.
			_adoptee.flush();
		}

		@Override
		public boolean equals(Object obj) {
			return _adoptee.equals(obj);
		}

		@Override
		public void flush() throws IOException {
			_adoptee.flush();
		}

		@Override
		public int hashCode() {
			return _adoptee.hashCode();
		}

		@Override
		public String toString() {
			return _adoptee.toString();
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			_adoptee.write(b, off, len);
		}

		@Override
		public void write(byte[] b) throws IOException {
			_adoptee.write(b);
		}

		@Override
		public void write(int b) throws IOException {
			_adoptee.write(b);
		}
		
	}
	
	private static final class NonClosableInputStream extends InputStream {
		private final InputStream _adoptee;
		
		public NonClosableInputStream(final InputStream adoptee) {
			_adoptee = adoptee;
		}

		@Override
		public int available() throws IOException {
			return _adoptee.available();
		}

		@Override
		public void close() throws IOException {
		}

		@Override
		public void mark(int readlimit) {
			_adoptee.mark(readlimit);
		}

		@Override
		public boolean markSupported() {
			return _adoptee.markSupported();
		}

		@Override
		public int read() throws IOException {
			return _adoptee.read();
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			return _adoptee.read(b, off, len);
		}

		@Override
		public int read(byte[] b) throws IOException {
			return _adoptee.read(b);
		}

		@Override
		public void reset() throws IOException {
			_adoptee.reset();
		}

		@Override
		public long skip(long n) throws IOException {
			return _adoptee.skip(n);
		}
		
		
	}
	
}



