package org.jraph

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

class SimpleGraph implements DirectedGraph<String, Edge, Integer> {

	SimpleGraph(){}

	private final Set<Edge> edges = new HashSet<>()

	@Override
	Collection<Edge> getOutgoingEdges(String vertex) {
		return edges.findAll { vertex == it.from }
	}

	@Override
	String getStartVertex(Edge edge) {
		return edge.from
	}

	@Override
	String getEndVertex(Edge edge) {
		return edge.to
	}

	@Override
	Integer getCost(Edge edge) {
		return edge.cost
	}

	SimpleGraph addEdge(String from, String to, int cost) {
		edges.add(new Edge(from, to, cost))
		return this
	}

	@TupleConstructor
	@EqualsAndHashCode
	static class Edge {
		final String from, to
		final int cost

		@Override
		String toString() {
			"${from}--${cost}-->${to}"
		}
	}

}
