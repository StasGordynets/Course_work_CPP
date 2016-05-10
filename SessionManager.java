package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.StringProperty;
import javafx.stage.FileChooser;
import javafx.stage.Window;


public class SessionManager {


  public String SESSION_PROPERTIES_FILENAME;
  public String SESSION_PROPERTIES_FILENAME_Replay;

  private final static Properties props = new Properties();

  private final GridOperator gridOperator;
  public static long numberSave = 0;
  public static int numberRead = 1;
  public static boolean endFile = false;

  public SessionManager(GridOperator gridOperator) {
    this.gridOperator = gridOperator;
    this.SESSION_PROPERTIES_FILENAME = "Save3.txt";
  }

  public void saveSession(Map<Location, Tile> gameGrid, Integer score, Long time, Long number) {
    try {
      gridOperator.traverseGrid((x, y) -> {
        Tile tile = gameGrid.get(new Location(x, y));
        props.setProperty("Location_" + x + "_" + y, tile != null ? tile.getValue().toString() : "0");
        return 0;
      });
      props.setProperty("score", score.toString());
      props.setProperty("time", time.toString());
      props.setProperty("NumberOfSave", number.toString());
      props.store(new FileWriter(GameManager.gameSaveFile, true), SESSION_PROPERTIES_FILENAME);
    } catch (IOException ex) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
    }
  }

  public int restoreSession(Map<Location, Tile> gameGrid, StringProperty time) {
    BufferedReader reader = null;
    long lines = 0;	
    try {
      lines = Files.lines(GameManager.gameReadFile.toPath()).count();  
      reader = new BufferedReader(new FileReader(GameManager.gameReadFile));
      props.load(reader);
    } catch (FileNotFoundException ignored) {
      System.out.println("Not found file Save replay!");
      return -1;
    } catch (IOException ex) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
    } finally {
      System.out.printf("EXIT, NumberRead: %d \n", NumberRead);
      if (reader != null) {
        reader.close();
      }
    }
    gridOperator.traverseGrid((x, y) -> {
      String val = props.getProperty("Location_" + x + "_" + y);
      if (!val.equals("0")) {
        Tile t = Tile.newTile(new Integer(val));
        Location l = new Location(x, y);
        t.setLocation(l);
        gameGrid.put(l, t);
      }
      return 0;
    });
    time.set(props.getProperty("time"));

    String score = props.getProperty("score");
    if (score != null) {
      return new Integer(score);
    }
    return 0;
  }

  public int openLastStepOfSession(Map<Location, Tile> gameGrid, StringProperty time) {
    Reader reader = null;
    try {
      reader = new FileReader(SESSION_PROPERTIES_FILENAME);
      props.load(reader);
    } catch (FileNotFoundException ignored) {
      System.out.println("Not found file Save replay!");
      return -1;
    } catch (IOException ex) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
    } finally {
      try {
        if (reader != null) {
          reader.close();
        }
      } catch (IOException ex) {
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
      }
    }
    gridOperator.traverseGrid((x, y) -> {
      String val = props.getProperty("Location_" + x + "_" + y);
      if (!val.equals("0")) {
        Tile t = Tile.newTile(new Integer(val));
        Location l = new Location(x, y);
        t.setLocation(l);
        gameGrid.put(l, t);
      }
      return 0;
    });
    time.set(props.getProperty("time"));
    String score = props.getProperty("score");
    if (score != null) {
      return new Integer(score);
    }
    return 0;
  }

  public static int sortingFileScore(File file) {
    Reader reader = null;
    try {
      reader = new FileReader(file);
      props.load(reader);
    } catch (FileNotFoundException ignored) {
      System.out.println("Not found file!");
      return 1;
    } catch (IOException ex) {
      Logger.getLogger(SessionManager.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      try {
        if (reader != null) {
          reader.close();
        }
      } catch (IOException ex) {
        Logger.getLogger(SessionManager.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    String score = props.getProperty("score");
    if (score != null) {
      return new Integer(score);
    }
    return 0;
  }

  public static int sortingFileStep(File file) {
    Reader reader = null;
    try {
      reader = new FileReader(file);
      props.load(reader);
    } catch (FileNotFoundException ignored) {
      System.out.println("Not found file!");
      return 1;
    } catch (IOException ex) {
      Logger.getLogger(SessionManager.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      try {
        if (reader != null) {
          reader.close();
        }
      } catch (IOException ex) {
        Logger.getLogger(SessionManager.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    String NumberOfSave = props.getProperty("NumberOfSave");
    if (NumberOfSave != null) {
      return new Integer(NumberOfSave);
    }
    return 0;
  }
}

