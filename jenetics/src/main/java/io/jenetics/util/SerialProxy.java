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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serial;
import java.io.StreamCorruptedException;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 6.0
 * @since 6.0
 */
final class SerialProxy implements Externalizable {

	@Serial
	private static final long serialVersionUID = 1;

	static final byte DOUBLE_RANGE = 1;
	static final byte INT_RANGE = 2;
	static final byte LONG_RANGE = 3;

	/**
	 * The type being serialized.
	 */
	private byte _type;

	/**
	 * The object being serialized.
	 */
	private Object _object;

	/**
	 * Constructor for deserialization.
	 */
	public SerialProxy() {
	}

	/**
	 * Creates an instance for serialization.
	 *
	 * @param type  the type
	 * @param object  the object
	 */
	SerialProxy(final byte type, final Object object) {
		_type = type;
		_object = object;
	}

	@Override
	public void writeExternal(final ObjectOutput out) throws IOException {
		out.writeByte(_type);
		switch (_type) {
			case DOUBLE_RANGE -> ((DoubleRange)_object).write(out);
			case INT_RANGE -> ((IntRange)_object).write(out);
			case LONG_RANGE -> ((LongRange)_object).write(out);
			default -> throw new StreamCorruptedException("Unknown serialized type.");
		}
	}

	@Override
	public void readExternal(final ObjectInput in)
		throws IOException
	{
		_type = in.readByte();
		_object = switch (_type) {
			case DOUBLE_RANGE -> DoubleRange.read(in);
			case INT_RANGE -> IntRange.read(in);
			case LONG_RANGE -> LongRange.read(in);
			default -> throw new StreamCorruptedException("Unknown serialized type.");
		};
	}

	@Serial
	private Object readResolve() {
		return _object;
	}

}
