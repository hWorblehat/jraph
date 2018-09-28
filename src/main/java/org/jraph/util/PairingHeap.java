package org.jraph.util;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class PairingHeap<E, P> implements MinPriorityQueue<E, P> {

	private final Comparator<? super P> comparator;
	private final Map<E, PairingTree<E, P>> lookup = new HashMap<>();
	private PairingTree<E, P> root;

	public static <E, P extends Comparable<P>> PairingHeap<E,P> create() {
		return new PairingHeap<>(Comparator.naturalOrder());
	}

	public PairingHeap(Comparator<? super P> comparator) {
		this.comparator = comparator;
	}

	@Nonnull
	@Override
	public Optional<E> peek() {
		return Optional.ofNullable(root).map(r -> r.element);
	}

	@Nonnull
	@Override
	public Optional<E> poll() {
		if (root == null) {
			return Optional.empty();
		}

		lookup.remove(root.element);
		Optional<E> rc = Optional.of(root.element);
		root = mergePairs(root.firstChild);
		if(root!=null) {
			root.prevSiblingOrParent = null;
			assert root.nextSibling == null;
		}
		return rc;
	}

	@Override
	public boolean offer(E element, P priority) {
		PairingTree<E, P> tree = lookup.get(element);
		if (tree == null) {
			tree = new PairingTree<>(element, priority);
			lookup.put(element, tree);
		} else {
			if (comparator.compare(priority, tree.priority) >= 0) {
				return false; // No change, or cannot increase priority
			}

			tree.priority = priority; // Update priority

			if (tree.prevSiblingOrParent == null) {
				assert tree == root;
				return true; // Already min
			}

			// Extract subtree from heap
			if (tree.prevSiblingOrParent.nextSibling == tree) {
				tree.prevSiblingOrParent.nextSibling = tree.nextSibling;
			} else {
				assert tree.prevSiblingOrParent.firstChild == tree;
				tree.prevSiblingOrParent.firstChild = tree.nextSibling;
			}
			tree.nextSibling = null;
			tree.prevSiblingOrParent = null;
		}

		root = merge(tree, root);
		return true;
	}

	@Nonnegative
	@Override
	public int size() {
		return lookup.size();
	}


	private PairingTree<E, P> merge(PairingTree<E, P> a, @Nullable PairingTree<E, P> b) {
		assert a != b;
		if (b == null) {
			return a;
		}
		assert a.nextSibling == null;
		assert b.nextSibling == null;
		return (comparator.compare(a.priority, b.priority) <= 0)
				? insertFirstChild(a, b)
				: insertFirstChild(b, a);
	}

	private PairingTree<E, P> mergePairs(@Nullable PairingTree<E, P> first) {
		if (first == null) {
			return null;
		}
		if (first.nextSibling == null) {
			return first;
		}
		PairingTree<E, P> second = first.nextSibling, third = second.nextSibling;
		first.nextSibling = null;
		second.nextSibling = null;
		return merge(merge(first, second), mergePairs(third));
	}

	private PairingTree<E, P> insertFirstChild(PairingTree<E, P> parent, PairingTree<E, P> child) {
		assert child.nextSibling == null;

		if (parent.firstChild != null) {
			child.nextSibling = parent.firstChild;
			parent.firstChild.prevSiblingOrParent = child;
		}

		parent.firstChild = child;
		child.prevSiblingOrParent = parent;

		return parent;
	}

	private static class PairingTree<E, C> {
		private final E element;
		private C priority;
		private @Nullable PairingTree<E, C> prevSiblingOrParent, nextSibling, firstChild;

		PairingTree(E element, C initialPriority) {
			this.element = element;
			this.priority = initialPriority;
		}
	}

}
