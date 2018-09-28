package org.jraph;

import java.util.Collections;
import java.util.List;

final class ImmutableRoute<V, E, C> implements Route<V, E, C> {

	private final C cost;
	private final List<V> vertices;
	private final List<E> edges;

	ImmutableRoute(List<V> vertices, List<E> edges, C cost) {
		assert vertices.size() == edges.size() + 1;
		this.vertices = Collections.unmodifiableList(vertices);
		this.edges = Collections.unmodifiableList(edges);
		this.cost = cost;
	}

	@Override
	public C getTotalCost() {
		return cost;
	}

	@Override
	public List<V> getVertices() {
		return vertices;
	}

	@Override
	public List<E> getEdges() {
		return edges;
	}
}
