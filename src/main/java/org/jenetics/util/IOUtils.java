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
public class IOUtils {

	private IOUtils() {
	}
	
	/**
	 * Closes the given {@code closeable}. {@code null} values are allowed. 
	 * IOExceptions are swallowed
	 * 
	 * @param closeable the closeable to close.
	 */
	public static void closeQuitely(final Closeable closeable) {
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
	public static void closeQuitely(final XMLObjectWriter writer) {
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
	public static void closeQuitely(final XMLObjectReader reader) {
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
	 * @param out the output stream.
	 * @param object the object to serialize.
	 * 
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws XMLStreamException if the object could not be serialized.
	 */
	public static <T extends XMLSerializable> void writeXML(
		final OutputStream out, 
		final T object
	) 
		throws XMLStreamException 
	{
		writeXMLAndClose(new NonClosableOutputStream(out), object);
	}
	
	/**
	 * Write the XML serializable object to the given output stream. The output
	 * stream is closed by this method.
	 * @param out the output stream.
	 * @param object the object to serialize.
	 * 
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws XMLStreamException if the object could not be serialized.
	 */
	public static <T extends XMLSerializable> void writeXMLAndClose(
		final OutputStream out, 
		final T object
	) 
		throws XMLStreamException 
	{
		Validator.nonNull(out, "Output stream");
		Validator.nonNull(object, "Object");
		
		XMLObjectWriter writer = null;
		try {
			writer = XMLObjectWriter.newInstance(out);
			writer.setIndentation("\t");
			writer.write(object);
		} finally {
			closeQuitely(writer);
		}
	}
	
	/**
	 * Write the XML serializable object to the given path.
	 * @param path the output path.
	 * @param object the object to serialize.
	 * 
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws XMLStreamException if the object could not be serialized.
	 */
	public static <T extends XMLSerializable> void writeXML(
		final File path, 
		final T object
	) 
		throws XMLStreamException, FileNotFoundException 
	{
		writeXMLAndClose(new FileOutputStream(path), object);
	}
	
	/**
	 * Write the XML serializable object to the given path.
	 * @param path the output path.
	 * @param object the object to serialize.
	 * 
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws XMLStreamException if the object could not be serialized.
	 */
	public static <T extends XMLSerializable> void writeXML(
		final String path, 
		final T object
	) 
		throws XMLStreamException, FileNotFoundException 
	{
		writeXML(new File(path), object);
	}
	
	/**
	 * Reads an object (which was serialized by the 
	 * {@link #writeXML(OutputStream, XMLSerializable)} method) from the given
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
		Validator.nonNull(type, "Object type");
		Validator.nonNull(in, "Input stream");
		
		final XMLObjectReader reader = XMLObjectReader.newInstance(
			new NonClosableInputStream(in)
		);
		try {
			return type.cast(reader.read());
		} finally {
			closeQuitely(reader);
		}
	}
	
	/**
	 * Reads an object (which was serialized by the 
	 * {@link #writeXML(OutputStream, XMLSerializable)} method) from the given
	 * input stream. The input stream is closed by this method.
	 * 
	 * @param in the input stream to read from.
	 * @return the de-serialized object.
	 * @throws NullPointerException if the input stream {@code in} is {@code null}.
	 * @throws XMLStreamException if the object could not be read.
	 */
	public static <T> T readXMLAndClose(final Class<T> type, final InputStream in) 
		throws XMLStreamException 
	{
		Validator.nonNull(type, "Object type");
		Validator.nonNull(in, "Input stream");
		
		final XMLObjectReader reader = XMLObjectReader.newInstance(in);
		try {
			return type.cast(reader.read());
		} finally {
			closeQuitely(reader);
		}
	}
	
	/**
	 * Reads an object (which was serialized by the 
	 * {@link #writeXML(File, XMLSerializable)} method) from the given path.
	 * 
	 * @param path the path to read from.
	 * @return the de-serialized object.
	 * @throws NullPointerException if the input stream {@code in} is {@code null}.
	 * @throws XMLStreamException if the object could not be read.
	 */
	public static <T> T readXML(final Class<T> type, final File path) 
		throws FileNotFoundException, XMLStreamException 
	{
		return readXMLAndClose(type, new FileInputStream(path));
	}
	
	/**
	 * Reads an object (which was serialized by the 
	 * {@link #writeXML(File, XMLSerializable)} method) from the given path.
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
	 * @param out the output stream.
	 * @param object the object to serialize.
	 * 
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws IOException if the object could not be serialized.
	 */
	public static void writeObject(
		final OutputStream out, 
		final Serializable object
	) 
		throws IOException 
	{
		Validator.nonNull(out, "Output");
		Validator.nonNull(object, "Object");
		
		final ObjectOutputStream oout = new ObjectOutputStream(
				new NonClosableOutputStream(out)
			);
		try {
			oout.writeObject(object);
		} finally {
			closeQuitely(oout);
		}
	}
	
	/**
	 * Write the serializable object to the given output stream. The output
	 * stream is closed by this method.
	 * @param out the output stream.
	 * @param object the object to serialize.
	 * 
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws IOException if the object could not be serialized.
	 */
	public static void writeObjectAndClose(
		final OutputStream out, 
		final Serializable object
	) 
		throws IOException 
	{
		try {
			writeObject(out, object);
		} finally {
			closeQuitely(out);
		}
	}
	
	/**
	 * Write the serializable object to the given output stream.
	 * @param path the output paths.
	 * @param object the object to serialize.
	 * 
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws IOException if the object could not be serialized.
	 */
	public static void writeObject(
		final File path, 
		final Serializable object
	) 
		throws IOException 
	{
		Validator.nonNull(path, "Path");
		writeObjectAndClose(new FileOutputStream(path), object);
	}
	
	/**
	 * Write the serializable object to the given output stream.
	 * @param path the output paths.
	 * @param object the object to serialize.
	 * 
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws IOException if the object could not be serialized.
	 */
	public static void writeObject(
		final String path, 
		final Serializable object
	) 
		throws IOException 
	{
		writeObject(new File(path), object);
	}
	
	/**
	 * Reads an object (which was serialized by the 
	 * {@link #writeObject(OutputStream, Serializable)} method) from the given
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
		Validator.nonNull(type, "Object type");
		Validator.nonNull(in, "Input stream");
		
		T object = null;
		
		final ObjectInputStream oin = new ObjectInputStream(
				new NonClosableInputStream(in)
			);
		try {
			object = type.cast(oin.readObject());
		} catch (ClassNotFoundException e) {
			throw new IOException(e);
		} finally {
			closeQuitely(oin);
		}
		
		return object;
	}
	
	/**
	 * Reads an object (which was serialized by the 
	 * {@link #writeObject(OutputStream, Serializable)} method) from the given
	 * input stream. The input stream is closed by this method.
	 * 
	 * @param in the input stream to read from.
	 * @return the de-serialized object.
	 * @throws NullPointerException if the input stream {@code in} is {@code null}.
	 * @throws IOException if the object could not be read.
	 */
	public static <T extends Serializable> T readObjectAndClose(
		final Class<T> type, 
		final InputStream in
	) 
		throws IOException 
	{
		try {
			return readObject(type, in);
		} finally {
			closeQuitely(in);
		}
	}
	
	/**
	 * Reads an object (which was serialized by the 
	 * {@link #writeObject(File, Serializable)} method) from the given
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
		Validator.nonNull(path, "Path");
		return readObjectAndClose(type, new FileInputStream(path));
	}
	
	/**
	 * Reads an object (which was serialized by the 
	 * {@link #writeObject(String, Serializable)} method) from the given
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
		Validator.nonNull(path, "Path");
		return readObjectAndClose(type, new FileInputStream(path));
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



