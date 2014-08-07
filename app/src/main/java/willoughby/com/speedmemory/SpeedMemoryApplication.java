package willoughby.com.speedmemory;



import android.app.Application;



/**
 * Created by dan on 7/20/14.
 */
public class SpeedMemoryApplication extends Application {



  private ChatSocket mChatSocket;
  private BoardData  mBoardData;
  private boolean isLoggedIn;


  @Override
  public void onCreate() {
    mBoardData = new BoardData();
    mChatSocket = new ChatSocket(this, mBoardData);
    isLoggedIn = false;
  }


  // region Getter & Setters
  public boolean isLoggedIn() {
    return isLoggedIn;
  }


  public void setLoggedIn(boolean isLoggedIn) {
    this.isLoggedIn = isLoggedIn;
  }


  public BoardData getBoardData() {
    return mBoardData;
  }


  public ChatSocket getChatSocket() {
    return mChatSocket;
  }
  // endregion
}
