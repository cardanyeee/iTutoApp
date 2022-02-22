package com.ituto.android;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ituto.android.Adapters.MessagesAdapter;
import com.ituto.android.Models.Conversation;
import com.ituto.android.Models.Message;
import com.ituto.android.Models.User;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.IO;
import io.socket.client.Socket;

public class ConversationActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private RecyclerView recyclerConversation;
    private EditText txtEnterMessage;
    private Button btnSend;
    private String conversationID;
    private CircleImageView imgYouHeader;
    private TextView txtConversationName;

    private ArrayList<Message> messageArrayList;
    private MessagesAdapter messagesAdapter;
    private User signedUser;

    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        init();
    }

    private void init() {
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        imgYouHeader = findViewById(R.id.imgConversationAvatar);
        txtConversationName = findViewById(R.id.txtConversationName);
        txtEnterMessage = findViewById(R.id.txtEnterMessage);
        btnSend = findViewById(R.id.btnSend);
        recyclerConversation = findViewById(R.id.recyclerConversation);
        recyclerConversation.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        conversationID = getIntent().getStringExtra("conversationID");
        Picasso.get().load(getIntent().getStringExtra("avatar")).fit().centerCrop().into(imgYouHeader);
        txtConversationName.setText(getIntent().getStringExtra("name"));

        try {
            socket = IO.socket("http://192.168.1.2:8080");

            socket.connect();

            socket.emit("connection", conversationID);
            socket.emit("join", conversationID);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        btnSend.setOnClickListener(v -> {
            sendMessage();
        });

        socket.on("received", args -> {
            Log.d("TAGTAGTAGTAGTAGTAG", args.toString());
            runOnUiThread(() -> {
                JSONObject messageObject = (JSONObject) args[0];
                try {
                    JSONObject senderObject = messageObject.getJSONObject("sender");
                    JSONObject conversationObject = messageObject.getJSONObject("conversationID");

                    Message newMessage = new Message();
                    User sender = new User();
                    Conversation conversation = new Conversation();
                    JSONObject avatar = senderObject.getJSONObject("avatar");

                    sender.setUserID(senderObject.getString("_id"));
                    sender.setFirstname(senderObject.getString("firstname"));
                    sender.setAvatar(avatar.getString("url"));

                    JSONArray userArray = conversationObject.getJSONArray("users");
                    conversation.setConversationID(conversationObject.getString("_id"));
                    conversation.setConversationName(conversationObject.getString("conversationName"));
                    ArrayList<String> userIDArrayList = new ArrayList<String>();
                    for ( int a = 0; a < userArray.length(); a++) {
                        String userID = userArray.getString(a);
                        userIDArrayList.add(userID);
                    }
                    conversation.setUserIDArrayList(userIDArrayList);

                    newMessage.setUser(sender);
                    newMessage.setContent(messageObject.getString("content"));
                    newMessage.setConversation(conversation);

//                    recyclerConversation.getAdapter().notifyItemInserted(0);
                    messageArrayList.add(newMessage);

                    recyclerConversation.getAdapter().notifyDataSetChanged();

                    recyclerConversation.smoothScrollToPosition(recyclerConversation.getAdapter().getItemCount());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        });

        getMessages();

        getSignedUser();

    }

    private void getMessages() {
        messageArrayList = new ArrayList<>();
        StringRequest request = new StringRequest(Request.Method.GET, Constant.MESSAGES + "/" + conversationID, response -> {
            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("success")) {

                    JSONArray resultArray = new JSONArray(object.getString("messages"));

                    for (int i = 0; i < resultArray.length(); i++) {

                        JSONObject messageObject = resultArray.getJSONObject(i);
                        JSONObject userObject = messageObject.getJSONObject("sender");
                        JSONObject avatar = userObject.getJSONObject("avatar");

                        Message message = new Message();
                        message.setMessageID(messageObject.getString("_id"));
                        message.setContent(messageObject.getString("content"));

                        User user = new User();
                        user.setUserID(userObject.getString("_id"));
                        user.setFirstname(userObject.getString("firstname"));
                        user.setAvatar(avatar.getString("url"));
                        message.setUser(user);

                        messageArrayList.add(message);
                    }

                    messagesAdapter = new MessagesAdapter(getApplicationContext(), messageArrayList, signedUser.getUserID());
                    recyclerConversation.setAdapter(messagesAdapter);
                    recyclerConversation.smoothScrollToPosition(messagesAdapter.getItemCount());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            error.printStackTrace();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);

//        User user = messageArrayList.get(1).getUser();
//        Picasso.get().load(user.getAvatar()).fit().centerCrop().into(imgYouHeader);
    }

    private void sendMessage() {
        StringRequest request = new StringRequest(Request.Method.POST, Constant.SEND_MESSAGE, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    socket.emit("new message", response);
                    txtEnterMessage.setText("");
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }, error -> {
            error.printStackTrace();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("content", txtEnterMessage.getText().toString());
                map.put("conversationID", conversationID);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);

    }

    private void getSignedUser() {
        signedUser = new User();
        StringRequest request = new StringRequest(Request.Method.GET, Constant.USER_PROFILE, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONObject userObject = object.getJSONObject("user");
                    signedUser.setUserID(userObject.getString("_id"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            error.printStackTrace();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }


}