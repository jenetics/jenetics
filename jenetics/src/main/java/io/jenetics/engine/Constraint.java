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
package io.jenetics.engine;

import static java.util.Objects.requireNonNull;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.util.Factory;

/**
 * This interface allows you to define constraints on single phenotypes. It is a
 * more advanced version of the {@link Phenotype#isValid()} method, which checks
 * the validity of the underlying genotypes and/or chromosomes. Additionally it
 * is possible to <em>repair</em> invalid individuals. The evolution
 * {@link Engine} is using the constraint in the following way: check the validity
 * and repair invalid individuals.
 * <pre>{@code
 * for (int i = 0; i < population.size(); ++i) {
 *     final Phenotype<G, C> individual = population.get(i);
 *     if (!constraint.test(individual)) {
 *         population.set(i, constraint.repair(individual, generation));
 *     }
 * }
 * }</pre>
 *
 * <b>Note</b><br>
 * Keep in mind, that this interface only repairs invalid individuals, which
 * has been destroyed by the <em>evolution</em> process. Individuals, created
 * by the given {@code Factory<Genotype<G>>}, are not validated and repaired.
 * This means, that it is still possible, to have invalid individuals, created
 * by the genotype factory. The {@link #constrain(Factory)} will wrap the given
 * factory which obeys {@code this} constraint. The following code will show
 * how to create such a <em>constrained</em> genotype factory and use it for
 * creating an evolution engine.
 * <pre>{@code
 * final Constraint<DoubleGene, Double> constraint = ...;
 * final Factory<Genotype<DoubleGene>> gtf = ...;
 * final Engine<DoubleGene, Double> engine = Engine
 *     .builder(fitness, constraint.constrain(gtf))
 *     .constraint(constraint)
 *     .build();
 * }</pre>
 *
 * The following example illustrates how a constraint which its repair function
 * can be look like. Imagine that your problem domain consists of double values
 * between <em>[0, 2)</em> and <em>[8, 10)</em>. Since it is not possible
 * <pre>{@code
 *   +--+--+--+--+--+--+--+--+--+--+
 *   |  |  |  |  |  |  |  |  |  |  |
 *   0  1  2  3  4  5  6  7  8  9  10
 *   |-----|xxxxxxxxxxxxxxxxx|-----|
 *      ^  |llllllll|rrrrrrrr|  ^
 *      |       |        |      |
 *      +-------+        +------+
 * }</pre>
 * The invalid range is marked with {@code x}. Repairing an invalid value will
 * map values in the {@code l} range on the valid range <em>[0, 2)</em>, and
 * value in the {@code r} range on the valid range <em>[8, 10)</em>. This mapping
 * guarantees an even distribution of the values in the valid ranges, which is
 * an important characteristic of the repair function.
 *
 * <pre>{@code
 * final InvertibleCodec<Double, DoubleGene> codec = Codecs.ofScalar(DoubleRange.of(0, 10));
 * final Constraint<DoubleGene, Double> constraint = Constraint.of(
 *     codec,
 *     v -> v < 2 || v >= 8,
 *     v -> {
 *         if (v >= 2 && v < 8) {
 *             return v < 5 ? ((v - 2)/3)*2 : ((8 - v)/3)*2 + 8;
 *         }
 *         return v;
 *     }
 * );
 * }</pre>
 *
 * <b>Alternative solution</b><br>
 * Instead of repairing individuals, it is better to not create invalid one in
 * the first place. Once you have a proper <em>repair</em> strategy, you can use
 * it to create a {@link Codec} which only creates valid individuals, using your
 * repair method.
 * <pre>{@code
 * final Codec<Double, DoubleGene> codec = Codecs
 *     .ofScalar(DoubleRange.of(0, 10))
 *     .map(v -> {
 *             if (v >= 2 && v < 8) {
 *                 return v < 5 ? ((v - 2)/3)*2 : ((8 - v)/3)*2 + 8;
 *             }
 *             return v;
 *         });
 * }</pre>
 * The same example with an {@link InvertibleCodec} will look like this:
 * <pre>{@code
 * final InvertibleCodec<Double, DoubleGene> codec = Codecs
 *     .ofScalar(DoubleRange.of(0, 10))
 *     .map(v -> {
 *             if (v >= 2 && v < 8) {
 *                 return v < 5 ? ((v - 2)/3)*2 : ((8 - v)/3)*2 + 8;
 *             }
 *             return v;
 *         },
 *         Function.identity());
 * }</pre>
 *
 * @see Engine.Builder#constraint(Constraint)
 * @see RetryConstraint
 * @see #constrain(Factory)
 *
 * @apiNote
 * This class is part of the more advanced API and is not needed for default use
 * cases. If the {@link Engine} is created with an explicit constraint
 * ({@link Engine.Builder#constraint(Constraint)}), the <em>default</em>
 * validation mechanism via {@link Phenotype#isValid()} is overridden. Also keep
 * in mind, that a defined constraint doesn't protect the fitness function from
 * <em>invalid</em> values. It is still necessary that the fitness function must
 * handle invalid values accordingly. The constraint <em>only</em> filters
 * invalid individuals after the selection and altering step.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 6.1
 * @since 5.0
 */
public interface Constraint<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
> {

	/**
	 * Checks the validity of the given {@code individual}.
	 *
	 * @param individual the phenotype to check
	 * @return {@code true} if the given {@code individual} is valid,
	 *         {@code false} otherwise
	 * @throws NullPointerException if the given {@code individual} is
	 *         {@code null}
	 */
	boolean test(final Phenotype<G, C> individual);

	/**
	 * Tries to repair the given phenotype. This method is called by the
	 * evolution {@link Engine} if the {@link #test(Phenotype)} method returned
	 * {@code false}.
	 *
	 * @param individual the phenotype to repair
	 * @param generation the actual generation, where this method is called by
	 *        the evolution engine
	 * @return a newly created, valid phenotype. The implementation is free to
	 *         use the given invalid {@code individual} as a starting point for
	 *         the created phenotype.
	 * @throws NullPointerException if the given {@code individual} is
	 *         {@code null}
	 */
	Phenotype<G, C> repair(
		final Phenotype<G, C> individual,
		final long generation
	);

	/**
	 * Wraps the given genotype factory into a factory, which only creates
	 * individuals obeying {@code this} constraint. The following code will
	 * create an evolution engine, where also the genotype factory will only
	 * create valid individuals.
	 *
	 * <pre>{@code
	 * final Constraint<DoubleGene, Double> constraint = ...;
	 * final Factory<Genotype<DoubleGene>> gtf = ...;
	 * final Engine<DoubleGene, Double> engine = Engine
	 *     .builder(fitness, constraint.constrain(gtf))
	 *     .constraint(constraint)
	 *     .build();
	 * }</pre>
	 *
	 * @since 6.1
	 *
	 * @see #constrain(Codec)
	 * @see #constrain(InvertibleCodec)
	 *
	 * @param gtf the genotype factory to wrap
	 * @return a new constrained genotype factory.
	 * @throws NullPointerException if the given genotype factory is {@code null}
	 */
	default Factory<Genotype<G>> constrain(final Factory<Genotype<G>> gtf) {
		requireNonNull(gtf);
		return () -> {
			final Phenotype<G, C> result = Phenotype.of(gtf.newInstance(), 1);
			return (test(result) ? result : repair(result, 1)).genotype();
		};
	}

	/**
	 * Wraps the given codec into a codec, which obeys {@code this} constraint.
	 *
	 * @since 6.1
	 *
	 * @see #constrain(Factory)
	 * @see #constrain(InvertibleCodec)
	 *
	 * @param codec the codec to wrap
	 * @param <T> the argument type of a given problem
	 * @return the wrapped codec, which obeys {@code this} constraint
	 * @throws NullPointerException if the given {@code codec} is {@code null}
	 */
	default <T> Codec<T, G> constrain(final Codec<T, G> codec) {
		return Codec.of(constrain(codec.encoding()), codec.decoder());
	}

	/**
	 * Wraps the given codec into a codec, which obeys {@code this} constraint.
	 *
	 * @since 6.1
	 *
	 * @see #constrain(Factory)
	 * @see #constrain(Codec)
	 *
	 * @param codec the codec to wrap
	 * @param <T> the argument type of a given problem
	 * @return the wrapped codec, which obeys {@code this} constraint
	 * @throws NullPointerException if the given {@code codec} is {@code null}
	 */
	default <T> InvertibleCodec<T, G> constrain(final InvertibleCodec<T, G> codec) {
		return InvertibleCodec.of(
			constrain(codec.encoding()),
			codec.decoder(),
			codec.encoder()
		);
	}

	/**
	 * Return a new constraint object with the given {@code validator} and
	 * {@code repairer}.
	 *
	 * @param validator the phenotype validator used by the constraint
	 * @param repairer the phenotype repairer used by the constraint
	 * @param <G> the gene type
	 * @param <C> the fitness value type
	 * @return a new constraint strategy
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Constraint<G, C> of(
		final Predicate<? super Phenotype<G, C>> validator,
		final BiFunction<? super Phenotype<G, C>, Long, Phenotype<G, C>> repairer
	) {
		requireNonNull(validator);
		requireNonNull(repairer);

		return new Constraint<>() {
			@Override
			public boolean test(final Phenotype<G, C> individual) {
				return validator.test(individual);
			}

			@Override
			public Phenotype<G, C> repair(
				final Phenotype<G, C> individual,
				final long generation
			) {
				return repairer.apply(individual, generation);
			}
		};
	}

	/**
	 * Return a new constraint object with the given {@code validator}. The used
	 * repairer just creates a new phenotype by using the phenotype to be
	 * repaired as template. The <em>repaired</em> phenotype might still be
	 * invalid.
	 *
	 * @see RetryConstraint#of(Predicate)
	 *
	 * @param validator the phenotype validator used by the constraint
	 * @param <G> the gene type
	 * @param <C> the fitness value type
	 * @return a new constraint strategy
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Constraint<G, C> of(final Predicate<? super Phenotype<G, C>> validator) {
		return RetryConstraint.of(validator);
	}

	/**
	 * Return a new constraint object with the given {@code validator} and
	 * {@code repairer}. The given invertible codec allows to simplify the
	 * needed validator and repairer.
	 *
	 * @since 5.2
	 *
	 * @param codec the invertible codec used for simplify the needed
	 *        validator and repairer
	 * @param validator the phenotype validator used by the constraint
	 * @param repairer the phenotype repairer used by the constraint
	 * @param <T> the type of the <em>native</em> problem domain
	 * @param <G> the gene type
	 * @param <C> the fitness value type
	 * @return a new constraint strategy
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	static <T, G extends Gene<?, G>, C extends Comparable<? super C>>
	Constraint<G, C> of(
		final InvertibleCodec<T, G> codec,
		final Predicate<? super T> validator,
		final Function<? super T, ? extends T> repairer
	) {
		requireNonNull(codec);
		requireNonNull(validator);
		requireNonNull(repairer);

		return of(
			pt -> validator.test(codec.decode(pt.genotype())),
			(pt, gen) -> Phenotype.of(
				codec.encode(repairer.apply(codec.decode(pt.genotype()))),
				gen
			)
		);
	}

}
