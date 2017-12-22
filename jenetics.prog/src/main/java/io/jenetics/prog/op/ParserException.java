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
package io.jenetics.prog.op;

import io.jenetics.prog.op.Tokenizer.Token;

/**
 * A simple subclass of RuntimeException that indicates errors when trying to
 * parse the input to Parser.
 *
 * The exception stores the token that caused the error.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class ParserException extends RuntimeException {
	private static final long serialVersionUID = -1009747984332258423L;

	/** the token that caused the error */
	private Token token = null;

	/**
	 * Construct the evaluation exception with a message.
	 * @param message the message containing the cause of the exception
	 */
	public ParserException(String message)
	{
		super(message);
	}

	/**
	 * Construct the evaluation exception with a message and a token.
	 * @param message the message containing the cause of the exception
	 * @param token the token that caused the exception
	 */
	public ParserException(String message, Token token)
	{
		super(message);
		this.token = token;
	}

	/**
	 * Get the token.
	 * @return the token that caused the exception
	 */
	public Token getToken()
	{
		return token;
	}

	/**
	 * Overrides RuntimeException.getMessage to add the token information
	 * into the error message.
	 *
	 *  @return the error message
	 */
	public String getMessage()
	{
		String msg = super.getMessage();
		if (token != null)
		{
			msg = msg.replace("%s", token.sequence);
		}
		return msg;
	}

}
