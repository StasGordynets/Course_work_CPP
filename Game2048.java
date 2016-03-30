package game2048;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * BSUIR2016 - Create the Game 2048 with Java 8 and JavaFX [Stanislav]
 *
 * @authors stasgordynets@gmail.com
 */
public class Game2048 extends Application {
  public static GameManager gameManager;
  final int WIDTH = 650;
  final int HEIGHT = 850;

  /**
   * Initialize fonts
   */
  @Override
  public void init() {
    Font.loadFont(Game2048.class.getResource("ClearSans-Bold.ttf").toExternalForm(), 10.0);
  }

  @Override
  public void start(Stage primaryStage) {
    StackPane root = new StackPane();

    gameManager = new GameManager();
    root.getChildren().add(gameManager);

    Scene scene = new Scene(root, WIDTH, HEIGHT);
    primaryStage.setResizable(false);

    scene.getStylesheets().add(Game2048.class.getResource("game.css").toExternalForm());
    root.getStyleClass().addAll("game-root");

    scene.setOnKeyPressed(ke -> {
      KeyCode keyCode = ke.getCode();
      if (keyCode.isArrowKey()) {
        Direction dir = Direction.valueFor(keyCode);
        gameManager.move(dir);
      }
    });

    primaryStage.setTitle("Games 2048 on JavaFX");
    primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("2048.png")));

    primaryStage.setScene(scene);
    primaryStage.show();
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    launch(args);
  }
}
