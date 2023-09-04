import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static io.jenetics.prog.op.MathExpr.parseTree;

import java.util.List;
import java.util.function.Function;

import io.jenetics.IntegerGene;
import io.jenetics.Phenotype;
import io.jenetics.SinglePointCrossover;
import io.jenetics.SwapMutator;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Limits;
import io.jenetics.engine.Problem;
import io.jenetics.util.IntRange;

import io.jenetics.ext.grammar.Bnf;
import io.jenetics.ext.grammar.Cfg;
import io.jenetics.ext.grammar.Cfg.Symbol;
import io.jenetics.ext.grammar.Mappers;
import io.jenetics.ext.grammar.SentenceGenerator;
import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.TreeNode;

import io.jenetics.prog.op.Const;
import io.jenetics.prog.op.MathExpr;
import io.jenetics.prog.op.Op;
import io.jenetics.prog.regression.Error;
import io.jenetics.prog.regression.LossFunction;
import io.jenetics.prog.regression.Sample;
import io.jenetics.prog.regression.Sampling;
import io.jenetics.prog.regression.Sampling.Result;

public class GrammarBasedRegression
	implements Problem<Tree<Op<Double>, ?>, IntegerGene, Double>
{

	private static final Cfg<String> GRAMMAR = Bnf.parse("""
		<expr> ::= x | <num> | <expr> <op> <expr>
		<op>   ::= + | - | * | /
		<num>  ::= 2 | 3 | 4
		"""
	);

	private static final Codec<Tree<Op<Double>, ?>, IntegerGene>
		CODEC = Mappers.multiIntegerChromosomeMapper(
			GRAMMAR,
			// The length of the chromosome is 25 times the length
			// of the alternatives of a given rule. Every rule
			// gets its own chromosome. It would also be possible
			// to define variable chromosome length with the
			// returned integer range.
			rule -> IntRange.of(rule.alternatives().size()*25),
			// The used generator defines the generated data type,
			// which is `List<Terminal<String>>`.
			index -> new SentenceGenerator<>(index, 50)
		)
		// Map the type of the codec from `List<Terminal<String>>`
		// to `String`
		.map(s -> s.stream().map(Symbol::name).collect(joining()))
		// Map the type of the codec from `String` to
		// `Tree<Op<Double>, ?>`
		.map(e -> e.isEmpty()
			? TreeNode.of(Const.of(0.0))
			: parseTree(e));

	private static final Error<Double> ERROR =
		Error.of(LossFunction::mse);

	private final Sampling<Double> _sampling;

	public GrammarBasedRegression(Sampling<Double> sampling) {
		_sampling = requireNonNull(sampling);
	}

	public GrammarBasedRegression(List<Sample<Double>> samples) {
		this(Sampling.of(samples));
	}

	@Override
	public Codec<Tree<Op<Double>, ?>, IntegerGene> codec() {
		return CODEC;
	}

	@Override
	public Function<Tree<Op<Double>, ?>, Double> fitness() {
		return program -> {
			final Result<Double> result = _sampling.eval(program);
			return ERROR.apply(
				program, result.calculated(), result.expected()
			);
		};
	}

	public static void main(final String[] args) {
		final var regression = new GrammarBasedRegression(
			SymbolicRegression.SAMPLES
		);

		final Engine<IntegerGene, Double> engine = Engine
			.builder(regression)
			.alterers(
				new SwapMutator<>(),
				new SinglePointCrossover<>())
			.minimizing()
			.build();

		final EvolutionResult<IntegerGene, Double> result = engine
			.stream()
			.limit(Limits.byFitnessThreshold(0.05))
			.collect(EvolutionResult.toBestEvolutionResult());

		final Phenotype<IntegerGene, Double> best =
			result.bestPhenotype();

		final Tree<Op<Double>, ?> program =
			regression.decode(best.genotype());

		System.out.println(
			"Generations: " + result.totalGenerations());
		System.out.println(
			"Function:    " + new MathExpr(program).simplify());
		System.out.println(
			"Error:       " + regression.fitness().apply(program));
	}

}
