package org.jenetics;

public class Generation<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
{

	private int _generation;
	private Population<G, C> _population = new Population<>();
	
}
