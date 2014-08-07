package willoughby.com.speedmemory;



import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;



/**
 * Created by dan on 7/20/14.
 */
public class BoardData {
  private JSONArray mBoardData;

  public void setBoard(JSONArray jsonArray) {
    mBoardData = jsonArray;
    Log.d("BOARDDATA", "set board");
  }

  public Object get(int row, int col) {
    if (mBoardData != null) {
      try {
        JSONArray rowArr = mBoardData.getJSONArray(row);
        return rowArr.get(col);
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    return null;
  }
}
