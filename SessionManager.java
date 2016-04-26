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
  private final Properties props = new Properties();
  private final GridOperator gridOperator;
  public static int NumberSave = 0;
  public static int NumberRead = 1;
  public static boolean EndFile = false;

  public SessionManager(GridOperator gridOperator) {
    this.gridOperator = gridOperator;
  }

  public void saveSession(Map<Location, Tile> gameGrid, Integer score, Long time) {
    this.SESSION_PROPERTIES_FILENAME = "Step#" + NumberSave + ".txt";
    try {
      gridOperator.traverseGrid((x, y) -> {
        Tile t = gameGrid.get(new Location(x, y));
        props.setProperty("Location_" + x + "_" + y, t != null ? t.getValue().toString() : "0");
        return 0;
      });
      props.setProperty("score", score.toString());
      props.setProperty("time", time.toString());
      props.store(new FileWriter(SESSION_PROPERTIES_FILENAME), SESSION_PROPERTIES_FILENAME);
    } catch (IOException ex) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
    }
  }

  public int restoreSession(Map<Location, Tile> gameGrid, StringProperty time, int NumberSave) {
    this.SESSION_PROPERTIES_FILENAME_Replay = "Step#" + NumberRead + ".txt";
    NumberRead++;
    EndFile = false;
    Reader reader = null;
    try {
      reader = new FileReader(SESSION_PROPERTIES_FILENAME_Replay);
      props.load(reader);
    } catch (FileNotFoundException ignored) {
      System.out.println("Not found file");
      EndFile = true;
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
}

