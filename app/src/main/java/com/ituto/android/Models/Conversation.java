package com.ituto.android.Models;

import java.util.ArrayList;

public class Conversation {

    private String conversationID, latestMessageID;
    private ArrayList<User> userArrayList;

    public String getConversationID() {
        return conversationID;
    }

    public void setConversationID(String conversationID) {
        this.conversationID = conversationID;
    }

    public String getLatestMessageID() {
        return latestMessageID;
    }

    public void setLatestMessageID(String latestMessageID) {
        this.latestMessageID = latestMessageID;
    }

    public ArrayList<User> getUserArrayList() {
        return userArrayList;
    }

    public void setUserArrayList(ArrayList<User> userArrayList) {
        this.userArrayList = userArrayList;
    }

}
