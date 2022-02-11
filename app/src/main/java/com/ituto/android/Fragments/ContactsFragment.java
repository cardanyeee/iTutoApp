package com.ituto.android.Fragments;

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
    private User user;

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

        getContacts();

        swipeContacts.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getContacts();
            }
        });

    }

    private void getContacts() {
        messageArrayList = new ArrayList<>();
        swipeContacts.setRefreshing(true);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.CONVERSATIONS, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {

                    JSONArray resultArray = new JSONArray(object.getString("results"));

                    for ( int i = 0; i < resultArray.length(); i++) {

                        JSONObject conversationObject = resultArray.getJSONObject(i);
                        JSONObject messageObject = conversationObject.getJSONObject("latestMessage");

                        JSONObject userObject = messageObject.getJSONObject("sender");
                        JSONObject avatar = userObject.getJSONObject("avatar");

                        Message message = new Message();
                        message.setMessageID(messageObject.getString("_id"));
                        message.setContent(messageObject.getString("content"));
                        message.setConversationID(messageObject.getString("conversationID"));

                        User user = new User();
                        user.setUserID(userObject.getString("_id"));
                        user.setFirstname(userObject.getString("firstname"));
                        user.setLastname(userObject.getString("lastname"));
                        user.setAvatar(avatar.getString("url"));

                        message.setUser(user);

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

    @Override
    public void onItemClick(int position) {

    }
}