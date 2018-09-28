package org.jraph.util;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@ParametersAreNonnullByDefault
public interface MinPriorityQueue<E, C> {

	@Nonnull
	Optional<E> peek();

	@Nonnull
	Optional<E> poll();

	boolean offer(E element, C priority);

	@Nonnegative
	int size();

	@Nonnull
	default Stream<E> consume() {
		return StreamSupport.stream(new Spliterators.AbstractSpliterator<E>(size(),
				Spliterator.SIZED | Spliterator.ORDERED | Spliterator.DISTINCT) {
			@Override
			public boolean tryAdvance(Consumer<? super E> action) {
				Optional<E> e = poll();
				if (e.isPresent()) {
					action.accept(e.get());
					return true;
				}
				return false;
			}
		}, false);
	}

}
