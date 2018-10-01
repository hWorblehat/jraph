package org.hworblehat.jraph;

import java.util.List;

public interface Route<V, E, C> {

	C getTotalCost();

	List<V> getVertices();

	List<E> getEdges();

}
