package com.ituto.android.Services;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.ituto.android.Constant;
import com.ituto.android.ConversationActivity;
import com.ituto.android.MainActivity;
import com.ituto.android.R;
import com.ituto.android.Utils.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketIOService extends Service {
    private SharedPreferences sharedPreferences;
    private NotificationUtils notificationUtils;
    private String conversationID;
    private Socket socket;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        try {
            socket = IO.socket(Constant.URL);
            socket.connect();
            socket.emit("join", sharedPreferences.getString("_id", ""));
            socket.on("received", args -> {

                JSONObject messageObject = (JSONObject) args[0];

                try {
                    JSONObject senderObject = messageObject.getJSONObject("sender");
                    JSONObject conversationObject = messageObject.getJSONObject("conversationID");

                    String sender = senderObject.getString("firstname") + " " + senderObject.getString("lastname");

                    JSONObject avatar = senderObject.getJSONObject("avatar");
                    Intent resultIntent = new Intent(this, ConversationActivity.class);
                    resultIntent.putExtra("conversationID", conversationObject.getString("_id"));
                    resultIntent.putExtra("avatar", avatar.getString("url"));
                    resultIntent.putExtra("name", senderObject.getString("firstname") + " " + senderObject.getString("lastname"));
                    showNotificationMessage(this, sender, messageObject.has("attachment") ? "Sent an attachment" : messageObject.getString("content"), "", resultIntent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
//        notificationUtils.playNotificationSound();
    }
}

