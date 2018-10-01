package org.hWorblehat.jraph

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

import java.util.stream.Stream

@CompileStatic
class SimpleGraph implements DirectedGraph<String, Edge, Integer> {

	SimpleGraph(){}

	private final Set<Edge> edges = new HashSet<>()

	@Override
	Stream<Edge> getOutgoingEdges(String vertex) {
		edges.stream().filter { it.from==vertex }
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
