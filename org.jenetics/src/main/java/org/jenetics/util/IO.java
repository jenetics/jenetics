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

import static org.jenetics.internal.util.JAXBContextCache.context;
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
import java.util.Arrays;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.jenetics.internal.util.JAXBContextCache;
import org.jenetics.internal.util.require;

/**
 * Class for object serialization. The following example shows how to write and
 * reload a given population.
 *
 * <pre>{@code
 * // Creating result population.
 * EvolutionResult<DoubleGene, Double> result = stream
 *     .collect(toBestEvolutionResult());
 *
 * // Writing the population to disk.
 * final File file = new File("population.xml");
 * IO.jaxb.write(result.getPopulation(), file);
 *
 * // Reading the population from disk.
 * Population<DoubleGene, Double> population =
 *     (Population<DoubleGene, Double>)IO.jaxb.read(file);
 * EvolutionStream<DoubleGene, Double> stream = Engine
 *     .build(ff, gtf)
 *     .stream(population, 1);
 * }</pre>
 *
 * The {@code jaxb} marshalling also allows to read and write own classes. For
 * this you have to register your {@code @XmlType}d class first.
 * <pre>{@code
 * // The user defined 'JAXB' model class.
 * \@XmlRootElement(name = "data-class")
 * \@XmlType(name = "DataClass")
 * \@XmlAccessorType(XmlAccessType.FIELD)
 * public static final class DataClass {
 *     \@XmlAttribute public String name;
 *     \@XmlValue public String value;
 * }
 *
 * // Register the 'JAXB' model class.
 * IO.JAXB.register(DataClass.class);
 * final DataClass data = ...;
 * IO.jaxb.write(data, "data.xml");
 * }</pre>
 *
 * It is safe to call {@code IO.JAXB.register(DataClass.class)} more than once.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 3.5
 */
public abstract class IO {

	protected IO() {
	}

	/**
	 * Helper class for <em>JAXB</em> class registering/de-registering.
	 *
	 * <pre>{@code
	 * // The user defined 'JAXB' model class.
	 * \@XmlRootElement(name = "data-class")
	 * \@XmlType(name = "DataClass")
	 * \@XmlAccessorType(XmlAccessType.FIELD)
	 * public static final class DataClass {
	 *     \@XmlAttribute public String name;
	 *     \@XmlValue public String value;
	 * }
	 *
	 * // Register the 'JAXB' model class.
	 * IO.JAXB.register(DataClass.class);
	 * final DataClass data = ...;
	 * IO.jaxb.write(data, "data.xml");
	 * }</pre>
	 *
	 * It is safe to call {@code IO.JAXB.register(DataClass.class)} more than
	 * once.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 3.5
	 * @version 3.5
	 */
	public static final class JAXB {
		private JAXB() {require.noInstance();}

		/**
		 * Registers the given <em>JAXB</em> model classes. This allows to use
		 * the {@code IO.jaxb} class with own <em>JAXB</em> marshallings.
		 * <p>
		 * <em>It is safe to call this method more than once for a given class.
		 * The class is registered only once.</em>
		 *
		 * @param classes the <em>JAXB</em> model classes to register
		 * @throws NullPointerException if one of the classes is {@code null}
		 */
		public static void register(final Class<?>... classes) {
			Arrays.asList(classes).forEach(JAXBContextCache::add);
		}

		/**
		 * De-registers the given <em>JAXB</em> model classes.
		 *
		 * @param classes the <em>JAXB</em> model classes to register
		 * @throws NullPointerException if one of the classes is {@code null}
		 */
		public static void deregister(final Class<?>... classes) {
			Arrays.asList(classes).forEach(JAXBContextCache::remove);
		}

		/**
		 * Check is the given class is already registered.
		 *
		 * @param cls the class to check
		 * @return {@code true} if the given class is already registered,
		 *         {@code false} otherwise.
		 */
		public static boolean contains(final Class<?> cls) {
			return JAXBContextCache.contains(cls);
		}
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
