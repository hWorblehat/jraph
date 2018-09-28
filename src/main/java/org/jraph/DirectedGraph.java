package org.jraph;

import java.util.Collection;

public interface DirectedGraph<V, E, C> {

	Collection<E> getOutgoingEdges(V vertex);

	V getStartVertex(E edge);

	V getEndVertex(E edge);

	C getCost(E edge);

}
