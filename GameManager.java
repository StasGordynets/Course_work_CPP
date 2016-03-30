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

  /**
   * GameManager is a Group containing a Board that holds a grid and the score a Map holds the
   * location of the tiles in the grid The purpose of the game is sum the value of the tiles up to
   * 2048 points
   */
  public GameManager() {
    // Create board and it to gameManager
    board = new Board();
    getChildren().add(board);
    // Add listener to reset game
    board.resetGameProperty().addListener((ov, b, b1) -> {
      if (b1) {
        initializeGameGrid();
        startGame();
      }
    });
    // Ñall initilize gameGrid
    initializeGameGrid();
    // Ñall start game to display a tile on the board
    startGame();
    board.setGameStart(true);
  }

  /**
   * Initializes all cells in gameGrid map to null
   */
  private void initializeGameGrid() {
    // Clear the lists, add all locations, and call it before startGame
    gameGrid.clear();
    locations.clear();
    GridOperator.traverseGrid((i, j) -> {
      Location location = new Location(i, j);
      locations.add(location);
      gameGrid.put(location, null);
      return 0;
    });
  }

  /**
   * Starts the game by adding 1 or 2 tiles at random locations
   */
  private void startGame() {
    Tile tileNew = Tile.newRandomTile();
    List<Location> locCopy = locations.stream().collect(Collectors.toList());
    Collections.shuffle(locCopy);
    tileNew.setLocation(locCopy.get(0));
    gameGrid.put(tileNew.getLocation(), tileNew);
    Tile tile1 = Tile.newRandomTile();
    tile1.setLocation(locCopy.get(1));
    gameGrid.put(tile1.getLocation(), tile1);
    redrawTilesInGameGrid();
  }

  /**
   * Redraws all tiles in the <code>gameGrid</code> object
   */
  private void redrawTilesInGameGrid() {
    gameGrid.values().stream().filter(Objects::nonNull).forEach(board::addTile);
  }

  /**
   * Moves the tiles according to given direction At any move, takes care of merge tiles, add a new
   * one and perform the required animations It updates the score and checks if the user won the
   * game or if the game is over direction is the selected direction to move the tiles
   */
  public void move(Direction direction) {
    synchronized (gameGrid) {
      if (movingTiles) {
        return;
      }
    }
    GridOperator.sortGrid(direction);
    board.setPoints(0);
    tilesWereMoved = GridOperator.traverseGrid((xPosition, yPosition) -> {
      AtomicInteger result = new AtomicInteger();
      optionalTile(new Location(xPosition, yPosition)).ifPresent(fitstTileToCheck -> {
        final Location newLocation =
            findFarthestLocation(fitstTileToCheck.getLocation(), direction);
        Location nextLocation = newLocation.offset(direction); // calculates to a possible merge
        optionalTile(nextLocation)
            .filter(secondTileToCheck -> fitstTileToCheck.isMergeable(secondTileToCheck)
                && !secondTileToCheck.isMerged())
            .ifPresent(secondTileToCheck -> {
              secondTileToCheck.merge(fitstTileToCheck);
              secondTileToCheck.toFront();
              gameGrid.put(nextLocation, secondTileToCheck);
              gameGrid.replace(fitstTileToCheck.getLocation(), null);
              board.addPoints(secondTileToCheck.getValue());
              board.BestPoints(secondTileToCheck.getValue());
              if (secondTileToCheck.getValue() == 2048) {
                System.out.println("You win!!!");
                board.setGameWin(true);
                Board.Winner = true;
              }
              parallelTransition.getChildren()
                  .add(animateExistingTile(fitstTileToCheck, nextLocation));
              parallelTransition.getChildren().add(animateMergedTile(secondTileToCheck));
              mergedToBeRemoved.add(fitstTileToCheck);
              result.set(1);
            });

        if (result.get() == 0 && !newLocation.equals(fitstTileToCheck.getLocation())) {
          parallelTransition.getChildren().add(animateExistingTile(fitstTileToCheck, newLocation));
          gameGrid.put(newLocation, fitstTileToCheck);
          gameGrid.replace(fitstTileToCheck.getLocation(), null);
          fitstTileToCheck.setLocation(newLocation);
          result.set(1);
        }
      });
      return result.get();
    });
    board.animateScore();
    parallelTransition.setOnFinished(e -> {
      synchronized (gameGrid) {
        movingTiles = false;
      }
      board.getGridGroup().getChildren().removeAll(mergedToBeRemoved);
      mergedToBeRemoved.clear();
      gameGrid.values().stream().filter(Objects::nonNull).forEach(t -> t.setMerged(false));
      Location randomAvailableLocation = findRandomAvailableLocation();
      if (randomAvailableLocation != null) {
        addAndAnimateRandomTile(randomAvailableLocation);
      } else {
        if (mergeMovementsAvailable() == 0) {
          System.out.println("Game Over!!!");
          board.setGameOver(true);
          Board.GameOver = true;
        }
      }
    });
    synchronized (gameGrid) {
      movingTiles = true;
    }
    parallelTransition.play();
    parallelTransition.getChildren().clear();
  }

  /**
   * Searchs for the farthest empty location where the current tile could go
   *
   * @param location of the tile
   * @param direction of movement
   * @return a location
   */
  private Location findFarthestLocation(Location location, Direction direction) {
    Location farthest = location;
    do {
      farthest = location;
      location = farthest.offset(direction);
    } while (location.isValidFor() && gameGrid.get(location) == null);
    return farthest;
  }

  /**
   * Animation that moves the tile from its previous location to a new location
   *
   * @param tile to be animated
   * @param newLocation new location of the tile
   * @return a timeline
   */
  private Timeline animateExistingTile(Tile tile, Location newLocation) {
    Timeline timeline = new Timeline();
    KeyValue kvX = new KeyValue(tile.layoutXProperty(),
        newLocation.getLayoutX(Board.CELL_SIZE) - (tile.getMinHeight() / 2), Interpolator.EASE_OUT);
    KeyValue kvY = new KeyValue(tile.layoutYProperty(),
        newLocation.getLayoutY(Board.CELL_SIZE) - (tile.getMinHeight() / 2), Interpolator.EASE_OUT);
    KeyFrame kfX = new KeyFrame(Duration.millis(100), kvX);
    KeyFrame kfY = new KeyFrame(Duration.millis(100), kvY);
    timeline.getKeyFrames().add(kfX);
    timeline.getKeyFrames().add(kfY);
    return timeline;
  }

  /**
   * Finds a random location or returns null if none exist
   *
   * @return a random location or <code>null</code> if there are no more locations available
   */
  private Location findRandomAvailableLocation() {
    Location location = null;
    List<Location> availableLocations =
        locations.stream().filter(l -> gameGrid.get(l) == null).collect(Collectors.toList());
    if (availableLocations.isEmpty()) {
      return null;
    }
    Collections.shuffle(availableLocations);
    location = availableLocations.get(0);
    return location;
  }

  /**
   * Adds a tile of random value to a random location with a proper animation
   *
   * @param randomLocation
   */
  private void addAndAnimateRandomTile(Location randomLocation) {
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
    scaleTransition.setOnFinished(e -> {
      if (gameGrid.values().parallelStream().noneMatch(Objects::isNull)
          && mergeMovementsAvailable() == 0) {
        board.setGameOver(true);
      }
    });
    scaleTransition.play();
  }

  /**
   * Animation that creates a pop effect when two tiles merge by increasing the tile scale to 120%
   * at the middle, and then going back to 100%
   *
   * @param tile to be animated
   * @return a sequential transition
   */
  private SequentialTransition animateMergedTile(Tile tile) {
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

  /**
   * Finds the number of pairs of tiles that can be merged
   *
   * This method is called only when the grid is full of tiles, what makes the use of Optional
   * unnecessary, but it could be used when the board is not full to find the number of pairs of
   * mergeable tiles and provide a hint for the user, for instance
   * 
   * @return the number of pairs of tiles that can be merged
   */
  private int mergeMovementsAvailable() {
    final AtomicInteger numMergeableTile = new AtomicInteger();
    Stream.of(Direction.UP, Direction.LEFT).parallel().forEach(direction -> {
      GridOperator.traverseGrid((x, y) -> {
        Location thisloc = new Location(x, y);
        optionalTile(thisloc).ifPresent(fitstTileToCheck -> {
          optionalTile(thisloc.offset(direction))
              .filter(secondTileToCheck -> fitstTileToCheck.isMergeable(secondTileToCheck))
              .ifPresent(secondTileToCheck -> numMergeableTile.incrementAndGet());
        });
        return 0;
      });
    });
    return numMergeableTile.get();
  }

  /**
   * optionalTile allows using tiles from the map at some location, whether they are null or not
   *
   * @param loc location of the tile
   * @return an Optional<Tile> containing null or a valid tile
   */
  private Optional<Tile> optionalTile(Location loc) {
    return Optional.ofNullable(gameGrid.get(loc));
  }
}
