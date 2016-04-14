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
package org.jenetics.engine;

import java.util.function.Function;
import java.util.stream.LongStream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.Genotype;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class EngineTest {

	@Test(dataProvider = "generations")
	public void generationCount(final Long generations) {
		final Engine<DoubleGene, Double> engine = Engine
			.builder(a -> a.getGene().getAllele(), DoubleChromosome.of(0, 1))
			.build();

		final EvolutionResult<DoubleGene, Double> result = engine.stream()
			.limit(generations)
			.collect(EvolutionResult.toBestEvolutionResult());

		Assert.assertEquals(generations.longValue(), result.getTotalGenerations());
	}

	@Test(dataProvider = "generations")
	public void generationLimit(final Long generations) {
		final Engine<DoubleGene, Double> engine = Engine
			.builder(a -> a.getGene().getAllele(), DoubleChromosome.of(0, 1))
			.build();

		final EvolutionResult<DoubleGene, Double> result = engine.stream()
			.limit(limit.byFixedGeneration(generations))
			.collect(EvolutionResult.toBestEvolutionResult());

		Assert.assertEquals(generations.longValue(), result.getTotalGenerations());
	}

	@DataProvider(name = "generations")
	public Object[][] generations() {
		return LongStream.rangeClosed(1, 10)
			.mapToObj(i -> new Object[]{i})
			.toArray(Object[][]::new);
	}

	@Test
	public void phenotypeValidator() {
		final int populationSize = 100;

		final Engine<DoubleGene, Double> engine = Engine
			.builder(a -> a.getGene().getAllele(), DoubleChromosome.of(0, 1))
			.phenotypeValidator(pt -> false)
			.populationSize(populationSize)
			.build();

		final EvolutionResult<DoubleGene, Double> result = engine.stream()
			.limit(10)
			.collect(EvolutionResult.toBestEvolutionResult());

		Assert.assertEquals(result.getInvalidCount(), populationSize);
	}

	@Test
	public void genotypeValidator() {
		final int populationSize = 100;

		final Engine<DoubleGene, Double> engine = Engine
			.builder(a -> a.getGene().getAllele(), DoubleChromosome.of(0, 1))
			.genotypeValidator(pt -> false)
			.populationSize(populationSize)
			.build();

		final EvolutionResult<DoubleGene, Double> result = engine.stream()
			.limit(10)
			.collect(EvolutionResult.toBestEvolutionResult());

		Assert.assertEquals(result.getInvalidCount(), populationSize);
	}

	// https://github.com/jenetics/jenetics/issues/47
	@Test(timeOut = 15_000L)
	public void deadLock() {
		final Function<Genotype<DoubleGene>, Double> ff = gt -> {
			try {
				Thread.sleep( 50 );
			} catch (InterruptedException ignore) {
				Thread.currentThread().interrupt();
			}
			return gt.getGene().getAllele();
		};

		final Engine<DoubleGene, Double> engine = Engine
			.builder(ff, DoubleChromosome.of(0, 1))
			//.executor(Executors.newFixedThreadPool(10))
			.populationSize(10)
			.build();

		final EvolutionResult<DoubleGene, Double> result = engine.stream()
			.limit(3)
			.collect(EvolutionResult.toBestEvolutionResult());

		//Assert.assertEquals(25L, result.getTotalGenerations());
	}

}
/*
Program Files\Java\jdk1.8.0_74\bin\java" -Didea.launcher.port=7532 "-Didea.launcher.bin.path=C:\Program Files (x86)\JetBrains\IntelliJ IDEA 15.0.3\bin" -Dfile.encoding=UTF-8 -classpath "C:\Program Files\Java\jdk1.8.0_74\jre\lib\charsets.jar;C:\Program Files\Java\jdk1.8.0_74\jre\lib\deploy.jar;C:\Program Files\Java\jdk1.8.0_74\jre\lib\ext\access-bridge-64.jar;C:\Program Files\Java\jdk1.8.0_74\jre\lib\ext\cldrdata.jar;C:\Program Files\Java\jdk1.8.0_74\jre\lib\ext\dnsns.jar;C:\Program Files\Java\jdk1.8.0_74\jre\lib\ext\jaccess.jar;C:\Program Files\Java\jdk1.8.0_74\jre\lib\ext\jfxrt.jar;C:\Program Files\Java\jdk1.8.0_74\jre\lib\ext\localedata.jar;C:\Program Files\Java\jdk1.8.0_74\jre\lib\ext\nashorn.jar;C:\Program Files\Java\jdk1.8.0_74\jre\lib\ext\sunec.jar;C:\Program Files\Java\jdk1.8.0_74\jre\lib\ext\sunjce_provider.jar;C:\Program Files\Java\jdk1.8.0_74\jre\lib\ext\sunmscapi.jar;C:\Program Files\Java\jdk1.8.0_74\jre\lib\ext\sunpkcs11.jar;C:\Program Files\Java\jdk1.8.0_74\jre\lib\ext\zipfs.jar;C:\Program Files\Java\jdk1.8.0_74\jre\lib\javaws.jar;C:\Program Files\Java\jdk1.8.0_74\jre\lib\jce.jar;C:\Program Files\Java\jdk1.8.0_74\jre\lib\jfr.jar;C:\Program Files\Java\jdk1.8.0_74\jre\lib\jfxswt.jar;C:\Program Files\Java\jdk1.8.0_74\jre\lib\jsse.jar;C:\Program Files\Java\jdk1.8.0_74\jre\lib\management-agent.jar;C:\Program Files\Java\jdk1.8.0_74\jre\lib\plugin.jar;C:\Program Files\Java\jdk1.8.0_74\jre\lib\resources.jar;C:\Program Files\Java\jdk1.8.0_74\jre\lib\rt.jar;C:\Users\franz\Workspace\Development\Projects\Jenetics\org.jenetics.tool\build\classes\main;C:\Users\franz\Workspace\Development\Projects\Jenetics\org.jenetics.tool\build\resources\main;C:\Users\franz\Workspace\Development\Projects\Jenetics\org.jenetics\build\classes\main;C:\Users\franz\Workspace\Development\Projects\Jenetics\org.jenetics\build\resources\main;C:\Users\franz\.gradle\caches\modules-2\files-2.1\org.openjdk.jmh\jmh-core\1.11.1\edf0778da76c9b487035285e558c4f27f4e4cd64\jmh-core-1.11.1.jar;C:\Users\franz\.gradle\caches\modules-2\files-2.1\org.openjdk.jmh\jmh-generator-annprocess\1.11.1\25676b9861ad732b7042b220b71b7bcf8e06d8a9\jmh-generator-annprocess-1.11.1.jar;C:\Users\franz\.gradle\caches\modules-2\files-2.1\net.sf.jopt-simple\jopt-simple\4.6\306816fb57cf94f108a43c95731b08934dcae15c\jopt-simple-4.6.jar;C:\Users\franz\.gradle\caches\modules-2\files-2.1\org.apache.commons\commons-math3\3.2\ec2544ab27e110d2d431bdad7d538ed509b21e62\commons-math3-3.2.jar;C:\Program Files (x86)\JetBrains\IntelliJ IDEA 15.0.3\lib\idea_rt.jar" com.intellij.rt.execution.application.AppMain org.jenetics.tool.optimizer.EvolutionParamOptimizer
Continue at generation 1.
Generation=1, Fitness=2197.000000, Raw fitness=10985.000000
Exception in thread "main" java.util.concurrent.CompletionException: java.util.ConcurrentModificationException: java.util.ConcurrentModificationException: java.util.ConcurrentModificationException: java.util.ConcurrentModificationException
    at java.util.concurrent.CompletableFuture.encodeThrowable(CompletableFuture.java:273)
    at java.util.concurrent.CompletableFuture.completeThrowable(CompletableFuture.java:280)
    at java.util.concurrent.CompletableFuture.uniApply(CompletableFuture.java:604)
    at java.util.concurrent.CompletableFuture$UniApply.tryFire(CompletableFuture.java:577)
    at java.util.concurrent.CompletableFuture.postComplete(CompletableFuture.java:474)
    at java.util.concurrent.CompletableFuture.postFire(CompletableFuture.java:561)
    at java.util.concurrent.CompletableFuture.postFire(CompletableFuture.java:1054)
    at java.util.concurrent.CompletableFuture$BiApply.tryFire(CompletableFuture.java:1073)
    at java.util.concurrent.CompletableFuture$Completion.exec(CompletableFuture.java:443)
    at java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:289)
    at java.util.concurrent.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1056)
    at java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1692)
    at java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:157)
Caused by: java.util.ConcurrentModificationException: java.util.ConcurrentModificationException: java.util.ConcurrentModificationException: java.util.ConcurrentModificationException
    at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
    at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
    at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
    at java.lang.reflect.Constructor.newInstance(Constructor.java:423)
    at java.util.concurrent.ForkJoinTask.getThrowableException(ForkJoinTask.java:593)
    at java.util.concurrent.ForkJoinTask.reportException(ForkJoinTask.java:677)
    at java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:720)
    at org.jenetics.internal.util.Concurrency$ForkJoinPoolConcurrency.close(Concurrency.java:119)
    at org.jenetics.engine.Engine.evaluate(Engine.java:421)
    at org.jenetics.engine.TimedResult.lambda$of$1(TimedResult.java:81)
    at java.util.concurrent.CompletableFuture.uniApply(CompletableFuture.java:602)
    ... 10 more
Caused by: java.util.ConcurrentModificationException: java.util.ConcurrentModificationException: java.util.ConcurrentModificationException
    at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
    at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
    at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
    at java.lang.reflect.Constructor.newInstance(Constructor.java:423)
    at java.util.concurrent.ForkJoinTask.getThrowableException(ForkJoinTask.java:593)
    at java.util.concurrent.ForkJoinTask.reportException(ForkJoinTask.java:677)
    at java.util.concurrent.ForkJoinTask.invokeAll(ForkJoinTask.java:762)
    at org.jenetics.internal.util.RunnablesAction.compute(RunnablesAction.java:77)
    at java.util.concurrent.RecursiveAction.exec(RecursiveAction.java:189)
    ... 4 more
Caused by: java.util.ConcurrentModificationException: java.util.ConcurrentModificationException
    at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
    at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
    at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
    at java.lang.reflect.Constructor.newInstance(Constructor.java:423)
    at java.util.concurrent.ForkJoinTask.getThrowableException(ForkJoinTask.java:593)
    at java.util.concurrent.ForkJoinTask.reportException(ForkJoinTask.java:677)
    at java.util.concurrent.ForkJoinTask.invokeAll(ForkJoinTask.java:762)
    at org.jenetics.internal.util.RunnablesAction.compute(RunnablesAction.java:77)
    at java.util.concurrent.RecursiveAction.exec(RecursiveAction.java:189)
    at java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:289)
    at java.util.concurrent.ForkJoinTask.doInvoke(ForkJoinTask.java:401)
    at java.util.concurrent.ForkJoinTask.invokeAll(ForkJoinTask.java:759)
    at org.jenetics.internal.util.RunnablesAction.compute(RunnablesAction.java:77)
    at java.util.concurrent.RecursiveAction.exec(RecursiveAction.java:189)
    at java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:289)
    at java.util.concurrent.ForkJoinTask.doInvoke(ForkJoinTask.java:401)
    at java.util.concurrent.ForkJoinTask.invokeAll(ForkJoinTask.java:759)
    ... 6 more
Caused by: java.util.ConcurrentModificationException
    at java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1380)
    at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:481)
    at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:471)
    at java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:708)
    at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
    at java.util.stream.ReferencePipeline.reduce(ReferencePipeline.java:479)
    at java.util.stream.ReferencePipeline.max(ReferencePipeline.java:515)
    at org.jenetics.engine.EvolutionResult.best(EvolutionResult.java:107)
    at org.jenetics.internal.util.Lazy.evaluate(Lazy.java:64)
    at org.jenetics.internal.util.Lazy.get(Lazy.java:49)
    at org.jenetics.engine.EvolutionResult.compareTo(EvolutionResult.java:242)
    at org.jenetics.engine.EvolutionResult.compareTo(EvolutionResult.java:61)
    at org.jenetics.stat.MinMax.lambda$of$2(MinMax.java:234)
    at org.jenetics.stat.MinMax.min(MinMax.java:142)
    at org.jenetics.stat.MinMax.accept(MinMax.java:74)
    at java.util.stream.ReduceOps$3ReducingSink.accept(ReduceOps.java:169)
    at org.jenetics.engine.EvolutionSpliterator.tryAdvance(EvolutionSpliterator.java:69)
    at java.util.Spliterator.forEachRemaining(Spliterator.java:326)
    at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:481)
    at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:471)
    at java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:708)
    at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
    at java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:499)
    at org.jenetics.engine.StreamProxy.collect(StreamProxy.java:83)
    at org.jenetics.tool.optimizer.EvolutionParamOptimizer.lambda$evolutionParamFitness$2(EvolutionParamOptimizer.java:255)
    at java.util.stream.StreamSpliterators$InfiniteSupplyingSpliterator$OfRef.tryAdvance(StreamSpliterators.java:1356)
    at java.util.stream.ReferencePipeline.forEachWithCancel(ReferencePipeline.java:126)
    at java.util.stream.AbstractPipeline.copyIntoWithCancel(AbstractPipeline.java:498)
    at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:485)
    at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:471)
    at java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:708)
    at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
    at java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:499)
    at org.jenetics.tool.optimizer.EvolutionParamOptimizer.evolutionParamFitness(EvolutionParamOptimizer.java:259)
    at org.jenetics.tool.optimizer.EvolutionParamOptimizer.lambda$stream$0(EvolutionParamOptimizer.java:158)
    at java.util.function.Function.lambda$andThen$1(Function.java:88)
    at org.jenetics.Phenotype.eval(Phenotype.java:143)
    at org.jenetics.Phenotype.evaluate(Phenotype.java:136)
    at org.jenetics.Phenotype.run(Phenotype.java:156)
    at org.jenetics.internal.util.RunnablesAction.compute(RunnablesAction.java:73)
    ... 5 more

Process finished with exit code 1
 */
