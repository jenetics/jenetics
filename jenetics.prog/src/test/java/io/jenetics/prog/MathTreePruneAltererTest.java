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
package io.jenetics.prog;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.AltererResult;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.util.ISeq;
import io.jenetics.util.Seq;

import io.jenetics.prog.op.Const;
import io.jenetics.prog.op.MathExpr;
import io.jenetics.prog.op.MathOp;
import io.jenetics.prog.op.Op;
import io.jenetics.prog.op.Var;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
@Test(enabled = false)
public class MathTreePruneAltererTest {

	static final ISeq<Op<Double>> OPERATIONS = ISeq.of(
		MathOp.ADD,
		MathOp.SUB,
		MathOp.MUL,
		MathOp.MOD,
		MathOp.DIV,
		MathOp.POW
	);

	static final ISeq<Op<Double>> TERMINALS = ISeq.of(
		Var.of("x", 0),
		Var.of("y", 1),
		Var.of("z", 2),
		Const.of(1.0),
		Const.of(2.0),
		Const.of(10.0)
	);

	//@Test(invocationCount = 10)
	public void prune() {
		final MathRewriteAlterer<ProgramGene<Double>, Double> alterer =
			new MathRewriteAlterer<>(1);

		final Genotype<ProgramGene<Double>> gt = Genotype.of(
			ProgramChromosome.of(10, OPERATIONS, TERMINALS)
		);

		final Phenotype<ProgramGene<Double>, Double> pt =
			Phenotype.of(gt, 1);

		final AltererResult<ProgramGene<Double>, Double> result =
			alterer.alter(Seq.of(pt), 1);

		final ProgramGene<Double> program = result
			.getPopulation().get(0)
			.getGenotype()
			.getGene();


		final MathExpr exp1 = new MathExpr(gt.getGene()).simplify();
		final MathExpr exp2 = new MathExpr(program);
		Assert.assertEquals(exp2, exp1);
	}

}
