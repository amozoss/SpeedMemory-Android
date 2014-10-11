package willoughby.com.speedmemory.model;



import java.util.ArrayList;



/**
 * Created by dan on 10/10/14.
 */
public class FeedData {

  public static final int FEED_COUNT = 4;
  private ArrayList<FeedItem> mFeedItems;


  public FeedData() {
    mFeedItems = new ArrayList<FeedItem>();
  }


  public void add(int amount, String color) {
    mFeedItems.add(0, new FeedItem(amount, color));
    if (mFeedItems.size() > FEED_COUNT && !mFeedItems.isEmpty()) {
      mFeedItems.remove(mFeedItems.size() - 1);
    }
  }

  public int getAmount(int row, int col) {
    if (col < mFeedItems.size()) {
      return mFeedItems.get(col).getAmount();
    }
    return -1;
  }

  public String getColor(int row, int col) {
    if (col < mFeedItems.size()) {
      return mFeedItems.get(col).getColor();
    }
    return "#FFFFFF";
  }


  public void clear() {
    mFeedItems.clear();
  }

}
