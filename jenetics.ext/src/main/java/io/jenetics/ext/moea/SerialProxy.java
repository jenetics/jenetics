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
package io.jenetics.ext.moea;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serial;
import java.io.StreamCorruptedException;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.2
 * @since 5.2
 */
final class SerialProxy implements Externalizable {

	@Serial
	private static final long serialVersionUID = 1;

	static final byte SIMPLE_INT_VEC = 1;
	static final byte SIMPLE_LONG_VEC = 2;
	static final byte SIMPLE_DOUBLE_VEC = 3;

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
			case SIMPLE_INT_VEC -> ((SimpleIntVec)_object).write(out);
			case SIMPLE_LONG_VEC -> ((SimpleLongVec)_object).write(out);
			case SIMPLE_DOUBLE_VEC -> ((SimpleDoubleVec)_object).write(out);
			default -> throw new StreamCorruptedException("Unknown serialized type.");
		}
	}

	@Override
	public void readExternal(final ObjectInput in)
		throws IOException
	{
		_type = in.readByte();
		_object = switch (_type) {
			case SIMPLE_INT_VEC -> SimpleIntVec.read(in);
			case SIMPLE_LONG_VEC -> SimpleLongVec.read(in);
			case SIMPLE_DOUBLE_VEC -> SimpleDoubleVec.read(in);
			default -> throw new StreamCorruptedException("Unknown serialized type.");
		};
	}

	@Serial
	private Object readResolve() {
		return _object;
	}

}
