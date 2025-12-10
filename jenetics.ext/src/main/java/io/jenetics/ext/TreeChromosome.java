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
package io.jenetics.ext;

import io.jenetics.Chromosome;

/**
 * Chromosome for tree shaped genes.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 6.0
 * @since 3.9
 */
public interface TreeChromosome<A, G extends TreeGene<A, G>>
	extends Chromosome<G>
{

	/**
	 * Return the root gene of this chromosome. Since the gene type is also a
	 * {@link io.jenetics.ext.util.Tree}, you are able to assign it to one.
	 * {@snippet lang="java":
	 * final Tree<A, ?> t1 = root();
	 * final Tree<?, ?> t2 = root();
	 * }
	 * This method is also an alias for {@link #gene()}, which returns the
	 * first gene of the chromosome.
	 *
	 * @see #gene()
	 *
	 * @return the root tree gene of this chromosome
	 */
	default G root() {
		return gene();
	}

}
