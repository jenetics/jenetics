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

/**
 * <h2>Operations</h2>
 *
 * When creating a new program tree, it is not necessary to implement own
 * instance of the {@code ProgramGene} or {@code ProgramChromosome} class. The
 * extension point for own programs is the {@code Op} interface.
 *
 * <pre>{@code
 * public interface Op<T> {
 *     public String name();
 *     public int arity();
 *     public T apply(T[] args);
 * }
 * }</pre>
 *
 * <pre>{@code
 * final Op<Double> myop = Op.of("myop", 3, v -> v[0]*v[1] + v[2]);
 * }</pre>
 *
 * In the example above, a new operation with the "myop" and arity 3 is defined.
 * Whenever the operation is evaluated, the function <em>f(x, y, z) = x*y + z</em>
 * is executed.
 * <p>
 *     <b>NOTE</b>: <em>The class {@link io.jenetics.prog.op.MathOp} in the
 *     defines a set of mathematical standard operations/functions.</em>
 * </p>
 *
 * When creating a new {@link io.jenetics.prog.ProgramChromosome} we must
 * distinguish two different kind of operations:
 * <ol>
 *     <li><em>Non-terminal</em> operations have an arity greater than zero and
 *     form their own sub-tree</li>
 *     <li><em>Terminal</em> operations have an arity of zero and form the
 *     leaves of a program tree.</li>
 * </ol>
 *
 * There are currently three different types of non-terminal operations
 * implemented, {@link io.jenetics.prog.op.Var},
 * {@link io.jenetics.prog.op.Const} and
 * {@link io.jenetics.prog.op.EphemeralConst}.
 *
 * <h3>Var</h3>
 *
 * The {@code Var} operation defines a variable of a program, which are set
 * from the program arguments.
 *
 * <pre>{@code
 * final ISeq<Op<Double>> terminals = ISeq.of(
 *     Var.of("x", 0), Var.of("y", 1), Var.of("z", 2)
 * );
 * }</pre>
 *
 * The terminal operation list in the example code above will lead to a program
 * which takes three input parameters, <em>x</em>, <em>y</em> and <em>z</em>,
 * with the argument indices <em>0</em>, <em>1</em> and <em>2</em>.
 *
 * <h3>Const</h3>
 *
 * The {@code Const} operation will always return the same, constant, value
 * when evaluated.
 *
 * <pre>{@code
 * final Op<Double> one = Const.of(1.0);
 * final Op<Double> pi = Const.of("π", Math.PI);
 * }</pre>
 *
 * We can create a constant operation in to flavors, with a value only and with
 * a dedicated name. If a constant has a name, the <em>symbolic</em> name is
 * used, instead of the value, when the program tree is printing.
 *
 * <h3>EphemeralConst</h3>
 *
 * An ephemeral constant is a special constant, which is only constant within an
 * tree. If a new tree is created, a new constant is created, by the `
 * {@link java.util.function.Supplier} function the ephemeral constant is created
 * with.
 *
 * <pre>{@code
 * final Op<Double> rand1 = EphemeralConst.of(Math::random);
 * final Op<Double> rand2 = EphemeralConst.of("R", Math::random);
 * }</pre>
 *
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.9
 * @since 3.9
 */
package io.jenetics.prog.op;
