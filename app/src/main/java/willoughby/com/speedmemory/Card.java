package willoughby.com.speedmemory;



import android.graphics.Rect;



/**
 * Created by dan on 8/7/14.
 */
public class Card {


  private int x;
  private int y;



  private Rect rect;


  public Card(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public Card(Rect rect, int x, int y) {
    this.rect = rect;
    this.x = x;
    this.y = y;
  }





  @Override
  public boolean equals(Object other) {
    if (other == null) {
      return false;
    }
    if (other == this) {
      return true;
    }
    if (!(other instanceof Card)) {
      return false;
    }
    Card otherCard = (Card)other;
    return (this.x == otherCard.getX() && this.y == otherCard.getY());
  }


  // region Getter & Setters
  public int getX() {
    return x;
  }


  public void setX(int x) {
    this.x = x;
  }


  public int getY() {
    return y;
  }


  public void setY(int y) {
    this.y = y;
  }


  public Rect getRect() {
    return rect;
  }


  public void setRect(Rect rect) {
    this.rect = rect;
  }
  // endregion
}
