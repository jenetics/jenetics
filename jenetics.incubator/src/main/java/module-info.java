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
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 8.1
 */
@SuppressWarnings("module")
module io.jenetics.incubator {
	requires io.jenetics.base;
	requires io.jenetics.ext;
	requires io.jenetics.prog;

	requires java.desktop;
	requires java.net.http;

	requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.datatype.jsr310;
//	requires jakarta.annotation;
//	requires jakarta.validation;
	requires java.logging;
	requires org.apache.commons.statistics.distribution;
//	requires org.openapitools.jackson.nullable;
    requires org.apache.commons.numbers.gamma;
	requires com.sun.codemodel;
	//requires swagger.parser.v3;
	//requires swagger.parser.core;

	exports io.jenetics.incubator.combinatorial;
	exports io.jenetics.incubator.csv;
	exports io.jenetics.incubator.math.iterative;
	exports io.jenetics.incubator.math.rootfinder;
	exports io.jenetics.incubator.math.special;
	exports io.jenetics.incubator.metamodel;
	exports io.jenetics.incubator.metamodel.access;
	exports io.jenetics.incubator.metamodel.internal;
	exports io.jenetics.incubator.metamodel.property;
	exports io.jenetics.incubator.metamodel.type;
	exports io.jenetics.incubator.prog;
	exports io.jenetics.incubator.restful;
	exports io.jenetics.incubator.util;
    exports io.jenetics.incubator.http;
}
