package application;

import application.Direction;
import javafx.scene.input.KeyCode;

public enum Direction {

  UP(0, -1), RIGHT(1, 0), DOWN(0, 1), LEFT(-1, 0);

  private final int y;
  private final int x;

  Direction(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  @Override
  public String toString() {
    return "Direction{" + "y=" + y + ", x=" + x + '}' + name();
  }

  public static Direction valueFor(KeyCode keyCode) {
    return valueOf(keyCode.name());
  }

  /**
   * Install Code imaginary keystrokes for the bot and get action on these buttons for bot
   */
  public static Direction valueForBOT(int direct) {
    KeyCode keyCode = null;
    switch (direct) {
      case 0: {
        keyCode = KeyCode.LEFT;
        break;
      }
      case 1: {
        keyCode = KeyCode.UP;
        break;
      }
      case 2: {
        keyCode = KeyCode.RIGHT;
        break;
      }
    }
    return valueOf(keyCode.name());
  }
}
