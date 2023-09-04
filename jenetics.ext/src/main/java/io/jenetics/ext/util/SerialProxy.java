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
package io.jenetics.ext.util;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serial;
import java.io.StreamCorruptedException;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 6.0
 * @since 5.0
 */
final class SerialProxy implements Externalizable {

	@Serial
	private static final long serialVersionUID = 1;

	static final byte TREE_NODE = 1;
	static final byte FLAT_TREE_NODE = 2;
	static final byte TREE_PATH = 3;

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
			case TREE_NODE -> ((TreeNode<?>)_object).write(out);
			case FLAT_TREE_NODE -> ((FlatTreeNode<?>)_object).write(out);
			case TREE_PATH -> ((Tree.Path)_object).write(out);
			default -> throw new StreamCorruptedException("Unknown serialized type.");
		}
	}

	@Override
	public void readExternal(final ObjectInput in)
		throws IOException, ClassNotFoundException
	{
		_type = in.readByte();
		_object = switch (_type) {
			case TREE_NODE -> TreeNode.read(in);
			case FLAT_TREE_NODE -> FlatTreeNode.read(in);
			case TREE_PATH -> Tree.Path.read(in);
			default -> throw new StreamCorruptedException("Unknown serialized type.");
		};
	}

	@Serial
	private Object readResolve() {
		return _object;
	}

}
