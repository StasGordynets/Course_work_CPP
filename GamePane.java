package application;

import application.QSort;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

public class GamePane extends StackPane {
  static GameManager gameManager;
  private Bounds gameBounds;
  private final static int MARGIN = 36;

  static {
    Font.loadFont(Game2048.class.getResource("ClearSans-Bold.ttf").toExternalForm(), 10.0);
  }

  public GamePane() {
    gameManager = new GameManager();
    gameManager.setToolBar(createToolBar());
    gameBounds = gameManager.getLayoutBounds();

    getChildren().add(gameManager);

    getStyleClass().addAll("game-root");
    ChangeListener<Number> resize = (ov, v, v1) -> {
      double scale = Math.min((getWidth() - MARGIN) / gameBounds.getWidth(),
          (getHeight() - MARGIN) / gameBounds.getHeight());
      gameManager.setScale(scale);
      gameManager.setLayoutX((getWidth() - gameBounds.getWidth()) / 2d);
      gameManager.setLayoutY((getHeight() - gameBounds.getHeight()) / 2d);
    };
    widthProperty().addListener(resize);
    heightProperty().addListener(resize);

    addKeyHandler(this);
    setFocusTraversable(true);
    this.setOnMouseClicked(e -> requestFocus());
  }

  private void addKeyHandler(Node node) {
    node.setOnKeyPressed(ke -> {
      KeyCode keyCode = ke.getCode();
      if (keyCode.equals(KeyCode.S)) {
        gameManager.saveSession();
        return;
      }
      if (keyCode.equals(KeyCode.R)) {
        gameManager.restoreSession();
        return;
      }
      if (keyCode.equals(KeyCode.P)) {
        gameManager.pauseGame();
        return;
      }
      if (keyCode.equals(KeyCode.Q) || keyCode.equals(KeyCode.ESCAPE)) {
        gameManager.quitGame();
        return;
      }
      if (keyCode.isArrowKey()) {
        Direction direction = Direction.valueFor(keyCode);
        move(direction);
      }
    });
  }

  private void move(Direction direction) {
    gameManager.move(direction);
  }

  private HBox createToolBar() {
    HBox toolbar = new HBox();
    toolbar.setAlignment(Pos.CENTER);
    toolbar.setPadding(new Insets(10.0));
    Button save = createButtonItem("mSave", "Set path to file", t -> gameManager.SaveFile());
    Button restore = createButtonItem("mRestore", "Open Path file", t -> gameManager.OpenFile());
    Button bot = createButtonItem("mBot", "Actevated Bot", t -> gameManager.BotGame());
    Button pause = createButtonItem("mPause", "Pause Game", t -> gameManager.pauseGame());
    Button replay = createButtonItem("mReplay", "Try Again", t -> gameManager.tryAgain());
    Button info = createButtonItem("mInfo", "About the Game", t -> gameManager.aboutGame());
    Button sorting = createButtonItem("mSorting", "Sorting of the Game", t->QSort.StageSorting());
    toolbar.getChildren().setAll(save, restore, bot, pause, replay, info, sorting);
    Button exit = createButtonItem("mQuit", "Quit the Game", t->gameManager.quitGame());
    toolbar.getChildren().add(exit);
    return toolbar;
  }

  private Button createButtonItem(String symbol, String text, EventHandler<ActionEvent> t) {
    Button newButton = new Button();
    newButton.setPrefSize(30, 30);
    newButton.setId(symbol);
    newButton.setOnAction(t);
    newButton.setTooltip(new Tooltip(text));
    return newButton;
  }

  public GameManager getGameManager() {
    return gameManager;
  }

  public static int getMargin() {
    return MARGIN;
  }
}
