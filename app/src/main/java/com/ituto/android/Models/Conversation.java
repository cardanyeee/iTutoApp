package com.ituto.android.Models;

import java.util.ArrayList;

public class Conversation {

    private String conversationID, conversationName, latestMessageID;
    private ArrayList<User> userArrayList;
    private ArrayList<String> userIDArrayList;

    public String getConversationID() {
        return conversationID;
    }

    public void setConversationID(String conversationID) {
        this.conversationID = conversationID;
    }

    public String getConversationName() {
        return conversationName;
    }

    public void setConversationName(String conversationName) {
        this.conversationName = conversationName;
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

    public ArrayList<String> getUserIDArrayList() {
        return userIDArrayList;
    }

    public void setUserIDArrayList(ArrayList<String> userIDArrayList) {
        this.userIDArrayList = userIDArrayList;
    }

}
