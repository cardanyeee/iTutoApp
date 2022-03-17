package com.ituto.android.Fragments.MainFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ituto.android.Adapters.ContactsAdapter;
import com.ituto.android.Constant;
import com.ituto.android.Models.Conversation;
import com.ituto.android.Models.Message;
import com.ituto.android.Models.User;
import com.ituto.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ContactsFragment extends Fragment implements ContactsAdapter.OnItemListener {
    private View view;

    public static SwipeRefreshLayout swipeContacts;
    public static RecyclerView recyclerContacts;
    public static ArrayList<Message> messageArrayList;
    private User signedUser, contactUser;

    private SharedPreferences sharedPreferences;
    private ContactsAdapter contactsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contacts, container, false);
        init();
        return view;
    }

    private void init() {
        sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerContacts = view.findViewById(R.id.recyclerContacts);
        recyclerContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeContacts = view.findViewById(R.id.swipeContacts);

        getSignedUser();

        getContacts();

        swipeContacts.setOnRefreshListener(() -> getContacts());

    }

    private void getContacts() {
        messageArrayList = new ArrayList<>();
        swipeContacts.setRefreshing(true);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.CONVERSATIONS, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {

                    JSONArray resultArray = new JSONArray(object.getString("results"));

                    for (int i = 0; i < resultArray.length(); i++) {

                        JSONObject conversationObject = resultArray.getJSONObject(i);
                        JSONObject messageObject = new JSONObject();
                        JSONArray userArray = conversationObject.getJSONArray("users");


                        Message message = new Message();

                        if (conversationObject.has("latestMessage")) {
                            messageObject = conversationObject.getJSONObject("latestMessage");
                            message.setMessageID(messageObject.getString("_id"));
                            message.setContent(messageObject.getString("content"));
                            message.setConversationID(messageObject.getString("conversationID"));
                            message.setTimestamp(messageObject.getString("createdAt"));
                        } else {
                            message.setMessageID("none");
                            message.setContent("Start a conversation...");
                            message.setConversationID(conversationObject.getString("_id"));
                        }

                        User user = new User();
                        for ( int a = 0; a < userArray.length(); a++) {
                            JSONObject userObjectInConversation = userArray.getJSONObject(a);
                            if (sharedPreferences.getString("_id", "").equals(userObjectInConversation.getString("_id"))) {

                            } else {
                                JSONObject avatar = userObjectInConversation.getJSONObject("avatar");

                                user.setUserID(userObjectInConversation.getString("_id"));
                                user.setFirstname(userObjectInConversation.getString("firstname"));
                                user.setLastname(userObjectInConversation.getString("lastname"));
                                user.setAvatar(avatar.getString("url"));
                            }
                        }

                        Conversation conversation = new Conversation();
                        ArrayList<String> userIDArrayList = new ArrayList<String>();
                        for ( int a = 0; a < userArray.length(); a++) {
                            JSONObject userObject = userArray.getJSONObject(a);
                            String userID = userObject.getString("_id");
                            userIDArrayList.add(userID);
                        }
                        conversation.setUserIDArrayList(userIDArrayList);

                        message.setUser(user);
                        message.setConversation(conversation);

                        messageArrayList.add(message);
                    }

                    contactsAdapter = new ContactsAdapter(getContext(), messageArrayList, this);
                    recyclerContacts.setAdapter(contactsAdapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            swipeContacts.setRefreshing(false);

        }, error -> {
            error.printStackTrace();
            swipeContacts.setRefreshing(false);
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getContext());
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
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    @Override
    public void onItemClick(int position) {

    }
}