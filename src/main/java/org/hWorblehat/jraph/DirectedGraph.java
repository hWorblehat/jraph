package org.hWorblehat.jraph;

import java.util.stream.Stream;

public interface DirectedGraph<V, E, C> {

	Stream<E> getOutgoingEdges(V vertex);

	V getStartVertex(E edge);

	V getEndVertex(E edge);

	C getCost(E edge);

}
