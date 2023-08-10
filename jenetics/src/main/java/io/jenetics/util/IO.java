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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;

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
 * final File file = new File("population.bin");
 * IO.object.write(result.getPopulation(), file);
 *
 * // Reading the population from disk.
 * ISeq<Phenotype<G, C>> population = (ISeq<Phenotype<G, C>>)IO.object.read(file);
 * EvolutionStream<DoubleGene, Double> stream = Engine
 *     .build(ff, gtf)
 *     .stream(population, 1);
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 4.0
 */
public abstract class IO {

	protected IO() {
	}

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
	 * Serializes the given {@code object} to a {@code byte[]} array.
	 *
	 * @since 4.1
	 *
	 * @param object the object to serialize.
	 * @throws NullPointerException if one of the objects is {@code null}.
	 * @throws IOException if the object could not be serialized.
	 * @return the serialized {@code object} as {@code byte[]} array
	 */
	public byte[] toByteArray(final Object object) throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		write(object, out);
		return out.toByteArray();
	}

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
	 * Creates a, previously serialized, object from the given {@code byte[]}
	 * array.
	 *
	 * @since 4.1
	 *
	 * @param bytes the serialized object.
	 * @return the deserialized object.
	 * @throws NullPointerException if the input {@code bytes} is {@code null}.
	 * @throws IOException if the object could not be deserialized.
	 */
	public Object fromByteArray(final byte[] bytes) throws IOException {
		final ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		return read(in);
	}

	/**
	 * Reads an object from the given file.
	 *
	 * @param <T> the type of the read object
	 * @param path the path to read from.
	 * @param type the type of the read object.
	 * @return the deserialized object.
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
	 * @return the deserialized object.
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
	 * @return the deserialized object.
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
	 * @return the deserialized object.
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
	 * @return the deserialized object.
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
	 * @return the deserialized object.
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
	 * @return the deserialized object.
	 * @throws NullPointerException if the input stream {@code in} is {@code null}.
	 * @throws IOException if the object could not be read.
	 */
	public abstract <T> T read(final Class<T> type, final InputStream in)
		throws IOException;

	/**
	 * Reads an object from the given input stream.
	 *
	 * @param in the input stream to read from.
	 * @return the deserialized object.
	 * @throws NullPointerException if the input stream {@code in} is {@code null}.
	 * @throws IOException if the object could not be read.
	 */
	public Object read(final InputStream in) throws IOException {
		return read(Object.class, in);
	}
}
