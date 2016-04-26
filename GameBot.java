package application;

import java.util.Random;
import application.Direction;
import javafx.animation.AnimationTimer;

public class GameBot {
  static Random rand = new Random();

  // Generate random number
  public static int RandomNumber() {
    int RandomNumber = rand.nextInt(3);
    return RandomNumber;
  }

  /**
   * Activate and set the bot flags about that when we Won or lost, disable and set the bot flags
   * end of the game and in the state of not activating
   */
  static AnimationTimer AnimationTimer = new AnimationTimer() {
    @Override
    public void handle(long now) {
      try {
        System.out.println(">>>>>> bot is active<<<<<<");
        Thread.sleep(0);
      } catch (InterruptedException e) {
      }
      GameBot.PlayGameBot();
      try {
        Thread.sleep(0);
      } catch (InterruptedException e)  {
      }
      if ((Board.GameOver == true) || (Board.Winner == true)) {
        this.stop();
        Board.GameOver = false;
        Board.Winner = false;
        Board.BotActive = false;
      }
    }
  };

  /**
   * Create a random effect for bot We passed to the function and the action moves from the selected
   * action
   */
  public static void PlayGameBot() {
    int action = RandomNumber();
    Direction direction = Direction.valueForBOT(action);
    GamePane.gameManager.move(direction);
  }
}
