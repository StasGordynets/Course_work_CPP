package game2048;

import java.util.Random;
import javafx.geometry.Pos;
import javafx.scene.control.Label;

public class Tile extends Label {
  private int value;
  private Location location;
  private boolean merged;

  /**
   * Create random value, 90% chance 2, 10% 4
   */
  public static Tile newRandomTile() {
    return newTile(new Random().nextDouble() < 0.9 ? 2 : 4);
  }

  public static Tile newTile(int value) {
    return new Tile(value);
  }

  /**
   * Create tile
   */
  private Tile(int value) {
    // Board.CELL_SIZE - 13 -> this is the tile that was a bit less than the field
    final int squareSize = Board.CELL_SIZE - 13;
    setMinSize(squareSize, squareSize);
    setMaxSize(squareSize, squareSize);
    setPrefSize(squareSize, squareSize);
    getStyleClass().addAll("game-label", "game-tile-" + value);
    setAlignment(Pos.CENTER);

    this.value = value;
    this.merged = false;
    setText(Integer.toString(value));
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public boolean isMerged() {
    return merged;
  }

  public void setMerged(boolean merged) {
    this.merged = merged;
  }

  @Override
  public String toString() {
    return "Tile{" + "value=" + value + ", location=" + location + ", merged=" + merged + '}';
  }

  /**
   * Add to tile's value the value of the tile to be merged to, set the text with the new value and
   * replace the old style ‘game-title-“-value with the new one
   */
  public void merge(Tile anotherTile) {
    getStyleClass().remove("game-tile-" + value);
    this.value += anotherTile.getValue();
    setText(Integer.toString(value));
    merged = true;
    getStyleClass().add("game-tile-" + value);
  }

  /**
   * Check it this.tile can be merged with anotherTile
   */
  public boolean isMergeable(Tile anotherTile) {
    return anotherTile != null && getValue() == anotherTile.getValue();
  }
}
