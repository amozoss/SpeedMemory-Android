package willoughby.com.speedmemory;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;



/**
 * Created by dan on 8/7/14.
 */
public class LoginActivity extends Activity implements ChatSocket.onConnectionListener {

  private AutoCompleteTextView mServerView;
  private AutoCompleteTextView mNameView;
  private View                 mProgressView;
  private View                 mContentView;
  private ChatSocket           mChatSocket;
  private Button               mLoginButton;
  private Handler              mMainHandler;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_login);

    mMainHandler = new Handler(getApplicationContext().getMainLooper());
    SpeedMemoryApplication speedMemoryApplication = (SpeedMemoryApplication)getApplication();
    mChatSocket = speedMemoryApplication.getChatSocket();
    mChatSocket.setOnConnectionListener(this);

    mServerView = (AutoCompleteTextView)findViewById(R.id.login_server_view);
    mNameView = (AutoCompleteTextView)findViewById(R.id.login_name_view);
    //mNameView.setText("dan");
    mServerView.setText("http://192.168.1.106:3000");
    mProgressView = findViewById(R.id.login_progress);
    mContentView = findViewById(R.id.login_form);
    mLoginButton = (Button)findViewById(R.id.login_log_in_button);
    mLoginButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        attemptLogin();
      }
    });
  }


  @Override
  public void onBackPressed() {
    // do nothing
  }


  public void attemptLogin() {
    boolean cancel = false;
    View focusView = null;

    mServerView.setError(null);

    String serverAddress = mServerView.getText().toString();
    if (!serverAddress.contains("http://")) {
      serverAddress = "http://" + serverAddress;
    }
    String name = mNameView.getText().toString();

    if (TextUtils.isEmpty(serverAddress)) {
      mServerView.setError(getString(R.string.error_field_required));
      focusView = mServerView;
      cancel = true;
    }

    if (cancel) {
      focusView.requestFocus();
      return;
    }

    mMainHandler.post(new Runnable() {
      @Override
      public void run() {
        showProgress(true);
      }
    });

    mChatSocket.connect(serverAddress, name);
  }


  private void showProgress(final boolean show) {
    mContentView.setVisibility(show ? View.GONE : View.VISIBLE);
    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
  }


  // region onConnectionListener
  @Override
  public void connected() {

    ((SpeedMemoryApplication)getApplication()).setLoggedIn(true);
    finish();
  }


  @Override
  public void exception(final Exception ex) {
    mMainHandler.post(new Runnable() {
      @Override
      public void run() {
        showProgress(false);
        new AlertDialog.Builder(LoginActivity.this)
            .setTitle("Login Error")
            .setMessage(ex.getMessage())
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                // continue with delete
              }
            })
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
        Log.e("LOGIN", ">>>>>>>COULD NOT CONNECT<<<<<<<<<<");
      }
    });
  }
  // endregion
}
