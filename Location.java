package game2048;

public class Location {
  private int xPosition;
  private int yPosition;

  public Location(int x, int y) {
    this.xPosition = x;
    this.yPosition = y;
  }

  public int getX() {
    return xPosition;
  }

  public void setX(int x) {
    this.xPosition = x;
  }

  public int getY() {
    return yPosition;
  }

  public void setY(int y) {
    this.yPosition = y;
  }

  @Override
  public String toString() {
    return "Location{" + "x=" + xPosition + ", y=" + yPosition + '}';
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 61 * hash + this.xPosition;
    hash = 61 * hash + this.yPosition;
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Location other = (Location) obj;
    if (this.xPosition != other.xPosition) {
      return false;
    }
    return this.yPosition == other.yPosition;
  }

  /**
   * Return the location of the tile in the selected direction
   */
  public Location offset(Direction direction) {
    return new Location(xPosition + direction.getX(), yPosition + direction.getY());
  }

  public double getLayoutX(int CELL_SIZE) {
    return (xPosition * CELL_SIZE) + CELL_SIZE / 2;
  }

  public double getLayoutY(int CELL_SIZE) {
    return (yPosition * CELL_SIZE) + CELL_SIZE / 2;
  }

  public boolean isValidFor() {
    return xPosition >= 0 && xPosition < 4 && yPosition >= 0 && yPosition < 4;
  }

}
