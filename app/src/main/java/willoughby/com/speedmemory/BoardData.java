package willoughby.com.speedmemory;



import java.util.HashMap;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;



/**
 * Created by dan on 7/20/14.
 */
public class BoardData {

  private       JSONArray                    mBoardData;
  private       HashMap<String, Queue<Card>> mPreviousChoices;
  public static int                          HIGHLIGHT_COUNT = 4; // how many numbers to keep showing

  public BoardData() {
    mPreviousChoices = new HashMap<String, Queue<Card>>();
  }

  public void setBoard(JSONArray jsonArray) {
    mBoardData = jsonArray;
    Log.d("BOARDDATA", "setBoard");
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

  public int getAlpha(int row, int col) {
    int a = 0;
    double count = 0.0;
    Card toFind = new Card(row, col);
    for (Queue<Card> q : mPreviousChoices.values()) {
      Iterator<Card> it = q.iterator();
      while (it.hasNext()) {
        Card card = it.next();
        count ++;
        if (card.equals(toFind)) {
          return (int)(255.0 * (count/q.size()));
        }
      }
      count = 0.0;
    }
    return (int)count;
  }


  public void setChoice(String id, int x, int y) {
    Card card = new Card(x, y);
    if (mPreviousChoices.containsKey(id)) {
      Queue<Card> queue = mPreviousChoices.get(id);
      queue.add(card);
      if (queue.size() > HIGHLIGHT_COUNT) {
        queue.remove();
      }
    }
    else {
      Queue<Card> queue = new ConcurrentLinkedQueue<Card>();
      queue.add(card);
      mPreviousChoices.put(id, queue);
    }
  }
}
