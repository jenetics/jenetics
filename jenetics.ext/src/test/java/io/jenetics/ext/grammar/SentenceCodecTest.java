package io.jenetics.ext.grammar;

import java.util.List;

import io.jenetics.IntegerGene;
import io.jenetics.engine.Codec;

import io.jenetics.ext.grammar.Cfg.Terminal;

public class SentenceCodecTest {

	//@Test
	public void create() {
		final Cfg<String> cfg = null;
		Codec<List<Terminal<String>>, IntegerGene> codec =
			new MultiIntegerChromosomeMapper<>(cfg, null, null);
	}
}
