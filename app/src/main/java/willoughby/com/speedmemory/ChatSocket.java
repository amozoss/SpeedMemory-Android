package willoughby.com.speedmemory;



import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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



/**
 * Created by dan on 7/20/14.
 */
public class ChatSocket {

  private final Gson gson = new Gson();
  private SocketIOClient         mSocketClient;
  private SpeedMemoryApplication mSpeedMemoryApplication;
  private boolean                mIsConnecting;
  private BoardData              mBoardData;




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
              JSONArray arr = jsonArray.getJSONArray(0);
              mBoardData.setBoard(arr);
              if (mOnBoardListener != null) {
                mOnBoardListener.updateBoard();
              }
              Log.d("SOCKET-ON-board", jsonArray.toString());
            } catch (JSONException e) {
              e.printStackTrace();
            } catch (JsonSyntaxException e) {
              e.printStackTrace();
            }
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
            try {
              JSONObject choice = jsonArray.getJSONObject(0);
              mBoardData.setChoice(choice.getString("id"), choice.getInt("y"), choice.getInt("x"), choice.getInt("color"));
              if (mOnBoardListener != null) {
                mOnBoardListener.updateBoard();
              }
            } catch (JSONException e) {
              e.printStackTrace();
            } catch (JsonSyntaxException e) {
              e.printStackTrace();
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
                mOnBoardListener.playerScored(scored.getString("name"), scored.getInt("amount"));
              }
            } catch (JSONException e) {
              e.printStackTrace();
            } catch (JsonSyntaxException e) {
              e.printStackTrace();
            }
          }
        });


        client.on("players", new EventCallback() {
          @Override
          public void onEvent(JSONArray jsonArray, Acknowledge acknowledge) {
            Log.d("SOCKET-ON-players", jsonArray.toString());
            try {
              ArrayList<String> playerData = new ArrayList<String>();
              JSONObject playerDict = jsonArray.getJSONObject(0);
              Iterator<?> keys = playerDict.keys();

              ArrayList<String> tempPlayers = new ArrayList<String>();
              while (keys.hasNext()) {
                String key = (String)keys.next();
                if (playerDict.get(key) instanceof JSONObject) {
                  JSONObject p = playerDict.getJSONObject(key);
                  String player = p.getString("name") + " " + p.getString("score");
                  tempPlayers.add(player);
                }
              }
              // condense (kinda ghetto)
              int totalSize = tempPlayers.size();
              int count = 0;
              String combined = "";
              for (String player : tempPlayers) {
                if (totalSize >= 2 && count == 0) {
                  combined = player;
                  totalSize--;
                  count++;
                } else if (count == 1) {
                  combined += "                     " + player;
                  totalSize--;
                  count = 0;
                  playerData.add(combined);
                } else {
                  playerData.add(player);
                }
              }


              if (mOnBoardListener != null) {
                mOnBoardListener.updatePlayers(playerData);
              }
            } catch (JSONException e) {
              e.printStackTrace();
            }
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


  public void connect(String serverAddress, String name) {
    mName = name;
    if (mSocketClient == null && !mIsConnecting) {
      mIsConnecting = true;
      //String url = "http://192.168.1.106:3000";

      try {
        SocketIOClient.connect(AsyncHttpClient.getDefaultInstance(), serverAddress, mConnectCallback);
        Log.d("SOCKET", "Connected");
      } catch (Exception e) {
        if (mOnConnectionListener != null) {
          mOnConnectionListener.exception(e);
        }
      }
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

    void updatePlayers(List<String> players);

    void playerScored(String name, int amount);
  }


  public interface onConnectionListener {

    void connected();

    void exception(Exception ex);
  }
}
