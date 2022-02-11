package com.ituto.android;

public class Constant {
    public static final String URL = "http://192.168.1.2:8080/";
    public static final String HOME = URL + "api";

    public static final String LOGIN = HOME + "/auth/login";
    public static final String LOGOUT = HOME + "/auth/logout";
    public static final String REGISTER = HOME + "/auth/register";
    public static final String USER_PROFILE = HOME + "/android/profile/me";
    public static final String SAVE_USER_INFO = HOME + "/save_user_info ";

    public static final String COURSES = HOME + "/course";

    public static final String CONVERSATIONS = HOME + "/conversations";

    public static final String MESSAGES = HOME + "/messages";
    public static final String SEND_MESSAGE = HOME + "/message/send";

}