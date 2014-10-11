package willoughby.com.speedmemory;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;



public class MainActivity extends Activity {
  private GameFragment mGameFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    SpeedMemoryApplication speedMemoryApplication = (SpeedMemoryApplication)getApplication();
    if (!speedMemoryApplication.isLoggedIn()) {
      startActivity(new Intent(this, LoginActivity.class));
    }
    if (savedInstanceState == null) {
      mGameFragment = new GameFragment();
      getFragmentManager().beginTransaction()
                          .add(R.id.container, mGameFragment)
                          .commit();
    }
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    SpeedMemoryApplication speedMemoryApplication = (SpeedMemoryApplication)getApplication();
    if (id == R.id.action_disconnect) {
      speedMemoryApplication.setLoggedIn(false);
      speedMemoryApplication.getChatSocket().disconnect();
      speedMemoryApplication.getBoardData().clear();
      startActivity(new Intent(this, LoginActivity.class));
      mGameFragment.restart();
      return true;
    }
    else if (id == R.id.action_restart) {
      speedMemoryApplication.getChatSocket().emitRestart();
    }
    return super.onOptionsItemSelected(item);
  }
}
