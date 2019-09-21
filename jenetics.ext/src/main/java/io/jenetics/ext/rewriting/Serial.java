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
package io.jenetics.ext.rewriting;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.StreamCorruptedException;

import io.jenetics.ext.rewriting.TreePattern.Val;
import io.jenetics.ext.rewriting.TreePattern.Var;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.0
 * @since 5.0
 */
final class Serial implements Externalizable {

	private static final long serialVersionUID = 1;

	static final byte TREE_PATTERN = 1;
	static final byte TREE_PATTERN_VAR = 2;
	static final byte TREE_PATTERN_VAL = 3;
	static final byte TREE_REWRITE_RULE = 4;
	static final byte TRS_KEY = 5;

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
	public Serial() {
	}

	/**
	 * Creates an instance for serialization.
	 *
	 * @param type  the type
	 * @param object  the object
	 */
	Serial(final byte type, final Object object) {
		_type = type;
		_object = object;
	}

	@Override
	public void writeExternal(final ObjectOutput out) throws IOException {
		out.writeByte(_type);
		switch (_type) {
			case TREE_PATTERN: ((TreePattern)_object).write(out); break;
			case TREE_PATTERN_VAR: ((Var)_object).write(out); break;
			case TREE_PATTERN_VAL: ((Val)_object).write(out); break;
			case TREE_REWRITE_RULE: ((TreeRewriteRule)_object).write(out); break;
			case TRS_KEY: ((TRS)_object).write(out); break;
			default:
				throw new StreamCorruptedException("Unknown serialized type.");
		}
	}

	@Override
	public void readExternal(final ObjectInput in)
		throws IOException, ClassNotFoundException
	{
		_type = in.readByte();
		switch (_type) {
			case TREE_PATTERN: _object = TreePattern.read(in); break;
			case TREE_PATTERN_VAR: _object = Var.read(in); break;
			case TREE_PATTERN_VAL: _object = Val.read(in); break;
			case TREE_REWRITE_RULE: _object = TreeRewriteRule.read(in); break;
			case TRS_KEY: _object = TRS.read(in); break;
			default:
				throw new StreamCorruptedException("Unknown serialized type.");
		}
	}

	private Object readResolve() {
		return _object;
	}

}

