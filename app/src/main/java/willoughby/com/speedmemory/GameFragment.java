package willoughby.com.speedmemory;



import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import willoughby.com.speedmemory.model.BoardData;
import willoughby.com.speedmemory.views.BoardView;



/**
 * Created by dan on 7/19/14.
 */
public class GameFragment extends Fragment implements BoardView.BoardViewDelegate, ChatSocket.onBoardListener {

  private BoardView            mBoardView;
  private BoardData            mBoardData;
  private Handler              mMainHandler;
  private ChatSocket           mChatSocket;
  private ListView             mScoreListView;
  private ArrayAdapter<String> mAdapter;


  public GameFragment() {
  }


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    SpeedMemoryApplication speedMemoryApplication = (SpeedMemoryApplication)getActivity().getApplication();
    mBoardData = speedMemoryApplication.getBoardData();
    mMainHandler = new Handler(getActivity().getApplicationContext().getMainLooper());
    mChatSocket = speedMemoryApplication.getChatSocket();
    speedMemoryApplication.getChatSocket().setOnBoardListener(this);
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_game, container, false);
    mBoardView = (BoardView)rootView.findViewById(R.id.fragment_game_board_view);
    mBoardView.setBoardViewDelegate(this);
    mScoreListView = (ListView)rootView.findViewById(R.id.fragment_game_score_listview);
    mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
    mScoreListView.setAdapter(mAdapter);

    return rootView;
  }




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
  public void choose(int row, int col) {
    if (mChatSocket != null) {
      mChatSocket.emitChoose(row, col);
    }
  }


  @Override
  public void updateBoard() {
    mMainHandler.post(new Runnable() {
      @Override
      public void run() {
        mBoardView.invalidate();
      }
    });
  }


  @Override
  public void updatePlayers(final List<String> players) {
    mMainHandler.post(new Runnable() {
      @Override
      public void run() {
        mAdapter.clear();
        mAdapter.addAll(players);
        mAdapter.notifyDataSetChanged();
      }
    });
  }
}
