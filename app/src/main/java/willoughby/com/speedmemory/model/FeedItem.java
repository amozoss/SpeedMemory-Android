package willoughby.com.speedmemory.model;



/**
 * Created by dan on 10/10/14.
 */
public class FeedItem {
  private int mAmount;
  private String mColor;

  public FeedItem(int amount, String color) {
    mAmount = amount;
    mColor = color;
  }

  public String getColor() {
    return mColor;
  }


  public void setColor(String mColor) {
    this.mColor = mColor;
  }


  public int getAmount() {
    return mAmount;
  }


  public void setAmount(int mAmount) {
    this.mAmount = mAmount;
  }


}
