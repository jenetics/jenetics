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
package org.jenetix;

import static org.jenetics.internal.math.random.nextInt;
import static org.jenetics.util.RandomRegistry.getRandom;

import java.io.Serializable;
import java.util.stream.Collector;

import org.jenetics.NumericGene;
import org.jenetics.util.ISeq;
import org.jenetics.util.IntRange;
import org.jenetics.util.Mean;

import org.jenetix.util.TreeNode;

/**
 * Represents a {@link TreeGene} implementation, which holds an {@code Integer}.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class IntegerTreeGene
	extends NumericTreeGene<Integer, IntegerTreeGene>
	implements
		NumericGene<Integer, IntegerTreeGene>,
		Mean<IntegerTreeGene>,
		Comparable<IntegerTreeGene>,
		Serializable
{

	IntegerTreeGene(
		final Integer value,
		final Integer min,
		final Integer max,
		final int[] children
	) {
		super(value, min, max, children);

	}

	@Override
	public IntegerTreeGene mean(final IntegerTreeGene that) {
		return new IntegerTreeGene(
			_value + (that._value - _value)/2,
			_min,
			_max,
			_children
		);
	}

	/* *************************************************************************
	 *  Factory Methods.
	 * ************************************************************************/

	@Override
	public IntegerTreeGene newInstance() {
		return new IntegerTreeGene(
			nextInt(getRandom(), _min, _max), _min, _max, _children
		);
	}

	@Override
	public IntegerTreeGene newInstance(final Integer value) {
		return new IntegerTreeGene(value, _min, _max, _children);
	}

	@Override
	public IntegerTreeGene newInstance(final Number number) {
		return new IntegerTreeGene(number.intValue(), _min, _max, _children);
	}

	/* *************************************************************************
	 *  Static factory methods.
	 * ************************************************************************/

	/**
	 * Create a new  {@code IntegerTreeGene} with the given value and the given
	 * range. If the {@code value} isn't within the interval [min, max], no
	 * exception is thrown. In this case the method
	 * {@link IntegerTreeGene#isValid()} returns {@code false}.
	 *
	 * @param value the value of the gene.
	 * @param min the minimal valid value of this gene (inclusively)
	 * @param max the maximal valid value of this gene (inclusively)
	 * @param children the indexes of the child genes
	 * @return a new {@code IntegerTreeGene} with the given {@code value}
	 * @throws NullPointerException if the given {@code children} is {@code null}
	 */
	public static IntegerTreeGene
	of(final int value, final int min, final int max, final int... children) {
		return new IntegerTreeGene(value, min, max, children);
	}

	/**
	 * Create a new  {@code IntegerTreeGene} with the given value and the given
	 * range. If the {@code value} isn't within the interval [min, max], no
	 * exception is thrown. In this case the method
	 * {@link IntegerTreeGene#isValid()} returns {@code false}.
	 *
	 * @param value the value of the gene.
	 * @param range the integer range to use
	 * @param children the indexes of the child genes
	 * @return a new random {@code IntegerGene}
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public static IntegerTreeGene
	of(final int value, final IntRange range, final int... children) {
		return new IntegerTreeGene(
			value,
			range.getMin(),
			range.getMax(),
			children
		);
	}

	/**
	 * Create a new random {@code IntegerTreeGene}. It is guaranteed that the
	 * value of the {@code IntegerTreeGene} lies in the interval [min, max].
	 *
	 * @param min the minimal valid value of this gene (inclusively)
	 * @param max the maximal valid value of this gene (inclusively)
	 * @param children the indexes of the child genes
	 * @return a new random {@code IntegerTreeGene}
	 * @throws NullPointerException if the given {@code children} is {@code null}
	 */
	public static IntegerTreeGene
	of(final int min, final int max, final int... children) {
		return of(nextInt(getRandom(), min, max), min, max, children);
	}

	/**
	 * Create a new random {@code IntegerTreeGene}. It is guaranteed that the
	 * value of the {@code IntegerTreeGene} lies in the interval [min, max].
	 *
	 * @param range the integer range to use
	 * @param children the indexes of the child genes
	 * @return a new random {@code IntegerTreeGene}
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public static IntegerTreeGene
	of(final IntRange range, final int... children) {
		return of(
			nextInt(getRandom(), range.getMin(), range.getMax()),
			range,
			children
		);
	}

	/**
	 * Return a collector, which collects a {@link TreeNode} stream into a
	 * sequence of {@link TreeGene}s. The collection process is also referred as
	 * <em>node linearization</em>.
	 *
	 * <pre>{@code
	 * final TreeNode<Integer> root =
	 * TreeNode.of(0)
	 *     .add(TreeNode.of(-1)
	 *         .add(TreeNode.of(-2))
	 *         .add(TreeNode.of(-3)))
	 *     .add(TreeNode.of(1)
	 *         .add(TreeNode.of(2))
	 *         .add(TreeNode.of(3)));
	 *
	 * final ISeq<IntegerTreeGene> linearizedTree = root
	 *     .breathFirstStream()
	 *     // It is assumed that 'MyTreeGene' has a (Integer, int[]) constructor.
	 *     .collect(toLinearizedGenes(IntRange.of(-100, 100));
	 * }</pre>
	 *
	 * @param range the integer range to use
	 * @return a linearized {@code TreeGene} representation of the
	 *         <em>collected</em> {@code TreeNode} stream
	 * @throws NullPointerException if the given gene factory is {@code null}
	 */
	public static Collector<TreeNode<Integer>, ?, ISeq<IntegerTreeGene>>
	toLinearizedGenes(final IntRange range) {
		return null; //TreeGene.toLinearizedGenes((v, c) -> of(v, range, c));
	}

}
