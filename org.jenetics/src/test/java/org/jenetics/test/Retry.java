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
package org.jenetics.test;

import java.util.concurrent.atomic.AtomicInteger;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public abstract class Retry implements IRetryAnalyzer {

	public static final class One extends Retry {{
		_retry = 1;
	}}

	public static final class Two extends Retry {{
		_retry = 2;
	}}

	public static final class Three extends Retry {{
		_retry = 3;
	}}

	public static final class Four extends Retry {{
		_retry = 4;
	}}

	public static final class Five extends Retry {{
		_retry = 5;
	}}


	int _retry = 3;

	private final AtomicInteger _retryCount = new AtomicInteger();

	public boolean isRetryAvailable() {
		return _retryCount.intValue() <= 5;
	}

	@Override
	public boolean retry(final ITestResult result) {
		boolean retry = false;
		if (!result.isSuccess() && isRetryAvailable()) {
			System.out.println("Going to retry test case: " +
				result.getMethod() + ", " +
				(_retry - _retryCount.intValue()) + " out of " + _retry);
			retry = true;
			_retryCount.incrementAndGet();
			result.setStatus(ITestResult.SKIP);
		}

		return retry;
	}

}
