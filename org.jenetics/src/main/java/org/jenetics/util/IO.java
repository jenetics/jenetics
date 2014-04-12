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

import static org.jenetics.internal.util.jaxb.context;
import static org.jenetics.internal.util.jaxb.adapterFor;
import static org.jenetics.internal.util.jaxb.marshal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
 * Class for object serialization. The following example shows how to write and
 * reload a given population.
 *
 * [code]
 * // Writing the population to disk.
 * final File file = new File("population.xml");
 * IO.jaxb.write(ga.getPopulation(), file);
 *
 * // Reading the population from disk.
 * final Population&lt;DoubleGene,Double&gt; population =
 *     (Population&lt;DoubleGene, Double&gt;)IO.jaxb.read(file);
 * ga.setPopulation(population);
 * [/code]
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0 &mdash; <em>$Date: 2014-04-12 $</em>
 */
public abstract class IO {

	protected IO() {
	}

	/**
	 * JAXB for <i>XML</i> serialization.
	 */
	public static final IO jaxb = new IO() {

		@Override
		public void write(final Object object, final OutputStream out)
			throws IOException
		{
			try {
				final Marshaller marshaller = context().createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				marshaller.marshal(marshal(object), out);
			} catch (Exception e) {
				throw new IOException(e);
			}
		}

		@Override
		public <T> T read(final Class<T> type, final InputStream in)
			throws IOException
		{
			try {
				final Unmarshaller unmarshaller = context().createUnmarshaller();

				//final XMLInputFactory factory = XMLInputFactory.newInstance();
				//final XMLStreamReader reader = factory.createXMLStreamReader(in);
				//try {
					final Object object = unmarshaller.unmarshal(in);
					final XmlAdapter<Object, Object> adapter = adapterFor(object);
					if (adapter != null) {
						return type.cast(adapter.unmarshal(object));
					} else {
						return type.cast(object);
					}
				//} finally {
				//	reader.close();
				//}
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
	};

	/**
	 * IO implementation for "native" <i>Java</i> serialization.
	 */
	public static final IO object = new IO() {

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
			} catch (ClassNotFoundException | ClassCastException e) {
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
	 * @param <T> the type of the read object
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
	 * @param <T> the type of the read object
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
	 * @param <T> the type of the read object
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
	 * @param <T> the type of the read object
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
}
