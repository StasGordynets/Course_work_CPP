package game2048;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.util.Duration;


public class GameManager extends Group {

  private Board board;
  private final List<Location> locations = new ArrayList<>();
  private final Map<Location, Tile> gameGrid = new HashMap<>();
  private final ParallelTransition parallelTransition = new ParallelTransition();
  private volatile boolean movingTiles = false;
  private int tilesWereMoved = 0;
  private final Set<Tile> mergedToBeRemoved = new HashSet<>();
  privete final int Step25 = 25; 
  privete final int Step30 = 30;
  privete final int Step30 = 35;
  privete final int Step37 = 37;
  privete final int Step41 = 41;
  
  public GameManager() {
    board = new Board();
    getChildren().add(board);
    board.resetGameProperty().addListener((ov, b, b1) -> {
      if (b1) {
        initializeGameGrid();
        startGame();
      }
    });
    initializeGameGrid();
    startGame();
  }

  private void initializeGameGrid() {
    gameGrid.clear();
    locations.clear();
    GridOperator.traverseGrid((i, j) -> {
      Location location = new Location(i, j);
      locations.add(location);
      gameGrid.put(location, null);
      return 0;
    });
  }

  private void startGame() {

    Tile tile0 = Tile.newRandomTile();

    List<Location> locCopy = locations.stream().collect(Collectors.toList());
    Collections.shuffle(locCopy);
    tile0.setLocation(locCopy.get(0));
    gameGrid.put(tile0.getLocation(), tile0);
    Tile tile1 = Tile.newRandomTile();
    tile1.setLocation(locCopy.get(1));
    gameGrid.put(tile1.getLocation(), tile1);

    redrawTilesInGameGrid();
  }


  private void redrawTilesInGameGrid() {
    gameGrid.values().stream().filter(Objects::nonNull).forEach(board::addTile);
  }

  public void move(Direction direction) {
    synchronized (gameGrid) {
      if (movingTiles) {
        return;
      }
    }

    if (Game2048.STEP >= Step25) {
      GridOperator.sortGrid(direction);
      board.setPoints(0);
      tilesWereMoved = GridOperator.traverseGrid((i, j) -> {
        AtomicInteger result = new AtomicInteger();
        optionalTile(new Location(i, j)).ifPresent(t1 -> {
          final Location newLocation = findFarthestLocation(t1.getLocation(), direction);
          Location nextLocation = newLocation.offset(direction); // calculates to a possible merge
          optionalTile(nextLocation).filter(t2 -> t1.isMergeable(t2) && !t2.isMerged())
              .ifPresent(t2 -> {
                t2.merge(t1);
                t2.toFront();
                gameGrid.put(nextLocation, t2);
                gameGrid.replace(t1.getLocation(), null);
                board.addPoints(t2.getValue());
                if (t2.getValue() == 2048) {
                  board.setGameWin(true);
                }
                parallelTransition.getChildren().add(animateExistingTile(t1, nextLocation));
                parallelTransition.getChildren().add(animateMergedTile(t2));
                mergedToBeRemoved.add(t1);
                result.set(1);
              });

          if (result.get() == 0 && !newLocation.equals(t1.getLocation())) {
            parallelTransition.getChildren().add(animateExistingTile(t1, newLocation));
            gameGrid.put(newLocation, t1);
            gameGrid.replace(t1.getLocation(), null);
            t1.setLocation(newLocation);
            result.set(1);
          }
        });
        return result.get();
      });
    }
    if (Game2048.STEP >= Step35) {
      board.animateScore();
    }
    if (Game2048.STEP >= Step20) {
      parallelTransition.setOnFinished(e -> {
        synchronized (gameGrid) {
          movingTiles = false;
        }
        // TO-DO: Step 30. Remove the tiles in the set from the gridGroup and clear the set.
        // For all the tiles on the board: set to false their merged value
        if (Game2048.STEP >= Step30) {
          board.getGridGroup().getChildren().removeAll(mergedToBeRemoved);
          mergedToBeRemoved.clear();
          gameGrid.values().stream().filter(Objects::nonNull).forEach(t -> t.setMerged(false));
        }

        // TO-DO: Step 23. Start animation and block movingTiles till it has finished
        if (Game2048.STEP >= 23) {
          Location randomAvailableLocation = findRandomAvailableLocation();
          if (randomAvailableLocation != null) {
            if (Game2048.STEP < 25) {
              addAndAnimateRandomTile(randomAvailableLocation);
            } else if (Game2048.STEP >= 25) {
              if (tilesWereMoved > 0) {
                addAndAnimateRandomTile(randomAvailableLocation);
              }
            }
          } else {
            if (Game2048.STEP < 37) {
              System.out.println("Game Over");
            } else if (Game2048.STEP >= 37) {
              if (mergeMovementsAvailable() == 0) {
                System.out.println("Game Over");
                if (Game2048.STEP >= 41) {
                  board.setGameOver(true);
                }
              }
            }
          }
        }

      });
      synchronized (gameGrid) {
        movingTiles = true;
      }
      parallelTransition.play();
      parallelTransition.getChildren().clear();
    }
  }

  private Location findFarthestLocation(Location location, Direction direction) {
    Location farthest = location;
    if (Game2048.STEP >= 17) {
      do {
        farthest = location;
        location = farthest.offset(direction);
      } while (location.isValidFor() && gameGrid.get(location) == null);
    }
    return farthest;
  }

  private Timeline animateExistingTile(Tile tile, Location newLocation) {
    Timeline timeline = new Timeline();
    if (Game2048.STEP >= 19) {
      KeyValue kvX = new KeyValue(tile.layoutXProperty(),
          newLocation.getLayoutX(Board.CELL_SIZE) - (tile.getMinHeight() / 2),
          Interpolator.EASE_OUT);
      KeyValue kvY = new KeyValue(tile.layoutYProperty(),
          newLocation.getLayoutY(Board.CELL_SIZE) - (tile.getMinHeight() / 2),
          Interpolator.EASE_OUT);

      KeyFrame kfX = new KeyFrame(Duration.millis(100), kvX);
      KeyFrame kfY = new KeyFrame(Duration.millis(100), kvY);

      timeline.getKeyFrames().add(kfX);
      timeline.getKeyFrames().add(kfY);
    }
    return timeline;
  }

  private Location findRandomAvailableLocation() {
    Location location = null;
    if (Game2048.STEP >= 21) {
      List<Location> availableLocations =
          locations.stream().filter(l -> gameGrid.get(l) == null).collect(Collectors.toList());

      if (availableLocations.isEmpty()) {
        return null;
      }

      Collections.shuffle(availableLocations);
      location = availableLocations.get(0);
    }
    return location;
  }

  private void addAndAnimateRandomTile(Location randomLocation) {
    if (Game2048.STEP >= 22) {
      Tile tile = Tile.newRandomTile();
      tile.setLocation(randomLocation);
      tile.setScaleX(0);
      tile.setScaleY(0);
      board.addTile(tile);
      gameGrid.put(tile.getLocation(), tile);

      final ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(125), tile);
      scaleTransition.setToX(1.0);
      scaleTransition.setToY(1.0);
      scaleTransition.setInterpolator(Interpolator.EASE_OUT);
      if (Game2048.STEP >= 37) {
        scaleTransition.setOnFinished(e -> {
          if (gameGrid.values().parallelStream().noneMatch(Objects::isNull)
              && mergeMovementsAvailable() == 0) {
            System.out.println("Game Over");
            if (Game2048.STEP >= 41) {
              board.setGameOver(true);
            }
          }
        });
      }
      scaleTransition.play();
    }
  }

  private SequentialTransition animateMergedTile(Tile tile) {
    if (Game2048.STEP >= 28) {
      final ScaleTransition scale0 = new ScaleTransition(Duration.millis(80), tile);
      scale0.setToX(1.2);
      scale0.setToY(1.2);
      scale0.setInterpolator(Interpolator.EASE_IN);

      final ScaleTransition scale1 = new ScaleTransition(Duration.millis(80), tile);
      scale1.setToX(1.0);
      scale1.setToY(1.0);
      scale1.setInterpolator(Interpolator.EASE_OUT);

      return new SequentialTransition(scale0, scale1);
    }
    return new SequentialTransition();
  }

  private int mergeMovementsAvailable() {
    final AtomicInteger numMergeableTile = new AtomicInteger();
    // TO-DO: Step 36. Traverse grid in two directions, looking for pairs of mergeable tiles
    if (Game2048.STEP >= 36) {
      Stream.of(Direction.UP, Direction.LEFT).parallel().forEach(direction -> {
        GridOperator.traverseGrid((x, y) -> {
          Location thisloc = new Location(x, y);
          if (Game2048.STEP < 43) {
            Tile t1 = gameGrid.get(thisloc);
            if (t1 != null) {
              Location nextLoc = thisloc.offset(direction);
              if (nextLoc.isValidFor()) {
                Tile t2 = gameGrid.get(nextLoc);
                if (t2 != null && t1.isMergeable(t2)) {
                  numMergeableTile.incrementAndGet();
                }
              }
            }
          } else if (Game2048.STEP >= 44) {
            optionalTile(thisloc).ifPresent(t1 -> {
              optionalTile(thisloc.offset(direction)).filter(t2 -> t1.isMergeable(t2))
                  .ifPresent(t2 -> numMergeableTile.incrementAndGet());
            });
          }
          return 0;
        });
      });
    }

    return numMergeableTile.get();
  }

  private Optional<Tile> optionalTile(Location loc) {
    if (Game2048.STEP >= 43) {
      return Optional.ofNullable(gameGrid.get(loc));
    }
    return null;
  }
}
