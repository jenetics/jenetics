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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 * 	 
 */
package org.jenetics.util;

import static org.jenetics.util.validation.nonNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import javolution.xml.XMLObjectReader;
import javolution.xml.XMLObjectWriter;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public final class io {

	private io() {
		throw new AssertionError("Don't create an 'io' instance.");
	}
	
	
	/**
	 * Closes the given {@code closeable}. {@code null} values are allowed. 
	 * IOExceptions are swallowed
	 * 
	 * @param closeable the closeable to close.
	 */
	public static void closeQuietly(final Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (IOException ignore) {
		}
	}
	
	/**
	 * Closes the given {@code writer}. {@code null} values are allowed. 
	 * XMLStreamException are swallowed
	 * 
	 * @param writer the writer to close.
	 */
	public static void closeQuietly(final XMLObjectWriter writer) {
		try {
			if (writer != null) {
				writer.close();
			}
		} catch (XMLStreamException ignore) {
		}
	}
	
	/**
	 * Closes the given {@code reader}. {@code null} values are allowed. 
	 * XMLStreamException are swallowed
	 * 
	 * @param reader the reader to close.
	 */
	public static void closeQuietly(final XMLObjectReader reader) {
		try {
			if (reader != null) {
				reader.close();
			}
		} catch (XMLStreamException ignore) {
		}
	}

	/**
	 * Write the XML serializable object to the given output stream. The output
	 * stream is not closed by this method. 
	 * @param object the object to serialize.
	 * @param out the output stream.
	 * 
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws XMLStreamException if the object could not be serialized.
	 */
	public static <T extends XMLSerializable> void writeXML(
		final T object, 
		final OutputStream out
	) 
		throws XMLStreamException 
	{
		nonNull(out, "Output stream");
		nonNull(object, "Object");
		
		final OutputStream nonCloseOut = new NonClosableOutputStream(out);
		final XMLObjectWriter writer = XMLObjectWriter.newInstance(nonCloseOut);
		writer.setIndentation("\t");
		writer.write(object);
		writer.flush();
	}

	/**
	 * Write the XML serializable object to the given path.
	 * 
	 * @param object the object to serialize.
	 * @param path the output path.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws XMLStreamException if the object could not be serialized.
	 */
	public static <T extends XMLSerializable> void writeXML(
		final T object, 
		final File path
	) 
		throws XMLStreamException, FileNotFoundException 
	{
		final OutputStream out = new BufferedOutputStream(new FileOutputStream(path));
		try {
			writeXML(object, out);
		} finally {
			closeQuietly(out);
		}
	}

	/**
	 * Write the XML serializable object to the given path.
	 * 
	 * @param object the object to serialize.
	 * @param path the output path.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws XMLStreamException if the object could not be serialized.
	 */
	public static <T extends XMLSerializable> void writeXML(
		final T object, 
		final String path
	) 
		throws XMLStreamException, FileNotFoundException 
	{
		writeXML(object, new File(path));
	}
	
	/**
	 * Reads an object (which was serialized by the 
	 * {@link #writeXML(XMLSerializable, OutputStream)} method) from the given
	 * input stream. The input stream is not closed by this method.
	 * 
	 * @param in the input stream to read from.
	 * @return the de-serialized object.
	 * @throws NullPointerException if the input stream {@code in} is {@code null}.
	 * @throws XMLStreamException if the object could not be read.
	 */
	public static <T> T readXML(final Class<T> type, final InputStream in) 
		throws XMLStreamException 
	{
		nonNull(type, "Object type");
		nonNull(in, "Input stream");
		
		final InputStream nonCloseIn = new NonClosableInputStream(in);
		final XMLObjectReader reader = XMLObjectReader.newInstance(nonCloseIn);
		return type.cast(reader.read());
	}
	
	/**
	 * Reads an object (which was serialized by the 
	 * {@link #writeXML(XMLSerializable, File)} method) from the given path.
	 * 
	 * @param path the path to read from.
	 * @return the de-serialized object.
	 * @throws NullPointerException if the input stream {@code in} is {@code null}.
	 * @throws XMLStreamException if the object could not be read.
	 */
	public static <T> T readXML(final Class<T> type, final File path) 
		throws FileNotFoundException, XMLStreamException 
	{
		final BufferedInputStream in = new BufferedInputStream(
				new FileInputStream(path)
			);
		try {
			return readXML(type, in);
		} finally {
			closeQuietly(in);
		}
	}
	
	/**
	 * Reads an object (which was serialized by the 
	 * {@link #writeXML(XMLSerializable, File)} method) from the given path.
	 * 
	 * @param path the path to read from.
	 * @return the de-serialized object.
	 * @throws NullPointerException if the input stream {@code in} is {@code null}.
	 * @throws XMLStreamException if the object could not be read.
	 */
	public static <T> T readXML(final Class<T> type, final String path) 
		throws FileNotFoundException, XMLStreamException 
	{
		return readXML(type, new File(path));
	}

	/**
	 * Write the serializable object to the given output stream. The output
	 * stream is not closed by this method.
	 * 
	 * @param object the object to serialize.
	 * @param out the output stream.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws IOException if the object could not be serialized.
	 */
	public static void writeObject(
		final Serializable object, 
		final OutputStream out
	) 
		throws IOException 
	{
		nonNull(out, "Output");
		nonNull(object, "Object");
		
		final ObjectOutputStream oout = new ObjectOutputStream(out);
		oout.writeObject(object);
		oout.flush();
	}

	/**
	 * Write the serializable object to the given output stream.
	 * @param object the object to serialize.
	 * @param path the output paths.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws IOException if the object could not be serialized.
	 */
	public static void writeObject(
		final Serializable object, 
		final File path
	) 
		throws IOException 
	{
		nonNull(path, "Path");
		
		final BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(path));
		try {
			writeObject(object, out);
		} finally {
			closeQuietly(out);
		}
	}

	/**
	 * Write the serializable object to the given output stream.
	 * 
	 * @param object the object to serialize.
	 * @param path the output paths.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws IOException if the object could not be serialized.
	 */
	public static void writeObject(
		final Serializable object, 
		final String path
	) 
		throws IOException 
	{
		writeObject(object, new File(path));
	}
	
	/**
	 * Reads an object (which was serialized by the 
	 * {@link #writeObject(Serializable, OutputStream)} method) from the given
	 * input stream. The input stream is not closed by this method.
	 * 
	 * @param in the input stream to read from.
	 * @return the de-serialized object.
	 * @throws NullPointerException if the input stream {@code in} is {@code null}.
	 * @throws IOException if the object could not be read.
	 */
	public static <T extends Serializable> T readObject(
		final Class<T> type, 
		final InputStream in
	) 
		throws IOException 
	{
		nonNull(type, "Object type");
		nonNull(in, "Input stream");
		
		T object = null;
		
		final ObjectInputStream oin = new ObjectInputStream(in);
		try {
			object = type.cast(oin.readObject());
		} catch (ClassNotFoundException e) {
			throw new IOException(e);
		}
		
		return object;
	}
	
	/**
	 * Reads an object (which was serialized by the 
	 * {@link #writeObject(Serializable, File)} method) from the given
	 * input path.
	 * 
	 * @param path the input path to read from.
	 * @return the de-serialized object.
	 * @throws NullPointerException if the input stream {@code in} is {@code null}.
	 * @throws IOException if the object could not be read.
	 */
	public static <T extends Serializable> T readObject(
		final Class<T> type, 
		final File path
	) 
		throws IOException 
	{
		nonNull(path, "Path");
		
		final BufferedInputStream in = new BufferedInputStream(new FileInputStream(path));
		try {
			return readObject(type, in);
		} finally {
			closeQuietly(in);
		}
	}
	
	/**
	 * Reads an object (which was serialized by the 
	 * {@link #writeObject(Serializable, String)} method) from the given
	 * input path.
	 * 
	 * @param path the input path to read from.
	 * @return the de-serialized object.
	 * @throws NullPointerException if the input stream {@code in} is {@code null}.
	 * @throws IOException if the object could not be read.
	 */
	public static <T extends Serializable> T readObject(
		final Class<T> type, 
		final String path
	) 
		throws IOException 
	{
		nonNull(path, "Path");
		return readObject(type, new File(path));
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



