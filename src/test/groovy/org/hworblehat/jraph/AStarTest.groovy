package org.hworblehat.jraph

import spock.lang.*

import java.util.function.ToIntBiFunction

class AStarTest extends Specification {

	def "The route from a vertex to itself is zero"() {
		setup:
		def graph  = new SimpleGraph().addEdge("A", "B", 1)

		when:
		def route = AStar.findIntRoute("A", "A", graph, {a, b -> 0})

		then:
		route.present
		route.get().totalCost == 0
		route.get().edges.empty
		route.get().vertices == ["A"]
	}

	def "The route chooses the shortest edge between two vertices"() {
		setup:
		def graph  = new SimpleGraph()
				.addEdge("A", "B", 3)
				.addEdge("A", "B", 1)
				.addEdge("A", "B", 2)

		when:
		def route = AStar.findIntRoute("A", "B", graph, {a, b -> 0})

		then:
		route.present
		route.get().totalCost == 1
		route.get().edges.size() == 1
		route.get().vertices == ["A", "B"]

	}

	def "The route chooses the shortest of 2 paths"(SimpleGraph graph) {
		when:
		def route = AStar.findIntRoute("A", "B", graph, {a, b -> 0})

		then:
		route.present
		route.get().edges.size() == 2
		route.get().vertices == ["A", "short", "B"]

		where:
		graph << [
		        new SimpleGraph()
						.addEdge('A', 'long', 2)
						.addEdge('long', 'B', 1)
						.addEdge('A', 'short', 1)
						.addEdge('short', 'B', 1),

				new SimpleGraph()
						.addEdge('A', 'long', 1)
						.addEdge('long', 'B', 3)
						.addEdge('A', 'short', 2)
						.addEdge('short', 'B', 1),

				new SimpleGraph()
						.addEdge('A', 'not', 2)
						.addEdge('not', 'short', 2)
						.addEdge('A', 'short', 3)
						.addEdge('short', 'B', 1),

				new SimpleGraph()
						.addEdge('A', 'B', 3)
						.addEdge('A', 'short', 1)
						.addEdge('short', 'B', 1),

				new SimpleGraph()
						.addEdge('A', 'C', 1)
						.addEdge('C', 'D', 1)
						.addEdge('D', 'E', 1)
						.addEdge('E', 'F', 1)
						.addEdge('F', 'B', 1)
						.addEdge('A', 'short', 3)
						.addEdge('short', 'B', 1),
		]
	}

	def "The route chooses the shortest of 3 paths"(SimpleGraph graph) {
		when:
		def route = AStar.findIntRoute("A", "B", graph, {a, b -> 0})

		then:
		route.present
		route.get().totalCost == 4
		route.get().edges.size() == 2
		route.get().vertices == ["A", "short", "B"]

		where:
		graph << [
				new SimpleGraph()
						.addEdge('A', 'long', 4)
						.addEdge('long', 'B', 1)
						.addEdge('A', 'longer', 1)
						.addEdge('longer', 'B', 5)
						.addEdge('A', 'short', 2)
						.addEdge('short', 'B', 2),

				new SimpleGraph()
						.addEdge('A', 'not', 2)
						.addEdge('not', 'short', 2)
						.addEdge('not', 'B', 3)
						.addEdge('A', 'short', 3)
						.addEdge('short', 'B', 1),

				new SimpleGraph()
						.addEdge('A', 'B', 7)
						.addEdge('A', 'short', 3)
						.addEdge('short', 'B', 1)
						.addEdge('short', 'B', 2),

				new SimpleGraph()
						.addEdge('A', 'C', 1)
						.addEdge('C', 'D', 1)
						.addEdge('D', 'E', 1)
						.addEdge('E', 'F', 1)
						.addEdge('F', 'B', 1)
						.addEdge('A', 'E', 3)
						.addEdge('A', 'short', 3)
						.addEdge('short', 'B', 1),
		]
	}

	def 'No route is found between disconnected vertices'() {
		setup:
		def graph = new SimpleGraph()
				.addEdge('A', 'B', 1)
				.addEdge('C', 'D', 1)

		expect:
		! AStar.findIntRoute('A', 'D', graph, {a,b -> 0}).present
	}

	def 'Vertices with a high cost estimate ARE NOT checked when NOT part of the best route'() {
		setup:
		def spyGraph = Spy(SimpleGraph)
				.addEdge('A', 'B', 1)
				.addEdge('B', 'Z', 26)
				.addEdge('A', 'C', 1)
				.addEdge('C', 'D', 1)
				.addEdge('D', 'Z', 1)

		// Heuristic distance is the difference in ASCII value of the letters
		ToIntBiFunction<String, String> heuristic = {a,b ->
			Math.abs(b.codePointAt(0) - a.codePointAt(0))
		}

		when:
		def route = AStar.findIntRoute('A', 'Z', spyGraph, heuristic)

		then:
		0 * spyGraph.getOutgoingEdges('B')
		(1.._) * spyGraph.getOutgoingEdges(!'B')
		route.present
		route.get().totalCost == 3
		!route.get().vertices.contains('B')
	}

	def 'Vertices with a high cost estimate but part of the best route are still checked'() {
		setup:
		def graph = new SimpleGraph()
				.addEdge('A', 'B', 1)
				.addEdge('B', 'Z', 26)
				.addEdge('A', 'X', 1)
				.addEdge('X', 'Y', 2)
				.addEdge('Y', 'Z', 25)

		// Heuristic distance is the difference in ASCII value of the letters
		ToIntBiFunction<String, String> heuristic = {a,b ->
			Math.abs(b.codePointAt(0) - a.codePointAt(0))
		}

		when:
		def route = AStar.findIntRoute('A', 'Z', graph, heuristic)

		then:
		route.present
		route.get().totalCost == 27
		route.get().vertices.contains('B')
	}

}
