package org.jraph.util

import spock.lang.*

import java.util.stream.Collectors

class PairingHeapTest extends Specification {

	def "Elements are produced in increasing priority order"(List<Integer> elements) {

		given: "a list of elements, inserted one-by-one onto a heap"
		PairingHeap<Integer, Integer> heap = PairingHeap.create()
		elements.each {
			heap.offer(it, it)
		}
		elements.sort()

		expect: "the elements to be returned from the heap in increasing priority order"
		elements == heap.consume().collect(Collectors.toList())

		where: "various lists of elements are used"
		elements << [
				[],
				[1, 2, 3],
				[7, 4, 3],
				[7, 2, 4, 5, 3, 8]
		]
	}

	def "Multiple elements with the same priority are all produced"(List<String> elements) {

		given: "a list of elements, inserted one-by-one onto a heap"
		PairingHeap<String, Integer> heap = PairingHeap.create()
		elements.each {
			heap.offer(it, it.length())
		}
		int lastLength = 0

		when: "the elements are read off the heap"
		List<String> result = heap.consume().collect(Collectors.toList())

		then: "the set or read elements matches those added to the heap originally"
		elements.size() == result.size()
		elements.containsAll(result)

		and: "they were read in increasing priority order"
		!result.collect { it.length() }.find {
			if (it >= lastLength) {
				lastLength = it
				return false
			} else return true
		}

		where: "various lists are used, each with multiple elements of the same priority"
		elements << [
				['a', 'b'],
				['aa', 'a', 'bb', 'ccc', 'b'],
				['I', 'ask', 'what', 'does', 'the', 'fox', 'say', '?']
		]
	}

	def "An element's priority can be decreased"() {

		given: "a heap with 3 elements"
		PairingHeap<String, Integer> heap = PairingHeap.create()
		assert heap.offer('C', 3)
		assert heap.offer('A', 1)
		assert heap.offer('B', 2)

		when: "The head is peeked"
		def head = heap.peek()

		then: "the minimum priority value is returned"
		head.present
		'A' == head.get()

		when: "An existing element is offered with a new lowest priority"
		def res = heap.offer('C', 0)
		head = heap.peek()

		then: "the offer was accepted"
		res

		and: "the heap's size does not change"
		3 == heap.size()

		and: "the element with the new lowest priority is on the head"
		head.present
		'C' == head.get()

		when: "The head is removed"
		head = heap.poll()

		then: "the heap's size reduces by 1"
		2 == heap.size()

		and: "the removed head is as expected"
		head.present
		'C' == head.get()
	}

}
