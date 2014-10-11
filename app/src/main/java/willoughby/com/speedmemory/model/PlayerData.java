package willoughby.com.speedmemory.model;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



/**
 * Created by dan on 10/10/14.
 */
public class PlayerData {
  private static final int COL = 5;

  private JSONArray mPlayerData;



  public void setPlayerData(JSONArray jsonArray) {
    mPlayerData = jsonArray;
  }



  private JSONObject get(int row, int col) {
    if (mPlayerData != null) {
      if (row * COL + col >= mPlayerData.length()) {
        return null;
      }
      try {
        JSONObject obj = mPlayerData.getJSONObject(row * COL + col);
        return obj;
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  public int getScore(int row, int col) {
    JSONObject obj = get(row, col);

    if (obj == null) return 0;
    try {
      return obj.getInt("score");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return 0;
  }

  public String getColor(int row, int col) {
    JSONObject obj = get(row, col);

    if (obj == null) return "#ffffff";
    try {
      return obj.getString("color");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return "#ffffff";
  }

  public String getName(int row, int col) {
    JSONObject obj = get(row, col);

    if (obj == null) return "Speed Memory";
    try {
      return obj.getString("name");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return "Speed Memory";
  }

  public int size() {
    return mPlayerData.length();
  }
}
