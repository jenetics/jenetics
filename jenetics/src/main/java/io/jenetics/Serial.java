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
package io.jenetics;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.StreamCorruptedException;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.0
 * @since 5.0
 */
final class Serial implements Externalizable {

	private static final long serialVersionUID = 1;

	static final byte DOUBLE_GENE = 1;
	static final byte INTEGER_GENE = 2;
	static final byte LONG_GENE = 3;

	static final byte BIT_CHROMOSOME = 4;
	static final byte DOUBLE_CHROMOSOME = 5;
	static final byte INTEGER_CHROMOSOME = 6;
	static final byte LONG_CHROMOSOME = 7;
	static final byte CHARACTER_CHROMOSOME = 8;
	static final byte PERMUTATION_CHROMOSOME = 9;

	static final byte GENOTYPE = 10;
	static final byte PHENOTYPE = 11;

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
			case DOUBLE_GENE: ((DoubleGene)_object).write(out); break;
			case INTEGER_GENE: ((IntegerGene)_object).write(out); break;
			case LONG_GENE: ((LongGene)_object).write(out); break;
			case BIT_CHROMOSOME: ((BitChromosome)_object).write(out); break;
			case DOUBLE_CHROMOSOME: ((DoubleChromosome)_object).write(out); break;
			case INTEGER_CHROMOSOME: ((IntegerChromosome)_object).write(out); break;
			case LONG_CHROMOSOME: ((LongChromosome)_object).write(out); break;
			case CHARACTER_CHROMOSOME: ((CharacterChromosome)_object).write(out); break;
			case PERMUTATION_CHROMOSOME: ((PermutationChromosome)_object).write(out); break;
			case GENOTYPE: ((Genotype)_object).write(out); break;
			case PHENOTYPE: ((Phenotype)_object).write(out); break;
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
			case DOUBLE_GENE: _object = DoubleGene.read(in); break;
			case INTEGER_GENE: _object = IntegerGene.read(in); break;
			case LONG_GENE: _object = LongGene.read(in); break;
			case BIT_CHROMOSOME: _object = BitChromosome.read(in); break;
			case DOUBLE_CHROMOSOME: _object = DoubleChromosome.read(in); break;
			case INTEGER_CHROMOSOME: _object = IntegerChromosome.read(in); break;
			case LONG_CHROMOSOME: _object = LongChromosome.read(in); break;
			case CHARACTER_CHROMOSOME: _object = CharacterChromosome.read(in); break;
			case PERMUTATION_CHROMOSOME: _object = PermutationChromosome.read(in); break;
			case GENOTYPE: _object = Genotype.read(in); break;
			case PHENOTYPE: _object = Phenotype.read(in); break;
			default:
				throw new StreamCorruptedException("Unknown serialized type.");
		}
	}

	private Object readResolve() {
		return _object;
	}

}
