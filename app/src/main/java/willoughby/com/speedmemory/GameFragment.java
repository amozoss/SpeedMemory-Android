package willoughby.com.speedmemory;



import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import willoughby.com.speedmemory.model.BoardData;
import willoughby.com.speedmemory.model.FeedData;
import willoughby.com.speedmemory.model.PlayerData;
import willoughby.com.speedmemory.views.BoardView;



/**
 * Created by dan on 7/19/14.
 */
public class GameFragment extends Fragment implements ChatSocket.onBoardListener {

  private BoardView  mBoardView;
  private BoardView  mScoreBoardView;
  private BoardView  mFeedScoreBoardView;
  private BoardData  mBoardData;
  private PlayerData mPlayerData;
  private FeedData   mFeedData;
  private Handler    mMainHandler;
  private ChatSocket mChatSocket;
  private Handler    mHandler;
  private TextView   mNameTextView;


  public GameFragment() {
  }


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mMainHandler = new Handler(getActivity().getApplicationContext().getMainLooper());

    SpeedMemoryApplication speedMemoryApplication = (SpeedMemoryApplication)getActivity().getApplication();
    mBoardData = speedMemoryApplication.getBoardData();
    mFeedData = speedMemoryApplication.getFeedData();
    mPlayerData = speedMemoryApplication.getPlayerData();
    mMainHandler = new Handler(getActivity().getApplicationContext().getMainLooper());
    mChatSocket = speedMemoryApplication.getChatSocket();
    speedMemoryApplication.getChatSocket().setOnBoardListener(this);
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_game, container, false);
    mBoardView = (BoardView)rootView.findViewById(R.id.fragment_game_board_view);
    mBoardView.setBoardViewDelegate(mBoardViewDelegate);

    mScoreBoardView = (BoardView)rootView.findViewById(R.id.fragment_game_score_view);
    mScoreBoardView.setIsCleared(true);
    mScoreBoardView.setRows(2);
    mScoreBoardView.setCols(5);
    mScoreBoardView.setBoardViewDelegate(mScoreBoardViewDelegate);

    mFeedScoreBoardView = (BoardView)rootView.findViewById(R.id.fragment_game_feed_score_view);
    mFeedScoreBoardView.setIsCleared(true);
    mFeedScoreBoardView.setRows(4);
    mFeedScoreBoardView.setCols(1);
    mFeedScoreBoardView.setBoardViewDelegate(mFeedScoreBoardViewDelegate);

    mNameTextView = (TextView)rootView.findViewById(R.id.fragment_game_name_text_view);
    return rootView;
  }


  private BoardView.BoardViewDelegate mBoardViewDelegate = new BoardView.BoardViewDelegate() {
    @Override
    public String get(int row, int col) {
      String number = null;
      if (mBoardData != null) {
        Object num = mBoardData.get(row, col);
        number = (num == null) ? "" : num.toString().equals("null") ? "" : num.toString();
      }
      return number;
    }


    @Override
    public int getAlpha(int row, int col) {
      if (mBoardData != null) {
        return mBoardData.getAlpha(row, col);
      }
      return 0;
    }


    @Override
    public String getColor(int row, int col) {
      if (mBoardData != null) {
        return mBoardData.getColor(row, col);
      }
      return "#000000";
    }


    @Override
    public void choose(int row, int col) {
      if (mChatSocket != null) {
        mChatSocket.emitChoose(row, col);
      }
    }
  };

  private BoardView.BoardViewDelegate mFeedScoreBoardViewDelegate = new BoardView.BoardViewDelegate() {
    @Override
    public String get(int col, int row) {
      return Integer.toString(mFeedData.getAmount(row, col));
    }


    @Override
    public int getAlpha(int row, int col) {
      return 255;
    }


    @Override
    public String getColor(int col, int row) {
      return mFeedData.getColor(row, col);
    }


    @Override
    public void choose(int row, int col) {

    }
  };

  private BoardView.BoardViewDelegate mScoreBoardViewDelegate = new BoardView.BoardViewDelegate() {
    @Override
    public String get(int row, int col) {
      return Integer.toString(mPlayerData.getScore(row, col));
    }


    @Override
    public int getAlpha(int row, int col) {
      return 255;
    }


    @Override
    public String getColor(int row, int col) {
      return mPlayerData.getColor(row, col);
    }


    @Override
    public void choose(int col, int row) {
      String color = mPlayerData.getColor(row, col);
      if (color.equals("#ffffff")) {
        color = "#aaaaff";
      }
      String name = mPlayerData.getName(row, col);
      mNameTextView.setText(name);
      mNameTextView.setBackgroundColor(Color.parseColor(color));
      mScoreBoardView.invalidate();
    }
  };



  @Override
  public void playerScored(final String color, final int amount) {

    mMainHandler.post(new Runnable() {
      @Override
      public void run() {
        mFeedData.add(amount, color);
        mFeedScoreBoardView.invalidate();
      }
    });
  }


  @Override
  public void updateBoard() {
    mMainHandler.post(new Runnable() {
      @Override
      public void run() {

        mBoardView.invalidate();
        //mScoreBoardView.invalidate();
      }
    });
  }


  @Override
  public void updateLeaderboard() {
    mMainHandler.post(new Runnable() {
      @Override
      public void run() {
        mScoreBoardView.invalidate();
      }
    });
  }
}
