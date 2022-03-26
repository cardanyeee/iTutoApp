package com.ituto.android;

public class Constant {
//    https://mern-ituto.herokuapp.com
//    http://192.168.1.2:8080
    public static final String URL = "http://192.168.1.2:8080";
    public static final String HOME = URL + "/api";

    public static final String LOGIN = HOME + "/auth/login";
    public static final String LOGOUT = HOME + "/auth/logout";
    public static final String CHECK_EMAIL = HOME + "/auth/check/email";
    public static final String REGISTER = HOME + "/auth/register";
    public static final String USER_PROFILE = HOME + "/profile/me";
    public static final String UPDATE_PROFILE = HOME + "/profile/update";
    public static final String SAVE_USER_INFO = HOME + "/save_user_info ";
    public static final String GOOGLE_LOGIN = HOME + "/auth/google/login";

    public static final String COURSES = HOME + "/courses";
    public static final String SUBJECT_COURSES = HOME + "/course-subjects";

    public static final String MESSAGES = HOME + "/messages";
    public static final String SEND_MESSAGE = HOME + "/message/send";
    public static final String CONVERSATIONS = HOME + "/conversations";
    public static final String CONVERSATION = HOME + "/conversation";
    public static final String ACCESS_IMAGE = HOME + "/file/";
    public static final String DOWNLOAD_FILE = HOME + "/download/";

    public static final String REQUEST_SESSION = HOME + "/session/request";
    public static final String DECLINE_SESSION = HOME + "/session/decline";
    public static final String ACCEPT_SESSION = HOME + "/session/accept";
    public static final String TUTOR_SESSIONS = HOME + "/sessions/tutor";
    public static final String TUTEE_SESSIONS = HOME + "/sessions/tutee";
    public static final String SESSIONS = HOME + "/sessions";
    public static final String GET_SESSION = HOME + "/session";
    public static final String REVIEW_TUTOR = HOME + "/session/tutor/review";

    public static final String CREATE_ASSESSMENT  = HOME + "/assessment/create";
    public static final String TUTOR_ASSESSMENTS = HOME + "/assessment/tutor";
    public static final String ALL_ASSESSMENTS = HOME + "/assessments";
    public static final String GET_ASSESSMENT = HOME + "/assessment";
    public static final String ANSWER_ASSESSMENT = HOME + "/assessment/answer";

    public static final String TUTORS = HOME + "/tutors";
    public static final String ADD_TUTOR_SUBJECTS = HOME + "/tutor/subject/add";
    public static final String CREATE_TUTOR_ACCOUNT = HOME + "/tutor/signup";
    public static final String TUTOR_PROFILE = HOME + "/tutor";
    public static final String TUTOR_REVIEWS = HOME + "/tutor/reviews";

}