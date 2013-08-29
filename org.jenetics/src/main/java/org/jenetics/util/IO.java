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
package org.jenetics.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;

import javolution.xml.XMLObjectReader;
import javolution.xml.XMLObjectWriter;
import javolution.xml.stream.XMLStreamException;

/**
 * Class for object serialization. The following example shows how to write and
 * reload a given population.
 *
 * [code]
 * // Writing the population to disk.
 * final File file = new File("population.xml");
 * IO.xml.write(ga.getPopulation(), file);
 *
 * // Reading the population from disk.
 * final Population<Float64Gene,Float64> population =
 *     (Population<Float64Gene, Float64)IO.xml.read(file);
 * ga.setPopulation(population);
 *[/code]
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date: 2013-04-27 $</em>
 */
public abstract class IO {

	protected IO() {
	}

	/**
	 * IO implementation for <i>XML</i> serialization.
	 */
	public static final IO xml = new IO() {

		@Override
		public void write(final Object object, final OutputStream out)
			throws IOException
		{
			try {
				final OutputStream nco = new NonClosableOutputStream(out);
				final XMLObjectWriter writer = XMLObjectWriter.newInstance(nco);
				writer.setIndentation("\t");
				try {
					writer.write(object);
					writer.flush();
				} finally {
					writer.reset();
				}
			} catch (XMLStreamException e) {
				throw new IOException(e);
			}
		}

		@Override
		public <T> T read(final Class<T> type, final InputStream in)
			throws IOException
		{
			try {
				final InputStream nci = new NonClosableInputStream(in);
				final XMLObjectReader reader = XMLObjectReader.newInstance(nci);
				try {
					return type.cast(reader.read());
				} finally {
					reader.reset();
				}
			} catch (XMLStreamException e) {
				throw new IOException(e);
			}
		}
	};

	/**
	 * IO implementation for "native" <i>Java</i> serialization.
	 */
	public static IO object = new IO() {

		@Override
		public void write(final Object object, final OutputStream out)
			throws IOException
		{
			final ObjectOutputStream oout = new ObjectOutputStream(out);
			oout.writeObject(object);
			out.flush();
		}

		@Override
		public <T> T read(final Class<T> type, final InputStream in)
			throws IOException
		{
			final ObjectInputStream oin = new ObjectInputStream(in);
			try {
				return type.cast(oin.readObject());
			} catch (ClassNotFoundException e) {
				throw new IOException(e);
			}
		}
	};


	/**
	 * Write the (serializable) object to the given path.
	 *
	 * @param object the object to serialize.
	 * @param path the path to write the object to.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws IOException if the object could not be serialized.
	 */
	public void write(final Object object, final String path)
		throws IOException
	{
		write(object, new File(path));
	}

	/**
	 * Write the (serializable) object to the given path.
	 *
	 * @param object the object to serialize.
	 * @param path the path to write the object to.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws IOException if the object could not be serialized.
	 */
	public void write(final Object object, final Path path)
		throws IOException
	{
		write(object, path.toFile());
	}

	/**
	 * Write the (serializable) object to the given file.
	 *
	 * @param object the object to serialize.
	 * @param file the file to write the object to.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws IOException if the object could not be serialized.
	 */
	public void write(final Object object, final File file)
		throws IOException
	{
		try (final FileOutputStream out = new FileOutputStream(file)) {
			write(object, out);
		}
	}

	/**
	 * Write the (serializable) object to the given output stream.
	 *
	 * @param object the object to serialize.
	 * @param out the output stream to write the object to.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws IOException if the object could not be serialized.
	 */
	public abstract void write(final Object object, final OutputStream out)
		throws IOException;

	/**
	 * Reads an object from the given file.
	 *
	 * @param path the path to read from.
	 * @param type the type of the read object.
	 * @return the de-serialized object.
	 * @throws NullPointerException if the input stream {@code in} is {@code null}.
	 * @throws IOException if the object could not be read.
	 */
	public <T> T read(final Class<T> type, final String path)
		throws IOException
	{
		try (final FileInputStream in = new FileInputStream(new File(path))) {
			return read(type, in);
		}
	}

	/**
	 * Reads an object from the given file.
	 *
	 * @param path the path to read from.
	 * @return the de-serialized object.
	 * @throws NullPointerException if the input stream {@code in} is {@code null}.
	 * @throws IOException if the object could not be read.
	 */
	public Object read(final String path) throws IOException {
		return read(Object.class, path);
	}

	/**
	 * Reads an object from the given file.
	 *
	 * @param path the path to read from.
	 * @param type the type of the read object.
	 * @return the de-serialized object.
	 * @throws NullPointerException if the input stream {@code in} is {@code null}.
	 * @throws IOException if the object could not be read.
	 */
	public <T> T read(final Class<T> type, final Path path)
		throws IOException
	{
		try (final FileInputStream in = new FileInputStream(path.toFile())) {
			return read(type, in);
		}
	}

	/**
	 * Reads an object from the given file.
	 *
	 * @param path the path to read from.
	 * @return the de-serialized object.
	 * @throws NullPointerException if the input stream {@code in} is {@code null}.
	 * @throws IOException if the object could not be read.
	 */
	public Object read(final Path path) throws IOException {
		return read(Object.class, path);
	}

	/**
	 * Reads an object from the given file.
	 *
	 * @param file the file to read from.
	 * @param type the type of the read object.
	 * @return the de-serialized object.
	 * @throws NullPointerException if the input stream {@code in} is {@code null}.
	 * @throws IOException if the object could not be read.
	 */
	public <T> T read(final Class<T> type, final File file)
		throws IOException
	{
		try (final FileInputStream in = new FileInputStream(file)) {
			return read(type, in);
		}
	}

	/**
	 * Reads an object from the given file.
	 *
	 * @param file the file to read from.
	 * @return the de-serialized object.
	 * @throws NullPointerException if the input stream {@code in} is {@code null}.
	 * @throws IOException if the object could not be read.
	 */
	public Object read(final File file) throws IOException {
		return read(Object.class, file);
	}

	/**
	 * Reads an object from the given input stream.
	 *
	 * @param in the input stream to read from.
	 * @param type the type of the read object.
	 * @return the de-serialized object.
	 * @throws NullPointerException if the input stream {@code in} is {@code null}.
	 * @throws IOException if the object could not be read.
	 */
	public abstract <T> T read(final Class<T> type, final InputStream in)
		throws IOException;

	/**
	 * Reads an object from the given input stream.
	 *
	 * @param in the input stream to read from.
	 * @return the de-serialized object.
	 * @throws NullPointerException if the input stream {@code in} is {@code null}.
	 * @throws IOException if the object could not be read.
	 */
	public Object read(final InputStream in) throws IOException {
		return read(Object.class, in);
	}


	/**
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 *@version 1.0 &ndash; <em>$Revision$</em>
	 */
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

	/**
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version 1.0 &ndash; <em>$Revision$</em>
	 */
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
