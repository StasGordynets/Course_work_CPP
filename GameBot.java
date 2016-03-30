package game2048;

import java.util.Random;
import game2048.Direction;
import javafx.animation.AnimationTimer;

public class GameBot {
  static Random rand = new Random();

  public static int Number() {
    int RandomNumber = rand.nextInt(3);
    return RandomNumber;
  }

  static AnimationTimer AnimationTimer = new AnimationTimer() {
    @Override
    public void handle(long now) {
      GameBot.PlayGameBot();
      if ((Board.GameOver == true) || (Board.Winer == true)) {
        this.stop();
        Board.GameOver = false;
        Board.Winer = false;
        Board.BotActive = false;
      }
    }
  };

  public static void PlayGameBot() {
    int direction = Number();
    Direction dir = Direction.valueForBOT(direction);
    Game2048.gameManager.move(dir);
  }
}
