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
 * This package contains the implementation of a Tree (Term) Rewrite System.
 * It's main purpose is to deliver a DSL which simplifies the definition of
 * rewrite rules, which work on the existing {@link io.jenetics.ext.util.Tree}
 * implementations. This DSL is also used in the {@code io.jenetics.prog} module
 * for simplifying arithmetic expression trees.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Rewriting">TRS</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.0
 * @since 5.0
 */
package io.jenetics.ext.rewriting;
