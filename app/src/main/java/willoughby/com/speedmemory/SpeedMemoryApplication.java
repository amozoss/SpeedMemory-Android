package willoughby.com.speedmemory;



import android.app.Application;
import android.os.Handler;

import willoughby.com.speedmemory.model.BoardData;
import willoughby.com.speedmemory.model.FeedData;
import willoughby.com.speedmemory.model.PlayerData;



/**
 * Created by dan on 7/20/14.
 */
public class SpeedMemoryApplication extends Application {



  private ChatSocket mChatSocket;
  private BoardData  mBoardData;
  private FeedData mFeedData;
  private PlayerData mPlayerData;
  private boolean    isLoggedIn;


  @Override
  public void onCreate() {
    mBoardData = new BoardData();
    mFeedData = new FeedData();
    mPlayerData = new PlayerData();
    mChatSocket = new ChatSocket(this, mBoardData, mPlayerData, new Handler(getMainLooper()));
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


  public FeedData getFeedData() {
    return mFeedData;
  }

  public PlayerData getPlayerData() {
    return mPlayerData;
  }


  public ChatSocket getChatSocket() {
    return mChatSocket;
  }
  // endregion
}
