package willoughby.com.speedmemory;



import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



/**
 * Created by dan on 7/19/14.
 */
public class GameFragment extends Fragment implements BoardView.BoardViewDelegate, ChatSocket.onBoardListener{
  private BoardView mBoardView;
  private BoardData mBoardData;
  private Handler mMainHandler;

  public GameFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    SpeedMemoryApplication speedMemoryApplication = (SpeedMemoryApplication)getActivity().getApplication();
    mBoardData = speedMemoryApplication.getBoardData();
    mMainHandler = new Handler(getActivity().getApplicationContext().getMainLooper());
    speedMemoryApplication.getChatSocket().setOnBoardListener(this);

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_game, container, false);
    mBoardView = (BoardView) rootView.findViewById(R.id.fragment_game_board_view);
    mBoardView.setBoardViewDelegate(this);

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
  public void board() {
    mMainHandler.post(new Runnable() {
      @Override
      public void run() {
        mBoardView.invalidate();
      }
    });
  }
}
