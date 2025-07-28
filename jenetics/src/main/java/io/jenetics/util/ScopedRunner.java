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
package io.jenetics.util;

/**
 * Runs code with specifically bound context values. Its extracts the
 * {@link java.lang.ScopedValue.Carrier#run(Runnable)} and
 * {@link java.lang.ScopedValue.Carrier#call(ScopedValue.CallableOp)} method
 * into an interface.
 *
 * @see java.lang.ScopedValue.Carrier
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface ScopedRunner {

	/**
	 * Runs an operation with each context value in this mapping bound to
	 * its value in the current thread.
	 *
	 * @see ScopedValue.Carrier#run(Runnable)
	 *
	 * @param op the operation to run
	 */
	void run(Runnable op);

	/**
	 * Calls a value-returning operation with each context value in this
	 * mapping bound to its value in the current thread.
	 *
	 * @see ScopedValue.Carrier#call(ScopedValue.CallableOp)
	 *
	 * @param op the operation to run
	 * @param <R> the type of the result of the operation
	 * @param <X> type of the exception thrown by the operation
	 * @return the result
	 * @throws X if {@code op} completes with an exception
	 */
	<R, X extends Throwable> R call(ScopedValue.CallableOp<? extends R, X> op) throws X;

}
