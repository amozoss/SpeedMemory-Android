package willoughby.com.speedmemory;



import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.socketio.Acknowledge;
import com.koushikdutta.async.http.socketio.ConnectCallback;
import com.koushikdutta.async.http.socketio.DisconnectCallback;
import com.koushikdutta.async.http.socketio.ErrorCallback;
import com.koushikdutta.async.http.socketio.EventCallback;
import com.koushikdutta.async.http.socketio.ExceptionCallback;
import com.koushikdutta.async.http.socketio.JSONCallback;
import com.koushikdutta.async.http.socketio.ReconnectCallback;
import com.koushikdutta.async.http.socketio.SocketIOClient;
import com.koushikdutta.async.http.socketio.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



/**
 * Created by dan on 7/20/14.
 */
public class ChatSocket {

  private final Gson gson = new Gson();
  private SocketIOClient         mSocketClient;
  private SpeedMemoryApplication mSpeedMemoryApplication;
  private boolean                mIsConnecting;
  private BoardData mBoardData;




  private onBoardListener mOnBoardListener;

  ConnectCallback mConnectCallback = new ConnectCallback() {
    @Override
    public void onConnectCompleted(Exception ex, SocketIOClient client) {
      if (ex != null) {
        ex.printStackTrace();
        return;
      } else {
        if (client.isConnected()) {
          mSocketClient = client;
          emitRegister("Dan");
        }

        client.setDisconnectCallback(new DisconnectCallback() {
          @Override
          public void onDisconnect(Exception e) {
            mSocketClient = null;
            mIsConnecting = false;
            Log.d("SOCKET-ON-DISCONNECT", e.toString());
            e.printStackTrace();
          }
        });

        client.setErrorCallback(new ErrorCallback() {
          @Override
          public void onError(String error) {
            Log.e("SOCKET IO ERROR", error);
          }
        });

        client.setExceptionCallback(new ExceptionCallback() {
          @Override
          public void onException(Exception e) {
            e.printStackTrace();
          }
        });

        client.setReconnectCallback(new ReconnectCallback() {
          @Override
          public void onReconnect() {
            Log.d("SOCKET-ON-RECONNECT", "");
          }
        });

        client.setStringCallback(new StringCallback() {
          @Override
          public void onString(String string, Acknowledge acknowledge) {
            Log.d("SOCKET-ON-StringCallback", string);
          }
        });

        client.setJSONCallback(new JSONCallback() {
          @Override
          public void onJSON(JSONObject jsonObject, Acknowledge acknowledge) {
            Log.d("SOCKET-ON-JSONCallback", jsonObject.toString());
          }
        });


        client.on("board", new EventCallback() {
          @Override
          public void onEvent(JSONArray jsonArray, Acknowledge acknowledge) {
            try {
              // TODO ack token
              JSONArray arr = jsonArray.getJSONArray(0);
              mBoardData.setBoard(arr);
              if (mOnBoardListener != null) { mOnBoardListener.board();}
              Log.d("SOCKET-ON-board", jsonArray.toString());
            } catch (JSONException e) {
              e.printStackTrace();
            } catch (JsonSyntaxException e) {
              e.printStackTrace();
            }
            // SOCKET-ON-board﹕ [{"Wz1xjfyei0WrnUG3mfj3":{"score":0,"id":"Wz1xjfyei0WrnUG3mfj3","name":"Dan"}}]
            // SOCKET-ON-board﹕ [[[8,5,5,2,2,2,9,6,1,6],[7,8,4,7,2,3,2,2,4,5],[9,4,4,1,4,9,10,6,10,8],[1,4,3,9,4,4,9,3,3,6],[5,6,6,8,1,3,10,5,6,3],[5,7,10,9,8,8,1,8,10,7],[3,3,1,8,7,4,10,8,10,5],[6,1,5,6,9,2,2,5,1,8],[2,10,7,10,3,7,9,7,7,5],[1,2,10,1,9,6,9,7,4,3]]]

          }
        });

        client.on("mouse", new EventCallback() {
          @Override
          public void onEvent(JSONArray jsonArray, Acknowledge acknowledge) {
            Log.d("SOCKET-ON-mouse", jsonArray.toString());
          }
        });

        client.on("choose", new EventCallback() {
          @Override
          public void onEvent(JSONArray jsonArray, Acknowledge acknowledge) {
            Log.d("SOCKET-ON-choose", jsonArray.toString());
          }
        });

        client.on("players", new EventCallback() {
          @Override
          public void onEvent(JSONArray jsonArray, Acknowledge acknowledge) {
            Log.d("SOCKET-ON-players", jsonArray.toString());
          }
        });
      }
    }
  };




  public ChatSocket(SpeedMemoryApplication speedMemoryApplication, BoardData boardData) {
    mIsConnecting = false;
    mBoardData = boardData;
    mSpeedMemoryApplication = speedMemoryApplication;
  }


  public void connect() {
    if (mSocketClient == null && !mIsConnecting) {
      mIsConnecting = true;
      String url = "http://192.168.1.106:3000";
      SocketIOClient.connect(AsyncHttpClient.getDefaultInstance(), url, mConnectCallback);
      Log.d("SOCKET", "Connected");
    }
  }


  public boolean isConnected() {
    return (mSocketClient != null && mSocketClient.isConnected());
  }


  public void disconnect() {
    if (mSocketClient != null && mSocketClient.isConnected()) {
      try {
        mIsConnecting = false;
        mSocketClient.disconnect();
        mSocketClient = null;
      } catch (NullPointerException e) {
        mSocketClient = null; // For some reason it throws NUllPointer, but still disconnects
      } catch (Exception e) {
        e.printStackTrace();
        mSocketClient = null;
      }
    }
    Log.d("SOCKET", "Disconnected");
  }


  public void emitRegister(String name) {
    if (mSocketClient.isConnected()) {
      JSONObject object = new JSONObject();
      JSONArray params = new JSONArray();
      try {
        object.put("name", name);
          params.put(object);
          String json = object.toString();
          Log.d("SOCKET-EMIT-register", json);
          mSocketClient.emit("register", params);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }




    // region: Getters & Setters
    public void setOnBoardListener(onBoardListener mOnBoardListener) {
      this.mOnBoardListener = mOnBoardListener;
    }
    // endregion

    public interface onBoardListener {
      void board();
    }
  }
