package game2048;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntBinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GridOperator {

  private static final List<Integer> traversalX =
      IntStream.range(0, 4).boxed().collect(Collectors.toList());
  private static final List<Integer> traversalY =
      IntStream.range(0, 4).boxed().collect(Collectors.toList());

  public static int traverseGrid(IntBinaryOperator func) {
    AtomicInteger at = new AtomicInteger();

    traversalX.forEach(x -> {
      traversalY.forEach(y -> {
        at.addAndGet(func.applyAsInt(x, y));
      });
    });

    return at.get();
  }

  public static void sortGrid(Direction direction) {
    Collections.sort(traversalX,
        direction.equals(Direction.RIGHT) ? Collections.reverseOrder() : Integer::compareTo);
    Collections.sort(traversalY,
        direction.equals(Direction.DOWN) ? Collections.reverseOrder() : Integer::compareTo);
  }
}
