package application;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.StringProperty;

public class SessionManager {
  public String SESSION_PROPERTIES_FILENAME;
  public String SESSION_PROPERTIES_FILENAME_Replay;
  private final Properties PROPS = new Properties();
  private final GridOperator gridOperator;
  public static int numberSave = 0;
  public static int numberRead = 1;
  public static boolean endFile = false;

  public SessionManager(GridOperator gridOperator) {
    this.gridOperator = gridOperator;
  }

  public void saveSession(Map<Location, Tile> gameGrid, Integer score, Long time) {
    this.SESSION_PROPERTIES_FILENAME = "Step#" + numberSave + ".txt";
    try {
      gridOperator.traverseGrid((x, y) -> {
        Tile t = gameGrid.get(new Location(x, y));
        PROPS.setProperty("Location_" + x + "_" + y, t != null ? t.getValue().toString() : "0");
        return 0;
      });
      PROPS.setProperty("score", score.toString());
      PROPS.setProperty("time", time.toString());
      PROPS.store(new FileWriter(SESSION_PROPERTIES_FILENAME), SESSION_PROPERTIES_FILENAME);
    } catch (IOException ex) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
    }
  }

  public int restoreSession(Map<Location, Tile> gameGrid, StringProperty time, int numberSave) {
    this.SESSION_PROPERTIES_FILENAME_Replay = "Step#" + numberRead + ".txt";
    numberRead++;
    endFile = false;
    Reader reader = null;
    try {
      reader = new FileReader(SESSION_PROPERTIES_FILENAME_Replay);
      PROPS.load(reader);
    } catch (FileNotFoundException ignored) {
      System.out.println("Not found file");
      endFile = true;
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
      String val = PROPS.getProperty("Location_" + x + "_" + y);
      if (!val.equals("0")) {
        Tile t = Tile.newTile(new Integer(val));
        Location l = new Location(x, y);
        t.setLocation(l);
        gameGrid.put(l, t);
      }
      return 0;
    });
    time.set(PROPS.getProperty("time"));
    String score = PROPS.getProperty("score");
    if (score != null) {
      return new Integer(score);
    }
    return 0;
  }
}

