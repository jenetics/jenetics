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
import java.io.Serial;
import java.io.StreamCorruptedException;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.0
 * @since 5.0
 */
final class SerialProxy implements Externalizable {

	@Serial
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

	//static final byte BIT_GENE_STORE = 12;

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
			case DOUBLE_GENE -> ((DoubleGene)_object).write(out);
			case INTEGER_GENE -> ((IntegerGene)_object).write(out);
			case LONG_GENE -> ((LongGene)_object).write(out);
			case BIT_CHROMOSOME -> ((BitChromosome)_object).write(out);
			case DOUBLE_CHROMOSOME -> ((DoubleChromosome)_object).write(out);
			case INTEGER_CHROMOSOME -> ((IntegerChromosome)_object).write(out);
			case LONG_CHROMOSOME -> ((LongChromosome)_object).write(out);
			case CHARACTER_CHROMOSOME -> ((CharacterChromosome)_object).write(out);
			case PERMUTATION_CHROMOSOME -> ((PermutationChromosome<?>)_object).write(out);
			case GENOTYPE -> ((Genotype<?>)_object).write(out);
			case PHENOTYPE -> ((Phenotype<?, ?>)_object).write(out);
			default -> throw new StreamCorruptedException("Unknown serialized type.");
		}
	}

	@Override
	public void readExternal(final ObjectInput in)
		throws IOException, ClassNotFoundException
	{
		_type = in.readByte();
		_object = switch (_type) {
			case DOUBLE_GENE -> DoubleGene.read(in);
			case INTEGER_GENE -> IntegerGene.read(in);
			case LONG_GENE -> LongGene.read(in);
			case BIT_CHROMOSOME -> BitChromosome.read(in);
			case DOUBLE_CHROMOSOME -> DoubleChromosome.read(in);
			case INTEGER_CHROMOSOME -> IntegerChromosome.read(in);
			case LONG_CHROMOSOME -> LongChromosome.read(in);
			case CHARACTER_CHROMOSOME -> CharacterChromosome.read(in);
			case PERMUTATION_CHROMOSOME -> PermutationChromosome.read(in);
			case GENOTYPE -> Genotype.read(in);
			case PHENOTYPE -> Phenotype.read(in);
			default -> throw new StreamCorruptedException("Unknown serialized type.");
		};
	}

	@Serial
	private Object readResolve() {
		return _object;
	}

}
