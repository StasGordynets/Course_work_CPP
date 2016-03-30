package game2048;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Board extends Group {

  public static final int CELL_SIZE = 128;
  private static final int BORDER_WIDTH = (14 + 2) / 2;
  public static final int GRID_WIDTH = 4 * CELL_SIZE + 2 * BORDER_WIDTH;
  private static final int TOP_HEIGHT = 92;
  private static final int GAP_HEIGHT = 50;

  private final VBox vGame = new VBox(0);
  private final HBox hMid = new HBox();
  private final HBox hBottom = new HBox();

  private final HBox hTop = new HBox(0);
  private final VBox vScore = new VBox(0);
  private final Label lblScore = new Label("0");
  private final Label lblBest = new Label("0");
  private final Label lblPoints = new Label();

  private final Group gridGroup = new Group();
  private final IntegerProperty gameMovePoints = new SimpleIntegerProperty(0);
  private final IntegerProperty gameScoreProperty = new SimpleIntegerProperty(0);
  private final IntegerProperty gameScoreBestProperty = new SimpleIntegerProperty(0);
  private final Timeline animateAddedPoints = new Timeline();

  private final HBox overlay = new HBox();
  private final Label lOvrText = new Label();
  private final HBox buttonsOverlay = new HBox();
  private final Button bStart = new Button("Play game");
  private final Button bTry = new Button("Try again");
  private final Button bContinue = new Button("Continue");

  private final BooleanProperty gameStartProperty = new SimpleBooleanProperty(false);
  private final BooleanProperty gameWonProperty = new SimpleBooleanProperty(false);
  private final BooleanProperty gameOverProperty = new SimpleBooleanProperty(false);
  private final BooleanProperty gamePauseProperty = new SimpleBooleanProperty(false);
  private final BooleanProperty resetGame = new SimpleBooleanProperty(false);

  public static boolean GameOver = false;
  public static boolean Winner = false;
  public static boolean BotActive = false;
  public static boolean GamePause = false;

  /**
   * Create the game board
   */
  public Board() {
    CreateButtons();
    createScore();
    createGrid();
    initGameProperties();
  }

  /**
   * Create information window about the developer of the game
   */
  public void StageInformation() {
    Pane scene_info = new Pane();

    Stage info = new Stage();
    info.setTitle("Information about the games 2048");
    info.getIcons().add(new Image(this.getClass().getResourceAsStream("2048.png")));
    scene_info.getStylesheets().add("game2048/game.css");

    Text info_programm = new Text("Games 2048 on JavaFx");
    info_programm.getStyleClass().add("game-info_programm");

    Text info_version = new Text("Game version: v 1.0.1 ");
    info_version.getStyleClass().add("game-info_version");

    Text info_developer = new Text("Developer: Stanislav Gordynets");
    info_developer.getStyleClass().add("game-info_developer");

    Button Button_close = new Button("CLOSE");
    Button_close.getStyleClass().add("game-button_close_information");
    Button_close.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        info.close();
      }
    });

    Scene scene = new Scene(scene_info);
    scene_info.getStyleClass().add("game-about-information");

    scene_info.getChildren().addAll(info_programm, info_version, info_developer, Button_close);

    info.setScene(scene);
    info.show();
  }

  /**
   * Create buttons for games
   */
  public void CreateButtons() {
    Image imageSave = new Image(getClass().getResourceAsStream("save.png"));
    Button Save = new Button();
    Save.getStyleClass().addAll("game-button-save");
    Save.setGraphic(new ImageView(imageSave));

    Save.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent event) {
        System.out.println("You are press save game");
      }
    });

    Image imageLoad = new Image(getClass().getResourceAsStream("load.png"));
    Button Load = new Button();
    Load.getStyleClass().addAll("game-button-load");
    Load.setGraphic(new ImageView(imageLoad));

    Load.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent event) {
        System.out.println("You are press load game");
      }
    });

    Image imageBot = new Image(getClass().getResourceAsStream("bot.png"));
    Button Bot = new Button();
    Bot.getStyleClass().addAll("game-button-bot");
    Bot.setGraphic(new ImageView(imageBot));

    Bot.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        if (BotActive == false) {
          GameBot.AnimationTimer.start();
          BotActive = true;
        } else {
          GameBot.AnimationTimer.stop();
          BotActive = false;
        }
      }
    });

    Image imagePause = new Image(getClass().getResourceAsStream("pause.png"));
    Button Pause = new Button();
    Pause.getStyleClass().addAll("game-button-pause");
    Pause.setGraphic(new ImageView(imagePause));

    Pause.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        if (GamePause == false) {
          setGamePause(true);
          GamePause = true;
          if (BotActive == true) {
            GameBot.AnimationTimer.stop();
            BotActive = true;
          }
        } else {
          setGamePause(false);
          GamePause = false;
          if (BotActive == true) {
            GameBot.AnimationTimer.start();
          }
          getChildren().removeAll(overlay);
        }
      }
    });

    Image imageInformation = new Image(getClass().getResourceAsStream("information.png"));
    Button Information = new Button();
    Information.getStyleClass().addAll("game-button-information");
    Information.setGraphic(new ImageView(imageInformation));

    Information.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        StageInformation();
      }
    });

    Image imageExit = new Image(getClass().getResourceAsStream("exit.png"));
    Button Exit = new Button();
    Exit.getStyleClass().addAll("game-button-exit");
    Exit.setGraphic(new ImageView(imageExit));

    Exit.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        Platform.exit();
      }
    });
    getChildren().addAll(Save, Load, Bot, Pause, Information, Exit);
  }

  /**
   * �reate a place for the account and the best score for the game
   */
  private void createScore() {
    Label lblTitle = new Label("Games 2048");
    Label lblSubtitle = new Label("on JavaFx");

    HBox hFill = new HBox();
    HBox.setHgrow(hFill, Priority.ALWAYS);

    VBox vScores = new VBox();
    HBox hScores = new HBox(5);

    Label lblTit = new Label("SCORE");
    vScore.getChildren().addAll(lblTit, lblScore);

    VBox vRecord = new VBox(0);
    Label lblTitBest = new Label("BEST");
    vRecord.getChildren().addAll(lblTitBest, lblBest);

    hScores.getChildren().addAll(vScore, vRecord);
    VBox vFill = new VBox();
    VBox.setVgrow(vFill, Priority.ALWAYS);
    vScores.getChildren().addAll(hScores, vFill);

    hTop.getChildren().addAll(lblTitle, lblSubtitle, hFill, vScores);

    lblTitle.getStyleClass().addAll("game-label", "game-title");
    lblSubtitle.getStyleClass().addAll("game-label", "game-subtitle");
    vScore.getStyleClass().add("game-vbox");
    lblTit.getStyleClass().addAll("game-label", "game-titScore");
    lblScore.getStyleClass().addAll("game-label", "game-score");
    vRecord.getStyleClass().add("game-vbox");
    lblTitBest.getStyleClass().addAll("game-label", "game-titScore");
    lblBest.getStyleClass().addAll("game-label", "game-score");

    hTop.setMinSize(GRID_WIDTH, TOP_HEIGHT);
    hTop.setPrefSize(GRID_WIDTH, TOP_HEIGHT);
    hTop.setMaxSize(GRID_WIDTH, TOP_HEIGHT);

    vGame.getChildren().add(hTop);

    hMid.setMinSize(GRID_WIDTH, GAP_HEIGHT);

    vGame.getChildren().add(hMid);

    lblPoints.getStyleClass().addAll("game-label", "game-points");
    lblPoints.setAlignment(Pos.CENTER);
    lblPoints.setMinWidth(100);
    getChildren().add(lblPoints);

    lblPoints.textProperty().bind(Bindings.createStringBinding(
        () -> (gameMovePoints.get() > 0) ? "+".concat(Integer.toString
            (gameMovePoints.get())) : "",
        gameMovePoints.asObject()));
    lblScore.textProperty().bind(gameScoreProperty.asString());

    lblBest.textProperty().bind(gameScoreProperty.asString());

    lblScore.textProperty().addListener((ov, s, s1) -> {
      lblPoints.setLayoutX(0);
      double midScoreX = vScore.localToScene(vScore.getWidth() / 2, 0).getX();
      lblPoints.setLayoutX(lblPoints.sceneToLocal(
          midScoreX, 0).getX() - lblPoints.getWidth() / 2d);
    });

    final KeyValue OpacityProperty0 = new KeyValue(lblPoints.opacityProperty(), 1);
    final KeyValue LayoutYProperty0 = new KeyValue(lblPoints.layoutYProperty(), 20);
    final KeyValue OpacityProperty1 = new KeyValue(lblPoints.opacityProperty(), 0);
    final KeyValue LayoutYProperty1 = new KeyValue(lblPoints.layoutYProperty(), 100);
    final KeyFrame FrameOpacity0 = new KeyFrame(Duration.ZERO, OpacityProperty0);
    final KeyFrame FrameLayout0 = new KeyFrame(Duration.ZERO, LayoutYProperty0);

    Duration animationDuration = Duration.millis(1000);
    final KeyFrame FrameOpacity1 = new KeyFrame(animationDuration, OpacityProperty1);
    final KeyFrame FrameLayout1 = new KeyFrame(animationDuration, LayoutYProperty1);

    animateAddedPoints.getKeyFrames().addAll(FrameOpacity0, FrameLayout0, FrameOpacity1,
        FrameLayout1);
  }

  /**
   * Draw the cell
   */
  private Rectangle createCell(int PositionX, int PositionY) {
    Rectangle cell = null;

    cell = new Rectangle(PositionX * CELL_SIZE, PositionY * CELL_SIZE, CELL_SIZE, CELL_SIZE);
    cell.setFill(Color.WHITE);
    cell.setStroke(Color.GREY);

    if (cell != null) {
      cell.setArcHeight(CELL_SIZE / 6);
      cell.setArcWidth(CELL_SIZE / 6);
      cell.getStyleClass().add("game-grid-cell");
    }
    return cell;
  }

  /**
   * Draw the box the size of 4 by 4
   */
  private void createGrid() {
    GridOperator.traverseGrid((xPosition, yPosition) -> {
      gridGroup.getChildren().add(createCell(xPosition, yPosition));
      return 0;
    });

    gridGroup.getStyleClass().add("game-grid");
    hBottom.getStyleClass().add("game-backGrid");

    gridGroup.setManaged(false);
    gridGroup.setLayoutX(BORDER_WIDTH);
    gridGroup.setLayoutY(BORDER_WIDTH);

    hBottom.setMinSize(GRID_WIDTH, GRID_WIDTH);
    hBottom.setPrefSize(GRID_WIDTH, GRID_WIDTH);
    hBottom.setMaxSize(GRID_WIDTH, GRID_WIDTH);

    Rectangle rect = new Rectangle(GRID_WIDTH, GRID_WIDTH);
    hBottom.setClip(rect);

    hBottom.getChildren().add(gridGroup);

    vGame.getChildren().add(hBottom);
    getChildren().add(0, vGame);
  }

  /**
   * Add tile to the grid
   */
  public void addTile(Tile tile) {
    moveTile(tile, tile.getLocation());
    gridGroup.getChildren().add(tile);
  }

  /**
   * Move the tile on the field
   */
  public void moveTile(Tile tile, Location location) {
    double layoutX = location.getLayoutX(CELL_SIZE) - (tile.getMinWidth() / 2);
    double layoutY = location.getLayoutY(CELL_SIZE) - (tile.getMinHeight() / 2);
    tile.setLayoutX(layoutX);
    tile.setLayoutY(layoutY);
  }

  /**
   * We get information about the GridGroup
   */
  public Group getGridGroup() {
    return gridGroup;
  }

  /**
   * Set points in MovePoints
   */
  public void setPoints(int points) {
    gameMovePoints.set(points);
  }

  /**
   * Get points in MovePoints
   */
  public int getPoints() {
    return gameMovePoints.get();
  }

  /**
   * Get points in score
   */
  public int getBestPoints() {
    return gameScoreProperty.get();
  }

  /**
   * Set points in score
   */
  public void addPoints(int points) {
    gameMovePoints.set(gameMovePoints.get() + points);
    gameScoreProperty.set(gameScoreProperty.get() + points);
  }

  /**
   * Set points in best score
   */
  public void BestPoints(int points) {
    gameScoreBestProperty.set(gameScoreProperty.get() + points);
  }

  /**
   * Animated score
   */
  public void animateScore() {
    animateAddedPoints.playFromStart();
  }

  /**
   * Initialize the settings of various actions
   */
  private void initGameProperties() {
    overlay.setMinSize(GRID_WIDTH, GRID_WIDTH);
    overlay.setAlignment(Pos.CENTER);
    overlay.setTranslateY(TOP_HEIGHT + GAP_HEIGHT);

    overlay.getChildren().setAll(lOvrText);

    buttonsOverlay.setAlignment(Pos.CENTER);
    buttonsOverlay.setTranslateY(TOP_HEIGHT + GAP_HEIGHT + GRID_WIDTH / 2);
    buttonsOverlay.setMinSize(GRID_WIDTH, GRID_WIDTH / 2);
    buttonsOverlay.setSpacing(10);

    bStart.getStyleClass().add("game-button");
    bStart.setOnAction(e -> {
      getChildren().removeAll(overlay, buttonsOverlay);
      gridGroup.getChildren().removeIf(c -> c instanceof Tile);
      resetGame.set(false);
      gameScoreProperty.set(0);
      gameStartProperty.set(true);
      gameWonProperty.set(false);
      gameOverProperty.set(false);
      resetGame.set(true);
    });

    bTry.getStyleClass().add("game-button");
    bTry.setOnAction(e -> {
      getChildren().removeAll(overlay, buttonsOverlay);
      gridGroup.getChildren().removeIf(c -> c instanceof Tile);
      resetGame.set(false);
      gameScoreProperty.set(0);
      gameStartProperty.set(true);
      gameWonProperty.set(false);
      gameOverProperty.set(false);
      resetGame.set(true);
    });

    bContinue.getStyleClass().add("game-button");
    bContinue.setOnAction(e -> getChildren().removeAll(overlay, buttonsOverlay));

    gameStartProperty.addListener((observable, oldValue, newValue) -> {
      overlay.getStyleClass().setAll("game-overlay", "game-overlay-start");
      lOvrText.setText("A YOU READY?");
      lOvrText.getStyleClass().setAll("game-label", "game-lblStart");
      buttonsOverlay.getChildren().setAll(bStart);
      this.getChildren().addAll(overlay, buttonsOverlay);
    });

    gamePauseProperty.addListener((observable, oldValue, newValue) -> {
      overlay.getStyleClass().setAll("game-overlay", "game-overlay-pause");
      lOvrText.setText("Pause of Games!");
      lOvrText.getStyleClass().setAll("game-label", "game-lblpause");
      this.getChildren().addAll(overlay);
    });

    gameOverProperty.addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        overlay.getStyleClass().setAll("game-overlay", "game-overlay-over");
        lOvrText.setText("Game over!");
        lOvrText.getStyleClass().setAll("game-label", "game-lblOver");
        buttonsOverlay.getChildren().setAll(bTry);
        this.getChildren().addAll(overlay, buttonsOverlay);
      }
    });

    gameWonProperty.addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        overlay.getStyleClass().setAll("game-overlay", "game-overlay-won");
        lOvrText.setText("You win!");
        lOvrText.getStyleClass().setAll("game-label", "game-lblWon");
        buttonsOverlay.getChildren().setAll(bContinue, bTry);
        this.getChildren().addAll(overlay, buttonsOverlay);
      }
    });
  }

  /**
   * Set's flags of different actions
   */
  public void setGameStart(boolean start) {
    gameStartProperty.set(start);
  }

  public void setGamePause(boolean pause) {
    gamePauseProperty.set(pause);
  }

  public void setGameOver(boolean gameOver) {
    gameOverProperty.set(gameOver);
  }

  public void setGameWin(boolean won) {
    gameWonProperty.set(won);
  }

  public BooleanProperty resetGameProperty() {
    return resetGame;
  }
}