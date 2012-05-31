/*
 * Java Genetic Algorithm Library (@!identifier!@).
 * Copyright (c) @!year!@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.util;

import static org.jenetics.util.object.nonNull;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import javolution.context.ConcurrentContext;
import javolution.context.ObjectFactory;
import javolution.util.FastList;

/**
 * Since the parallelization of the library is build on the {@link ConcurrentContext}
 * of the <a href="http://javolution.org/">Javolution</a> project, this class
 * allows you to share a common {@link ForkJoinPool} for the GA and the rest of
 * your application.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &ndash; <em>$Revision$</em>
 */
public final class ForkJoinContext extends ConcurrentContext {

	private static final long serialVersionUID = 1L;

	private final static AtomicReference<ForkJoinPool> _POOL = new AtomicReference<>();

	private final FastList<Future<?>> _futures = new FastList<>(10);

	ForkJoinContext() {
	}

	/**
	 * Set the fork-join-pool used by this context. This method doesn't replace
	 * an already set {@link ForkJoinPool}. Before the <i>context</i> can be
	 * used a {@link ForkJoinPool} must be set.
	 *
	 * @param pool the fork-join-pool to use.
	 * @return {@code true} if the given pool has been set, {@code false}
	 *          otherwise.
	 * @throws NullPointerException if the pool is {@code null}.
	 */
	public static boolean setForkkJoinPool(final ForkJoinPool pool) {
		return _POOL.compareAndSet(null, nonNull(pool, "ForkJoinPool"));
	}

	/**
	 * Return the current fork-join-pool used by this context.
	 *
	 * @return the current fork-join-pool used by this context. Can be
	 *          {@code null} if not set jet.
	 */
	public static ForkJoinPool getForkJoinPool() {
		return _POOL.get();
	}

	@Override
	protected void enterAction() {
		if (_POOL.get() == null) {
			throw new IllegalStateException("No ForkJoinPool set.");
		}
		_futures.clear();
	}

	@Override
	protected void executeAction(final Runnable logic) {
		_futures.add(_POOL.get().submit(logic));
	}



	@Override
	protected void exitAction() {
		try {
			for (FastList.Node<Future<?>> n = _futures.head(),
				end = _futures.tail(); (n = n.getNext()) != end;)
			{
				n.getValue().get();
			}
		} catch (InterruptedException e) {
			throw (CancellationException)new CancellationException().initCause(e);
		} catch (ExecutionException e) {
			throw (CancellationException)new CancellationException().initCause(e);
		}
	}

	static {
		ObjectFactory.setInstance(
			new ObjectFactory<ForkJoinContext>() {
				@Override protected ForkJoinContext create() {
					return new ForkJoinContext();
				}
			},
			ForkJoinContext.class
		);
	}

}




