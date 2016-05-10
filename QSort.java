package application;

import java.awt.BorderLayout;
import java.util.stream.IntStream;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class QSort {

  public static void stageSorting() {
    Pane scene_info = new Pane();

    Stage sorting = new Stage();
    sorting.setWidth(800);
    sorting.setHeight(800);
    sorting.setTitle("Sorting of the games 2048");
    sorting.getIcons().add(new Image(QSort.class.getResourceAsStream("/Resources/2048.png")));
    scene_info.getStylesheets().add("application/game.css");

    Button buttonSortingLong = new Button("FX(Step)");
    buttonSortingLong.getStyleClass().add("game-buttonSortingLong");
    buttonSortingLong.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        double startTime = System.currentTimeMillis();

        GameManager.OpenFileToStep();

        sortingData();

        double timeSpent = (System.currentTimeMillis() - startTime) / 1000;

        printDataText(scene_info, timeSpent, Board.Files[Board.count - 1], Board.Files[0]);

        createSortingTable("Score", Board.Files);

        printMassiveString();

      }
    });

    Button buttonSortingScore = new Button("FX(score)");
    buttonSortingScore.getStyleClass().add("game-buttonSortingScore");
    buttonSortingScore.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {

        long startTime = System.currentTimeMillis();

        GameManager.OpenFileToScore();

        sortingData();

        double timeSpent = System.currentTimeMillis() - startTime;
        System.out.println("Time: " + timeSpent);

        printDataText(scene_info, timeSpent, Board.Files[Board.count - 1], Board.Files[1]);

        createSortingTable("Score", Board.Files);

        printMassiveString();
      }
    });

    Button buttonSortingScalaLong = new Button("Scala(Step)");
    buttonSortingScalaLong.getStyleClass().add("game-buttonSortingScalaLong");
    buttonSortingScalaLong.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        // Scala Code late )
      }
    });

    Button buttonSortingScalaScore = new Button("Scala(Score)");
    buttonSortingScalaScore.getStyleClass().add("game-buttonSortingScalaScore");
    buttonSortingScalaScore.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
		// Scala Code late )
      }
    });

    Text info_sorting = new Text("Sorting data games 2048:");
    info_sorting.getStyleClass().add("game-info_sorting");

    Button buttonClose = new Button("CLOSE");
    buttonClose.getStyleClass().add("game-buttonClose");
    buttonClose.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        sorting.close();
      }
    });

    Scene scene = new Scene(scene_info);
    scene_info.getStyleClass().add("game-sorting");

    scene_info.getChildren().addAll(info_sorting, buttonSortingLong, buttonSortingScore,
        buttonSortingScalaLong, buttonSortingScalaScore, buttonClose);

    sorting.setScene(scene);
    sorting.show();
  }

  static JDialog dialog = null;
  static JTable table = null;

  public static void createSortingTable(String name, String[] ms) {

    dialog = new JDialog();
    dialog.setTitle(name);

    DefaultTableModel model = new DefaultTableModel(new Object[] {"Sorted games"}, 0);
    table = new JTable(model);
    dialog.setLayout(new BorderLayout());
    dialog.add(new JScrollPane(table), BorderLayout.CENTER);

    for (int tempruaryFirstruary = 0; tempruaryFirstruary < Board.count; tempruaryFirstruary++)
      model.addRow(new Object[] {ms[tempruaryFirstruary]});
    dialog.pack();
    dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    dialog.setResizable(true);
    dialog.setVisible(true);

    Board.count = 0;
  }

  public static void sortingData() {
    String tempruaryFirstruaryFile = null;
    int pos = 0;
    for (int i = 0; i < Board.count; i++) {
      Board.MaxScore = Board.FilesData[i];
      tempruaryFirstruaryFile = Board.Files[i];

      for (int j = i; j < Board.count; j++)
        if (Board.MaxScore >= Board.FilesData[j]) {
          Board.MaxScore = Board.FilesData[j];
          tempruaryFirstruaryFile = Board.Files[j];
          pos = j;
        }
      int tempruaryFirstruary = Board.FilesData[i];
      String tempruaryFirst = Board.Files[i];
      Board.FilesData[i] = Board.MaxScore;
      Board.Files[i] = tempruaryFirstruaryFile;
      Board.FilesData[pos] = tempruaryFirstruary;
      Board.Files[pos] = tempruaryFirst;
    }
  }

  public static void printMassiveString() {
    for (int i = 0; i < Board.count; i++) {
      System.out.printf("Sort files[%d]: %s \n", i, Board.Files[i]);
    }
  }

  public static void printDataText(Pane sorting, double timer, String Best, String Worst) {
    Double value = timer;
    String Timers = value.toString();
    Text time = new Text();
    time.setText("Time sorting: " + Timers + " [sec]");
    time.setFont(Font.font("Arial", FontPosture.ITALIC, 24));
    time.setFill(Color.YELLOW);
    time.setLayoutX(100);
    time.setLayoutY(200);

    Text best = new Text();
    best.setText("Best game: " + Best);
    best.setFont(Font.font("Arial", FontPosture.ITALIC, 24));
    best.setFill(Color.YELLOW);
    best.setLayoutX(100);
    best.setLayoutY(250);

    Text worst = new Text();
    worst.setText("Worst game: " + Worst);
    worst.setFont(Font.font("Arial", FontPosture.ITALIC, 24));
    worst.setFill(Color.YELLOW);
    worst.setLayoutX(100);
    worst.setLayoutY(300);

    sorting.getChildren().addAll(time, best, worst);
  }
}
