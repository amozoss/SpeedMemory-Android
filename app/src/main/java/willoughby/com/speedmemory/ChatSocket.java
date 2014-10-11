package willoughby.com.speedmemory;



import android.os.Handler;
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

import willoughby.com.speedmemory.model.BoardData;
import willoughby.com.speedmemory.model.PlayerData;



/**
 * Created by dan on 7/20/14.
 */
public class ChatSocket {

  private final Gson gson = new Gson();
  private SocketIOClient         mSocketClient;
  private SpeedMemoryApplication mSpeedMemoryApplication;
  private boolean                mIsConnecting;
  private BoardData              mBoardData;
  private PlayerData             mPlayerData;
  private Handler                mMainHandler;




  private onConnectionListener mOnConnectionListener;



  private onBoardListener mOnBoardListener;
  private String          mName;

  ConnectCallback mConnectCallback = new ConnectCallback() {
    @Override
    public void onConnectCompleted(Exception ex, SocketIOClient client) {
      if (ex != null) {
        ex.printStackTrace();
        mIsConnecting = false;
        if (mOnConnectionListener != null) {
          mOnConnectionListener.exception(ex);
        }

        return;
      } else {
        if (client.isConnected()) {
          mSocketClient = client;
          if (mOnConnectionListener != null) {
            mOnConnectionListener.connected();
          }

          emitRegister(mName == null || "".equals(mName) ? "No name" : mName);
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
              final JSONArray arr = jsonArray.getJSONArray(0);
              mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                  mBoardData.setBoard(arr);
                  if (mOnBoardListener != null) {
                    mOnBoardListener.updateBoard();
                  }
                }
              });
              Log.d("SOCKET-ON-board", jsonArray.toString());
            } catch (JSONException e) {
              e.printStackTrace();
            } catch (JsonSyntaxException e) {
              e.printStackTrace();
            }
          }
        });

        client.on("choose", new EventCallback() {
          @Override
          public void onEvent(JSONArray jsonArray, Acknowledge acknowledge) {
            Log.d("SOCKET-ON-choose", jsonArray.toString());
            try {
              final JSONObject choice = jsonArray.getJSONObject(0);
              mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                  try {
                    mBoardData.setChoice(choice.getString("id"), choice.getInt("y"), choice.getInt("x"), choice.getString("color"));
                  } catch (JSONException e) {
                    e.printStackTrace();
                  }
                  if (mOnBoardListener != null) {
                    mOnBoardListener.updateBoard();
                  }
                }
              });
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
        });

        client.on("leaderboard", new EventCallback() {
                    @Override
                    public void onEvent(JSONArray jsonArray, Acknowledge acknowledge) {
                      Log.d("SOCKET-ON-leaderboard", jsonArray.toString());
                      try {
                        final JSONArray arr = jsonArray.getJSONArray(0);
                        mMainHandler.post(new Runnable() {
                          @Override
                          public void run() {
                            mPlayerData.setPlayerData(arr);
                            if (mOnBoardListener != null) {
                              mOnBoardListener.updateLeaderboard();
                            }
                          }
                        });
                      } catch (JSONException e) {
                        e.printStackTrace();
                      }
                    }
                  }
        );


        client.on("restart", new EventCallback() {
          @Override
          public void onEvent(JSONArray jsonArray, Acknowledge acknowledge) {
            Log.d("SOCKET-ON-restart", jsonArray.toString());
            if (mOnBoardListener != null) {
              mOnBoardListener.restart();
            }
          }
      });


      client.on("scored", new EventCallback() {
                  @Override
                  public void onEvent(JSONArray jsonArray, Acknowledge acknowledge) {
                    Log.d("SOCKET-ON-scored", jsonArray.toString());
                    try {
                      JSONObject scored = jsonArray.getJSONObject(0);
                      if (mOnBoardListener != null) {
                        mOnBoardListener.playerScored(scored.getString("color"), scored.getInt("amount"));
                      }
                    } catch (JSONException e) {
                      e.printStackTrace();
                    } catch (JsonSyntaxException e) {
                      e.printStackTrace();
                    }
                  }
                }
      );
    }
  }
};




public ChatSocket(SpeedMemoryApplication speedMemoryApplication,BoardData boardData,PlayerData playerData,Handler handler){
    mIsConnecting=false;
    mBoardData=boardData;
    mPlayerData=playerData;
    mSpeedMemoryApplication=speedMemoryApplication;
    mMainHandler=handler;
    }


public void connect(String serverAddress,String name){
    mName=name;
    if(mSocketClient==null&&!mIsConnecting){
    mIsConnecting=true;
    //String url = "http://192.168.1.106:3000";

    try{
    SocketIOClient.connect(AsyncHttpClient.getDefaultInstance(),serverAddress,mConnectCallback);
    Log.d("SOCKET","Connected");
    }catch(Exception e){
    if(mOnConnectionListener!=null){
    mOnConnectionListener.exception(e);
    }
    }
    }
    }


public boolean isConnected(){
    return(mSocketClient!=null&&mSocketClient.isConnected());
    }


public void disconnect(){
    if(mSocketClient!=null&&mSocketClient.isConnected()){
    try{
    mIsConnecting=false;
    mSocketClient.disconnect();
    mSocketClient=null;
    }catch(NullPointerException e){
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

  public void emitRestart() {
    if (mSocketClient.isConnected()) {
      JSONObject object = new JSONObject();
      JSONArray params = new JSONArray();
      try {
        object.put("restart", "true");
        params.put(object);
        String json = object.toString();
        Log.d("SOCKET-EMIT-restart", json);
        mSocketClient.emit("restart", params);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public void emitChoose(int x, int y) {
    if (mSocketClient != null && mSocketClient.isConnected()) {
      JSONObject object = new JSONObject();
      JSONArray params = new JSONArray();
      try {
        object.put("x", x);
        object.put("y", y);
        params.put(object);
        String json = object.toString();
        Log.d("SOCKET-EMIT-choose", json);
        mSocketClient.emit("choose", params);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }



  // region: Getters & Setters
  public void setOnBoardListener(onBoardListener mOnBoardListener) {
    this.mOnBoardListener = mOnBoardListener;
  }


  public void setOnConnectionListener(onConnectionListener onConnectionListener) {
    this.mOnConnectionListener = onConnectionListener;
  }
  // endregion


  public interface onBoardListener {

    void updateBoard();

    void updateLeaderboard();

    void playerScored(String name, int amount);

    void restart();
  }


  public interface onConnectionListener {

    void connected();

    void exception(Exception ex);
  }
}
