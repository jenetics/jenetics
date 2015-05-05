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
package org.jenetics.util;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public abstract class Retry implements IRetryAnalyzer {

	public static final class One extends Retry {{
		retry = 1;
	}}

	public static final class Two extends Retry {{
		retry = 2;
	}}

	public static final class Three extends Retry {{
		retry = 3;
	}}

	public static final class Four extends Retry {{
		retry = 4;
	}}

	public static final class Five extends Retry {{
		retry = 5;
	}}


	int retry;

	@Override
	public boolean retry(final ITestResult result) {
		return
			!result.isSuccess() &&
			result.getMethod().getFailedInvocationNumbers().size() < retry;
	}

}
