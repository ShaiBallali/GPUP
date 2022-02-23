package Util;

import com.google.gson.Gson;

public class Constants {

    // global constants

    public final static int REFRESH_RATE = 1000;
    public final static int DELAY_RATE = 0;
    public final static String DARK_THEME_SKIN = "Dark Theme";
    public final static String DEFAULT_SKIN = "Default";



    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/gpup";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/login";
    public final static String USERS_LIST = FULL_SERVER_PATH + "/userslist";
    public final static String EXECUTION_LIST = FULL_SERVER_PATH + "/execution-list";
    public final static String EXECUTION_SUBSCRIBE = FULL_SERVER_PATH + "/execution-subscribe";
    public final static String EXECUTION_UNSUBSCRIBE = FULL_SERVER_PATH + "/execution-unsubscribe";
    public final static String TARGET_FINISHED = FULL_SERVER_PATH + "/target-finished";
    public final static String EXECUTABLE_TARGETS = FULL_SERVER_PATH + "/get-executable-targets";
    public final static String PAUSE_REGISTRATION = FULL_SERVER_PATH + "/pause-registration";
    public final static String RESUME_REGISTRATION = FULL_SERVER_PATH + "/resume-registration";

    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();
}
