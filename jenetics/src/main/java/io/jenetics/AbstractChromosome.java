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

import static java.util.Objects.requireNonNull;
import static io.jenetics.internal.util.Hashes.hash;

import java.io.Serializable;
import java.util.Objects;

import io.jenetics.util.ISeq;
import io.jenetics.util.Verifiable;

/**
 * The abstract base implementation of the Chromosome interface. The implementors
 * of this class must assure that the protected member {@code _genes} is not
 * {@code null} and the length of the {@code genes} &gt; 0.
 *
 * @param <G> the gene type.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 5.2
 */
public abstract class AbstractChromosome<G extends Gene<?, G>>
	implements
		Chromosome<G>,
		Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Array of genes which forms the chromosome. This array must
	 * be initialized by the derived classes.
	 */
	protected final transient ISeq<G> _genes;

	/**
	 * Indicates whether this chromosome is valid or not. If the variable is
	 * {@code null} the validation state hasn't been calculated yet.
	 */
	protected transient Boolean _valid = null;

	/**
	 * Create a new {@code AbstractChromosome} from the given {@code genes}
	 * array.
	 *
	 * @param genes the genes that form the chromosome.
	 * @throws NullPointerException if the given gene array is {@code null}.
	 * @throws IllegalArgumentException if the length of the gene sequence is
	 *         empty.
	 */
	protected AbstractChromosome(final ISeq<? extends G> genes) {
		requireNonNull(genes, "Gene array");
		assert genes.forAll(Objects::nonNull) : "Found at least on null gene.";

		if (genes.isEmpty()) {
			throw new IllegalArgumentException(
				"The genes sequence must contain at least one gene."
			);
		}

		_genes = ISeq.upcast(genes);
	}

	@Override
	public G get(final int index) {
		return _genes.get(index);
	}

	@Override
	public int length() {
		return _genes.length();
	}

	@Override
	public boolean isValid() {
		if (_valid == null) {
			_valid = _genes.forAll(Verifiable::isValid);
		}
		return _valid;
	}

	@Override
	public int hashCode() {
		return hash(_genes, hash(getClass()));
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj != null &&
			getClass() == obj.getClass() &&
			Objects.equals(_genes, ((AbstractChromosome<?>)obj)._genes);
	}

	@Override
	public String toString() {
		return Objects.toString(_genes);
	}

}
