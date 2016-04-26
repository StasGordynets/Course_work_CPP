package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Game2048 extends Application {
  public static final String VERSION = "1.4";
  GamePane root;

  @Override
  public void start(Stage primaryStage) {
    root = new GamePane();
    Scene scene = new Scene(root);
    root.getStyleClass().addAll("game-root");
    scene.getStylesheets().add("application/game.css");
    primaryStage.setTitle("Games 2048 on JavaFX");
    primaryStage.getIcons()
        .add(new Image(this.getClass().getResourceAsStream("/resources/2048.png")));
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  @Override
  public void stop() {
    root.getGameManager().saveRecord();
  }

  public static void main(String[] args) {
    launch(args);
  }

}
